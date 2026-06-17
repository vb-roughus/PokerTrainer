package com.pokertrainer.data

import com.pokertrainer.data.model.Card
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.data.model.Rank
import com.pokertrainer.data.model.Suit

data class Scenario(
    val id: Int,
    val holeCards: List<Card>,
    val communityCards: List<Card>,
    val pot: Int,
    val currentBet: Int,
    val playerChips: Int,
    val correctAction: PlayerAction,
    val winRate: Float,
    val titleEn: String,
    val titleDe: String,
    val explanationEn: String,
    val explanationDe: String
)

val scenarios = listOf(
    Scenario(
        id = 1,
        holeCards = listOf(Card(Rank.ACE, Suit.SPADES), Card(Rank.ACE, Suit.HEARTS)),
        communityCards = emptyList(),
        pot = 30, currentBet = 20, playerChips = 980,
        correctAction = PlayerAction.RAISE,
        winRate = 0.85f,
        titleEn = "Premium Pre-Flop: Pocket Aces",
        titleDe = "Premium Pre-Flop: Pocket Asse",
        explanationEn = "Pocket Aces is the strongest starting hand in Texas Hold'em. Always raise pre-flop to build the pot and narrow the field.",
        explanationDe = "Pocket Asse ist die stärkste Starthand in Texas Hold'em. Immer vorher raisen, um den Pot aufzubauen und das Feld zu reduzieren."
    ),
    Scenario(
        id = 2,
        holeCards = listOf(Card(Rank.TWO, Suit.CLUBS), Card(Rank.SEVEN, Suit.DIAMONDS)),
        communityCards = emptyList(),
        pot = 30, currentBet = 20, playerChips = 980,
        correctAction = PlayerAction.FOLD,
        winRate = 0.32f,
        titleEn = "Trash Hand: 2-7 Offsuit",
        titleDe = "Schlechte Hand: 2-7 Offsuit",
        explanationEn = "2-7 offsuit is statistically the worst starting hand. No pair potential, no straight draws, no flush draws. Fold and save your chips.",
        explanationDe = "2-7 Offsuit ist statistisch die schlechteste Starthand. Kein Paar-Potenzial, keine Straight- oder Flush-Draws. Folden und Chips sparen."
    ),
    Scenario(
        id = 3,
        holeCards = listOf(Card(Rank.KING, Suit.HEARTS), Card(Rank.QUEEN, Suit.HEARTS)),
        communityCards = listOf(Card(Rank.JACK, Suit.HEARTS), Card(Rank.TEN, Suit.HEARTS), Card(Rank.TWO, Suit.CLUBS)),
        pot = 100, currentBet = 0, playerChips = 900,
        correctAction = PlayerAction.RAISE,
        winRate = 0.78f,
        titleEn = "Royal Flush Draw on the Flop",
        titleDe = "Royal Flush Draw auf dem Flop",
        explanationEn = "You have a Royal Flush draw! Any heart gives you a flush, any Ace completes your straight. This is an extremely strong semi-bluff opportunity. Raise!",
        explanationDe = "Du hast einen Royal Flush Draw! Jedes Herz gibt dir einen Flush, jeder Ass vervollständigt deinen Straight. Eine sehr starke Gelegenheit zum Raisen!"
    ),
    Scenario(
        id = 4,
        holeCards = listOf(Card(Rank.JACK, Suit.SPADES), Card(Rank.TEN, Suit.CLUBS)),
        communityCards = listOf(Card(Rank.JACK, Suit.HEARTS), Card(Rank.JACK, Suit.DIAMONDS), Card(Rank.FIVE, Suit.CLUBS)),
        pot = 60, currentBet = 0, playerChips = 940,
        correctAction = PlayerAction.RAISE,
        winRate = 0.92f,
        titleEn = "Flopped Three Jacks!",
        titleDe = "Drei Buben auf dem Flop!",
        explanationEn = "You have Three of a Kind (Jacks)! This is a very strong hand. Bet to build the pot — don't slow-play too much or opponents might get free cards.",
        explanationDe = "Du hast einen Drilling (Buben)! Das ist eine sehr starke Hand. Setze, um den Pot aufzubauen — vermeide zu viel Slow-Playing."
    ),
    Scenario(
        id = 5,
        holeCards = listOf(Card(Rank.ACE, Suit.CLUBS), Card(Rank.KING, Suit.DIAMONDS)),
        communityCards = listOf(Card(Rank.TWO, Suit.HEARTS), Card(Rank.SEVEN, Suit.CLUBS), Card(Rank.NINE, Suit.SPADES)),
        pot = 80, currentBet = 60, playerChips = 920,
        correctAction = PlayerAction.FOLD,
        winRate = 0.38f,
        titleEn = "Ace-King Missed the Flop",
        titleDe = "Ass-König hat den Flop verfehlt",
        explanationEn = "AK is strong pre-flop but missed this board completely. Facing a large bet with just Ace-high, the pot odds don't justify calling. Fold and wait for a better spot.",
        explanationDe = "AK ist stark vor dem Flop, hat dieses Board aber komplett verfehlt. Mit nur Ass-High und einem großen Bet stimmen die Pot Odds nicht. Folden."
    ),
    Scenario(
        id = 6,
        holeCards = listOf(Card(Rank.EIGHT, Suit.HEARTS), Card(Rank.NINE, Suit.HEARTS)),
        communityCards = listOf(Card(Rank.SIX, Suit.HEARTS), Card(Rank.SEVEN, Suit.HEARTS), Card(Rank.KING, Suit.CLUBS)),
        pot = 120, currentBet = 40, playerChips = 860,
        correctAction = PlayerAction.CALL,
        winRate = 0.68f,
        titleEn = "Open-Ended Straight Flush Draw",
        titleDe = "Open-Ended Straight Flush Draw",
        explanationEn = "You have both a straight draw (5 or 10 completes it) and a flush draw (any heart). With ~15 outs, your equity is very high. Call or even raise!",
        explanationDe = "Du hast sowohl einen Straight-Draw (5 oder 10 vervollständigt ihn) als auch einen Flush-Draw (jedes Herz). Mit ~15 Outs ist deine Equity sehr hoch. Callen!"
    ),
    Scenario(
        id = 7,
        holeCards = listOf(Card(Rank.QUEEN, Suit.SPADES), Card(Rank.QUEEN, Suit.CLUBS)),
        communityCards = listOf(Card(Rank.ACE, Suit.HEARTS), Card(Rank.KING, Suit.DIAMONDS), Card(Rank.JACK, Suit.CLUBS), Card(Rank.TEN, Suit.SPADES)),
        pot = 200, currentBet = 150, playerChips = 800,
        correctAction = PlayerAction.FOLD,
        winRate = 0.25f,
        titleEn = "Overpair vs. Scary Board",
        titleDe = "Overpair gegen ein gefährliches Board",
        explanationEn = "You have a pair of Queens but the board has A-K-J-10. Anyone with an Ace has a straight. With a large bet on a dangerous board, fold is the prudent choice.",
        explanationDe = "Du hast ein Paar Damen, aber das Board hat A-K-B-10. Jeder mit einem Ass hat einen Straight. Bei einem großen Bet auf einem gefährlichen Board ist Folden richtig."
    ),
    Scenario(
        id = 8,
        holeCards = listOf(Card(Rank.FIVE, Suit.HEARTS), Card(Rank.FIVE, Suit.DIAMONDS)),
        communityCards = listOf(Card(Rank.FIVE, Suit.CLUBS), Card(Rank.ACE, Suit.SPADES), Card(Rank.KING, Suit.HEARTS), Card(Rank.TWO, Suit.CLUBS), Card(Rank.NINE, Suit.HEARTS)),
        pot = 300, currentBet = 0, playerChips = 700,
        correctAction = PlayerAction.RAISE,
        winRate = 0.97f,
        titleEn = "Three of a Kind: Slow-Play or Bet?",
        titleDe = "Drilling: Langsam spielen oder Betten?",
        explanationEn = "You have Three Fives! On the river with no more cards to come, bet big for value. Your opponents may have hit strong second-best hands with this board.",
        explanationDe = "Du hast drei Fünfen! Auf dem River mit keinen weiteren Karten, groß betten für Value. Deine Gegner könnten starke Zweitbeste-Hände haben."
    ),
    Scenario(
        id = 9,
        holeCards = listOf(Card(Rank.ACE, Suit.SPADES), Card(Rank.TWO, Suit.SPADES)),
        communityCards = listOf(Card(Rank.KING, Suit.SPADES), Card(Rank.SEVEN, Suit.SPADES), Card(Rank.THREE, Suit.SPADES)),
        pot = 150, currentBet = 0, playerChips = 850,
        correctAction = PlayerAction.RAISE,
        winRate = 0.82f,
        titleEn = "Nut Flush Draw",
        titleDe = "Nut Flush Draw",
        explanationEn = "You have the Ace-high flush draw — the best possible flush if it hits. With the Ace of spades, you'd make the Nut Flush. Raise to build the pot!",
        explanationDe = "Du hast den Ass-hohen Flush-Draw — den bestmöglichen Flush wenn er trifft. Mit dem Ass der Piken machst du den Nut Flush. Raisen um den Pot aufzubauen!"
    ),
    Scenario(
        id = 10,
        holeCards = listOf(Card(Rank.THREE, Suit.CLUBS), Card(Rank.FOUR, Suit.DIAMONDS)),
        communityCards = listOf(Card(Rank.ACE, Suit.SPADES), Card(Rank.ACE, Suit.HEARTS), Card(Rank.ACE, Suit.DIAMONDS)),
        pot = 60, currentBet = 40, playerChips = 940,
        correctAction = PlayerAction.FOLD,
        winRate = 0.15f,
        titleEn = "Three Aces on the Board",
        titleDe = "Drei Asse auf dem Board",
        explanationEn = "With three Aces on the board, anyone with a fourth Ace has Four of a Kind. Your 3-4 is useless here. The pot odds are terrible. Fold immediately.",
        explanationDe = "Mit drei Assen auf dem Board hat jeder mit dem vierten Ass einen Vierling. Deine 3-4 ist hier wertlos. Die Pot Odds sind schlecht. Sofort folden."
    )
)
