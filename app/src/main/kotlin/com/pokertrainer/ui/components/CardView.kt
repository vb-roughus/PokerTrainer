package com.pokertrainer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokertrainer.data.model.Card
import com.pokertrainer.ui.theme.CardBlack
import com.pokertrainer.ui.theme.CardRed
import com.pokertrainer.ui.theme.CardWhite

@Composable
fun CardView(
    card: Card?,
    modifier: Modifier = Modifier,
    faceDown: Boolean = false,
    size: CardSize = CardSize.MEDIUM
) {
    val cardColor = if (card?.isRed == true) CardRed else CardBlack
    val (width, height, fontSize, suitSize) = when (size) {
        CardSize.SMALL -> listOf(36.dp, 52.dp, 12.sp, 10.sp)
        CardSize.MEDIUM -> listOf(52.dp, 72.dp, 16.sp, 14.sp)
        CardSize.LARGE -> listOf(70.dp, 100.dp, 22.sp, 18.sp)
    }

    Box(
        modifier = modifier
            .size(width as Dp, height as Dp)
            .background(
                color = if (faceDown) Color(0xFF1565C0) else CardWhite,
                shape = RoundedCornerShape(6.dp)
            )
            .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (faceDown || card == null) {
            Text(
                text = "🂠",
                fontSize = fontSize as androidx.compose.ui.unit.TextUnit,
                color = Color.White
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = card.rank.display,
                    fontSize = fontSize as androidx.compose.ui.unit.TextUnit,
                    fontWeight = FontWeight.Bold,
                    color = cardColor,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = card.suit.symbol,
                    fontSize = suitSize as androidx.compose.ui.unit.TextUnit,
                    color = cardColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EmptyCardSlot(modifier: Modifier = Modifier, size: CardSize = CardSize.MEDIUM) {
    val (width, height) = when (size) {
        CardSize.SMALL -> Pair(36.dp, 52.dp)
        CardSize.MEDIUM -> Pair(52.dp, 72.dp)
        CardSize.LARGE -> Pair(70.dp, 100.dp)
    }
    Box(
        modifier = modifier
            .size(width, height)
            .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(6.dp))
            .padding(4.dp)
    )
}

enum class CardSize { SMALL, MEDIUM, LARGE }
