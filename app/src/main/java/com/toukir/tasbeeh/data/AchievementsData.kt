package com.toukir.tasbeeh.data

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val type: AchievementType,
    val threshold: Int
)

enum class AchievementType {
    STREAK,
    TOTAL_COUNT,
    DAILY_COUNT
}

object AchievementsLibrary {
    val list = listOf(
        Achievement("bismillah", "Bismillah", "Complete your first Tasbeeh", "bismillah", AchievementType.TOTAL_COUNT, 1),
        Achievement("starter_100", "The Starter", "Reach 100 Total Count", "starter", AchievementType.TOTAL_COUNT, 100),
        Achievement("daily_33", "Sunnah Step", "33 counts in one day", "daily_33", AchievementType.DAILY_COUNT, 33),
        Achievement("daily_100", "Daily Devotion", "100 counts in one day", "daily_100", AchievementType.DAILY_COUNT, 100),
        Achievement("heating_up", "Heating Up", "3 Day Streak", "streak_3", AchievementType.STREAK, 3),
        Achievement("on_fire", "On Fire", "7 Day Streak", "streak_7", AchievementType.STREAK, 7),
        Achievement("streak_14", "Two Weeks", "14 Day Streak", "streak_14", AchievementType.STREAK, 14),
        Achievement("habitual", "Habitual", "30 Day Streak", "streak_30", AchievementType.STREAK, 30),
        Achievement("streak_40", "Spiritual 40", "40 Day Streak", "streak_40", AchievementType.STREAK, 40),
        Achievement("bronze_counter", "Bronze Counter", "Reach 1,000 Total Count", "bronze", AchievementType.TOTAL_COUNT, 1000),
        Achievement("dedicated_day", "Dedicated", "1,000 counts in one day", "dedicated", AchievementType.DAILY_COUNT, 1000),
        Achievement("silver_counter", "Silver Counter", "Reach 10,000 Total Count", "silver", AchievementType.TOTAL_COUNT, 10000),
        Achievement("gold_counter", "Gold Counter", "Reach 100,000 Total Count", "gold", AchievementType.TOTAL_COUNT, 100000),
        Achievement("platinum_counter", "Platinum Counter", "Reach 500,000 Total Count", "platinum", AchievementType.TOTAL_COUNT, 500000)
    )
}
