package com.pokertrainer.game.ai

import com.pokertrainer.data.model.BettingRound
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

        // Late position bonus (position 2+ gets more aggressive)
        val positionBonus = if (playerIndex >= state.players.size / 2) 1 else 0

        val effectiveStrength = handStrength + positionBonus

        // Occasional bluff (postflop only; this branch is never reached preflop
        // because the preflop case delegates to EasyAI above).
        val canBluff = Random.nextFloat() < 0.12f

        return when {
            effectiveStrength >= HandRank.FLUSH.rank -> {
                if (callAmount == 0) {
                    val raiseAmt = (state.pot * 0.75).toInt().coerceAtLeast(state.bigBlind)
                    Pair(PlayerAction.RAISE, raiseAmt)
                } else if (potOdds < 0.4f) Pair(PlayerAction.CALL, 0)
                else Pair(PlayerAction.RAISE, state.bigBlind * 2)
            }
            effectiveStrength >= HandRank.TWO_PAIR.rank -> {
                val raiseAmt = (state.pot * 0.6).toInt().coerceAtLeast(state.bigBlind * 2)
                if (callAmount == 0) Pair(PlayerAction.RAISE, raiseAmt)
                else if (potOdds < 0.35f) Pair(PlayerAction.RAISE, state.bigBlind * 2)
                else Pair(PlayerAction.FOLD, 0)
            }
            // Occasional bluff with a non-strong hand, to stay unpredictable.
            canBluff && callAmount == 0 ->
                Pair(PlayerAction.RAISE, (state.pot * 0.5).toInt().coerceAtLeast(state.bigBlind * 2))
            canBluff && callAmount <= state.bigBlind * 2 ->
                Pair(PlayerAction.RAISE, state.bigBlind * 2)
            effectiveStrength >= HandRank.ONE_PAIR.rank -> {
                val threshold = if (state.phase == BettingRound.PREFLOP) state.bigBlind * 3 else state.bigBlind
                if (callAmount == 0) Pair(PlayerAction.CHECK, 0)
                else if (callAmount <= threshold) Pair(PlayerAction.CALL, 0)
                else Pair(PlayerAction.FOLD, 0)
            }
            else -> {
                if (callAmount == 0) Pair(PlayerAction.CHECK, 0)
                else if (state.phase == BettingRound.PREFLOP && callAmount <= state.bigBlind) Pair(PlayerAction.CALL, 0)
                else Pair(PlayerAction.FOLD, 0)
            }
        }
    }
}
