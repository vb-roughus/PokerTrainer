package com.pokertrainer.ui.screens.strategy

import androidx.lifecycle.ViewModel
import com.pokertrainer.data.Scenario
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.data.scenarios
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StrategyUiState(
    val scenario: Scenario,
    val answered: Boolean = false,
    val wasCorrect: Boolean = false,
    val chosenAction: PlayerAction? = null,
    val currentIndex: Int = 0,
    val total: Int = 0,
    val score: Int = 0,
    val allComplete: Boolean = false
)

class StrategyViewModel : ViewModel() {
    private val shuffled = scenarios.shuffled()
    private var currentIndex = 0

    private val _uiState = MutableStateFlow(
        StrategyUiState(
            scenario = shuffled[0],
            currentIndex = 1,
            total = shuffled.size
        )
    )
    val uiState: StateFlow<StrategyUiState> = _uiState.asStateFlow()

    fun answer(action: PlayerAction) {
        val current = _uiState.value
        if (current.answered) return
        val correct = action == current.scenario.correctAction
        _uiState.value = current.copy(
            answered = true,
            wasCorrect = correct,
            chosenAction = action,
            score = if (correct) current.score + 1 else current.score
        )
    }

    fun next() {
        currentIndex++
        if (currentIndex >= shuffled.size) {
            _uiState.value = _uiState.value.copy(allComplete = true)
            return
        }
        _uiState.value = StrategyUiState(
            scenario = shuffled[currentIndex],
            currentIndex = currentIndex + 1,
            total = shuffled.size,
            score = _uiState.value.score
        )
    }

    fun restart() {
        currentIndex = 0
        val reshuffled = scenarios.shuffled()
        _uiState.value = StrategyUiState(
            scenario = reshuffled[0],
            currentIndex = 1,
            total = reshuffled.size,
            score = 0
        )
    }
}
