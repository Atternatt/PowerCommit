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

package com.github.atternatt.powercommit.feature.commits.model

import com.github.atternatt.powercommit.utils.wrap

private const val LINE_LENGTH = 75

data class Commit(
    val commitType: CommitType,
    val scope: String,
    val title: String,
    val body: String,
    val issueId: String,
    val useGitmoji: Boolean
) {

    private fun formattedBody(): String =
        StringBuilder().apply {
            if(body.isNotEmpty()) {
                append("\n")
                append("\n")
                append(body.wrap(LINE_LENGTH))
            }
        }.toString()

    private fun formattedIssueId(): String = StringBuilder().apply {
        if(issueId.isNotEmpty()) {
            append("\n")
            append("\n")
            append("Related issue id: $issueId")
        }
    }.toString()

    override fun toString(): String =

        "${if(useGitmoji) commitType.emoji else commitType.semver}($scope): $title ${formattedBody()}${formattedIssueId()}".trimMargin()
}
