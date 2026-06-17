package com.pokertrainer.game.engine

import com.pokertrainer.data.model.Card
import com.pokertrainer.data.model.Rank

enum class HandRank(val displayName: String, val rank: Int) {
    HIGH_CARD("High Card", 1),
    ONE_PAIR("One Pair", 2),
    TWO_PAIR("Two Pair", 3),
    THREE_OF_A_KIND("Three of a Kind", 4),
    STRAIGHT("Straight", 5),
    FLUSH("Flush", 6),
    FULL_HOUSE("Full House", 7),
    FOUR_OF_A_KIND("Four of a Kind", 8),
    STRAIGHT_FLUSH("Straight Flush", 9),
    ROYAL_FLUSH("Royal Flush", 10)
}

data class HandResult(
    val handRank: HandRank,
    val bestCards: List<Card>,
    val tiebreakers: List<Int>
) : Comparable<HandResult> {
    override fun compareTo(other: HandResult): Int {
        if (handRank.rank != other.handRank.rank) return handRank.rank - other.handRank.rank
        for (i in tiebreakers.indices) {
            val cmp = (tiebreakers.getOrElse(i) { 0 }) - (other.tiebreakers.getOrElse(i) { 0 })
            if (cmp != 0) return cmp
        }
        return 0
    }
}

object HandEvaluator {
    fun evaluate(cards: List<Card>): HandResult {
        val best = cards.combinations(5).map { evaluate5(it) }.maxOrNull()
        return best ?: evaluate5(cards.take(5))
    }

    private fun evaluate5(cards: List<Card>): HandResult {
        val sorted = cards.sortedByDescending { it.rank.value }
        val isFlush = cards.map { it.suit }.toSet().size == 1
        val values = sorted.map { it.rank.value }
        val isStraight = isStraight(values)
        val groupedByRank = cards.groupBy { it.rank }
        val counts = groupedByRank.values.map { it.size }.sortedDescending()

        return when {
            isFlush && isStraight && values.first() == Rank.ACE.value && values[1] == Rank.KING.value ->
                HandResult(HandRank.ROYAL_FLUSH, sorted, values)
            isFlush && isStraight ->
                HandResult(HandRank.STRAIGHT_FLUSH, sorted, values)
            counts[0] == 4 -> {
                val quadRank = groupedByRank.entries.first { it.value.size == 4 }.key.value
                HandResult(HandRank.FOUR_OF_A_KIND, sorted, listOf(quadRank) + values.filter { it != quadRank })
            }
            counts[0] == 3 && counts[1] == 2 -> {
                val tripleRank = groupedByRank.entries.first { it.value.size == 3 }.key.value
                val pairRank = groupedByRank.entries.first { it.value.size == 2 }.key.value
                HandResult(HandRank.FULL_HOUSE, sorted, listOf(tripleRank, pairRank))
            }
            isFlush ->
                HandResult(HandRank.FLUSH, sorted, values)
            isStraight ->
                HandResult(HandRank.STRAIGHT, sorted, values)
            counts[0] == 3 -> {
                val tripleRank = groupedByRank.entries.first { it.value.size == 3 }.key.value
                HandResult(HandRank.THREE_OF_A_KIND, sorted, listOf(tripleRank) + values.filter { it != tripleRank })
            }
            counts[0] == 2 && counts[1] == 2 -> {
                val pairs = groupedByRank.entries.filter { it.value.size == 2 }.map { it.key.value }.sortedDescending()
                val kicker = values.first { it != pairs[0] && it != pairs[1] }
                HandResult(HandRank.TWO_PAIR, sorted, pairs + listOf(kicker))
            }
            counts[0] == 2 -> {
                val pairRank = groupedByRank.entries.first { it.value.size == 2 }.key.value
                HandResult(HandRank.ONE_PAIR, sorted, listOf(pairRank) + values.filter { it != pairRank })
            }
            else -> HandResult(HandRank.HIGH_CARD, sorted, values)
        }
    }

    private fun isStraight(values: List<Int>): Boolean {
        val sorted = values.sortedDescending()
        // Normal straight
        if (sorted.zipWithNext().all { (a, b) -> a - b == 1 }) return true
        // Ace-low straight (A-2-3-4-5)
        val aceLow = listOf(14, 5, 4, 3, 2)
        return sorted == aceLow
    }

    fun winProbabilityApprox(holeCards: List<Card>, communityCards: List<Card>): Float {
        var wins = 0
        val trials = 500
        val deck = com.pokertrainer.game.engine.Deck()
        val usedCards = (holeCards + communityCards).toSet()
        repeat(trials) {
            deck.reset()
            val available = (holeCards + communityCards + deck.deal(7 - holeCards.size - communityCards.size))
                .filter { it !in usedCards || it in holeCards || it in communityCards }
            val opponentHole = deck.deal(2)
            val myResult = evaluate(holeCards + communityCards + deck.deal((5 - communityCards.size).coerceAtLeast(0)))
            val opponentResult = evaluate(opponentHole + communityCards + deck.deal((5 - communityCards.size).coerceAtLeast(0)))
            if (myResult >= opponentResult) wins++
            deck.reset()
        }
        return wins.toFloat() / trials
    }
}

private fun <T> List<T>.combinations(k: Int): List<List<T>> {
    if (k == 0) return listOf(emptyList())
    if (isEmpty() || k > size) return emptyList()
    val head = first()
    val tail = drop(1)
    val withHead = tail.combinations(k - 1).map { listOf(head) + it }
    val withoutHead = tail.combinations(k)
    return withHead + withoutHead
}
