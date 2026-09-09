package com.toukir.tasbeeh.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.AdhkarInfo
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import com.toukir.tasbeeh.utils.formatNumber

@Composable
fun TasbeehDetailsTopBar(
    displayGoalName: String,
    onBack: () -> Unit,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            StudioIcon(StudioIcons.ArrowBack, contentDescription = stringResource(R.string.cd_back))
        }
        Text(
            text = displayGoalName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f).padding(start = 8.dp)
        )
        IconButton(onClick = onEditClick) {
            StudioIcon(StudioIcons.Edit, contentDescription = stringResource(R.string.cd_edit_details))
        }
    }
}

@Composable
fun TasbeehDetailsBottomBar(onCountClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "ButtonScale")

        Button(
            onClick = onCountClick,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .scale(scale),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonColors().let {
                ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp)
            }
        ) {
            StudioIcon(StudioIcons.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.recite_now),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun TasbeehDetailsHeaderCard(adhkarInfo: AdhkarInfo) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (adhkarInfo.arabic.isNotEmpty()) {
                Text(
                    text = adhkarInfo.arabic,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            if (adhkarInfo.translation.isNotEmpty()) {
                SectionLabel(stringResource(R.string.details_meaning))
                Text(
                    text = adhkarInfo.translation,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            if (adhkarInfo.virtue.isNotEmpty()) {
                SectionLabel(stringResource(R.string.details_virtue))
                Text(
                    text = adhkarInfo.virtue,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun DetailsStatCard(title: String, value: String, @DrawableRes iconRes: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StudioIcon(
                iconRes = iconRes,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun HistoryLogItem(date: String, count: Int, language: String = "en") {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = CircleShape
            ) {
                Text(
                    text = formatNumber(count, language),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
