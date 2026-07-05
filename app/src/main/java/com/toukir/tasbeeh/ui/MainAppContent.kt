@file:Suppress("DEPRECATION")
package com.toukir.tasbeeh.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarInfo
import com.toukir.tasbeeh.data.ToastReminderService
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun TasbeehApp(
    viewModel: MainViewModel,
    settings: AppSettings,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var isLoggedIn by remember { mutableStateOf(viewModel.firebaseManager.isLoggedIn) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            scope.launch {
                Firebase.auth.signInWithCredential(credential).await()
                isLoggedIn = true
                Toast.makeText(context, "Logged in successfully. Restoring data...", Toast.LENGTH_SHORT).show()
                viewModel.restoreFromCloud { success ->
                    if (success) {
                        Toast.makeText(context, "Data restored from cloud", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val signInIntent = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso).signInIntent
    }

    // State from ViewModel
    val savedGoals by viewModel.savedGoals.collectAsState()
    val combinedHistory by viewModel.combinedHistory.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userIsMale by viewModel.userIsMale.collectAsState()
    val customDetails by viewModel.customDetails.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()

    // Toast Reminder State
    val isToastReminderEnabled by viewModel.isToastReminderEnabled.collectAsState()
    val toastReminderText by viewModel.toastReminderText.collectAsState()
    val toastReminderInterval by viewModel.toastReminderInterval.collectAsState()
    
    val leaderboardData by viewModel.leaderboardData.collectAsState()
    val isLeaderboardLoading by viewModel.isLeaderboardLoading.collectAsState()
    val leaderboardError by viewModel.leaderboardError.collectAsState()
    val isLeaderboardEnabled by viewModel.isLeaderboardEnabled.collectAsState()
    val leaderboardUsername by viewModel.leaderboardUsername.collectAsState()
    val isAnonymous by viewModel.isAnonymous.collectAsState()

    val isRestoring by viewModel.isRestoring.collectAsState()

    var currentScreen by remember { mutableStateOf(value = "home") }
    var historyInitialTab by remember { mutableIntStateOf(value = 0) }
    var selectedGoalId by remember { mutableStateOf<Int?>(value = null) }
    var selectedTasbeehDetailId by remember { mutableStateOf<Int?>(value = null) }
    
    var goalToEdit by remember { mutableStateOf<TasbeehGoal?>(value = null) }
    var tasbeehToAddToGoal by remember { mutableStateOf<TasbeehGoal?>(value = null) }
    var showAddGoalDialog by remember { mutableStateOf(value = false) }
    var managingGoalsType by remember { mutableStateOf<Boolean?>(value = null) } // null: none, false: daily, true: long-term
    var showSettingsDialog by remember { mutableStateOf(value = false) }
    var showNameInputDialog by remember { mutableStateOf(value = false) }
    var showLeaderboardSettingsDialog by remember { mutableStateOf(value = false) }
    
    var adhkarInfoToEdit by remember { mutableStateOf<AdhkarInfo?>(value = null) }

    // Handle Toast Reminder Service
    LaunchedEffect(isToastReminderEnabled, toastReminderText, toastReminderInterval) {
        val intent = Intent(context, ToastReminderService::class.java).apply {
            putExtra("text", toastReminderText)
            putExtra("interval", toastReminderInterval)
        }
        if (isToastReminderEnabled) {
            context.startService(intent)
        } else {
            context.stopService(intent)
        }
    }

    // Check for daily reset on app resume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkAndResetDailyCounts()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler(enabled = (selectedGoalId != null || selectedTasbeehDetailId != null || currentScreen != "home")) {
        when {
            selectedGoalId != null -> {
                selectedGoalId = null
                viewModel.syncToCloud()
            }
            selectedTasbeehDetailId != null -> selectedTasbeehDetailId = null
            currentScreen == "statistics" -> currentScreen = "dashboard"
            currentScreen == "history" -> currentScreen = "dashboard"
            currentScreen == "leaderboard" -> currentScreen = "dashboard"
            else -> currentScreen = "home"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
             if (selectedGoalId == null && selectedTasbeehDetailId == null) {
                 val colorScheme = MaterialTheme.colorScheme
                 NavigationBar(
                     containerColor = colorScheme.surface,
                     tonalElevation = 0.dp
                 ) {
                     val navItemColors = NavigationBarItemDefaults.colors(
                         selectedIconColor = colorScheme.primary,
                         unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                         selectedTextColor = colorScheme.primary,
                         unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                         indicatorColor = colorScheme.primary.copy(alpha = 0.12f)
                     )

                     NavigationBarItem(
                         icon = { 
                            Box(contentAlignment = Alignment.Center) {
                                if (currentScreen == "home") {
                                    Surface(
                                        modifier = Modifier.size(36.dp).shadow(8.dp, CircleShape, spotColor = colorScheme.primary),
                                        shape = CircleShape,
                                        color = colorScheme.primary.copy(alpha = 0.15f),
                                        content = {}
                                    )
                                }
                                Icon(Icons.Default.Home, contentDescription = null)
                            }
                         },
                         label = { Text(stringResource(R.string.nav_home)) },
                         selected = currentScreen == "home",
                         colors = navItemColors,
                         onClick = { 
                            currentScreen = "home"
                            selectedGoalId = null
                            selectedTasbeehDetailId = null
                         }
                     )
                     NavigationBarItem(
                         icon = { 
                            Box(contentAlignment = Alignment.Center) {
                                if (currentScreen == "tasbeehs") {
                                    Surface(
                                        modifier = Modifier.size(36.dp).shadow(8.dp, CircleShape, spotColor = colorScheme.primary),
                                        shape = CircleShape,
                                        color = colorScheme.primary.copy(alpha = 0.15f),
                                        content = {}
                                    )
                                }
                                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                            }
                         },
                         label = { Text(stringResource(R.string.nav_tasbeehs)) },
                         selected = currentScreen == "tasbeehs",
                         colors = navItemColors,
                         onClick = { 
                            currentScreen = "tasbeehs"
                            selectedGoalId = null
                            selectedTasbeehDetailId = null
                         }
                     )
                     NavigationBarItem(
                         icon = { 
                            Box(contentAlignment = Alignment.Center) {
                                if (currentScreen == "dashboard") {
                                    Surface(
                                        modifier = Modifier.size(36.dp).shadow(8.dp, CircleShape, spotColor = colorScheme.primary),
                                        shape = CircleShape,
                                        color = colorScheme.primary.copy(alpha = 0.15f),
                                        content = {}
                                    )
                                }
                                Icon(Icons.Default.Assessment, contentDescription = null)
                            }
                         },
                         label = { Text(stringResource(R.string.nav_profile)) },
                         selected = currentScreen == "dashboard",
                         colors = navItemColors,
                         onClick = { 
                            currentScreen = "dashboard"
                            selectedGoalId = null
                            selectedTasbeehDetailId = null
                         }
                     )
                 }
             }
        },
        floatingActionButton = {
             if (currentScreen == "tasbeehs" && selectedGoalId == null && selectedTasbeehDetailId == null) {
                FloatingActionButton(onClick = { showAddGoalDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Tasbeeh")
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = Triple(currentScreen, selectedGoalId, selectedTasbeehDetailId),
            transitionSpec = {
                val targetLevel = when {
                    targetState.second != null -> 2 // Counter
                    targetState.third != null -> 1  // Details
                    targetState.first == "statistics" || targetState.first == "history" -> 1
                    else -> 0 // Main screens (home, tasbeehs, dashboard)
                }
                val initialLevel = when {
                    initialState.second != null -> 2
                    initialState.third != null -> 1
                    initialState.first == "statistics" || initialState.first == "history" -> 1
                    else -> 0
                }

                if (targetLevel > initialLevel) {
                    // Moving deeper: Slide in from right, slide out to left
                    (slideInHorizontally { it } + fadeIn(tween(300))) togetherWith
                            (slideOutHorizontally { -it } + fadeOut(tween(300)))
                } else if (targetLevel < initialLevel) {
                    // Returning: Slide in from left, slide out to right
                    (slideInHorizontally { -it } + fadeIn(tween(300))) togetherWith
                            (slideOutHorizontally { it } + fadeOut(tween(300)))
                } else {
                    // Same level (usually main screen changes)
                    if (targetState.first != initialState.first) {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    } else {
                        // Should not happen often, but keep it smooth
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    }
                }
            },
            label = "ScreenTransition"
        ) { (screen, goalId, detailId) ->
            val goal = goalId?.let { id -> savedGoals.firstOrNull { it.id == id } }
            val detail = detailId?.let { id -> savedGoals.firstOrNull { it.id == id } }
            
            // Apply ONLY bottom padding from innerPadding to handle the NavigationBar
            // Top padding (status bar) should be handled by individual screens to avoid "blank space" gaps
            Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                when {
                    goal != null -> {
                        CounterScreen(
                            goal = goal,
                            onBack = { 
                                selectedGoalId = null
                                viewModel.syncToCloud()
                            },
                            onIncrement = {
                                viewModel.incrementGoal(goal)
                            },
                            onDetailsClick = {
                                selectedTasbeehDetailId = goal.id
                                selectedGoalId = null
                            },
                            isSoundEnabled = settings.isSoundEnabled,
                            isVibrateTapEnabled = settings.isVibrateTapEnabled,
                            isVibrate100Enabled = settings.isVibrate100Enabled,
                            language = settings.language
                        )
                    }
                    screen == "statistics" -> {
                        StatisticsScreen(
                            history = combinedHistory,
                            onBack = { currentScreen = "dashboard" },
                            language = settings.language
                        )
                    }
                    screen == "history" -> {
                        HistoryScreen(
                            history = combinedHistory,
                            initialTab = historyInitialTab,
                            onBack = { currentScreen = "dashboard" },
                            language = settings.language
                        )
                    }
                    screen == "leaderboard" -> {
                        LaunchedEffect(Unit) {
                            if (isLeaderboardEnabled) {
                                viewModel.fetchLeaderboard("daily")
                            }
                        }
                        LeaderboardScreen(
                            entries = leaderboardData,
                            isLoading = isLeaderboardLoading,
                            isEnabled = isLeaderboardEnabled,
                            error = leaderboardError,
                            onBack = { currentScreen = "dashboard" },
                            onRefresh = { period -> viewModel.fetchLeaderboard(period) },
                            onToggle = { enabled -> viewModel.toggleLeaderboard(enabled) },
                            onUsernameEdit = { showLeaderboardSettingsDialog = true },
                            currentUserId = viewModel.firebaseManager.currentUser?.uid ?: "",
                            language = settings.language
                        )
                    }
                    detail != null -> {
                        TasbeehDetailsScreen(
                            goal = detail,
                            history = combinedHistory,
                            customDetails = customDetails,
                            onBack = { 
                                selectedTasbeehDetailId = null
                            },
                            onCountClick = {
                                selectedGoalId = detail.id
                            },
                            onEditDetails = { info ->
                                adhkarInfoToEdit = info
                            }
                        )
                    }
                    screen == "home" -> {
                        HomeScreen(
                            viewModel = viewModel,
                            userName = userName,
                            streak = currentStreak,
                            onGoalClick = { selectedGoalId = it.id },
                            onManageGoals = { isCustom -> managingGoalsType = isCustom },
                            language = settings.language
                        )
                    }
                    screen == "tasbeehs" -> {
                        TasbeehsListScreen(
                            goals = savedGoals,
                            onGoalClick = { selectedTasbeehDetailId = it.id },
                            onEditGoal = { goalToEdit = it },
                            onAddToGoal = { tasbeehToAddToGoal = it }
                        )
                    }
                    screen == "dashboard" -> {
                        ProfileScreen(
                            history = combinedHistory,
                            goals = savedGoals,
                            onSettingsClick = { showSettingsDialog = true },
                            onStatisticsClick = { currentScreen = "statistics" },
                            onHistoryClick = { 
                                historyInitialTab = 1
                                currentScreen = "history" 
                            },
                            onLeaderboardClick = { currentScreen = "leaderboard" },
                            currentStreak = currentStreak,
                            userName = userName,
                            isMale = userIsMale,
                            onUserNameEdit = { showNameInputDialog = true },
                            language = settings.language
                        )
                        
                        LaunchedEffect(userName) {
                            if (userName.isEmpty()) {
                                showNameInputDialog = true
                            }
                        }
                    }
                }
            }
        }
        
        if (adhkarInfoToEdit != null) {
            EditTasbeehDetailsDialog(
                initialInfo = adhkarInfoToEdit!!,
                onDismiss = { adhkarInfoToEdit = null },
                onSave = { updatedInfo ->
                    viewModel.saveAdhkarInfo(updatedInfo)
                    adhkarInfoToEdit = null
                }
            )
        }
        
        if (showNameInputDialog) {
            NameInputDialog(
                currentName = userName,
                currentIsMale = userIsMale,
                onConfirm = { newName, newIsMale ->
                    viewModel.saveUserProfile(newName, newIsMale)
                    showNameInputDialog = false
                },
                onDismiss = { showNameInputDialog = false }
            )
        }

        if (showLeaderboardSettingsDialog) {
            LeaderboardSettingsDialog(
                currentUsername = leaderboardUsername,
                currentIsAnonymous = isAnonymous,
                onConfirm = { name, anon ->
                    viewModel.saveLeaderboardSettings(name, anon)
                    showLeaderboardSettingsDialog = false
                },
                onDismiss = { showLeaderboardSettingsDialog = false }
            )
        }
        
        if (showSettingsDialog) {
            SettingsDialog(
                currentTheme = settings.theme,
                onThemeChange = { theme -> viewModel.updateSetting { viewModel.saveTheme(theme) } },
                isSoundEnabled = settings.isSoundEnabled,
                onSoundEnabledChange = { enabled -> viewModel.updateSetting { viewModel.saveSoundEnabled(enabled) } },
                isVibrateTapEnabled = settings.isVibrateTapEnabled,
                onVibrateTapChange = { enabled -> viewModel.updateSetting { viewModel.saveVibrateTapEnabled(enabled) } },
                isVibrate100Enabled = settings.isVibrate100Enabled,
                onVibrate100Change = { enabled -> viewModel.updateSetting { viewModel.saveVibrate100Enabled(enabled) } },
                currentLanguage = settings.language,
                onLanguageChange = { lang -> viewModel.updateSetting { viewModel.saveLanguage(lang) } },
                showCounterCircle = settings.showCounterCircle,
                onShowCounterCircleChange = { enabled -> viewModel.updateSetting { viewModel.saveShowCounterCircleEnabled(enabled) } },
                isToastReminderEnabled = isToastReminderEnabled,
                onToastReminderEnabledChange = { enabled ->
                    viewModel.saveToastReminderSettings(enabled, toastReminderText, toastReminderInterval)
                },
                toastReminderText = toastReminderText,
                onToastReminderTextChange = { text ->
                    viewModel.saveToastReminderSettings(isToastReminderEnabled, text, toastReminderInterval)
                },
                toastReminderInterval = toastReminderInterval,
                onToastReminderIntervalChange = { interval ->
                    viewModel.saveToastReminderSettings(isToastReminderEnabled, toastReminderText, interval)
                },
                onBackup = { uri ->
                    viewModel.backupData(uri) { success ->
                        Toast.makeText(context, if (success) "Backup saved successfully" else "Backup failed", Toast.LENGTH_SHORT).show()
                    }
                },
                onRestore = { uri ->
                    viewModel.restoreData(uri) { success ->
                        Toast.makeText(context, if (success) "Data restored successfully" else "Restore failed", Toast.LENGTH_SHORT).show()
                    }
                },
                onDismiss = { showSettingsDialog = false },
                context = context,
                isLoggedIn = isLoggedIn,
                isRestoring = isRestoring,
                userEmail = viewModel.firebaseManager.currentUser?.email,
                userDisplayName = viewModel.firebaseManager.currentUser?.displayName,
                userPhotoUrl = viewModel.firebaseManager.currentUser?.photoUrl?.toString(),
                onLoginClick = { googleSignInLauncher.launch(signInIntent) },
                onLogoutClick = {
                    viewModel.firebaseManager.signOut()
                    isLoggedIn = false
                },
                isLeaderboardEnabled = isLeaderboardEnabled,
                onLeaderboardEnabledChange = { enabled -> viewModel.toggleLeaderboard(enabled) }
            )
        }
        
        if (showAddGoalDialog) {
            AddGoalDialog(
                onDismiss = { showAddGoalDialog = false },
                onAddNew = { name ->
                    val currentList = savedGoals.toMutableList()
                    val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
                    currentList.add(TasbeehGoal(newId, name, 100, 0, 0, isGoal = false, duration = GoalDuration.DAILY))
                    viewModel.saveGoals(currentList)
                    showAddGoalDialog = false
                }
            )
        }

        if (tasbeehToAddToGoal != null) {
            AddToGoalDialog(
                tasbeehName = tasbeehToAddToGoal!!.name,
                onDismiss = { tasbeehToAddToGoal = null },
                onConfirm = { duration, target ->
                    val currentList = savedGoals.toMutableList()
                    val existingIndex = currentList.indexOfFirst { it.name == tasbeehToAddToGoal!!.name && it.duration == duration && it.isGoal }
                    
                    if (existingIndex != -1) {
                        // Already exists with this duration, just update target
                        currentList[existingIndex] = currentList[existingIndex].copy(targetCount = target)
                    } else {
                        // Add new goal entry for this duration
                        val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
                        currentList.add(
                            TasbeehGoal(
                                id = newId,
                                name = tasbeehToAddToGoal!!.name,
                                targetCount = target,
                                currentCount = tasbeehToAddToGoal!!.currentCount,
                                dailyCount = tasbeehToAddToGoal!!.dailyCount,
                                totalCount = tasbeehToAddToGoal!!.totalCount,
                                isGoal = true,
                                duration = duration,
                                lastResetDate = System.currentTimeMillis()
                            )
                        )
                    }
                    viewModel.saveGoals(currentList)
                    tasbeehToAddToGoal = null
                }
            )
        }
        
        if (goalToEdit != null) {
            EditGoalDialog(
                goal = goalToEdit!!,
                isHomeSource = currentScreen == "home",
                onDismiss = { goalToEdit = null },
                onDelete = {
                    val currentList = savedGoals.toMutableList()
                    val index = currentList.indexOfFirst { it.id == goalToEdit!!.id }
                    if (index != -1) {
                        if (currentScreen == "home") {
                            currentList[index] = currentList[index].copy(isGoal = false)
                        } else {
                            currentList.removeAt(index)
                        }
                        viewModel.saveGoals(currentList)
                    }
                    goalToEdit = null
                },
                onRename = { newName ->
                    val currentList = savedGoals.toMutableList()
                    val index = currentList.indexOfFirst { it.id == goalToEdit!!.id }
                    if (index != -1) {
                        currentList[index] = currentList[index].copy(name = newName)
                        viewModel.saveGoals(currentList)
                    }
                    goalToEdit = null
                }
            )
        }
        
        if (managingGoalsType != null && currentScreen == "home") {
            val isCustom = managingGoalsType!!
            val filteredGoals = if (isCustom) {
                savedGoals.filter { it.isGoal && it.duration != GoalDuration.DAILY }
            } else {
                savedGoals.filter { it.isGoal && it.duration == GoalDuration.DAILY }
            }
            
            ManageGoalsDialog(
                goals = filteredGoals,
                onDismiss = { managingGoalsType = null },
                onUpdateGoals = { updatedFilteredGoals ->
                    // Get IDs of goals that were in the filtered list but are not in the updated list
                    val originalFilteredIds = filteredGoals.map { it.id }.toSet()
                    val newFilteredIds = updatedFilteredGoals.map { it.id }.toSet()
                    val removedIds = originalFilteredIds - newFilteredIds
                    
                    val finalGoals = ArrayList<TasbeehGoal>()
                    // Add the updated goals (reordered/edited)
                    finalGoals.addAll(updatedFilteredGoals)
                    
                    // Handle the rest of the goals
                    savedGoals.forEach { goal ->
                        if (goal.id in removedIds) {
                            // Deactivate goals that were removed from the manage list
                            finalGoals.add(goal.copy(isGoal = false))
                        } else if (goal.id !in originalFilteredIds) {
                            // Keep all other goals (those that weren't being managed right now)
                            finalGoals.add(goal)
                        }
                    }
                    
                    viewModel.saveGoals(finalGoals)
                    managingGoalsType = null
                }
            )
        }
    }
}
