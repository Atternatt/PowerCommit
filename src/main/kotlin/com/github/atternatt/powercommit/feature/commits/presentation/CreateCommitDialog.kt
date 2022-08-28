package com.github.atternatt.powercommit.feature.commits.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.unit.dp
import com.github.atternatt.powercommit.feature.commits.di.commitDependencies
import com.github.atternatt.powercommit.storage.PCProperties
import com.github.atternatt.powercommit.theme.WidgetTheme
import com.github.atternatt.powercommit.widgets.DropDownMenu
import com.github.atternatt.powercommit.widgets.EmptyContent
import com.github.atternatt.powercommit.widgets.LabelledCheckbox
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.swing.JComponent
import kotlin.contracts.ExperimentalContracts
import kotlin.coroutines.CoroutineContext

@ExperimentalContracts
class CreateCommitDialog(project: Project) : DialogWrapper(project), CoroutineScope {

  override val coroutineContext: CoroutineContext = Job()

  private val viewModel: CommitViewModel by lazy { commitDependencies(PCProperties(PropertiesComponent.getInstance())).commitViewModel }


  init {
    title = "Commit"
    setOKButtonText("OK")
    init()
  }

  override fun createCenterPanel(): JComponent =
    ComposePanel().apply {
      setBounds(0, 0, 600, 400)
      setContent {
        WidgetTheme {
          val state by viewModel.state.collectAsState()
          Screen(
            uiState = state,
            onTypeSelected = viewModel::selectCommitType,
            onGitmojiOptionChecked = viewModel::useGitmoji
          )
        }
      }
    }

  fun getCommit(): String = TODO()

  override fun dispose() {
    super.dispose()
    viewModel.dispose()
    cancel()
  }

  @Composable
  fun Screen(
    uiState: CommitViewModel.UiState,
    onTypeSelected: (Int) -> Unit,
    onGitmojiOptionChecked: (Boolean) -> Unit
  ) {
    Surface(modifier = Modifier.fillMaxSize()) {
      when (uiState) {
        is CommitViewModel.LoadedUiState -> {
          Column {
            Row(
              modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            ) {
              DropDownMenu(
                modifier = Modifier
                  .wrapContentSize()
                  .padding(end = 8.dp),
                label = "Commit Type",
                items = uiState.commitTypes,
                selectedItem = uiState.selectedCommitType,
                onItemSelected = { onTypeSelected(it) },
                adapter = CommitTypeAdapter
              )
              LabelledCheckbox(
                modifier = Modifier.padding(start = 8.dp),
                checked = uiState.useGitmoji,
                onCheckedChange = onGitmojiOptionChecked,
                label = "Gitmoji"
              )
            }
            Divider(
              modifier =
              Modifier
                .fillMaxWidth()
                .width(2.dp)
                .padding(horizontal = 16.dp)
            )
          }        }

        is CommitViewModel.IdleUiState -> {
          EmptyContent("The Content is loading")
        }

        else -> {
          val error = (uiState as CommitViewModel.Error).failure
          EmptyContent("There is no content to show")
        }
      }
    }
  }
}
