package com.pokertrainer.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokertrainer.R
import com.pokertrainer.data.model.BettingRound
import com.pokertrainer.data.model.Difficulty
import com.pokertrainer.data.model.GameState
import com.pokertrainer.data.model.activePlayers
import com.pokertrainer.data.model.humanPlayer
import com.pokertrainer.ui.components.BettingControls
import com.pokertrainer.ui.components.CardSize
import com.pokertrainer.ui.components.CardView
import com.pokertrainer.ui.components.EmptyCardSlot
import com.pokertrainer.ui.components.PlayerSeat
import com.pokertrainer.ui.theme.PrimaryGreenDark
import com.pokertrainer.ui.theme.TableFelt
import com.pokertrainer.ui.theme.TableGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onBack: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val gameStarted by viewModel.gameStarted.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val hint by viewModel.hint.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_play_game)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.nav_back))
                    }
                },
                actions = {
                    if (gameStarted) {
                        IconButton(onClick = { viewModel.newGame() }) {
                            Text("↩", color = Color.White, fontSize = 20.sp)
                        }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TableGreen)
                .padding(padding)
        ) {
            if (!gameStarted) {
                DifficultySelection(
                    selected = selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) },
                    onStart = { viewModel.startGame() }
                )
            } else if (gameState != null) {
                GameTable(
                    state = gameState!!,
                    onAction = { action, amount -> viewModel.humanAction(action, amount) },
                    onNextHand = { viewModel.nextHand() },
                    onRestart = { viewModel.restartMatch() },
                    onHintRequest = { viewModel.requestHint() },
                    highlightCards = hint?.highlightCards ?: emptyList()
                )
            }

            // Hint overlay – non-modal banner at the top, leaves the cards visible
            hint?.let { h ->
                HintOverlay(
                    hint = h,
                    onDismiss = { viewModel.dismissHint() },
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun HintOverlay(
    hint: HintState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val handColor = when {
        hint.handRank >= 7 -> Color(0xFF4CAF50)
        hint.handRank >= 3 -> Color(0xFFFFC107)
        else               -> Color(0xFFE53935)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xEE000000), RoundedCornerShape(12.dp))
            .border(2.dp, handColor, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "💡 Hilfe",
                    color = Color(0xFFBDBDBD),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = hint.handName,
                    color = handColor,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onDismiss) {
                Text("✕", color = Color.White, fontSize = 20.sp)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Empfehlung: ${hint.recommendation}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = hint.explanation,
            color = Color(0xFFE0E0E0),
            style = MaterialTheme.typography.bodyMedium
        )
        if (hint.highlightCards.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Die gelb umrandeten Karten machen deine Wertigkeit aus.",
                color = Color(0xFFFFC107),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun DifficultySelection(
    selected: Difficulty,
    onSelect: (Difficulty) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🃏",
            fontSize = 64.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.game_select_difficulty),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        val difficulties = listOf(
            Difficulty.EASY to stringResource(R.string.game_easy),
            Difficulty.MEDIUM to stringResource(R.string.game_medium),
            Difficulty.HARD to stringResource(R.string.game_hard)
        )

        difficulties.forEach { (diff, label) ->
            val isSelected = selected == diff
            Button(
                onClick = { onSelect(diff) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFFFFC107) else Color(0x44FFFFFF)
                )
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.Black else Color.White,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenDark)
        ) {
            Text(
                text = stringResource(R.string.game_start),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GameTable(
    state: GameState,
    onAction: (com.pokertrainer.data.model.PlayerAction, Int) -> Unit,
    onNextHand: () -> Unit,
    onRestart: () -> Unit,
    onHintRequest: () -> Unit,
    highlightCards: List<com.pokertrainer.data.model.Card> = emptyList()
) {
    val human = state.humanPlayer
    val isHumanTurn = state.players.getOrNull(state.activePlayerIndex)?.isHuman == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // AI players row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            state.players.filter { !it.isHuman }.forEach { aiPlayer ->
                PlayerSeat(
                    player = aiPlayer,
                    isActive = state.players.getOrNull(state.activePlayerIndex)?.id == aiPlayer.id,
                    showCards = state.phase == BettingRound.SHOWDOWN,
                    modifier = Modifier.weight(1f).padding(4.dp)
                )
            }
        }

        // Community cards + pot
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(TableFelt, RoundedCornerShape(16.dp))
                .border(2.dp, Color(0x44FFFFFF), RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Phase label
            val phaseLabel = when (state.phase) {
                BettingRound.PREFLOP -> stringResource(R.string.game_phase_preflop)
                BettingRound.FLOP -> stringResource(R.string.game_phase_flop)
                BettingRound.TURN -> stringResource(R.string.game_phase_turn)
                BettingRound.RIVER -> stringResource(R.string.game_phase_river)
                BettingRound.SHOWDOWN -> stringResource(R.string.game_phase_showdown)
            }
            Text(
                text = phaseLabel,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFFFFC107),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            // Community cards
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(5) { idx ->
                    val card = state.communityCards.getOrNull(idx)
                    if (card != null) {
                        CardView(card = card, size = CardSize.MEDIUM, highlighted = card in highlightCards)
                    } else {
                        EmptyCardSlot(size = CardSize.MEDIUM)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "💰 ${stringResource(R.string.game_pot)}: ${state.pot}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Winner message or human player + controls
        if (state.isHandOver && state.winnerMessage != null) {
            val humanChips = state.humanPlayer?.chips ?: 0
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xDD000000), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.winnerMessage,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (state.isGameOver) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            if (humanChips <= 0) R.string.game_match_lost else R.string.game_match_won
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (humanChips <= 0) Color(0xFFE53935) else Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(12.dp))
                if (state.isGameOver) {
                    Button(
                        onClick = onRestart,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenDark)
                    ) {
                        Text(stringResource(R.string.game_new_game), color = Color.White)
                    }
                } else {
                    Button(
                        onClick = onNextHand,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenDark)
                    ) {
                        Text(stringResource(R.string.game_next_hand), color = Color.White)
                    }
                }
            }
        } else {
            // Human player section
            if (human != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${human.name}  🪙 ${human.chips}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        human.hand.forEach { card ->
                            CardView(
                                card = card,
                                size = CardSize.MEDIUM,
                                modifier = Modifier.padding(2.dp),
                                highlighted = card in highlightCards
                            )
                        }
                        if (isHumanTurn && !state.isGameOver) {
                            Spacer(Modifier.size(8.dp))
                            Button(
                                onClick = onHintRequest,
                                modifier = Modifier.size(40.dp).padding(0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Text("?", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                    if (isHumanTurn && !state.isGameOver) {
                        Spacer(Modifier.height(4.dp))
                        BettingControls(
                            state = state,
                            onAction = onAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xDD000000), RoundedCornerShape(12.dp))
                        )
                    } else if (!state.isGameOver) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⏳ Waiting for ${state.players.getOrNull(state.activePlayerIndex)?.name}…",
                                color = Color(0xFFBDBDBD),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
