/*
 * MIT License
 *
 * Copyright (c) 2022 Marc Moreno Ferrer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.atternatt.powercommit.feature.commits.presentation

import com.github.atternatt.powercommit.feature.commits.model.Commit
import com.github.atternatt.powercommit.feature.commits.model.CommitType
import com.github.atternatt.powercommit.feature.commits.presentation.CommitViewModel.MetadataState
import com.github.atternatt.powercommit.feature.commits.usecase.GetCommitTypesUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.GetScopeUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.GitMojiEnabledUseCase
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
    fun getCommit(forCommitType: CommitType): Commit

    /**
     * Lifecycle event called to clean all the states when the object is no longer used
     */
    fun dispose()


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

    override val metadataState: StateFlow<MetadataState> = combine(
                gitMojiEnabledUseCase.getIsGitmojiEnabledStream(),
                scopeUseCase.getScopeStream(),
                taskId.asStateFlow()
            ) { gitmojiEnabled, scope, taskId ->
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
        titleStream.asStateFlow(),
        bodyStream.asStateFlow()
    ) { title, body ->
        CommitViewModel.CommitState(
            title = title,
            body = body
        )
    }.stateIn(
        scope = this,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CommitViewModel.CommitState()
    )

    //endregion


    //region API
    override fun selectCommitType(index: Int) {
        selectedCommitTypeStream.update {
            index
        }
    }

    override fun useGitmoji(flag: Boolean) {
        launch {
            gitMojiEnabledUseCase.setGitmojiEnabled(flag)
        }
    }

    override fun setTaskId(id: String) {
        taskId.update { id }
    }

    override fun setScope(scope: String) {
        launch {
            scopeUseCase.setGitmojiEnabled(scope)
        }
    }

    override fun setTitle(title: String) {
        titleStream.update { title }
    }

    override fun setBody(body: String) {
        bodyStream.update { body }
    }

    override fun getCommit(forCommitType: CommitType): Commit =
        Commit(
            commitType = forCommitType,
            scope = metadataState.value.scope,
            title = commitState.value.title,
            body = commitState.value.body,
            issueId = metadataState.value.id,
            useGitmoji = metadataState.value.useGitmoji
        )

    override fun dispose() {
        cancel()
    }

    //endregion
}

