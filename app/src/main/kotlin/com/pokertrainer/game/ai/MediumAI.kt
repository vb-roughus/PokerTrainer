package com.pokertrainer.game.ai

import com.pokertrainer.data.model.GameState
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.game.engine.HandEvaluator
import com.pokertrainer.game.engine.HandRank
import kotlin.random.Random

object MediumAI {
    fun decide(state: GameState, playerIndex: Int): Pair<PlayerAction, Int> {
        val player = state.players[playerIndex]
        val allCards = player.hand + state.communityCards
        val handResult = if (allCards.size >= 5) HandEvaluator.evaluate(allCards) else null
        val handStrength = handResult?.handRank?.rank ?: EasyAI.decide(state, playerIndex).let { return it }

        val callAmount = (state.currentBet - player.currentBet).coerceAtLeast(0)
        val potOdds = if (callAmount > 0) callAmount.toFloat() / (state.pot + callAmount) else 0f

        // Late position plays slightly looser (wider calling range), but this must
        // NOT inflate the hand into a higher raise tier – otherwise one pair would
        // always be treated as two pair and the AI would raise almost every hand.
        val inLatePosition = playerIndex >= state.players.size / 2

        // Occasional bluff (postflop only; this branch is never reached preflop
        // because the preflop case delegates to EasyAI above).
        val canBluff = Random.nextFloat() < 0.12f

        return when {
            // Value raises are based on the REAL made-hand strength.
            handStrength >= HandRank.FLUSH.rank -> {
                if (callAmount == 0) {
                    val raiseAmt = (state.pot * 0.75).toInt().coerceAtLeast(state.bigBlind)
                    Pair(PlayerAction.RAISE, raiseAmt)
                } else if (potOdds < 0.4f) Pair(PlayerAction.CALL, 0)
                else Pair(PlayerAction.RAISE, state.bigBlind * 2)
            }
            handStrength >= HandRank.TWO_PAIR.rank -> {
                val raiseAmt = (state.pot * 0.6).toInt().coerceAtLeast(state.bigBlind * 2)
                if (callAmount == 0) Pair(PlayerAction.RAISE, raiseAmt)
                else if (potOdds < 0.5f) Pair(PlayerAction.RAISE, state.bigBlind * 2)
                else Pair(PlayerAction.CALL, 0)
            }
            // Occasional bluff with a non-strong hand, to stay unpredictable.
            canBluff && callAmount == 0 ->
                Pair(PlayerAction.RAISE, (state.pot * 0.5).toInt().coerceAtLeast(state.bigBlind * 2))
            canBluff && callAmount <= state.bigBlind * 2 ->
                Pair(PlayerAction.RAISE, state.bigBlind * 2)
            // One pair: check, or call up to a threshold (a bit wider in position).
            handStrength >= HandRank.ONE_PAIR.rank -> {
                val threshold = if (inLatePosition) state.bigBlind * 3 else state.bigBlind * 2
                if (callAmount == 0) Pair(PlayerAction.CHECK, 0)
                else if (callAmount <= threshold) Pair(PlayerAction.CALL, 0)
                else Pair(PlayerAction.FOLD, 0)
            }
            else -> {
                if (callAmount == 0) Pair(PlayerAction.CHECK, 0)
                else Pair(PlayerAction.FOLD, 0)
            }
        }
    }
}
