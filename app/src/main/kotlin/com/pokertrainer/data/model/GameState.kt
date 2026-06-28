package com.pokertrainer.data.model

enum class BettingRound {
    PREFLOP, FLOP, TURN, RIVER, SHOWDOWN
}

enum class Difficulty {
    EASY, MEDIUM, HARD
}

data class GameState(
    val players: List<Player> = emptyList(),
    val communityCards: List<Card> = emptyList(),
    val pot: Int = 0,
    val currentBet: Int = 0,
    val phase: BettingRound = BettingRound.PREFLOP,
    val activePlayerIndex: Int = 0,
    val dealerIndex: Int = 0,
    val smallBlind: Int = 10,
    val bigBlind: Int = 20,
    val winnerMessage: String? = null,
    val isHandOver: Boolean = false,   // eine Hand ist fertig (Showdown)
    val isGameOver: Boolean = false,   // das Match ist fertig (Spieler pleite oder alle Chips gewonnen)
    val showdownCards: List<Card> = emptyList(),  // wertbildende Karten der Gewinnerhand
    val winnerId: Int? = null,                    // Gewinner der letzten Hand (für Hervorhebung)
    val difficulty: Difficulty = Difficulty.EASY,
    val minRaise: Int = 20
)

val GameState.humanPlayer: Player? get() = players.firstOrNull { it.isHuman }
val GameState.activePlayer: Player? get() = players.getOrNull(activePlayerIndex)
val GameState.activePlayers: List<Player> get() = players.filter { !it.hasFolded }
val GameState.canCheck: Boolean get() = currentBet == 0 || (humanPlayer?.currentBet ?: 0) >= currentBet
val GameState.callAmount: Int get() {
    val human = humanPlayer ?: return 0
    return (currentBet - human.currentBet).coerceAtLeast(0)
}
