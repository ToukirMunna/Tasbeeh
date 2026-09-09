package com.toukir.tasbeeh.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.ToastReminderService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun TasbeehApp(
    viewModel: MainViewModel,
    settings: AppSettings
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val appState = rememberTasbeehAppState(viewModel)

    val googleSignInLauncher = rememberGoogleAuthLauncher(context, scope, viewModel) {
        appState.isLoggedIn = true
    }
    val signInIntent = rememberGoogleSignInIntent(context)

    val savedGoals by viewModel.savedGoals.collectAsStateWithLifecycle()
    val combinedHistory by viewModel.combinedHistory.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userIsMale by viewModel.userIsMale.collectAsStateWithLifecycle()
    val customDetails by viewModel.customDetails.collectAsStateWithLifecycle()
    val currentStreak by viewModel.currentStreak.collectAsStateWithLifecycle()

    val isToastReminderEnabled by viewModel.isToastReminderEnabled.collectAsStateWithLifecycle()
    val toastReminderText by viewModel.toastReminderText.collectAsStateWithLifecycle()
    val toastReminderInterval by viewModel.toastReminderInterval.collectAsStateWithLifecycle()

    AppLifecycleAndServiceEffects(
        context = context,
        lifecycleOwner = lifecycleOwner,
        viewModel = viewModel,
        isToastReminderEnabled = isToastReminderEnabled,
        toastReminderText = toastReminderText,
        toastReminderInterval = toastReminderInterval
    )

    BackHandler(enabled = appState.isBackEnabled) {
        appState.handleBack()
    }

    TasbeehScaffoldLayout(
        appState = appState,
        viewModel = viewModel,
        settings = settings,
        context = context,
        savedGoals = savedGoals,
        combinedHistory = combinedHistory,
        customDetails = customDetails,
        userName = userName,
        userIsMale = userIsMale,
        currentStreak = currentStreak,
        onLoginClick = { googleSignInLauncher.launch(signInIntent) }
    )
}

@Composable
private fun AppLifecycleAndServiceEffects(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    viewModel: MainViewModel,
    isToastReminderEnabled: Boolean,
    toastReminderText: String,
    toastReminderInterval: Int
) {
    LaunchedEffect(isToastReminderEnabled, toastReminderText, toastReminderInterval) {
        val intent = Intent(context, ToastReminderService::class.java).apply {
            putExtra("text", toastReminderText)
            putExtra("interval", toastReminderInterval)
        }
        if (isToastReminderEnabled) context.startService(intent) else context.stopService(intent)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.checkAndResetDailyCounts()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
private fun rememberGoogleAuthLauncher(
    context: Context,
    scope: CoroutineScope,
    viewModel: MainViewModel,
    onSuccess: () -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    try {
        val account = task.getResult(ApiException::class.java)!!
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        scope.launch {
            Firebase.auth.signInWithCredential(credential).await()
            onSuccess()
            Toast.makeText(context, context.getString(R.string.toast_login_restore), Toast.LENGTH_SHORT).show()
            viewModel.restoreFromCloud { success ->
                if (success) {
                    Toast.makeText(context, context.getString(R.string.toast_data_restored), Toast.LENGTH_SHORT).show()
                }
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.toast_login_failed, e.message ?: ""), Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun rememberGoogleSignInIntent(context: Context): Intent = remember {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    GoogleSignIn.getClient(context, gso).signInIntent
}
