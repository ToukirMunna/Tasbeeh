package com.toukir.tasbeeh.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.tasbeehDataStore: DataStore<Preferences> by preferencesDataStore(name = "tasbeeh_prefs")
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")
