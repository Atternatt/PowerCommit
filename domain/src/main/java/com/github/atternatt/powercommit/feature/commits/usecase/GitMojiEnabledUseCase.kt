package com.github.atternatt.powercommit.feature.commits.usecase

import com.github.atternatt.powercommit.feature.commits.di.PCDispatchers
import com.github.atternatt.powercommit.storage.Properties
import com.github.atternatt.powercommit.storage.observableBooleanProperty
import kotlinx.coroutines.flow.*


interface GitMojiEnabledUseCase {
  fun getIsGitmojiEnabledStream(): Flow<Boolean>

  suspend fun setGitmojiEnabled(flag: Boolean)
}

internal fun gitmojiEnabledUseCase(properties: Properties, pcDispatchers: PCDispatchers) = object : GitMojiEnabledUseCase {

  private var gitmojiEnabled: Boolean by properties.observableBooleanProperty(false) {
    gitmojiEnabledFlow.tryEmit(it)
  }

  private val gitmojiEnabledFlow: MutableSharedFlow<Boolean> = MutableSharedFlow(1)

  override fun getIsGitmojiEnabledStream(): Flow<Boolean> = gitmojiEnabledFlow
    .onStart { emit(gitmojiEnabled) }
    .flowOn(pcDispatchers.io)

  override suspend fun setGitmojiEnabled(flag: Boolean) {
    gitmojiEnabled = flag
  }
}