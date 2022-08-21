package com.github.atternatt.powercommit.feature.commits.model

import kotlinx.serialization.Serializable


@Serializable
data class CommitType(
    val emoji: String,
    val entity: String,
    val code: String,
    val description: String,
    val name: String,
    val semver: String) {
    override fun toString(): String = "$emoji - $semver: $description"
}