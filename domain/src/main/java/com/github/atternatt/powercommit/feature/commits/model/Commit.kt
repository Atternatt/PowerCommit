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
                append(body.wrap(LINE_LENGTH))
            }
        }.toString()

    private fun formattedIssueId(): String = StringBuilder().apply {
        if(issueId.isNotEmpty()) {
            append("\n")
            append("Related issue id: $issueId")
        }
    }.toString()

    override fun toString(): String =

        """${if(useGitmoji) commitType.emoji else commitType.semver}($scope): $title ${formattedBody()}${formattedIssueId()}""".trimMargin()
}
