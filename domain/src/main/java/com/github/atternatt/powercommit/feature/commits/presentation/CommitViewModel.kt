package com.github.atternatt.powercommit.feature.commits.presentation

import arrow.core.Some
import arrow.core.continuations.effect
import com.github.atternatt.powercommit.feature.commits.di.PCDispatchers
import com.github.atternatt.powercommit.feature.commits.model.Commit
import com.github.atternatt.powercommit.feature.commits.model.CommitType
import com.github.atternatt.powercommit.feature.commits.usecase.GetCommitTypesUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.GitMojiEnabledUseCase
import com.github.atternatt.powercommit.feature.failue.DomainFailure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * The presentation layer for the Commit form.
 */
interface CommitViewModel {
    /**
     * The current state of the UI
     */
    val state: StateFlow<UiState>

    /**
     * Given a [LoadedUiState] you can call this function to update the selected commit type passing the index of the
     * item in [LoadedUiState.commitTypes]
     * @param index the index of the commit type
     */
    suspend fun selectCommitType(index: Int)

    /**
     * Set whether to use Gitmoji style or normal style
     * @param flag boolean flag to set up the option
     */
    fun useGitmoji(flag: Boolean)

    /**
     * Returs a [Commit] object created with all the information handled by this ViewModel
     */
    fun getCommit(): Commit

    /**
     * Lifecycle event called to clean all the states when the object is no longer used
     */
    fun dispose()

    /**
     * UI state representation
     */
    sealed interface UiState

    /**
     * State representing the loaded screen
     * @property commitTypes a [List] of all the [CommitType] available
     * @property selectedCommitType the current selected [CommitType] index
     * @property useGitmoji option that will be used to display emojis o regular text
     */
    data class LoadedUiState(
        val commitTypes: List<CommitType>, val selectedCommitType: Int, val useGitmoji: Boolean
    ) : UiState

    /**
     * Default UI state. Used when the UI is still idle
     */
    object IdleUiState : UiState

    /**
     * Error State that propagates the [DomainFailure]
     */
    data class Error(val failure: DomainFailure) : UiState
}

fun commitViewModel(
    getCommitTypesUseCase: GetCommitTypesUseCase,
    gitMojiEnabledUseCase: GitMojiEnabledUseCase,
    pCDispatchers: PCDispatchers
): CommitViewModel = object : CommitViewModel, CoroutineScope {

    override val coroutineContext: CoroutineContext = Job()

    //region State
    private val commitTypes = flow {
        emit(getCommitTypesUseCase.getCommitTypes())
    }

    private val selectedCommitType = MutableStateFlow<Int>(0)

    override val state: StateFlow<CommitViewModel.UiState> =
        combine(
            commitTypes,
            gitMojiEnabledUseCase.isGitmojiEnabled,
            selectedCommitType
        ) { types, gitmojiEnabled, selectedTypeIndex ->
            effect<DomainFailure, CommitViewModel.UiState> {
                CommitViewModel.LoadedUiState(
                    commitTypes = types.bind(),
                    selectedCommitType = selectedTypeIndex,
                    useGitmoji = gitmojiEnabled
                )
            }.toOption { Some(CommitViewModel.Error(it)) }
        }
            .flowOn(pCDispatchers.io)
            .map { (it as Some).value }
            .flowOn(pCDispatchers.main)
            .stateIn(
                scope = this,
                started = SharingStarted.WhileSubscribed(),
                initialValue = CommitViewModel.IdleUiState
            )

    //endregion

    //region API
    override suspend fun selectCommitType(index: Int) {
        selectedCommitType.emit(index)
    }

    override fun useGitmoji(flag: Boolean) {
        launch(pCDispatchers.io) {
            gitMojiEnabledUseCase.setGitmojiEnabled(flag)
        }
    }

    override fun getCommit(): Commit = TODO()

    override fun dispose() {
        coroutineContext.cancel()
    }

    //endregion
}

