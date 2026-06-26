package com.pokertrainer.ui.screens.game

import androidx.lifecycle.ViewModel
import com.pokertrainer.data.model.Difficulty
import com.pokertrainer.data.model.GameState
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.data.model.humanPlayer
import com.pokertrainer.game.engine.HandEvaluator
import com.pokertrainer.game.engine.PokerEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HintState(
    val handName: String,
    val handRank: Int,
    val recommendation: String,
    val explanation: String
)

class GameViewModel : ViewModel() {
    private val engine = PokerEngine()

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow(Difficulty.EASY)
    val selectedDifficulty: StateFlow<Difficulty> = _selectedDifficulty.asStateFlow()

    private val _gameStarted = MutableStateFlow(false)
    val gameStarted: StateFlow<Boolean> = _gameStarted.asStateFlow()

    private val _hint = MutableStateFlow<HintState?>(null)
    val hint: StateFlow<HintState?> = _hint.asStateFlow()

    fun selectDifficulty(difficulty: Difficulty) {
        _selectedDifficulty.value = difficulty
    }

    fun startGame() {
        _gameState.value = engine.newGame(_selectedDifficulty.value)
        _gameStarted.value = true
    }

    fun humanAction(action: PlayerAction, raiseAmount: Int = 0) {
        val current = _gameState.value ?: return
        _gameState.value = engine.humanAction(current, action, raiseAmount)
    }

    fun newGame() {
        _gameStarted.value = false
        _gameState.value = null
        _hint.value = null
    }

    fun requestHint() {
        val state = _gameState.value ?: return
        val human = state.humanPlayer ?: return
        if (human.hand.isEmpty()) return

        val allCards = human.hand + state.communityCards
        val result = HandEvaluator.evaluate(allCards)
        val rank = result.handRank.rank

        val (rec, expl) = when {
            rank >= 7 -> "RAISE" to "Sehr starke Hand – erhöhe, um den Pot zu maximieren."
            rank >= 5 -> "CALL" to "Solide Hand – mitspielen lohnt sich."
            rank >= 3 -> "CALL" to "Mittelmässige Hand – nur mitgehen, wenn der Einsatz niedrig ist."
            rank == 2 -> "CHECK / FOLD" to "Schwache Hand – nur checken, niemals erhöhen."
            else      -> "FOLD" to "Sehr schwache Hand – aussteigen ist meist am sinnvollsten."
        }

        _hint.value = HintState(result.handRank.displayName, rank, rec, expl)
    }

    fun dismissHint() {
        _hint.value = null
    }
}
