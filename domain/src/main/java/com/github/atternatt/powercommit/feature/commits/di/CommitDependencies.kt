package com.github.atternatt.powercommit.feature.commits.di

import com.github.atternatt.powercommit.feature.commits.presentation.CommitViewModel
import com.github.atternatt.powercommit.feature.commits.presentation.commitViewModel
import com.github.atternatt.powercommit.feature.commits.usecase.getCommitTypesUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.getScopeUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.gitmojiEnabledUseCase
import com.github.atternatt.powercommit.storage.Properties

interface CommitDependencies {

  val commitViewModel: CommitViewModel

}

fun commitDependencies(properties: Properties): CommitDependencies = object : CommitDependencies {

  override val commitViewModel: CommitViewModel by lazy {
    val dispatchers = pluginDispatchers()
    commitViewModel(
      getCommitTypesUseCase = getCommitTypesUseCase(dispatchers),
      gitMojiEnabledUseCase = gitmojiEnabledUseCase(properties, dispatchers),
      scopeUseCase = getScopeUseCase(properties, dispatchers)
    )
  }

}