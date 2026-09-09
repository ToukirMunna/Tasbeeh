package com.toukir.tasbeeh.data.cloud

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.toukir.tasbeeh.data.LeaderboardEntry
import com.toukir.tasbeeh.data.TasbeehRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class FirebaseLeaderboardManager(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val repository: TasbeehRepository
) {
    private val LEADERBOARD_COLLECTION = "leaderboards"

    suspend fun syncToLeaderboard(): Boolean {
        val user = auth.currentUser ?: run {
            Log.e("FirebaseLeaderboard", "Leaderboard sync failed: User not logged in")
            return false
        }

        val isEnabled = repository.isLeaderboardEnabledFlow.first()
        if (!isEnabled) {
            Log.d("FirebaseLeaderboard", "Skipping leaderboard sync: Feature is disabled")
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
            val dailyCount = repository.goalsDataStore.distinctDailyTotal(goals)

            val daysSinceMonday = (today.dayOfWeek.value - 1).toLong()
            val weeklyStart = today.minusDays(daysSinceMonday)
            val monthlyStart = today.withDayOfMonth(1)

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

            Log.d("FirebaseLeaderboard", "SUCCESS: Leaderboard entry updated: $entry")
            true
        } catch (e: Exception) {
            Log.e("FirebaseLeaderboard", "Error in leaderboard sync", e)
            false
        }
    }

    suspend fun removeFromLeaderboard(): Boolean {
        val user = auth.currentUser ?: return false
        return try {
            db.collection(LEADERBOARD_COLLECTION).document(user.uid).delete().await()
            Log.d("FirebaseLeaderboard", "User removed from leaderboard")
            true
        } catch (e: Exception) {
            Log.e("FirebaseLeaderboard", "Error removing user from leaderboard", e)
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

            val documents = try {
                db.collection(LEADERBOARD_COLLECTION)
                    .whereEqualTo(periodField, periodValue)
                    .orderBy(field, Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .await()
                    .documents
            } catch (e: Exception) {
                db.collection(LEADERBOARD_COLLECTION)
                    .whereEqualTo(periodField, periodValue)
                    .limit(50)
                    .get()
                    .await()
                    .documents
                    .sortedByDescending { (it.data?.get(field) as? Long) ?: 0L }
            }

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
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
