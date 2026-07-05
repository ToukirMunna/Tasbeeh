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
        Achievement("bismillah", "Bismillah", "Complete your first Tasbeeh", "🌱", AchievementType.TOTAL_COUNT, 1),
        Achievement("starter_100", "The Starter", "Reach 100 Total Count", "🏁", AchievementType.TOTAL_COUNT, 100),
        Achievement("daily_33", "Sunnah Step", "33 counts in one day", "📿", AchievementType.DAILY_COUNT, 33),
        Achievement("daily_100", "Daily Devotion", "100 counts in one day", "🤲", AchievementType.DAILY_COUNT, 100),
        Achievement("heating_up", "Heating Up", "3 Day Streak", "🔥", AchievementType.STREAK, 3),
        Achievement("on_fire", "On Fire", "7 Day Streak", "🔥🔥", AchievementType.STREAK, 7),
        Achievement("streak_14", "Two Weeks", "14 Day Streak", "🗓️", AchievementType.STREAK, 14),
        Achievement("habitual", "Habitual", "30 Day Streak", "📅", AchievementType.STREAK, 30),
        Achievement("streak_40", "Spiritual 40", "40 Day Streak", "🕌", AchievementType.STREAK, 40),
        Achievement("bronze_counter", "Bronze Counter", "Reach 1,000 Total Count", "🥉", AchievementType.TOTAL_COUNT, 1000),
        Achievement("dedicated_day", "Dedicated", "1,000 counts in one day", "💪", AchievementType.DAILY_COUNT, 1000),
        Achievement("silver_counter", "Silver Counter", "Reach 10,000 Total Count", "🥈", AchievementType.TOTAL_COUNT, 10000),
        Achievement("gold_counter", "Gold Counter", "Reach 100,000 Total Count", "🥇", AchievementType.TOTAL_COUNT, 100000),
        Achievement("platinum_counter", "Platinum Counter", "Reach 500,000 Total Count", "💎", AchievementType.TOTAL_COUNT, 500000)
    )
}
