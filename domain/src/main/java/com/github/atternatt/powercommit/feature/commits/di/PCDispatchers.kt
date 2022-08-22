package com.github.atternatt.powercommit.feature.commits.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlin.coroutines.CoroutineContext


interface PCDispatchers {
  val io: CoroutineContext
  val main: CoroutineContext
}

internal fun pluginDispatchers() = object : PCDispatchers {
  //  private val handler = CoroutineExceptionHandler { _, exception ->
//    Logger.getGlobal().log(Level.SEVERE, exception.toString())
//  }
  override val io: CoroutineContext = Dispatchers.IO
  override val main: CoroutineContext = Dispatchers.Swing
}