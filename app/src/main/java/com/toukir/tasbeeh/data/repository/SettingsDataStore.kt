package com.toukir.tasbeeh.data.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import com.toukir.tasbeeh.data.settingsDataStore
import com.toukir.tasbeeh.ui.AppSettings
import com.toukir.tasbeeh.ui.theme.AppColorTheme
import com.toukir.tasbeeh.ui.theme.AppTheme
import com.toukir.tasbeeh.ui.theme.GradientStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(private val context: Context) {
    companion object {
        val THEME_KEY = intPreferencesKey("app_theme")
        val COLOR_THEME_KEY = intPreferencesKey("app_color_theme")
        val GRADIENT_KEY = stringPreferencesKey("app_gradient")
        val THICKNESS_KEY = floatPreferencesKey("app_thickness")
        val SOUND_KEY = booleanPreferencesKey("app_sound")
        val VIBRATE_TAP_KEY = booleanPreferencesKey("app_vibrate_tap")
        val VIBRATE_100_KEY = booleanPreferencesKey("app_vibrate_100")
        val LANGUAGE_KEY = stringPreferencesKey("app_language")
        val SHOW_COUNTER_CIRCLE_KEY = booleanPreferencesKey("app_show_counter_circle")
    }

    val settingsFlow: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            theme = AppTheme.entries.getOrElse(prefs[THEME_KEY] ?: 0) { AppTheme.Light },
            colorTheme = AppColorTheme.entries.getOrElse(prefs[COLOR_THEME_KEY] ?: 0) { AppColorTheme.Gold },
            gradient = try { GradientStyle.valueOf(prefs[GRADIENT_KEY] ?: GradientStyle.Sunset.name) } catch (e: Exception) { GradientStyle.Sunset },
            thickness = prefs[THICKNESS_KEY] ?: 20f,
            isSoundEnabled = prefs[SOUND_KEY] ?: true,
            isVibrateTapEnabled = prefs[VIBRATE_TAP_KEY] ?: false,
            isVibrate100Enabled = prefs[VIBRATE_100_KEY] ?: true,
            language = prefs[LANGUAGE_KEY] ?: "en",
            showCounterCircle = prefs[SHOW_COUNTER_CIRCLE_KEY] ?: true
        )
    }

    suspend fun saveTheme(theme: AppTheme) { context.settingsDataStore.edit { it[THEME_KEY] = theme.ordinal } }
    suspend fun saveColorTheme(colorTheme: AppColorTheme) { context.settingsDataStore.edit { it[COLOR_THEME_KEY] = colorTheme.ordinal } }
    suspend fun saveGradient(gradient: GradientStyle) { context.settingsDataStore.edit { it[GRADIENT_KEY] = gradient.name } }
    suspend fun saveThickness(thickness: Float) { context.settingsDataStore.edit { it[THICKNESS_KEY] = thickness } }
    suspend fun saveSoundEnabled(enabled: Boolean) { context.settingsDataStore.edit { it[SOUND_KEY] = enabled } }
    suspend fun saveVibrateTapEnabled(enabled: Boolean) { context.settingsDataStore.edit { it[VIBRATE_TAP_KEY] = enabled } }
    suspend fun saveVibrate100Enabled(enabled: Boolean) { context.settingsDataStore.edit { it[VIBRATE_100_KEY] = enabled } }
    suspend fun saveLanguage(language: String) { context.settingsDataStore.edit { it[LANGUAGE_KEY] = language } }
    suspend fun saveShowCounterCircleEnabled(enabled: Boolean) { context.settingsDataStore.edit { it[SHOW_COUNTER_CIRCLE_KEY] = enabled } }
}
