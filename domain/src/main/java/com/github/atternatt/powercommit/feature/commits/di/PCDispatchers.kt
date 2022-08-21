package com.github.atternatt.powercommit.feature.commits.di

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext


interface PCDispatchers {
  val io: CoroutineContext
  val main: CoroutineContext
}

internal fun pluginDispatchers() = object : PCDispatchers {
  private val handler = CoroutineExceptionHandler { _, exception ->
    Logger.getGlobal().log(Level.SEVERE, exception.toString())
  }
  override val io: CoroutineContext = Dispatchers.IO + handler
  override val main: CoroutineContext = Dispatchers.Main + handler
}