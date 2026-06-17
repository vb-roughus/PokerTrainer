package com.pokertrainer.ui.screens.strategy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokertrainer.R
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.ui.components.CardSize
import com.pokertrainer.ui.components.CardView
import com.pokertrainer.ui.components.EmptyCardSlot
import com.pokertrainer.ui.theme.PrimaryGreen
import com.pokertrainer.ui.theme.PrimaryGreenDark
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrategyScreen(
    onBack: () -> Unit,
    viewModel: StrategyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isGerman = Locale.getDefault().language == "de"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.strategy_title)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.allComplete) {
                CompletionScreen(score = uiState.score, total = uiState.total, onRestart = { viewModel.restart() })
            } else {
                // Progress
                Text(
                    text = String.format(stringResource(R.string.strategy_progress), uiState.currentIndex, uiState.total) +
                            "  ✅ ${uiState.score}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFBDBDBD)
                )
                LinearProgressIndicator(
                    progress = { uiState.currentIndex.toFloat() / uiState.total },
                    modifier = Modifier.fillMaxWidth(),
                    color = PrimaryGreen,
                    trackColor = Color(0xFF333333)
                )

                // Scenario title
                val title = if (isGerman) uiState.scenario.titleDe else uiState.scenario.titleEn
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.Bold
                )

                // Hole cards
                Text(stringResource(R.string.strategy_your_hand), style = MaterialTheme.typography.labelLarge, color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    uiState.scenario.holeCards.forEach { CardView(it, size = CardSize.LARGE) }
                }

                // Community cards
                if (uiState.scenario.communityCards.isNotEmpty()) {
                    Text(stringResource(R.string.strategy_board), style = MaterialTheme.typography.labelLarge, color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        uiState.scenario.communityCards.forEach { CardView(it, size = CardSize.MEDIUM) }
                        repeat(5 - uiState.scenario.communityCards.size) { EmptyCardSlot(size = CardSize.MEDIUM) }
                    }
                }

                // Pot info
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoChip("💰 Pot: ${uiState.scenario.pot}")
                    if (uiState.scenario.currentBet > 0) InfoChip("📢 Bet: ${uiState.scenario.currentBet}")
                    InfoChip("🪙 Chips: ${uiState.scenario.playerChips}")
                }

                // Win rate
                Text(
                    text = "${stringResource(R.string.strategy_win_rate)} ${(uiState.scenario.winRate * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF80CBC4)
                )

                // Action buttons
                if (!uiState.answered) {
                    Text(
                        text = stringResource(R.string.strategy_question),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionButton(stringResource(R.string.action_fold), Color(0xFFEF5350)) { viewModel.answer(PlayerAction.FOLD) }
                        ActionButton(
                            if (uiState.scenario.currentBet == 0) stringResource(R.string.action_check) else stringResource(R.string.action_call),
                            Color(0xFF1565C0)
                        ) { viewModel.answer(if (uiState.scenario.currentBet == 0) PlayerAction.CHECK else PlayerAction.CALL) }
                        ActionButton(stringResource(R.string.action_raise), PrimaryGreen) { viewModel.answer(PlayerAction.RAISE) }
                    }
                } else {
                    // Feedback
                    val correct = uiState.wasCorrect
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (correct) Color(0xFF1B5E20) else Color(0xFF7F0000)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (correct) "✅ ${stringResource(R.string.strategy_correct)}" else "❌ ${stringResource(R.string.strategy_wrong)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            val correctLabel = when (uiState.scenario.correctAction) {
                                PlayerAction.FOLD -> stringResource(R.string.action_fold)
                                PlayerAction.CHECK -> stringResource(R.string.action_check)
                                PlayerAction.CALL -> stringResource(R.string.action_call)
                                PlayerAction.RAISE -> stringResource(R.string.action_raise)
                                PlayerAction.ALL_IN -> stringResource(R.string.action_allin)
                            }
                            if (!correct) {
                                Text(
                                    text = "Best: $correctLabel",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFFFCC80)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.strategy_explanation) + ":",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFFE0E0E0),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isGerman) uiState.scenario.explanationDe else uiState.scenario.explanationEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFE0E0E0)
                            )
                        }
                    }
                    Button(
                        onClick = { viewModel.next() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenDark)
                    ) {
                        Text(stringResource(R.string.strategy_next), color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(label: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InfoChip(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFFBDBDBD),
        modifier = Modifier
            .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun CompletionScreen(score: Int, total: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏆", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.strategy_complete),
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFFC107),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Score: $score / $total",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        val emoji = when {
            score == total -> "🌟 Perfect!"
            score >= total * 0.8 -> "🎉 Great job!"
            score >= total * 0.6 -> "👍 Good effort!"
            else -> "📚 Keep studying!"
        }
        Text(text = emoji, style = MaterialTheme.typography.titleMedium, color = Color(0xFF80CBC4))
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenDark),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(stringResource(R.string.strategy_restart), color = Color.White)
        }
    }
}
