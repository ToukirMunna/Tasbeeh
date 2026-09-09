package com.toukir.tasbeeh.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.LeaderboardEntry
import com.toukir.tasbeeh.ui.common.UserAvatar
import com.toukir.tasbeeh.utils.formatNumber

@Composable
fun LeaderboardRankItem(
    entry: LeaderboardEntry,
    rank: Int,
    isCurrentUser: Boolean,
    language: String,
    period: String
) {
    val count = when (period.lowercase()) {
        "daily" -> entry.dailyCount
        "weekly" -> entry.weeklyCount
        "monthly" -> entry.monthlyCount
        else -> entry.dailyCount
    }
    val displayName = if (isCurrentUser) {
        stringResource(R.string.leaderboard_you, entry.username)
    } else {
        entry.username
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentUser) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LeaderboardRankBadge(rank = rank, language = language)
            Spacer(modifier = Modifier.width(12.dp))
            LeaderboardUserAvatar(isMale = entry.isMale)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatNumber(count, language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LeaderboardRankBadge(rank: Int, language: String) {
    val badgeColor = when (rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Box(
        modifier = Modifier.size(32.dp).clip(CircleShape).background(badgeColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formatNumber(rank, language),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (rank <= 3) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LeaderboardUserAvatar(isMale: Boolean) {
    UserAvatar(
        isMale = isMale,
        size = 40.dp
    )
}
