package com.github.atternatt.powercommit

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vcs.CommitMessageI
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.ui.Refreshable
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class CreateCommitAction : AnAction(), DumbAware {

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val commitMessage: CommitMessageI = getCommitPanel(actionEvent) ?: return
        actionEvent.project?.also {
            /*val dialog = CreateCommitDialog(it)
            dialog.show()
            if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE) {
                commitMessage.setCommitMessage(dialog.getCommit())
            }*/
        }
    }

    private fun getCommitPanel(e: AnActionEvent): CommitMessageI? {
        val dataPanel = Refreshable.PANEL_KEY.getData(e.dataContext)
        val dataCommitMessage = VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.dataContext)
        return dataPanel as? CommitMessageI ?: dataCommitMessage
    }
}