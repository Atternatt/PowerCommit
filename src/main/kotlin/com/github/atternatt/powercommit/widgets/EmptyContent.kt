package com.github.atternatt.powercommit.widgets

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyContent(body: String) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(text = body)
    }
}

@Preview
@Composable
fun EmptyContentPreview() {
    EmptyContent("Demo text")
}