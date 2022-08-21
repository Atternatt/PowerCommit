package com.github.atternatt.powercommit.feature.commits.model

import com.github.atternatt.powercommit.storage.Properties
import com.github.atternatt.powercommit.storage.observableBooleanProperty
import com.github.atternatt.powercommit.storage.stringProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking

interface PowerCommitData {
    val isGitmojiEnabled: Flow<Boolean>
    var scope: String

    fun setGitMojiEnabled(flag: Boolean)

}


fun powerCommitData(properties: Properties) = object : PowerCommitData {
    private val _isGitmojiEnabled = MutableSharedFlow<Boolean>(replay = 1)

    override val isGitmojiEnabled: Flow<Boolean> = _isGitmojiEnabled

    private var gitmojiEnabled: Boolean by properties.observableBooleanProperty(false, _isGitmojiEnabled::tryEmit)

    override var scope: String by properties.stringProperty()

    override fun setGitMojiEnabled(flag: Boolean) {
        gitmojiEnabled = flag
    }

    init {
        runBlocking {
            _isGitmojiEnabled.emit(gitmojiEnabled)
        }
    }
}
