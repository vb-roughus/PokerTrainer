package com.pokertrainer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pokertrainer.R
import com.pokertrainer.data.model.GameState
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.data.model.callAmount
import com.pokertrainer.data.model.canCheck
import com.pokertrainer.data.model.humanPlayer
import com.pokertrainer.ui.theme.TableGreenLight

@Composable
fun BettingControls(
    state: GameState,
    onAction: (PlayerAction, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val human = state.humanPlayer ?: return
    val canCheck = state.canCheck
    val call = state.callAmount
    var raiseSlider by remember { mutableFloatStateOf(state.bigBlind.toFloat()) }
    val maxRaise = (human.chips - call).coerceAtLeast(state.bigBlind)

    Column(modifier = modifier.padding(8.dp)) {
        // Raise slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.game_raise_amount) + ": ${raiseSlider.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
        Slider(
            value = raiseSlider,
            onValueChange = { raiseSlider = it },
            valueRange = state.bigBlind.toFloat()..maxRaise.toFloat().coerceAtLeast(state.bigBlind.toFloat()),
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFFC107),
                activeTrackColor = TableGreenLight
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fold
            OutlinedButton(
                onClick = { onAction(PlayerAction.FOLD, 0) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350))
            ) {
                Text(stringResource(R.string.action_fold))
            }

            // Check or Call
            if (canCheck) {
                Button(
                    onClick = { onAction(PlayerAction.CHECK, 0) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text(stringResource(R.string.action_check))
                }
            } else {
                Button(
                    onClick = { onAction(PlayerAction.CALL, 0) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("${stringResource(R.string.action_call)} $call")
                }
            }

            // Raise
            Button(
                onClick = { onAction(PlayerAction.RAISE, raiseSlider.toInt()) },
                modifier = Modifier.weight(1f),
                enabled = human.chips > call,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(stringResource(R.string.action_raise))
            }
        }
    }
}
