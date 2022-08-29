package com.github.atternatt.powercommit.feature.commits.usecase

import com.github.atternatt.powercommit.feature.commits.di.PCDispatchers
import com.github.atternatt.powercommit.storage.Properties
import com.github.atternatt.powercommit.storage.observableStringProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

interface GetScopeUseCase {
  fun getScopeStream(): Flow<String>

  suspend fun setGitmojiEnabled(flag: String)
}

fun getScopeUseCase(properties: Properties, dispatchers: PCDispatchers) = object : GetScopeUseCase {
  private var scope: String by properties.observableStringProperty("") {
    scopeFlow.tryEmit(it)
  }

  private val scopeFlow: MutableSharedFlow<String> = MutableSharedFlow(1)

  override fun getScopeStream(): Flow<String> = scopeFlow
    .onStart { emit(scope) }
    .flowOn(dispatchers.io)

  override suspend fun setGitmojiEnabled(flag: String) {
    scope = flag
  }

}