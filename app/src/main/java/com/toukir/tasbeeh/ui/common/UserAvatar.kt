package com.toukir.tasbeeh.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val SkinTone = Color(0xFFF3D0B5)
private val DarkHair = Color(0xFF2C3238)
private val SoftLip = Color(0xFFD9777F)

@Composable
fun UserAvatar(
    isMale: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val bgCircle = primaryContainer.copy(alpha = 0.45f)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgCircle)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            if (isMale) {
                drawMaleAvatar(w, h, primaryColor, onPrimaryContainer)
            } else {
                drawFemaleAvatar(w, h, primaryColor, primaryContainer)
            }
        }
    }
}

private fun DrawScope.drawMaleAvatar(w: Float, h: Float, themeColor: Color, accentColor: Color) {
    // Kurta shoulders
    drawArc(
        color = themeColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.10f, h * 0.68f),
        size = Size(w * 0.80f, h * 0.70f)
    )
    // Kurta collar
    drawRoundRect(
        color = accentColor.copy(alpha = 0.35f),
        topLeft = Offset(w * 0.44f, h * 0.66f),
        size = Size(w * 0.12f, h * 0.16f),
        cornerRadius = CornerRadius(w * 0.02f)
    )
    // Head / Face
    drawCircle(color = SkinTone, radius = w * 0.23f, center = Offset(w * 0.50f, h * 0.46f))
    // Neat beard
    drawArc(
        color = DarkHair,
        startAngle = 15f,
        sweepAngle = 150f,
        useCenter = true,
        topLeft = Offset(w * 0.28f, h * 0.43f),
        size = Size(w * 0.44f, h * 0.28f)
    )
    // Taqiyah (prayer cap)
    drawArc(
        color = themeColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.26f, h * 0.22f),
        size = Size(w * 0.48f, h * 0.36f)
    )
    drawRoundRect(
        color = themeColor,
        topLeft = Offset(w * 0.25f, h * 0.36f),
        size = Size(w * 0.50f, h * 0.06f),
        cornerRadius = CornerRadius(w * 0.02f)
    )
    drawEyesAndSmile(w, h, isMale = true)
}

private fun DrawScope.drawFemaleAvatar(w: Float, h: Float, themeColor: Color, capColor: Color) {
    // Hijab shoulder drape / abaya
    drawArc(
        color = themeColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.08f, h * 0.60f),
        size = Size(w * 0.84f, h * 0.80f)
    )
    // Outer hijab head wrap
    drawOval(
        color = themeColor,
        topLeft = Offset(w * 0.20f, h * 0.16f),
        size = Size(w * 0.60f, h * 0.68f)
    )
    // Underscarf band
    drawArc(
        color = capColor,
        startAngle = 195f,
        sweepAngle = 150f,
        useCenter = true,
        topLeft = Offset(w * 0.28f, h * 0.25f),
        size = Size(w * 0.44f, h * 0.28f)
    )
    // Face opening
    drawOval(
        color = SkinTone,
        topLeft = Offset(w * 0.31f, h * 0.30f),
        size = Size(w * 0.38f, h * 0.42f)
    )
    // Hijab lower drape fold
    drawArc(
        color = themeColor.copy(alpha = 0.90f),
        startAngle = 10f,
        sweepAngle = 160f,
        useCenter = true,
        topLeft = Offset(w * 0.34f, h * 0.64f),
        size = Size(w * 0.32f, h * 0.18f)
    )
    drawEyesAndSmile(w, h, isMale = false)
}

private fun DrawScope.drawEyesAndSmile(w: Float, h: Float, isMale: Boolean) {
    // Eyes
    val eyeY = if (isMale) h * 0.46f else h * 0.48f
    drawCircle(color = DarkHair, radius = w * 0.028f, center = Offset(w * 0.42f, eyeY))
    drawCircle(color = DarkHair, radius = w * 0.028f, center = Offset(w * 0.58f, eyeY))

    // Eyebrows
    val browY = if (isMale) h * 0.40f else h * 0.42f
    val browStroke = Stroke(width = w * 0.020f, cap = StrokeCap.Round)
    drawArc(DarkHair, 205f, 130f, false, Offset(w * 0.37f, browY), Size(w * 0.10f, h * 0.04f), style = browStroke)
    drawArc(DarkHair, 205f, 130f, false, Offset(w * 0.53f, browY), Size(w * 0.10f, h * 0.04f), style = browStroke)

    // Smile
    val mouthY = if (isMale) h * 0.56f else h * 0.58f
    val smileColor = if (isMale) DarkHair.copy(alpha = 0.8f) else SoftLip
    val smileStroke = Stroke(width = w * 0.022f, cap = StrokeCap.Round)
    drawArc(smileColor, 20f, 140f, false, Offset(w * 0.44f, mouthY), Size(w * 0.12f, h * 0.06f), style = smileStroke)
}
