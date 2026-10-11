package dev.ruizj.loteriaapp_dpa.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

enum class BallType {
    NORMAL,
    SELECTED,
    MATCHED,
    DRAWN
}

@Composable
fun BallItem(
    number: Int,
    type: BallType = BallType.NORMAL,
    size: Dp = 44.dp,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (type) {
            BallType.NORMAL -> MaterialTheme.colorScheme.surfaceVariant
            BallType.SELECTED -> MaterialTheme.colorScheme.primary
            BallType.MATCHED -> Color(0xFF2E7D32) // Forest Green for hits
            BallType.DRAWN -> Color(0xFFD81B60) // Vivid Red/Pink for drawn balls
        },
        label = "ballBackground"
    )

    val textColor = when (type) {
        BallType.NORMAL -> MaterialTheme.colorScheme.onSurfaceVariant
        BallType.SELECTED -> MaterialTheme.colorScheme.onPrimary
        BallType.MATCHED -> Color.White
        BallType.DRAWN -> Color.White
    }

    val borderColor = when (type) {
        BallType.MATCHED -> Color(0xFF1B5E20)
        BallType.DRAWN -> Color(0xFF880E4F)
        BallType.SELECTED -> MaterialTheme.colorScheme.primary
        BallType.NORMAL -> Color.Transparent
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(2.dp, borderColor, CircleShape)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
    ) {
        Text(
            text = String.format(Locale.US, "%02d", number),
            color = textColor,
            fontSize = (size.value * 0.38f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}
