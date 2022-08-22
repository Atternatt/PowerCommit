package com.github.atternatt.powercommit.feature.commits.usecase

import com.github.atternatt.powercommit.feature.commits.di.PCDispatchers
import com.github.atternatt.powercommit.storage.Properties
import com.github.atternatt.powercommit.storage.observableBooleanProperty
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn


interface GitMojiEnabledUseCase {
  fun getIsGitmojiEnabledStream(): Flow<Boolean>

  suspend fun setGitmojiEnabled(flag: Boolean)
}

internal fun gitMojiEnabledUseCase(properties: Properties, pcDispatchers: PCDispatchers) = object : GitMojiEnabledUseCase {

  private var listener: (Boolean) -> Unit = {}

  private var gitmojiEnabled: Boolean by properties.observableBooleanProperty(false, listener)

  private fun onPropertChanged(block: (Boolean) -> Unit) {
    listener = block
  }

  override fun getIsGitmojiEnabledStream(): Flow<Boolean> = callbackFlow {
    onPropertChanged {
      trySend(it)
    }
    awaitClose { onPropertChanged {} }
  }
    .flowOn(pcDispatchers.io)

  override suspend fun setGitmojiEnabled(flag: Boolean) {
    gitmojiEnabled = flag
  }
}