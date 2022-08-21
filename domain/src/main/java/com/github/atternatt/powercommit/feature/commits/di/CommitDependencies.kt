package com.github.atternatt.powercommit.feature.commits.di

import com.github.atternatt.powercommit.feature.commits.presentation.CommitViewModel
import com.github.atternatt.powercommit.feature.commits.presentation.commitViewModel
import com.github.atternatt.powercommit.feature.commits.usecase.GetCommitTypesUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.GitMojiEnabledUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.getCommitTypesUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.gitMojiEnabledUseCase
import com.github.atternatt.powercommit.storage.Properties

interface CommitDependencies {

  val commitViewModel: CommitViewModel

}

fun commitDependencies(properties: Properties): CommitDependencies = object : CommitDependencies {

  override val commitViewModel: CommitViewModel by lazy {
    commitViewModel(
      getCommitTypesUseCase = getCommitTypesUseCase(),
      gitMojiEnabledUseCase = gitMojiEnabledUseCase(properties),
      pCDispatchers = pluginDispatchers()
    )
  }

}