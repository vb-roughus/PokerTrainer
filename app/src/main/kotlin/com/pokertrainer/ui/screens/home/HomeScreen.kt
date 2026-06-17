package com.pokertrainer.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokertrainer.R
import com.pokertrainer.ui.theme.PrimaryGreen
import com.pokertrainer.ui.theme.PrimaryGreenDark
import com.pokertrainer.ui.theme.SecondaryGold
import com.pokertrainer.ui.theme.TableFelt
import com.pokertrainer.ui.theme.TableGreen

@Composable
fun HomeScreen(
    onLearnRules: () -> Unit,
    onPlayGame: () -> Unit,
    onStrategy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(TableGreen, PrimaryGreenDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "♠ ♥",
                fontSize = 48.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineLarge,
                color = SecondaryGold,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "♣ ♦",
                fontSize = 48.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFB9F6CA),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            HomeButton(
                text = "📖  ${stringResource(R.string.home_learn_rules)}",
                onClick = onLearnRules,
                containerColor = Color(0xFF1565C0)
            )
            Spacer(modifier = Modifier.height(16.dp))
            HomeButton(
                text = "🃏  ${stringResource(R.string.home_play_game)}",
                onClick = onPlayGame,
                containerColor = PrimaryGreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            HomeButton(
                text = "🧠  ${stringResource(R.string.home_strategy)}",
                onClick = onStrategy,
                containerColor = Color(0xFF6A1B9A)
            )
        }
    }
}

@Composable
private fun HomeButton(text: String, onClick: () -> Unit, containerColor: Color) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
