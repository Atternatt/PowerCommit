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

package com.github.atternatt.powercommit.widgets

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize


@Composable
fun <T> DropDownMenu(
    modifier: Modifier = Modifier,
    label: String,
    items: List<T>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit = {},
    adapter: DropdownAdapter<T> = SimpleDropdownAdapter()
) {
    var expanded by remember { mutableStateOf(false) }
    var itemsToShow by remember { mutableStateOf(items) }

    var searchText by remember { mutableStateOf(adapter.content(items[selectedItem])) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(modifier = modifier.wrapContentSize()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { new ->
                searchText = new
                itemsToShow =
                    items.filter { it.toString().lowercase().contains(new.lowercase()) }
                expanded = searchText.length > 2 && itemsToShow.isNotEmpty()
            },
            label = { Text(label) },
            leadingIcon = adapter.icon(items[selectedItem]),
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable {
                        if (expanded) {
                            itemsToShow = items
                            searchText = adapter.content(items[selectedItem])
                        }
                        expanded = !expanded
                    })
            },
            singleLine = true,
            modifier = Modifier
                .onGloballyPositioned { textFieldSize = it.size.toSize() }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
            focusable = false
        ) {
            itemsToShow.forEachIndexed { index, ct ->
                DropdownMenuItem(
                    onClick = {
                        val selectedItemIndex = items.indexOf(ct)
                        expanded = false
                        onItemSelected(selectedItemIndex)
                        searchText = adapter.content(ct)
                        itemsToShow = items
                    }) {
                    Text(adapter.description(ct))
                }
            }
        }

    }
}

interface DropdownAdapter<T> {
    fun icon(t: T): @Composable (() -> Unit)? = null
    fun content(t: T): String
    fun description(t: T): String
}

class SimpleDropdownAdapter<T> : DropdownAdapter<T> {
    override fun content(t: T): String = t.toString()

    override fun description(t: T): String = t.toString()
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun DropDownMenuPreview() {
    val items = List(5) { "Item $it" }
    val itemsAdapte = object : DropdownAdapter<String> {
        override fun content(t: String): String = t
        override fun description(t: String): String = t
    }

    DropDownMenu(
        label = "Test",
        items = items,
        selectedItem = 0,
        adapter = itemsAdapte
    )
}
