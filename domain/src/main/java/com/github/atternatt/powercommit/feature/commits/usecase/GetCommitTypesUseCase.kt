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

package com.github.atternatt.powercommit.feature.commits.usecase

import arrow.core.continuations.Effect
import arrow.core.continuations.effect
import arrow.core.continuations.ensureNotNull
import com.github.atternatt.powercommit.feature.commits.di.PCDispatchers
import com.github.atternatt.powercommit.feature.commits.model.CommitType
import com.github.atternatt.powercommit.feature.failue.DataEmpty
import com.github.atternatt.powercommit.feature.failue.DomainFailure
import com.github.atternatt.powercommit.feature.failue.NotFound
import com.github.atternatt.powercommit.feature.failue.UnknownFailure
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


fun interface GetCommitTypesUseCase {
    fun getCommitTypes(): Flow<Effect<DomainFailure, Set<CommitType>>>
}

fun getCommitTypesUseCase(dispatcher: PCDispatchers) = GetCommitTypesUseCase {
    flowOf(effect<DomainFailure, Set<CommitType>> {
        val data = javaClass.classLoader.getResource("data/gitmoji.json")?.readText()
        ensureNotNull(data) { NotFound }
        ensure(data.isNotEmpty()) { DataEmpty }
        try {
            Json.decodeFromString(data)
        } catch (e: Exception) {
            shift(UnknownFailure(e))
        }
    })
        .flowOn(dispatcher.io)
}