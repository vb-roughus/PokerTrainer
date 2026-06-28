package com.pokertrainer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Poker-chip colour by bet size. */
private fun chipColor(amount: Int): Color = when {
    amount >= 200 -> Color(0xFF212121) // black
    amount >= 100 -> Color(0xFF2E7D32) // green
    amount >= 50 -> Color(0xFF1565C0)  // blue
    amount >= 20 -> Color(0xFFE53935)  // red
    else -> Color(0xFFEEEEEE)          // white
}

/**
 * Shows a player's current bet as a poker chip that animates in (and slides up,
 * suggesting the chips moving toward the pot) whenever the amount changes.
 */
@Composable
fun BetChip(amount: Int, modifier: Modifier = Modifier, fromBelow: Boolean = false) {
    val state = remember(amount) { MutableTransitionState(false) }
    state.targetState = true
    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn(tween(280)) +
            scaleIn(initialScale = 0.4f, animationSpec = tween(280)) +
            slideInVertically(tween(280)) { h -> if (fromBelow) h / 2 else -h / 2 }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(chipColor(amount), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "$amount",
                fontSize = 11.sp,
                color = Color(0xFF90CAF9),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** Pot display: a chip icon plus the (animated) amount, used in the centre of the table. */
@Composable
fun PotChip(amount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(Color(0xFFFFC107), CircleShape)
                .border(2.dp, Color.White, CircleShape)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "Pot: $amount",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}
