package com.pokertrainer.ui.screens.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.pokertrainer.data.model.humanPlayer
import com.pokertrainer.ui.components.AnimatedCard
import com.pokertrainer.ui.components.BetChip
import com.pokertrainer.ui.components.BettingControls
import com.pokertrainer.ui.components.CardSize
import com.pokertrainer.ui.components.EmptyCardSlot
import com.pokertrainer.ui.components.PlayerSeat
import com.pokertrainer.ui.components.RoleBadges
import com.pokertrainer.ui.components.PotChip
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
    val manualMode by viewModel.manualMode.collectAsState()

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
                    onStart = { viewModel.startGame() },
                    initialSpeed = viewModel.speedFraction(),
                    onSpeedChange = { viewModel.setSpeed(it) },
                    manualMode = manualMode,
                    onManualModeChange = { viewModel.setManualMode(it) }
                )
            } else if (gameState != null) {
                GameTable(
                    state = gameState!!,
                    onAction = { action, amount -> viewModel.humanAction(action, amount) },
                    onNextHand = { viewModel.nextHand() },
                    onRestart = { viewModel.restartMatch() },
                    onHintRequest = { viewModel.requestHint() },
                    highlightCards = hint?.highlightCards ?: emptyList(),
                    manualMode = manualMode,
                    stepPending = viewModel.hasPendingStep(gameState!!),
                    onAdvanceStep = { viewModel.advanceStep() }
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
    onStart: () -> Unit,
    initialSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    manualMode: Boolean,
    onManualModeChange: (Boolean) -> Unit
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

        // Speed control for the animations / AI steps
        Text(
            text = stringResource(R.string.game_speed),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        var speed by remember { mutableFloatStateOf(initialSpeed) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🐢", fontSize = 22.sp)
            Slider(
                value = speed,
                onValueChange = { speed = it; onSpeedChange(it) },
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFFC107),
                    activeTrackColor = Color(0xFFFFC107)
                )
            )
            Text("🐇", fontSize = 22.sp)
        }

        Spacer(Modifier.height(16.dp))

        // Manual mode: confirm every step with a button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.game_manual_mode),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.game_manual_mode_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBDBDBD)
                )
            }
            Switch(
                checked = manualMode,
                onCheckedChange = onManualModeChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = Color(0xFFFFC107)
                )
            )
        }

        Spacer(Modifier.height(24.dp))
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
    highlightCards: List<com.pokertrainer.data.model.Card> = emptyList(),
    manualMode: Boolean = false,
    stepPending: Boolean = false,
    onAdvanceStep: () -> Unit = {}
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
                    modifier = Modifier.weight(1f).padding(4.dp),
                    highlightCards = state.showdownCards,
                    isDealer = state.players.indexOfFirst { it.id == aiPlayer.id } == state.dealerIndex,
                    isSmallBlind = state.players.indexOfFirst { it.id == aiPlayer.id } == state.smallBlindIndex,
                    isBigBlind = state.players.indexOfFirst { it.id == aiPlayer.id } == state.bigBlindIndex,
                    isWinner = state.isHandOver && state.winnerId == aiPlayer.id
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
                        AnimatedCard(
                            card = card,
                            size = CardSize.MEDIUM,
                            highlighted = card in highlightCards || card in state.showdownCards,
                            // Stagger the three flop cards; turn & river appear on their own
                            delayMillis = if (idx < 3) idx * 130 else 0
                        )
                    } else {
                        EmptyCardSlot(size = CardSize.MEDIUM)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            val animatedPot by animateIntAsState(targetValue = state.pot, label = "pot")
            PotChip(amount = animatedPot)
        }

        // Human player row (always visible) + result banner / controls / waiting below it
        if (human != null) {
            val humanHighlight = highlightCards + state.showdownCards
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val humanIndex = state.players.indexOfFirst { it.id == human.id }
                    val humanIsWinner = state.isHandOver && state.winnerId == human.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val hasRole = humanIndex == state.dealerIndex ||
                                humanIndex == state.smallBlindIndex || humanIndex == state.bigBlindIndex
                            if (hasRole) {
                                RoleBadges(
                                    isDealer = humanIndex == state.dealerIndex,
                                    isSmallBlind = humanIndex == state.smallBlindIndex,
                                    isBigBlind = humanIndex == state.bigBlindIndex
                                )
                                Spacer(Modifier.size(4.dp))
                            }
                            Text(
                                text = (if (humanIsWinner) "🏆 " else "") + "${human.name}  🪙 ${human.chips}",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (humanIsWinner) Color(0xFF81C784) else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (human.currentBet > 0) {
                            Spacer(Modifier.size(2.dp))
                            BetChip(amount = human.currentBet, fromBelow = true)
                        }
                    }
                    human.hand.forEach { card ->
                        AnimatedCard(
                            card = card,
                            size = CardSize.MEDIUM,
                            modifier = Modifier.padding(2.dp),
                            highlighted = card in humanHighlight
                        )
                    }
                    val humanMustAct = !state.isHandOver && !stepPending && isHumanTurn
                    if (humanMustAct) {
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

                Spacer(Modifier.height(8.dp))

                when {
                    state.isHandOver && state.winnerMessage != null -> {
                        val humanChips = human.chips
                        val humanWon = state.winnerId == human.id
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xDD000000), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (humanWon) {
                                val celebrate = remember(state.winnerMessage) { MutableTransitionState(false) }
                                celebrate.targetState = true
                                AnimatedVisibility(
                                    visibleState = celebrate,
                                    enter = scaleIn(initialScale = 0.3f, animationSpec = tween(400)) + fadeIn(tween(400))
                                ) {
                                    Text(text = "🏆", fontSize = 44.sp)
                                }
                                Spacer(Modifier.height(4.dp))
                            }
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
                            Button(
                                onClick = if (state.isGameOver) onRestart else onNextHand,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenDark)
                            ) {
                                Text(
                                    text = stringResource(
                                        if (state.isGameOver) R.string.game_new_game else R.string.game_next_hand
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                    !state.isHandOver && !stepPending && isHumanTurn -> {
                        BettingControls(
                            state = state,
                            onAction = onAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xDD000000), RoundedCornerShape(12.dp))
                        )
                    }
                    manualMode && stepPending -> {
                        // Manual mode: wait for the player to confirm each step
                        Button(
                            onClick = onAdvanceStep,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreenDark)
                        ) {
                            Text(
                                text = "▶  ${stringResource(R.string.game_next_step)}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⏳ Warte auf ${state.players.getOrNull(state.activePlayerIndex)?.name}…",
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
