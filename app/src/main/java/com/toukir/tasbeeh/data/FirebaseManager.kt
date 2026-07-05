package com.toukir.tasbeeh.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toukir.tasbeeh.TasbeehGoal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class FirebaseManager(private val repository: TasbeehRepository) {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val gson = Gson()
    
    private val ROOT_COLLECTION = "tasbeeh_app_data"
    private val LEADERBOARD_COLLECTION = "leaderboards"
    
    val currentUser get() = auth.currentUser
    val isLoggedIn get() = auth.currentUser != null

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
                "version" to data.version,
                "lastUpdated" to System.currentTimeMillis()
            )
            
            userRef.set(rootData, SetOptions.merge()).await()

            // Update Leaderboard collection separately for easy fetching if enabled
            if (data.isLeaderboardEnabled == true) {
                syncToLeaderboard()
            } else {
                removeFromLeaderboard()
            }

            val historyCollection = userRef.collection("history")
            // Sync history in chunks of 500 (Firestore batch limit) for much faster performance
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

            Log.d("FirebaseManager", "Sync to $ROOT_COLLECTION successful")
            
            // Sync to Leaderboard after successful main sync
            // syncToLeaderboard() // Already called above
            
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
            
            if (!rootSnapshot.exists()) {
                Log.d("FirebaseManager", "No cloud data found")
                return false
            }

            // 1. Restore Goals
            val goalsJson = rootSnapshot.getString("goalsJson") ?: return false
            val goalsType = object : TypeToken<List<TasbeehGoal>>() {}.type
            val goals: List<TasbeehGoal> = gson.fromJson(goalsJson, goalsType)
            
            // 2. Restore Profile
            val profileJson = rootSnapshot.getString("userProfileJson")
            val userProfile = if (profileJson != null) gson.fromJson(profileJson, UserProfileBackup::class.java) else null
            
            // 3. Restore Settings
            val settingsJson = rootSnapshot.getString("settingsJson")
            val settings = if (settingsJson != null) gson.fromJson(settingsJson, SettingsBackup::class.java) else null
            
            // 4. Restore Achievements
            val achievementsJson = rootSnapshot.getString("earnedAchievementsJson")
            val earnedAchievements = if (achievementsJson != null) {
                val type = object : TypeToken<Map<String, Int>>() {}.type
                gson.fromJson<Map<String, Int>>(achievementsJson, type)
            } else null
            
            // 5. Restore Custom Adhkar
            val customDetailsJson = rootSnapshot.getString("customDetailsJson")
            val customDetails = if (customDetailsJson != null) {
                val type = object : TypeToken<Map<String, AdhkarInfo>>() {}.type
                gson.fromJson<Map<String, AdhkarInfo>>(customDetailsJson, type)
            } else null

            // 6. Restore Metadata
            val lastResetDate = rootSnapshot.getString("lastResetDate")

            // 7. Fetch ALL history documents
            val historySnapshot = userRef.collection("history").get().await()
            val historyList = historySnapshot.documents.mapNotNull { doc ->
                // Try JSON-string format first (most reliable)
                val entryJson = doc.getString("data")
                if (entryJson != null) {
                    gson.fromJson(entryJson, TasbeehHistory::class.java)
                } else {
                    // Fallback to raw map format (if data was synced by older app version)
                    val map = doc.data
                    if (map != null) {
                        gson.fromJson(gson.toJson(map), TasbeehHistory::class.java)
                    } else null
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
                version = (rootSnapshot.getLong("version") ?: 3).toInt()
            )

            repository.applyFullBackupData(backupData)
            Log.d("FirebaseManager", "Restore from cloud successful")
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error in restore", e)
            false
        }
    }
    
    fun signOut() {
        auth.signOut()
    }

    suspend fun syncToLeaderboard(): Boolean {
        val user = auth.currentUser ?: run {
            Log.e("FirebaseManager", "Leaderboard sync failed: User not logged in")
            return false
        }
        
        // Final check: Only sync if explicitly enabled in local settings
        val isEnabled = repository.isLeaderboardEnabledFlow.first()
        if (!isEnabled) {
            Log.d("FirebaseManager", "Skipping leaderboard sync: Feature is disabled")
            removeFromLeaderboard()
            return false
        }

        return try {
            val goals = repository.goalsFlow.first()
            val username = repository.leaderboardUsernameFlow.first().let { 
                if (it.isEmpty()) repository.userNameFlow.first() else it 
            }
            val isAnonymous = repository.isAnonymousFlow.first()
            val isMale = repository.userIsMaleFlow.first()

            val today = LocalDate.now()
            val dailyCount = goals.sumOf { it.dailyCount }

            Log.d("FirebaseManager", "Starting Leaderboard sync for ${user.uid}. Daily count: $dailyCount")

            // Calculate Weekly (Starting Monday of current week)
            val daysSinceMonday = (today.dayOfWeek.value - 1).toLong()
            val weeklyStart = today.minusDays(daysSinceMonday)
            
            // Calculate Monthly (Starting 1st of current month)
            val monthlyStart = today.withDayOfMonth(1)

            Log.d("FirebaseManager", "Aggregation range: WeeklyStart=$weeklyStart, MonthlyStart=$monthlyStart")

            val weeklyCountFromHistory = repository.calculateTotalHistoryForPeriod(weeklyStart, today)
            val weeklyCount = weeklyCountFromHistory + dailyCount

            val monthlyCountFromHistory = repository.calculateTotalHistoryForPeriod(monthlyStart, today)
            val monthlyCount = monthlyCountFromHistory + dailyCount

            val entry = mapOf(
                "userId" to user.uid,
                "username" to if (isAnonymous) "Anonymous" else username.ifEmpty { "User" },
                "dailyCount" to dailyCount,
                "weeklyCount" to weeklyCount,
                "monthlyCount" to monthlyCount,
                "dailyPeriod" to today.toString(),
                "weeklyPeriod" to weeklyStart.toString(),
                "monthlyPeriod" to monthlyStart.toString(),
                "lastUpdated" to System.currentTimeMillis(),
                "isAnonymous" to isAnonymous,
                "isMale" to isMale
            )

            db.collection(LEADERBOARD_COLLECTION).document(user.uid)
                .set(entry, SetOptions.merge())
                .await()

            Log.d("FirebaseManager", "SUCCESS: Leaderboard entry updated in Firestore: $entry")
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "CRITICAL ERROR in leaderboard sync", e)
            false
        }
    }

    suspend fun removeFromLeaderboard(): Boolean {
        val user = auth.currentUser ?: return false
        return try {
            db.collection(LEADERBOARD_COLLECTION).document(user.uid).delete().await()
            Log.d("FirebaseManager", "User removed from leaderboard")
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error removing user from leaderboard", e)
            false
        }
    }

    suspend fun getLeaderboard(period: String): List<LeaderboardEntry> {
        return try {
            val today = LocalDate.now()
            val (field, periodField, periodValue) = when (period.lowercase()) {
                "daily" -> Triple("dailyCount", "dailyPeriod", today.toString())
                "weekly" -> {
                    val daysSinceMonday = (today.dayOfWeek.value - 1).toLong()
                    val weeklyStart = today.minusDays(daysSinceMonday)
                    Triple("weeklyCount", "weeklyPeriod", weeklyStart.toString())
                }
                "monthly" -> {
                    val monthlyStart = today.withDayOfMonth(1)
                    Triple("monthlyCount", "monthlyPeriod", monthlyStart.toString())
                }
                else -> Triple("dailyCount", "dailyPeriod", today.toString())
            }

            Log.d("FirebaseManager", "Fetching leaderboard for $field where $periodField == $periodValue")
            
            // Limit results to top 50 users for each category
            val documents = try {
                db.collection(LEADERBOARD_COLLECTION)
                    .whereEqualTo(periodField, periodValue)
                    .orderBy(field, Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .await()
                    .documents
            } catch (e: Exception) {
                Log.w("FirebaseManager", "Ordered fetch failed (likely missing index): ${e.message}")
                // Fallback: fetch without order and sort in memory (works for small sets)
                db.collection(LEADERBOARD_COLLECTION)
                    .whereEqualTo(periodField, periodValue)
                    .limit(50)
                    .get()
                    .await()
                    .documents
                    .sortedByDescending { (it.data?.get(field) as? Long) ?: 0L }
            }
            
            Log.d("FirebaseManager", "Fetched ${documents.size} entries for $period")

            documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    LeaderboardEntry(
                        userId = data["userId"] as? String ?: "",
                        username = data["username"] as? String ?: "User",
                        dailyCount = (data["dailyCount"] as? Long)?.toInt() ?: 0,
                        weeklyCount = (data["weeklyCount"] as? Long)?.toInt() ?: 0,
                        monthlyCount = (data["monthlyCount"] as? Long)?.toInt() ?: 0,
                        lastUpdated = data["lastUpdated"] as? Long ?: 0L,
                        isAnonymous = data["isAnonymous"] as? Boolean ?: false,
                        isMale = data["isMale"] as? Boolean ?: true
                    )
                } catch (e: Exception) {
                    Log.e("FirebaseManager", "Error parsing document ${doc.id}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error fetching leaderboard", e)
            emptyList()
        }
    }
}
