package com.github.atternatt.powercommit.feature.commits.presentation

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.continuations.either
import com.github.atternatt.powercommit.feature.commits.model.Commit
import com.github.atternatt.powercommit.feature.commits.model.CommitType
import com.github.atternatt.powercommit.feature.commits.presentation.CommitViewModel.MetadataState
import com.github.atternatt.powercommit.feature.commits.usecase.GetCommitTypesUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.GetScopeUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.GitMojiEnabledUseCase
import com.github.atternatt.powercommit.feature.failue.DomainFailure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
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
    val metadataState: StateFlow<MetadataState>
    val commitState: StateFlow<CommitState>
    val commitTypeState: StateFlow<CommitTypeState>
//    val commitType: StateFlow<>

    /**
     * Given a [Success] you can call this function to update the selected commit type passing the index of the
     * item in [Success.commitTypes]
     * @param index the index of the commit type
     */
    fun selectCommitType(index: Int)

    /**
     * Set whether to use Gitmoji style or normal style
     * @param flag boolean flag to set up the option
     */
    fun useGitmoji(flag: Boolean)

    /**
     * Set the scope of the commit
     */
    fun setScope(scope: String)

    /**
     * Set the id of the task
     */
    fun setTaskId(id: String)

    /**
     * Set the title of the commit
     */
    fun setTitle(title: String)

    /**
     * Set the body of the commit
     */
    fun setBody(body: String)

    /**
     * Returns a [Commit] object created with all the information handled by this ViewModel
     */
    fun getCommit(): Option<Commit>

    /**
     * Lifecycle event called to clean all the states when the object is no longer used
     */
    fun dispose()


    sealed interface CommitTypeState {
        object Idle : CommitTypeState
        data class Error(val failure: DomainFailure) : CommitTypeState

        /**
         * @property commitTypes a [Set] of all the [CommitType] available
         * @property selectedCommitType the current selected [CommitType] index*/
        data class Success(
            val commitTypes: Set<CommitType>,
            val selectedCommitType: Int
        ) : CommitTypeState
    }

    /**
     * State representing the loaded screen
     * @param id the id of the task attached to the commit (can be an empty string)
     * @property useGitmoji option that will be used to display emojis o regular text
     * @property scope option used to show the scope of the commit
     */
    data class MetadataState(
        val id: String = "",
        val useGitmoji: Boolean = false,
        val scope: String = ""
    )

    data class CommitState(
        val title: String = "",
        val body: String = ""
    )
}


fun commitViewModel(
    getCommitTypesUseCase: GetCommitTypesUseCase,
    gitMojiEnabledUseCase: GitMojiEnabledUseCase,
    scopeUseCase: GetScopeUseCase
): CommitViewModel = object : CommitViewModel, CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob()

    //region MetadataState
    private val selectedCommitTypeStream = MutableStateFlow(0)

    private val taskId = MutableStateFlow("")

    override val commitTypeState: StateFlow<CommitViewModel.CommitTypeState> =
        combine(
            getCommitTypesUseCase.getCommitTypes().shareIn(this, started = SharingStarted.Eagerly, replay = 1),
            selectedCommitTypeStream
        ) { commitTypes, selectedType ->
            either<DomainFailure, CommitViewModel.CommitTypeState> {
                CommitViewModel.CommitTypeState.Success(
                    commitTypes = commitTypes.bind(),
                    selectedCommitType = selectedType
                )
            }
        }.map {
            it.fold(
                ifLeft = { CommitViewModel.CommitTypeState.Error(it) },
                ifRight = { it }
            )
        }
            .stateIn(
                scope = this,
                started = SharingStarted.Eagerly,
                initialValue = CommitViewModel.CommitTypeState.Idle
            )

    override val metadataState: StateFlow<MetadataState> =
        combine(
            gitMojiEnabledUseCase.getIsGitmojiEnabledStream(),
            selectedCommitTypeStream.onStart { emit(0) },
            scopeUseCase.getScopeStream(),
            taskId.onStart { emit("") }
        ) { gitmojiEnabled, selectedTypeIndex, scope, taskId ->
            MetadataState(
                id = taskId,
                useGitmoji = gitmojiEnabled,
                scope = scope
            )
        }
            .stateIn(
                scope = this,
                started = SharingStarted.WhileSubscribed(),
                initialValue = MetadataState()
            )


    //endregion

    //region Commit state

    private val titleStream = MutableStateFlow("")

    private val bodyStream = MutableStateFlow("")

    override val commitState: StateFlow<CommitViewModel.CommitState> = combine(
        titleStream,
        bodyStream
    ) { title, body ->
        CommitViewModel.CommitState(
            title = title,
            body = body
        )
    }.stateIn(
        scope = this,
        started = SharingStarted.WhileSubscribed(),
        initialValue = CommitViewModel.CommitState()
    )

    //endregion


    //region API
    override fun selectCommitType(index: Int) {
        launch {
            selectedCommitTypeStream.value = index
        }
    }

    override fun useGitmoji(flag: Boolean) {
        launch {
            gitMojiEnabledUseCase.setGitmojiEnabled(flag)
        }
    }

    override fun setTaskId(id: String) {
        launch {
            taskId.emit(id)
        }
    }

    override fun setScope(scope: String) {
        launch {
            scopeUseCase.setGitmojiEnabled(scope)
        }
    }

    override fun setTitle(title: String) {
        launch {
            titleStream.emit(title)
        }
    }

    override fun setBody(body: String) {
        launch {
            bodyStream.emit(body)
        }
    }

    override fun getCommit(): Option<Commit> {
        val commitTypeValue = commitTypeState.value
        return if (commitTypeValue is CommitViewModel.CommitTypeState.Success) {
            Some(
                Commit(
                    commitType = commitTypeValue.commitTypes.toList()[commitTypeValue.selectedCommitType],
                    scope = metadataState.value.scope,
                    title = commitState.value.title,
                    body = commitState.value.body,
                    issueId = metadataState.value.id,
                    useGitmoji = metadataState.value.useGitmoji
                )
            )
        } else {
            None
        }
    }

    override fun dispose() {
        cancel()
    }

    //endregion
}

