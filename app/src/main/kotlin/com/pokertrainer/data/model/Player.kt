package com.pokertrainer.data.model

data class Player(
    val id: Int,
    val name: String,
    val chips: Int,
    val hand: List<Card> = emptyList(),
    val isHuman: Boolean = false,
    val currentBet: Int = 0,
    val hasFolded: Boolean = false,
    val isAllIn: Boolean = false,
    val lastAction: PlayerAction? = null
)

enum class PlayerAction {
    FOLD, CHECK, CALL, RAISE, ALL_IN
}
