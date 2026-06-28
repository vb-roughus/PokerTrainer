package com.pokertrainer.game.ai

import com.pokertrainer.data.model.BettingRound
import com.pokertrainer.data.model.GameState
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.game.engine.HandEvaluator
import com.pokertrainer.game.engine.HandRank
import kotlin.random.Random

object HardAI {
    fun decide(state: GameState, playerIndex: Int): Pair<PlayerAction, Int> {
        val player = state.players[playerIndex]
        val allCards = player.hand + state.communityCards
        val handResult = if (allCards.size >= 5) HandEvaluator.evaluate(allCards) else null
        val handStrength = handResult?.handRank?.rank ?: estimatePreflop(player.hand)

        val callAmount = (state.currentBet - player.currentBet).coerceAtLeast(0)
        val potOdds = if (callAmount > 0) callAmount.toFloat() / (state.pot + callAmount) else 0f

        // Bluff occasionally with weak hands on later streets
        val canBluff = state.phase in listOf(BettingRound.TURN, BettingRound.RIVER) && Random.nextFloat() < 0.15f

        return when {
            handStrength >= HandRank.STRAIGHT.rank || canBluff -> {
                val potBet = (state.pot * 0.75f).toInt().coerceAtLeast(state.bigBlind * 2)
                if (callAmount == 0) Pair(PlayerAction.RAISE, potBet)
                else if (potOdds < 0.5f) Pair(PlayerAction.RAISE, potBet)
                else Pair(PlayerAction.ALL_IN, 0)
            }
            // Two pair / trips postflop, or a premium starting hand preflop: value-raise.
            handStrength >= HandRank.TWO_PAIR.rank -> {
                when {
                    callAmount == 0 -> {
                        val bet = (state.pot * 0.5f).toInt().coerceAtLeast(state.bigBlind * 2)
                        Pair(PlayerAction.RAISE, bet)
                    }
                    potOdds < 0.4f -> Pair(PlayerAction.RAISE, state.bigBlind * 2)
                    else -> Pair(PlayerAction.FOLD, 0)
                }
            }
            handStrength >= HandRank.ONE_PAIR.rank -> {
                if (callAmount == 0) Pair(PlayerAction.CHECK, 0)
                else if (potOdds < 0.2f) Pair(PlayerAction.CALL, 0)
                else Pair(PlayerAction.FOLD, 0)
            }
            else -> {
                if (callAmount == 0) Pair(PlayerAction.CHECK, 0)
                else if (state.phase == BettingRound.PREFLOP && callAmount <= state.bigBlind) Pair(PlayerAction.CALL, 0)
                else Pair(PlayerAction.FOLD, 0)
            }
        }
    }

    private fun estimatePreflop(hand: List<com.pokertrainer.data.model.Card>): Int {
        if (hand.size < 2) return 0
        val sorted = hand.sortedByDescending { it.rank.value }
        val isPair = sorted[0].rank == sorted[1].rank
        val highVal = sorted[0].rank.value
        val isSuited = sorted[0].suit == sorted[1].suit
        return when {
            isPair && highVal >= 10 -> HandRank.TWO_PAIR.rank
            isPair -> HandRank.ONE_PAIR.rank
            highVal >= 12 && isSuited -> HandRank.ONE_PAIR.rank
            highVal >= 12 -> HandRank.HIGH_CARD.rank + 1
            else -> HandRank.HIGH_CARD.rank
        }
    }
}
