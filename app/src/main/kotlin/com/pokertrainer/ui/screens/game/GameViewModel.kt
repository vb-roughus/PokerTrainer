package com.pokertrainer.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokertrainer.data.model.Card
import com.pokertrainer.data.model.Difficulty
import com.pokertrainer.data.model.GameState
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.data.model.humanPlayer
import com.pokertrainer.game.engine.HandEvaluator
import com.pokertrainer.game.engine.PokerEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    /** Delay between automatic steps in ms (lower = faster). Adjustable via the speed slider. */
    private val _stepDelayMs = MutableStateFlow(DEFAULT_STEP_DELAY_MS)
    val stepDelayMs: StateFlow<Long> = _stepDelayMs.asStateFlow()

    /** When on, each step waits for the player to tap "Weiter" instead of auto-advancing. */
    private val _manualMode = MutableStateFlow(false)
    val manualMode: StateFlow<Boolean> = _manualMode.asStateFlow()

    private var stepJob: Job? = null

    fun selectDifficulty(difficulty: Difficulty) {
        _selectedDifficulty.value = difficulty
    }

    /** [fraction] 0f = slowest, 1f = fastest. */
    fun setSpeed(fraction: Float) {
        val clamped = fraction.coerceIn(0f, 1f)
        _stepDelayMs.value = (MAX_STEP_DELAY_MS - clamped * (MAX_STEP_DELAY_MS - MIN_STEP_DELAY_MS)).toLong()
    }

    /** Current slider position derived from the delay (0f = slowest, 1f = fastest). */
    fun speedFraction(): Float =
        ((MAX_STEP_DELAY_MS - _stepDelayMs.value).toFloat() / (MAX_STEP_DELAY_MS - MIN_STEP_DELAY_MS)).coerceIn(0f, 1f)

    fun setManualMode(enabled: Boolean) {
        _manualMode.value = enabled
        if (enabled) stepJob?.cancel() else autoPlay()  // resume automatic play if there are pending steps
    }

    /** Whether an automatic step is waiting (used by the UI to show the "Weiter" button). */
    fun hasPendingStep(state: GameState): Boolean = engine.hasPendingStep(state)

    /** Manual mode: apply exactly one step when the player taps "Weiter". */
    fun advanceStep() {
        val current = _gameState.value ?: return
        _gameState.value = engine.nextStep(current) ?: return
    }

    fun startGame() {
        _hint.value = null
        _gameState.value = engine.newGame(_selectedDifficulty.value)
        _gameStarted.value = true
        autoPlay()
    }

    fun humanAction(action: PlayerAction, raiseAmount: Int = 0) {
        val current = _gameState.value ?: return
        _hint.value = null
        _gameState.value = engine.humanAction(current, action, raiseAmount)
        autoPlay()
    }

    /** Deal the next hand, keeping the current chip stacks. */
    fun nextHand() {
        val current = _gameState.value ?: return
        _hint.value = null
        _gameState.value = engine.nextHand(current)
        autoPlay()
    }

    /** Restart a fresh match in the same difficulty (used after the match ends). */
    fun restartMatch() {
        _hint.value = null
        _gameState.value = engine.newGame(_selectedDifficulty.value)
        autoPlay()
    }

    /** Back to the difficulty selection (top-bar reset). */
    fun newGame() {
        stepJob?.cancel()
        _gameStarted.value = false
        _gameState.value = null
        _hint.value = null
    }

    /**
     * Plays out the automatic steps (AI actions, dealing the flop/turn/river,
     * showdown) one at a time with a short delay, so the player can follow what
     * happens. Stops as soon as it is the human's turn or the hand is over.
     */
    private fun autoPlay() {
        stepJob?.cancel()
        if (_manualMode.value) return  // steps are driven by the "Weiter" button
        stepJob = viewModelScope.launch {
            while (true) {
                val current = _gameState.value ?: break
                if (current.isHandOver) break
                val next = engine.nextStep(current) ?: break
                delay(_stepDelayMs.value)
                _gameState.value = next
            }
        }
    }

    companion object {
        private const val DEFAULT_STEP_DELAY_MS = 750L
        private const val MIN_STEP_DELAY_MS = 150L   // fastest
        private const val MAX_STEP_DELAY_MS = 3000L  // slowest (50% langsamer als zuvor)
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
