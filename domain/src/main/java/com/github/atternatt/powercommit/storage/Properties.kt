package com.github.atternatt.powercommit.storage

import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Properties {
    fun getBoolean(key: String, default: Boolean): Boolean
    fun setBoolean(key: String, flag: Boolean)

    fun getString(key: String, default: String): String
    fun setString(key: String, flag: String)
}

internal fun Properties.booleanProperty() = object : ReadWriteProperty<Any, Boolean> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return getBoolean(property.name, default = false)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        setBoolean(property.name, value)
    }
}

internal fun Properties.stringProperty() = object : ReadWriteProperty<Any, String> {
    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return getString(property.name, default = "")
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        setString(property.name, value)
    }
}

internal fun Properties.observableBooleanProperty(initialValue: Boolean, emit: (Boolean) -> Unit) =
    object : ObservableProperty<Boolean>(initialValue) {

        override fun afterChange(property: KProperty<*>, oldValue: Boolean, newValue: Boolean) {
            emit(newValue)
        }

        override fun beforeChange(property: KProperty<*>, oldValue: Boolean, newValue: Boolean): Boolean =
            oldValue != newValue

        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return getBoolean(property.name, default = false)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            val oldValue = getBoolean(property.name, default = false)
            setBoolean(property.name, value)
            afterChange(property, oldValue, value)
        }
    }

internal fun Properties.observableStringProperty(initialValue: String, emit: (String) -> Unit) =
    object : ObservableProperty<String>(initialValue) {

        override fun afterChange(property: KProperty<*>, oldValue: String, newValue: String) {
            emit(newValue)
        }

        override fun beforeChange(property: KProperty<*>, oldValue: String, newValue: String): Boolean =
            oldValue != newValue

        override fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return getString(property.name, default = "")
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            val oldValue = getString(property.name, default = "")
            setString(property.name, value)
            afterChange(property, oldValue, value)
        }
    }

