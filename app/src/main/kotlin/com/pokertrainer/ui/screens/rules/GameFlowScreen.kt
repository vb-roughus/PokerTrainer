package com.pokertrainer.ui.screens.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokertrainer.R
import com.pokertrainer.ui.theme.PrimaryGreen
import com.pokertrainer.ui.theme.PrimaryGreenDark

private data class FlowStep(
    val number: Int,
    val emoji: String,
    val titleRes: Int,
    val descRes: Int,
    val color: Color
)

private val flowSteps = listOf(
    FlowStep(1, "💰", R.string.flow_blinds_title, R.string.flow_blinds_desc, Color(0xFFFFC107)),
    FlowStep(2, "🃏", R.string.flow_preflop_title, R.string.flow_preflop_desc, Color(0xFF1565C0)),
    FlowStep(3, "🌊", R.string.flow_flop_title, R.string.flow_flop_desc, Color(0xFF2E7D32)),
    FlowStep(4, "↩️", R.string.flow_turn_title, R.string.flow_turn_desc, Color(0xFF6A1B9A)),
    FlowStep(5, "🏞️", R.string.flow_river_title, R.string.flow_river_desc, Color(0xFF00838F)),
    FlowStep(6, "🏆", R.string.flow_showdown_title, R.string.flow_showdown_desc, Color(0xFFE65100))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameFlowScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.flow_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.nav_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreenDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(flowSteps.size) { idx ->
                FlowStepCard(flowSteps[idx])
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💡 Actions", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        ActionTip("FOLD", "Give up your hand and forfeit the round.")
                        ActionTip("CHECK", "Pass the action (only when no bet is open).")
                        ActionTip("CALL", "Match the current bet to stay in the hand.")
                        ActionTip("RAISE", "Increase the current bet amount.")
                        ActionTip("ALL IN", "Bet all your remaining chips.")
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun FlowStepCard(step: FlowStep) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(step.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = step.number.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = step.emoji, fontSize = 20.sp)
                    Text(
                        text = " ${stringResource(step.titleRes)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = step.color,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(step.descRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFBDBDBD)
                )
            }
        }
    }
}

@Composable
private fun ActionTip(action: String, desc: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = action,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF80CBC4),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFBDBDBD))
    }
}
