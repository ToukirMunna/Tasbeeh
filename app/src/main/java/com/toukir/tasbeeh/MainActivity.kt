package com.toukir.tasbeeh

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toukir.tasbeeh.data.TasbeehRepository
import com.toukir.tasbeeh.ui.MainViewModel
import com.toukir.tasbeeh.ui.TasbeehApp
import com.toukir.tasbeeh.ui.splash.SplashScreen
import com.toukir.tasbeeh.ui.theme.AppTheme
import com.toukir.tasbeeh.ui.theme.TasbeehTheme
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private var activeDarkTheme: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Instant 0ms removal: drops OS starting window with zero exit fade jitter
        val splashScreen = installSplashScreen()
        splashScreen.setOnExitAnimationListener { provider ->
            provider.remove()
        }

        super.onCreate(savedInstanceState)
        
        val repository = TasbeehRepository(applicationContext)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(applicationContext, repository) as T
            }
        })[MainViewModel::class.java]

        // 2. Set locale immediately before rendering to avoid language flash/re-composition
        val initialLanguage = viewModel.settings.value.language
        val appLocales = AppCompatDelegate.getApplicationLocales()
        if (appLocales.isEmpty || appLocales.get(0)?.language != initialLanguage) {
            val localeList = LocaleListCompat.forLanguageTags(initialLanguage)
            AppCompatDelegate.setApplicationLocales(localeList)
        }

        // 3. Synchronous initial system bar contrast configuration
        val isSystemNight = (resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        activeDarkTheme = isSystemNight
        applySystemBarTheme(activeDarkTheme)
        
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                // App went to background
                viewModel.syncToCloud()
            }
        })

        setContent {
            val settings by viewModel.settings.collectAsState()
            val isDark = when (settings.theme) {
                AppTheme.Dark -> true
                AppTheme.Light -> false
            }

            // 4. Dynamic system bar synchronization on theme changes
            DisposableEffect(isDark) {
                activeDarkTheme = isDark
                applySystemBarTheme(isDark)
                onDispose {}
            }

            // 5. 0ms Splash intro with pixel-matched initial frame
            var minSplashElapsed by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(800L)
                minSplashElapsed = true
            }

            TasbeehTheme(
                theme = settings.theme,
                colorTheme = settings.colorTheme
            ) {
                Crossfade(
                    targetState = minSplashElapsed,
                    animationSpec = tween(durationMillis = 300),
                    label = "SplashCrossfade"
                ) { ready ->
                    if (ready) {
                        TasbeehApp(
                            viewModel = viewModel,
                            settings = settings
                        )
                    } else {
                        SplashScreen(isDark = isDark)
                    }
                }
            }
        }
    }

    // 6. Window focus gain re-assertion (prevents status bar whiteout after splash exit)
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            applySystemBarTheme(activeDarkTheme)
        }
    }

    private fun applySystemBarTheme(darkTheme: Boolean) {
        enableEdgeToEdge(
            statusBarStyle = if (darkTheme) {
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
            },
            navigationBarStyle = if (darkTheme) {
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
            }
        )
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = !darkTheme
        insetsController.isAppearanceLightNavigationBars = !darkTheme
    }
}
