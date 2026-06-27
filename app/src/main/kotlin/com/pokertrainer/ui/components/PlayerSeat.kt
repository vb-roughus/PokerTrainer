package com.pokertrainer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokertrainer.data.model.Card
import com.pokertrainer.data.model.Player
import com.pokertrainer.data.model.PlayerAction

@Composable
fun PlayerSeat(
    player: Player,
    isActive: Boolean,
    showCards: Boolean = false,
    modifier: Modifier = Modifier,
    highlightCards: List<Card> = emptyList()
) {
    val borderColor = when {
        player.hasFolded -> Color(0xFF616161)
        isActive -> Color(0xFFFFC107)
        else -> Color(0xFF424242)
    }

    Column(
        modifier = modifier
            .background(
                color = if (player.hasFolded) Color(0x44000000) else Color(0x88000000),
                shape = RoundedCornerShape(12.dp)
            )
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = player.name,
            style = MaterialTheme.typography.labelLarge,
            color = if (player.hasFolded) Color.Gray else Color.White,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = "🪙 ${player.chips}",
            fontSize = 12.sp,
            color = Color(0xFFFFC107)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (player.hand.isEmpty()) {
                EmptyCardSlot(size = CardSize.SMALL)
                EmptyCardSlot(size = CardSize.SMALL)
            } else {
                player.hand.forEach { card ->
                    val revealed = showCards || player.isHuman
                    CardView(
                        card = card,
                        faceDown = !revealed,
                        size = CardSize.SMALL,
                        highlighted = revealed && card in highlightCards
                    )
                }
            }
        }
        if (player.currentBet > 0) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Bet: ${player.currentBet}", fontSize = 10.sp, color = Color(0xFF90CAF9))
        }
        player.lastAction?.let { action ->
            val label = when (action) {
                PlayerAction.FOLD -> "Fold"
                PlayerAction.CHECK -> "Check"
                PlayerAction.CALL -> "Call"
                PlayerAction.RAISE -> "Raise"
                PlayerAction.ALL_IN -> "All In!"
            }
            Text(text = label, fontSize = 10.sp, color = Color(0xFF80CBC4), fontWeight = FontWeight.Bold)
        }
    }
}
