package com.github.atternatt.powercommit.feature.commits.usecase

import com.github.atternatt.powercommit.storage.Properties
import com.github.atternatt.powercommit.storage.observableBooleanProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow


interface GitMojiEnabledUseCase {
  val isGitmojiEnabled: Flow<Boolean>

  suspend fun setGitmojiEnabled(flag: Boolean)
}

internal fun gitMojiEnabledUseCase(properties: Properties) = object : GitMojiEnabledUseCase {

  private val _isGitmojiEnabled = MutableSharedFlow<Boolean>(replay = 1)

  override val isGitmojiEnabled: Flow<Boolean> = _isGitmojiEnabled

  private var gitmojiEnabled: Boolean by properties.observableBooleanProperty(false, _isGitmojiEnabled::tryEmit)


  override suspend fun setGitmojiEnabled(flag: Boolean) {
    _isGitmojiEnabled.emit(flag)
  }
}