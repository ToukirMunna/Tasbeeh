package com.toukir.tasbeeh

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toukir.tasbeeh.data.TasbeehRepository
import com.toukir.tasbeeh.ui.MainViewModel
import com.toukir.tasbeeh.ui.TasbeehApp
import com.toukir.tasbeeh.ui.theme.KalpurushFontFamily
import com.toukir.tasbeeh.ui.theme.TasbeehTheme

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = TasbeehRepository(applicationContext)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(applicationContext, repository) as T
            }
        })[MainViewModel::class.java]

        // 1. Set locale immediately before rendering to avoid language flash/re-composition
        val initialLanguage = viewModel.settings.value.language
        val appLocales = AppCompatDelegate.getApplicationLocales()
        if (appLocales.isEmpty || appLocales.get(0)?.language != initialLanguage) {
            val localeList = LocaleListCompat.forLanguageTags(initialLanguage)
            AppCompatDelegate.setApplicationLocales(localeList)
        }

        enableEdgeToEdge()
        
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                // App went to background
                viewModel.syncToCloud()
            }
        })

        setContent {
            val settings by viewModel.settings.collectAsState()
            
            TasbeehTheme(theme = settings.theme) {
                TasbeehApp(
                    viewModel = viewModel,
                    settings = settings
                )
            }
        }
    }
}
