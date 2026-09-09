package com.toukir.tasbeeh.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import kotlin.math.cos
import kotlin.math.sin

private fun createRoundedOctagonPath(size: androidx.compose.ui.geometry.Size, cornerRadius: Float): Path {
    val path = Path()
    val sides = 8
    val radius = minOf(size.width, size.height) / 2f
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val angleStep = (2 * Math.PI / sides).toFloat()
    val startAngle = (-Math.PI / 2).toFloat()

    val points = List(sides) { i ->
        val angle = startAngle + i * angleStep
        androidx.compose.ui.geometry.Offset(
            centerX + radius * cos(angle),
            centerY + radius * sin(angle)
        )
    }

    if (radius <= 0f) {
        path.addOval(androidx.compose.ui.geometry.Rect(androidx.compose.ui.geometry.Offset.Zero, size))
        return path
    }

    for (i in 0 until sides) {
        val p1 = points[i]
        val p2 = points[(i + 1) % sides]
        val p3 = points[(i + 2) % sides]
        val dx1 = p2.x - p1.x
        val dy1 = p2.y - p1.y
        val len1 = kotlin.math.sqrt(dx1 * dx1 + dy1 * dy1)
        val dx2 = p3.x - p2.x
        val dy2 = p3.y - p2.y
        val len2 = kotlin.math.sqrt(dx2 * dx2 + dy2 * dy2)

        if (len1 > 0 && len2 > 0) {
            val startX = p2.x - (dx1 / len1) * cornerRadius
            val startY = p2.y - (dy1 / len1) * cornerRadius
            val endX = p2.x + (dx2 / len2) * cornerRadius
            val endY = p2.y + (dy2 / len2) * cornerRadius

            if (i == 0) path.moveTo(startX, startY) else path.lineTo(startX, startY)
            path.quadraticTo(p2.x, p2.y, endX, endY)
        }
    }
    path.close()
    return path
}

val SmoothOctagonShape = GenericShape { size, _ ->
    if (size.width > 0 && size.height > 0) {
        addPath(createRoundedOctagonPath(size, cornerRadius = size.width * 0.12f))
    }
}

@Composable
fun CounterHeader(
    goalName: String,
    onBack: () -> Unit,
    onDetailsClick: () -> Unit,
    onMeaningClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            IconButton(onClick = onBack) {
                StudioIcon(StudioIcons.ArrowBack, contentDescription = stringResource(R.string.cd_back))
            }
            Text(
                text = goalName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMeaningClick) {
                StudioIcon(
                    iconRes = StudioIcons.Translate,
                    contentDescription = stringResource(R.string.cd_meaning),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onDetailsClick() }) {
                StudioIcon(
                    iconRes = StudioIcons.Info,
                    contentDescription = stringResource(R.string.cd_details),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MeaningDisplay(
    translation: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = translation,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
