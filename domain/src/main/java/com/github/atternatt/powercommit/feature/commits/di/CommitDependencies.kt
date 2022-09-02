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

package com.github.atternatt.powercommit.feature.commits.di

import com.github.atternatt.powercommit.feature.commits.presentation.CommitViewModel
import com.github.atternatt.powercommit.feature.commits.presentation.commitViewModel
import com.github.atternatt.powercommit.feature.commits.usecase.GetCommitTypesUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.getCommitTypesUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.getScopeUseCase
import com.github.atternatt.powercommit.feature.commits.usecase.gitmojiEnabledUseCase
import com.github.atternatt.powercommit.storage.Properties

interface CommitDependencies {
  val commitViewModel: CommitViewModel
  val getCommitTypesUseCase: GetCommitTypesUseCase
}

fun commitDependencies(properties: Properties): CommitDependencies = object : CommitDependencies {
  private val dispatchers = pluginDispatchers()

  override val getCommitTypesUseCase: GetCommitTypesUseCase by lazy { getCommitTypesUseCase(dispatchers) }

  override val commitViewModel: CommitViewModel by lazy {
    commitViewModel(
      getCommitTypesUseCase = getCommitTypesUseCase,
      gitMojiEnabledUseCase = gitmojiEnabledUseCase(properties, dispatchers),
      scopeUseCase = getScopeUseCase(properties, dispatchers)
    )
  }

}