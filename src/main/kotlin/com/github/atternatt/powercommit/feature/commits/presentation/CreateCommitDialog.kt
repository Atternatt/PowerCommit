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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.github.atternatt.powercommit.feature.commits.di.commitDependencies
import com.github.atternatt.powercommit.feature.commits.presentation.CommitViewModel.MetadataState
import com.github.atternatt.powercommit.storage.PCProperties
import com.github.atternatt.powercommit.theme.WidgetTheme
import com.github.atternatt.powercommit.widgets.DropDownMenu
import com.github.atternatt.powercommit.widgets.EmptyContent
import com.github.atternatt.powercommit.widgets.LabelledCheckbox
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.swing.Swing
import javax.swing.JComponent
import kotlin.contracts.ExperimentalContracts
import kotlin.coroutines.CoroutineContext

@ExperimentalContracts
class CreateCommitDialog(project: Project) : DialogWrapper(project), CoroutineScope by CoroutineScope(Dispatchers.Swing) {


  private val viewModel: CommitViewModel by lazy { commitDependencies(PCProperties(PropertiesComponent.getInstance())).commitViewModel }

  init {
    title = "Commit"
    setOKButtonText("OK")
    init()
  }

  @OptIn(ExperimentalFoundationApi::class)
  override fun createCenterPanel(): JComponent =
    ComposePanel().apply {
      setBounds(0, 0, 400, 400)
      setContent {
        WidgetTheme {
          val state by viewModel.metadataState.collectAsState(context = coroutineContext)
          val commitState by viewModel.commitState.collectAsState(context = coroutineContext)
          val commitTypeState by viewModel.commitTypeState.collectAsState(context = coroutineContext)
          Surface(modifier = Modifier.wrapContentSize()) {
            Row {
              Column(
                modifier = Modifier.wrapContentSize()
              ) {
                MetadataSection(
                  uiState = state,
                  onScopeChanged = viewModel::setScope,
                  onIdChanged = viewModel::setTaskId
                ) {
                  Row(
                    modifier = Modifier
                      .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    CommitTypesSection(
                      state = commitTypeState,
                      onCommitTypeSelected = viewModel::selectCommitType
                    )
                    TooltipArea(
                      tooltip = {
                        // composable tooltip content
                        Surface(
                          modifier = Modifier.shadow(4.dp),
                          shape = RoundedCornerShape(4.dp)
                        ) {
                          Text(
                            text = "When checked the plugin will use the emoji instead of the text code",
                            modifier = Modifier.padding(8.dp)
                          )
                        }
                      },
                      modifier = Modifier.padding(start = 40.dp),
                      delayMillis = 600, // in milliseconds
                      tooltipPlacement = TooltipPlacement.CursorPoint(
                        alignment = Alignment.BottomEnd
                      )
                    ) {
                      Icon(
                        modifier = Modifier
                          .alpha(if (state.useGitmoji) 1f else 0.35f)
                          .clickable { viewModel.useGitmoji(!state.useGitmoji) },
                        imageVector = Icons.Default.Face, contentDescription = "Use Emoji"
                      )
                    }
                  }
                }
                CommitSection(
                  uiState = commitState,
                  onTitleChanged = viewModel::setTitle,
                  onBodyChanged = viewModel::setBody
                )
              }
              Divider(
                modifier = Modifier
                  .fillMaxHeight()
                  .width(2.dp)
                  .padding(vertical = 16.dp)
              )
              Column {
                LabelledCheckbox(
                  modifier = Modifier.padding(start = 8.dp),
                  checked = false,
                  onCheckedChange = viewModel::useGitmoji,
                  label = "Gitmoji"
                )
              }
            }
          }
        }
      }
    }

  fun getCommit(): String {
    return viewModel.getCommit().fold(
      ifEmpty = { "" },
      ifSome = { it.toString() }
    )
  }

  override fun dispose() {
    super.dispose()
    viewModel.dispose()
    cancel()
  }


  @Composable
  fun CommitTypesSection(
    state: CommitViewModel.CommitTypeState,
    onCommitTypeSelected: (Int) -> Unit
  ) {
    when (state) {
      is CommitViewModel.CommitTypeState.Error -> EmptyContent("Error loading commit types")
      CommitViewModel.CommitTypeState.Idle -> EmptyContent("Loading Commit types")
      is CommitViewModel.CommitTypeState.Success -> {
        DropDownMenu(
          label = "Commit Type",
          items = state.commitTypes.toList(),
          selectedItem = state.selectedCommitType,
          onItemSelected = onCommitTypeSelected,
          adapter = CommitTypeAdapter
        )
      }
    }
  }

  @Composable
  fun MetadataSection(
    uiState: MetadataState,
    onScopeChanged: (String) -> Unit,
    onIdChanged: (String) -> Unit,
    header: @Composable () -> Unit
  ) {
    Box {
      header()
    }
    Divider(
      modifier =
      Modifier
        .fillMaxWidth()
        .width(2.dp)
        .padding(horizontal = 16.dp)
    )
    Row(
      modifier = Modifier.fillMaxWidth()
        .height(IntrinsicSize.Min)
        .padding(horizontal = 16.dp)
        .padding(top = 8.dp)
    ) {
      OutlinedTextField(value = uiState.scope, onValueChange = onScopeChanged,
        modifier = Modifier
          .weight(1f, true)
          .padding(end = 4.dp),
        maxLines = 1,
        label = { Text("Scope") }
      )
      OutlinedTextField(value = uiState.id, onValueChange = onIdChanged,
        modifier = Modifier
          .weight(1f, true)
          .padding(start = 4.dp),
        maxLines = 1,
        label = { Text("Task ID") }
      )
    }
  }

}

@Composable
fun CommitSection(
  uiState: CommitViewModel.CommitState,
  onTitleChanged: (String) -> Unit,
  onBodyChanged: (String) -> Unit
) {
  OutlinedTextField(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    maxLines = 1,
    value = uiState.title,
    label = { Text("Title") },
    onValueChange = onTitleChanged
  )
  Text(
    modifier = Modifier.padding(horizontal = 20.dp),
    text = "Body",
    style = MaterialTheme.typography.overline
  )
  OutlinedTextField(
    modifier = Modifier.wrapContentSize()
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .padding(top = 4.dp, bottom = 16.dp),
    value = uiState.body,
    onValueChange = onBodyChanged
  )
}
