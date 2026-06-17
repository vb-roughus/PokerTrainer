package com.pokertrainer.ui.screens.game

import androidx.lifecycle.ViewModel
import com.pokertrainer.data.model.Difficulty
import com.pokertrainer.data.model.GameState
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.game.engine.PokerEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private val engine = PokerEngine()

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow(Difficulty.EASY)
    val selectedDifficulty: StateFlow<Difficulty> = _selectedDifficulty.asStateFlow()

    private val _gameStarted = MutableStateFlow(false)
    val gameStarted: StateFlow<Boolean> = _gameStarted.asStateFlow()

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
    }
}
