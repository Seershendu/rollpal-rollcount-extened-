package fm.mrc.rollpal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import fm.mrc.rollpal.theme.ThemeManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val themeManager = ThemeManager(application)
    val isDarkMode: Flow<Boolean> = themeManager.isDarkMode

    fun toggleTheme() {
        viewModelScope.launch {
            themeManager.toggleTheme()
        }
    }
} 