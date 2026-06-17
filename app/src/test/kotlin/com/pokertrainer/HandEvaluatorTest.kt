package com.pokertrainer

import com.pokertrainer.data.model.Card
import com.pokertrainer.data.model.Rank
import com.pokertrainer.data.model.Suit
import com.pokertrainer.game.engine.HandEvaluator
import com.pokertrainer.game.engine.HandRank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HandEvaluatorTest {

    private fun c(rank: Rank, suit: Suit) = Card(rank, suit)

    @Test
    fun royalFlush() {
        val cards = listOf(
            c(Rank.ACE, Suit.SPADES), c(Rank.KING, Suit.SPADES),
            c(Rank.QUEEN, Suit.SPADES), c(Rank.JACK, Suit.SPADES),
            c(Rank.TEN, Suit.SPADES)
        )
        assertEquals(HandRank.ROYAL_FLUSH, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun straightFlush() {
        val cards = listOf(
            c(Rank.NINE, Suit.HEARTS), c(Rank.EIGHT, Suit.HEARTS),
            c(Rank.SEVEN, Suit.HEARTS), c(Rank.SIX, Suit.HEARTS),
            c(Rank.FIVE, Suit.HEARTS)
        )
        assertEquals(HandRank.STRAIGHT_FLUSH, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun fourOfAKind() {
        val cards = listOf(
            c(Rank.ACE, Suit.SPADES), c(Rank.ACE, Suit.HEARTS),
            c(Rank.ACE, Suit.DIAMONDS), c(Rank.ACE, Suit.CLUBS),
            c(Rank.KING, Suit.SPADES)
        )
        assertEquals(HandRank.FOUR_OF_A_KIND, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun fullHouse() {
        val cards = listOf(
            c(Rank.KING, Suit.SPADES), c(Rank.KING, Suit.HEARTS),
            c(Rank.KING, Suit.DIAMONDS), c(Rank.QUEEN, Suit.CLUBS),
            c(Rank.QUEEN, Suit.HEARTS)
        )
        assertEquals(HandRank.FULL_HOUSE, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun flush() {
        val cards = listOf(
            c(Rank.ACE, Suit.HEARTS), c(Rank.JACK, Suit.HEARTS),
            c(Rank.NINE, Suit.HEARTS), c(Rank.SIX, Suit.HEARTS),
            c(Rank.TWO, Suit.HEARTS)
        )
        assertEquals(HandRank.FLUSH, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun straight() {
        val cards = listOf(
            c(Rank.TEN, Suit.SPADES), c(Rank.NINE, Suit.HEARTS),
            c(Rank.EIGHT, Suit.DIAMONDS), c(Rank.SEVEN, Suit.CLUBS),
            c(Rank.SIX, Suit.SPADES)
        )
        assertEquals(HandRank.STRAIGHT, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun aceLowStraight() {
        val cards = listOf(
            c(Rank.ACE, Suit.SPADES), c(Rank.TWO, Suit.HEARTS),
            c(Rank.THREE, Suit.DIAMONDS), c(Rank.FOUR, Suit.CLUBS),
            c(Rank.FIVE, Suit.SPADES)
        )
        assertEquals(HandRank.STRAIGHT, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun threeOfAKind() {
        val cards = listOf(
            c(Rank.QUEEN, Suit.SPADES), c(Rank.QUEEN, Suit.HEARTS),
            c(Rank.QUEEN, Suit.DIAMONDS), c(Rank.SEVEN, Suit.CLUBS),
            c(Rank.TWO, Suit.SPADES)
        )
        assertEquals(HandRank.THREE_OF_A_KIND, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun twoPair() {
        val cards = listOf(
            c(Rank.ACE, Suit.SPADES), c(Rank.ACE, Suit.HEARTS),
            c(Rank.KING, Suit.DIAMONDS), c(Rank.KING, Suit.CLUBS),
            c(Rank.SEVEN, Suit.SPADES)
        )
        assertEquals(HandRank.TWO_PAIR, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun onePair() {
        val cards = listOf(
            c(Rank.JACK, Suit.SPADES), c(Rank.JACK, Suit.HEARTS),
            c(Rank.ACE, Suit.DIAMONDS), c(Rank.SEVEN, Suit.CLUBS),
            c(Rank.TWO, Suit.SPADES)
        )
        assertEquals(HandRank.ONE_PAIR, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun highCard() {
        val cards = listOf(
            c(Rank.ACE, Suit.SPADES), c(Rank.JACK, Suit.HEARTS),
            c(Rank.NINE, Suit.DIAMONDS), c(Rank.FIVE, Suit.CLUBS),
            c(Rank.TWO, Suit.SPADES)
        )
        assertEquals(HandRank.HIGH_CARD, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun bestHandFrom7Cards() {
        val cards = listOf(
            c(Rank.ACE, Suit.SPADES), c(Rank.KING, Suit.SPADES),
            c(Rank.QUEEN, Suit.SPADES), c(Rank.JACK, Suit.SPADES),
            c(Rank.TEN, Suit.SPADES), c(Rank.TWO, Suit.HEARTS),
            c(Rank.THREE, Suit.CLUBS)
        )
        assertEquals(HandRank.ROYAL_FLUSH, HandEvaluator.evaluate(cards).handRank)
    }

    @Test
    fun handComparison() {
        val flushCards = listOf(
            c(Rank.ACE, Suit.HEARTS), c(Rank.JACK, Suit.HEARTS),
            c(Rank.NINE, Suit.HEARTS), c(Rank.SIX, Suit.HEARTS),
            c(Rank.TWO, Suit.HEARTS)
        )
        val pairCards = listOf(
            c(Rank.ACE, Suit.SPADES), c(Rank.ACE, Suit.DIAMONDS),
            c(Rank.KING, Suit.CLUBS), c(Rank.QUEEN, Suit.HEARTS),
            c(Rank.JACK, Suit.SPADES)
        )
        val flush = HandEvaluator.evaluate(flushCards)
        val pair = HandEvaluator.evaluate(pairCards)
        assertTrue(flush > pair)
    }
}
