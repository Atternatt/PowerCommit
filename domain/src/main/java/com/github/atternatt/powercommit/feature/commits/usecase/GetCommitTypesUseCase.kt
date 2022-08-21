package com.github.atternatt.powercommit.feature.commits.usecase

import arrow.core.continuations.Effect
import arrow.core.continuations.effect
import arrow.core.continuations.ensureNotNull
import com.github.atternatt.powercommit.feature.commits.model.CommitType
import com.github.atternatt.powercommit.feature.failue.DataEmpty
import com.github.atternatt.powercommit.feature.failue.DomainFailure
import com.github.atternatt.powercommit.feature.failue.NotFound
import com.github.atternatt.powercommit.feature.failue.UnknownFailure
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


fun interface GetCommitTypesUseCase {
    suspend fun getCommitTypes(): Effect<DomainFailure, List<CommitType>>
}

fun getCommitTypesUseCase() = GetCommitTypesUseCase {
    effect {
        val data = javaClass.classLoader.getResource("data/gitmoji.json")?.readText()
        ensureNotNull(data) { NotFound }
        ensure(data.isNotEmpty()) { DataEmpty }
        try {
            Json.decodeFromString(data)
        } catch (e: Exception) {
            shift(UnknownFailure(e))
        }
    }
}