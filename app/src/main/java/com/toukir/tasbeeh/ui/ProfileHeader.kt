package com.toukir.tasbeeh.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.common.UserAvatar
import com.toukir.tasbeeh.ui.theme.StudioIcons
import com.toukir.tasbeeh.utils.formatNumber
import java.util.Locale

@Composable
fun ProfileHeaderSection(
    userName: String,
    currentStreak: Int,
    isMale: Boolean,
    onNameClick: () -> Unit,
    language: String = "en"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfileWelcomeCard(
            userName = userName,
            isMale = isMale,
            onNameClick = onNameClick,
            modifier = Modifier
                .weight(0.65f)
                .fillMaxHeight()
        )
        ProfileStreakCard(
            currentStreak = currentStreak,
            language = language,
            modifier = Modifier
                .weight(0.35f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun ProfileWelcomeCard(
    userName: String,
    isMale: Boolean,
    onNameClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(isMale = isMale, onClick = onNameClick)
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.greeting),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = userName.ifEmpty { stringResource(R.string.user) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.clickable { onNameClick() }
                )
            }
        }
    }
}

@Composable
private fun ProfileAvatar(isMale: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(54.dp).clickable { onClick() },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        UserAvatar(
            isMale = isMale,
            size = 54.dp
        )
    }
}

@Composable
private fun ProfileStreakCard(
    currentStreak: Int,
    language: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                StudioIcon(
                    iconRes = StudioIcons.LocalFireDepartment,
                    contentDescription = stringResource(R.string.cd_streak),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatNumber(currentStreak, language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = stringResource(R.string.day_streak),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

