package com.pokertrainer.game.engine

import com.pokertrainer.data.model.Card
import com.pokertrainer.data.model.Rank
import com.pokertrainer.data.model.Suit

class Deck {
    private val cards: ArrayDeque<Card> = ArrayDeque()

    init {
        reset()
    }

    fun reset() {
        cards.clear()
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                cards.add(Card(rank, suit))
            }
        }
        shuffle()
    }

    fun shuffle() {
        val list = cards.toMutableList()
        list.shuffle()
        cards.clear()
        cards.addAll(list)
    }

    fun deal(): Card = cards.removeFirst()

    fun deal(count: Int): List<Card> = (1..count).map { deal() }

    val remaining: Int get() = cards.size
}
