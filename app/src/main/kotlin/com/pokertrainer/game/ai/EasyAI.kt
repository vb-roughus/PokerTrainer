package com.pokertrainer.game.ai

import com.pokertrainer.data.model.BettingRound
import com.pokertrainer.data.model.GameState
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.data.model.Rank
import com.pokertrainer.game.engine.HandEvaluator
import com.pokertrainer.game.engine.HandRank

object EasyAI {
    fun decide(state: GameState, playerIndex: Int): Pair<PlayerAction, Int> {
        val player = state.players[playerIndex]
        val allCards = player.hand + state.communityCards
        val handResult = if (allCards.size >= 5) HandEvaluator.evaluate(allCards) else null
        val handStrength = handResult?.handRank?.rank ?: estimatePreflopStrength(player.hand)

        val callAmount = (state.currentBet - player.currentBet).coerceAtLeast(0)

        return when {
            // Strong hand: bet or call
            handStrength >= HandRank.TWO_PAIR.rank -> {
                if (callAmount == 0) Pair(PlayerAction.CHECK, 0)
                else if (callAmount <= player.chips / 3) Pair(PlayerAction.CALL, 0)
                else Pair(PlayerAction.FOLD, 0)
            }
            // Medium hand: call small bets
            handStrength >= HandRank.ONE_PAIR.rank -> {
                if (callAmount == 0) Pair(PlayerAction.CHECK, 0)
                else if (callAmount <= state.bigBlind * 2) Pair(PlayerAction.CALL, 0)
                else Pair(PlayerAction.FOLD, 0)
            }
            // Weak hand: check, limp cheaply preflop, otherwise fold
            else -> {
                if (callAmount == 0) Pair(PlayerAction.CHECK, 0)
                else if (state.phase == BettingRound.PREFLOP && callAmount <= state.bigBlind) Pair(PlayerAction.CALL, 0)
                else Pair(PlayerAction.FOLD, 0)
            }
        }
    }

    private fun estimatePreflopStrength(hand: List<com.pokertrainer.data.model.Card>): Int {
        if (hand.size < 2) return 0
        val (a, b) = hand.sortedByDescending { it.rank.value }
        val isPair = a.rank == b.rank
        val isPremium = a.rank.value >= Rank.JACK.value && b.rank.value >= Rank.TEN.value
        return when {
            isPair && a.rank.value >= Rank.JACK.value -> HandRank.TWO_PAIR.rank
            isPremium -> HandRank.ONE_PAIR.rank
            isPair -> HandRank.ONE_PAIR.rank
            else -> HandRank.HIGH_CARD.rank
        }
    }
}
