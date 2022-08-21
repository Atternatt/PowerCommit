package com.github.atternatt.powercommit.feature.commits.di

import com.github.atternatt.powercommit.feature.commits.usecase.GetCommitTypesUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.GitMojiEnabledUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.getCommitTypesUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.gitMojiEnabledUseCase
import com.github.atternatt.powercommit.storage.Properties

interface CommitDependencies {

  val gitMojiEnabledUseCase: GitMojiEnabledUseCase

  val getCommitTypesUseCase: GetCommitTypesUseCase

}

fun commitDependencies(properties: Properties): CommitDependencies = object : CommitDependencies {
  override val gitMojiEnabledUseCase: GitMojiEnabledUseCase by lazy { gitMojiEnabledUseCase(properties) }
  override val getCommitTypesUseCase: GetCommitTypesUseCase by lazy { getCommitTypesUseCase() }

}