package com.github.atternatt.powercommit.feature.commits.presentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.atternatt.powercommit.feature.commits.model.CommitType
import com.github.atternatt.powercommit.widgets.DropdownAdapter

object CommitTypeAdapter: DropdownAdapter<CommitType> {

    override fun icon(t: CommitType): @Composable (() -> Unit) = {
        Text(
            text = t.emoji,
            color = Color.White
        )
    }

    override fun content(t: CommitType): String = t.semver

    override fun description(t: CommitType): String = t.toString()
}