package com.pokertrainer.ui.screens.game

import androidx.lifecycle.ViewModel
import com.pokertrainer.data.model.Card
import com.pokertrainer.data.model.Difficulty
import com.pokertrainer.data.model.GameState
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.data.model.humanPlayer
import com.pokertrainer.game.engine.HandEvaluator
import com.pokertrainer.game.engine.PokerEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

data class HintState(
    val handName: String,
    val handRank: Int,            // 1–10, nur für die Farbgebung
    val recommendation: String,
    val explanation: String,
    val highlightCards: List<Card> = emptyList()
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
        if (human.hand.size < 2) return

        val allCards = human.hand + state.communityCards
        _hint.value = if (allCards.size < 5) {
            // Pre-Flop: nur 2 Handkarten – der 5-Karten-Evaluator passt hier nicht,
            // daher eine einfache Starthand-Bewertung.
            preflopHint(human.hand)
        } else {
            val result = HandEvaluator.evaluate(allCards)
            val rank = result.handRank.rank
            val (rec, expl) = recommendationFor(rank)
            HintState(result.handRank.displayName, rank, rec, expl, HandEvaluator.definingCards(result))
        }
    }

    fun dismissHint() {
        _hint.value = null
    }

    private fun recommendationFor(rank: Int): Pair<String, String> = when {
        rank >= 7 -> "RAISE" to "Sehr starke Hand – erhöhe, um den Pot zu maximieren."
        rank >= 5 -> "CALL" to "Solide Hand – mitspielen lohnt sich."
        rank >= 3 -> "CALL" to "Mittelmässige Hand – nur mitgehen, wenn der Einsatz niedrig ist."
        rank == 2 -> "CHECK / FOLD" to "Schwache Hand – nur checken, niemals erhöhen."
        else      -> "FOLD" to "Sehr schwache Hand – aussteigen ist meist am sinnvollsten."
    }

    private fun preflopHint(hole: List<Card>): HintState {
        val a = hole[0]
        val b = hole[1]
        val isPair = a.rank == b.rank
        val highVal = maxOf(a.rank.value, b.rank.value)
        val lowVal = minOf(a.rank.value, b.rank.value)
        val suited = a.suit == b.suit
        val connected = abs(a.rank.value - b.rank.value) == 1

        // colorRank dient nur der Farbgebung (>=7 grün, >=3 gelb, sonst rot)
        return when {
            isPair && highVal >= 10 -> HintState(
                "Hohes Paar (${a.rank.display}${a.rank.display})", 7, "RAISE",
                "Starke Starthand – ein hohes Paar. Erhöhen ist sinnvoll.", hole
            )
            isPair -> HintState(
                "Paar (${a.rank.display}${a.rank.display})", 4, "CALL",
                "Ein Paar als Starthand – mitgehen lohnt sich meistens.", hole
            )
            highVal >= 13 && lowVal >= 11 -> HintState(
                "Zwei hohe Karten", 5, "RAISE",
                "Zwei hohe Karten – gute Ausgangslage, Erhöhen ist möglich.", hole
            )
            highVal >= 12 -> HintState(
                "Eine hohe Karte", 3, "CALL",
                "Eine hohe Karte dabei – vorsichtig mitgehen.", hole
            )
            suited || connected -> HintState(
                "Spekulative Hand", 3, "CALL",
                if (suited) "Gleiche Farbe – günstig auf einen Flush spielen."
                else "Verbundene Karten – günstig auf eine Strasse spielen.", hole
            )
            else -> HintState(
                "Schwache Starthand", 1, "FOLD",
                "Niedrige, unverbundene Karten – meist besser aussteigen.", hole
            )
        }
    }
}
