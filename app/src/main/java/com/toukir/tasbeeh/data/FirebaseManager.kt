package com.toukir.tasbeeh.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.cloud.FirebaseLeaderboardManager
import kotlinx.coroutines.tasks.await

class FirebaseManager(private val repository: TasbeehRepository) {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val gson = Gson()
    private val ROOT_COLLECTION = "tasbeeh_app_data"

    val currentUser get() = auth.currentUser
    val isLoggedIn get() = auth.currentUser != null

    private val leaderboardManager = FirebaseLeaderboardManager(auth, db, repository)

    suspend fun syncToCloud(): Boolean {
        val user = auth.currentUser ?: return false
        return try {
            val data = repository.getFullBackupData()

            val hasHistory = data.history.isNotEmpty()
            val hasProgress = data.goals.any { it.totalCount > 0 }
            if (!hasHistory && !hasProgress) {
                Log.d("FirebaseManager", "Skipping sync: Local app data is empty.")
                return false
            }

            val userRef = db.collection(ROOT_COLLECTION).document(user.uid)
            val rootData = mutableMapOf<String, Any>(
                "goalsJson" to gson.toJson(data.goals),
                "userProfileJson" to gson.toJson(data.userProfile),
                "settingsJson" to gson.toJson(data.settings),
                "earnedAchievementsJson" to gson.toJson(data.earnedAchievements),
                "customDetailsJson" to gson.toJson(data.customDetails),
                "lastResetDate" to (data.lastResetDate ?: ""),
                "leaderboardUsername" to (data.leaderboardUsername ?: ""),
                "isAnonymous" to (data.isAnonymous ?: false),
                "isLeaderboardEnabled" to (data.isLeaderboardEnabled ?: false),
                "version" to data.version,
                "lastUpdated" to System.currentTimeMillis()
            )

            userRef.set(rootData, SetOptions.merge()).await()

            if (data.isLeaderboardEnabled == true) {
                syncToLeaderboard()
            } else {
                removeFromLeaderboard()
            }

            val historyCollection = userRef.collection("history")
            data.history.chunked(500).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { entry ->
                    val entryMap = mutableMapOf<String, Any>()
                    entryMap["data"] = gson.toJson(entry)
                    entryMap["date"] = entry.date
                    entryMap["totalCount"] = entry.totalCount
                    batch.set(historyCollection.document(entry.date), entryMap)
                }
                batch.commit().await()
            }
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error in sync", e)
            false
        }
    }

    suspend fun restoreFromCloud(): Boolean {
        val user = auth.currentUser ?: return false
        return try {
            val userRef = db.collection(ROOT_COLLECTION).document(user.uid)
            val rootSnapshot = userRef.get().await()

            if (!rootSnapshot.exists()) return false

            val goalsJson = rootSnapshot.getString("goalsJson") ?: return false
            val goalsType = object : TypeToken<List<TasbeehGoal>>() {}.type
            val goals: List<TasbeehGoal> = gson.fromJson(goalsJson, goalsType)

            val profileJson = rootSnapshot.getString("userProfileJson")
            val userProfile = if (profileJson != null) gson.fromJson(profileJson, UserProfileBackup::class.java) else null

            val settingsJson = rootSnapshot.getString("settingsJson")
            val settings = if (settingsJson != null) gson.fromJson(settingsJson, SettingsBackup::class.java) else null

            val achievementsJson = rootSnapshot.getString("earnedAchievementsJson")
            val earnedAchievements = if (achievementsJson != null) {
                val type = object : TypeToken<Map<String, Int>>() {}.type
                gson.fromJson<Map<String, Int>>(achievementsJson, type)
            } else null

            val customDetailsJson = rootSnapshot.getString("customDetailsJson")
            val customDetails = if (customDetailsJson != null) {
                val type = object : TypeToken<Map<String, AdhkarInfo>>() {}.type
                gson.fromJson<Map<String, AdhkarInfo>>(customDetailsJson, type)
            } else null

            val lastResetDate = rootSnapshot.getString("lastResetDate")
            val leaderboardUsername = rootSnapshot.getString("leaderboardUsername")
            val isAnonymous = rootSnapshot.getBoolean("isAnonymous")
            val isLeaderboardEnabled = rootSnapshot.getBoolean("isLeaderboardEnabled")

            val historySnapshot = userRef.collection("history").get().await()
            val historyList = historySnapshot.documents.mapNotNull { doc ->
                val entryJson = doc.getString("data")
                if (entryJson != null) {
                    gson.fromJson(entryJson, TasbeehHistory::class.java)
                } else {
                    val map = doc.data
                    if (map != null) gson.fromJson(gson.toJson(map), TasbeehHistory::class.java) else null
                }
            }.sortedBy { it.date }

            val backupData = BackupData(
                goals = goals,
                history = historyList,
                earnedAchievements = earnedAchievements,
                lastResetDate = lastResetDate,
                userProfile = userProfile,
                customDetails = customDetails,
                settings = settings,
                leaderboardUsername = leaderboardUsername,
                isAnonymous = isAnonymous,
                isLeaderboardEnabled = isLeaderboardEnabled,
                version = (rootSnapshot.getLong("version") ?: 3).toInt()
            )

            repository.applyFullBackupData(backupData)
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error in restore", e)
            false
        }
    }

    fun signOut() = auth.signOut()

    suspend fun syncToLeaderboard(): Boolean = leaderboardManager.syncToLeaderboard()

    suspend fun removeFromLeaderboard(): Boolean = leaderboardManager.removeFromLeaderboard()

    suspend fun getLeaderboard(period: String): List<LeaderboardEntry> = leaderboardManager.getLeaderboard(period)
}
