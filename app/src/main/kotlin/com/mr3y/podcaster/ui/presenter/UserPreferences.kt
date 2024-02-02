package com.mr3y.podcaster.ui.presenter

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mr3y.podcaster.ui.presenter.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserPreferences @Inject constructor(
    private val datastore: DataStore<Preferences>,
    @ApplicationScope private val applicationScope: CoroutineScope,
) {

    private val _selectedTheme = MutableStateFlow<Theme?>(null)
    val selectedTheme = _selectedTheme.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(false)
    val dynamicColorEnabled = _dynamicColorEnabled.asStateFlow()

    init {
        applicationScope.launch {
            datastore.data.collectLatest { prefs ->
                val selectedThemeValue = prefs[SelectedThemeKey]?.let { Theme.valueOf(it) } ?: Theme.SystemDefault
                _selectedTheme.update { selectedThemeValue }
                val isDynamicColorOn = prefs[DynamicColorKey] ?: false
                _dynamicColorEnabled.update { isDynamicColorOn }
            }
        }
    }

    fun setAppTheme(theme: Theme) {
        _selectedTheme.update { theme }
        applicationScope.launch {
            datastore.edit { prefs ->
                prefs[SelectedThemeKey] = theme.name
            }
        }
    }

    fun enableDynamicColor() {
        _dynamicColorEnabled.update { true }
        applicationScope.launch {
            datastore.edit { prefs ->
                prefs[DynamicColorKey] = true
            }
        }
    }

    fun disableDynamicColor() {
        _dynamicColorEnabled.update { false }
        applicationScope.launch {
            datastore.edit { prefs ->
                prefs[DynamicColorKey] = false
            }
        }
    }

    companion object {
        private val SelectedThemeKey = stringPreferencesKey("selected_theme")
        private val DynamicColorKey = booleanPreferencesKey("dynamic_color")
    }
}

enum class Theme {
    Light,
    Dark,
    SystemDefault,
}
