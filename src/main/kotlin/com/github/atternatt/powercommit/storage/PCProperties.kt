package com.github.atternatt.powercommit.storage

import com.intellij.ide.util.PropertiesComponent

class PCProperties(private val component: PropertiesComponent): Properties {
    override fun getBoolean(key: String, default: Boolean): Boolean = component.getBoolean(key, default)

    override fun setBoolean(key: String, flag: Boolean) {
        component.setValue(key, flag)
    }

    override fun getString(key: String, default: String): String = component.getValue(key, default)

    override fun setString(key: String, flag: String) {
        component.setValue(key, flag)
    }

}