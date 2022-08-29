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

package com.github.atternatt.powercommit

import com.github.atternatt.powercommit.feature.commits.presentation.CreateCommitDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vcs.CommitMessageI
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.ui.Refreshable
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class CreateCommitAction : AnAction(), DumbAware {

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val commitMessage: CommitMessageI = getCommitPanel(actionEvent) ?: return
        actionEvent.project?.also {
            val dialog = CreateCommitDialog(it)
            dialog.show()
            if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE) {
                commitMessage.setCommitMessage(dialog.getCommit())
            }
        }
    }

    private fun getCommitPanel(e: AnActionEvent): CommitMessageI? {
        val dataPanel = Refreshable.PANEL_KEY.getData(e.dataContext)
        val dataCommitMessage = VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.dataContext)
        return dataPanel as? CommitMessageI ?: dataCommitMessage
    }
}