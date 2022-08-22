package com.github.atternatt.powercommit.feature.commits.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.*
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
            onGitmojiOptionChecked = viewModel::useGitmoji,
            pull = viewModel::pull
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
    pull: () -> Unit,
    onGitmojiOptionChecked: (Boolean) -> Unit
  ) {
    val scope = rememberCoroutineScope()
    val currentPull by rememberUpdatedState(pull)
    val currentOnTypeSelected by rememberUpdatedState(onTypeSelected)
    val currentonGitmojiOptionChecked by rememberUpdatedState(onGitmojiOptionChecked)

    Surface(modifier = Modifier.fillMaxSize()) {
      when (uiState) {
        is CommitViewModel.LoadedUiState -> {
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
              onItemSelected = { currentOnTypeSelected(it) },
              adapter = CommitTypeAdapter
            )
            LabelledCheckbox(
              modifier = Modifier.padding(start = 8.dp),
              checked = uiState.useGitmoji,
              onCheckedChange = currentonGitmojiOptionChecked,
              label = "Gitmoji"
            )
          }
        }

        is CommitViewModel.IdleUiState -> {
            Button(modifier = Modifier, onClick = {
              currentPull()
            }) {
              Text("Pull")
            }
//            EmptyContent("The Content is loading")
        }

        else -> {
          EmptyContent("There is no content to show")
          Button(modifier = Modifier,
            onClick = { currentPull() },
          colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)) {
            Text("Pull")
          }
        }
      }
    }
  }
}
