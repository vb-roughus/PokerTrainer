package com.pokertrainer.game.engine

import com.pokertrainer.data.model.BettingRound
import com.pokertrainer.data.model.Difficulty
import com.pokertrainer.data.model.GameState
import com.pokertrainer.data.model.Player
import com.pokertrainer.data.model.PlayerAction
import com.pokertrainer.data.model.activePlayers
import com.pokertrainer.game.ai.EasyAI
import com.pokertrainer.game.ai.HardAI
import com.pokertrainer.game.ai.MediumAI

class PokerEngine {
    private val deck = Deck()

    private val startingChips = 1000
    private val smallBlind = 10
    private val bigBlind = 20

    /** Starts a brand-new match: fresh players with full stacks. */
    fun newGame(difficulty: Difficulty): GameState {
        val human = Player(id = 0, name = "You", chips = startingChips, isHuman = true)
        val aiNames = listOf("Alice", "Bob", "Charlie")
        val aiPlayers = (1..2).map { i ->
            Player(id = i, name = aiNames[i - 1], chips = startingChips, isHuman = false)
        }
        return startHand(listOf(human) + aiPlayers, dealerIndex = 0, difficulty = difficulty)
    }

    /** Deals the next hand of an ongoing match, carrying over each player's chips. */
    fun nextHand(state: GameState): GameState {
        val newDealer = nextWithChips(state.players, state.dealerIndex)
        return startHand(state.players, if (newDealer >= 0) newDealer else state.dealerIndex, state.difficulty)
    }

    private fun startHand(prevPlayers: List<Player>, dealerIndex: Int, difficulty: Difficulty): GameState {
        deck.reset()
        // Reset every player for the new hand. Players without chips sit out (folded).
        val players = prevPlayers.map { p ->
            if (p.chips > 0) {
                p.copy(hand = deck.deal(2), currentBet = 0, hasFolded = false, isAllIn = false, lastAction = null)
            } else {
                p.copy(hand = emptyList(), currentBet = 0, hasFolded = true, isAllIn = false, lastAction = null)
            }
        }.toMutableList()

        val sbIndex = nextWithChips(players, dealerIndex)
        val bbIndex = nextWithChips(players, sbIndex.takeIf { it >= 0 } ?: dealerIndex)

        var pot = 0
        var currentBet = 0
        if (sbIndex >= 0) {
            val p = players[sbIndex]
            val amount = minOf(smallBlind, p.chips)
            players[sbIndex] = p.copy(chips = p.chips - amount, currentBet = amount, isAllIn = p.chips - amount == 0)
            pot += amount
            currentBet = maxOf(currentBet, amount)
        }
        if (bbIndex >= 0 && bbIndex != sbIndex) {
            val p = players[bbIndex]
            val amount = minOf(bigBlind, p.chips)
            players[bbIndex] = p.copy(chips = p.chips - amount, currentBet = amount, isAllIn = p.chips - amount == 0)
            pot += amount
            currentBet = maxOf(currentBet, amount)
        }

        val firstToAct = nextWithChips(players, bbIndex.takeIf { it >= 0 } ?: dealerIndex)

        val started = GameState(
            players = players,
            communityCards = emptyList(),
            pot = pot,
            currentBet = currentBet,
            phase = BettingRound.PREFLOP,
            activePlayerIndex = if (firstToAct >= 0) firstToAct else dealerIndex,
            dealerIndex = dealerIndex,
            difficulty = difficulty
        )
        // If the first player to act is an AI, let it act right away.
        return progressGame(started)
    }

    fun humanAction(state: GameState, action: PlayerAction, raiseAmount: Int = 0): GameState {
        val human = state.players.indexOfFirst { it.isHuman }
        if (human < 0 || state.activePlayerIndex != human) return state
        val updated = applyAction(state, human, action, raiseAmount)
        return progressGame(updated)
    }

    private fun applyAction(state: GameState, playerIndex: Int, action: PlayerAction, raiseAmount: Int): GameState {
        val player = state.players[playerIndex]
        val players = state.players.toMutableList()
        var pot = state.pot
        var currentBet = state.currentBet

        when (action) {
            PlayerAction.FOLD -> {
                players[playerIndex] = player.copy(hasFolded = true, lastAction = PlayerAction.FOLD)
            }
            PlayerAction.CHECK -> {
                players[playerIndex] = player.copy(lastAction = PlayerAction.CHECK)
            }
            PlayerAction.CALL -> {
                val toCall = (currentBet - player.currentBet).coerceAtLeast(0).coerceAtMost(player.chips)
                players[playerIndex] = player.copy(
                    chips = player.chips - toCall,
                    currentBet = player.currentBet + toCall,
                    lastAction = PlayerAction.CALL,
                    isAllIn = player.chips - toCall == 0
                )
                pot += toCall
            }
            PlayerAction.RAISE -> {
                val toCall = (currentBet - player.currentBet).coerceAtLeast(0)
                val total = toCall + raiseAmount.coerceAtLeast(state.bigBlind)
                val actual = total.coerceAtMost(player.chips)
                players[playerIndex] = player.copy(
                    chips = player.chips - actual,
                    currentBet = player.currentBet + actual,
                    lastAction = PlayerAction.RAISE,
                    isAllIn = player.chips - actual == 0
                )
                currentBet = players[playerIndex].currentBet
                pot += actual
            }
            PlayerAction.ALL_IN -> {
                pot += player.chips
                currentBet = maxOf(currentBet, player.currentBet + player.chips)
                players[playerIndex] = player.copy(
                    currentBet = player.currentBet + player.chips,
                    chips = 0,
                    lastAction = PlayerAction.ALL_IN,
                    isAllIn = true
                )
            }
        }

        val nextActive = nextActivePlayer(players, playerIndex)
        return state.copy(players = players, pot = pot, currentBet = currentBet, activePlayerIndex = nextActive)
    }

    private fun progressGame(state: GameState): GameState {
        // Check if only one player remains
        val active = state.activePlayers
        if (active.size == 1) {
            return showdown(state)
        }

        // Check if betting round is complete
        if (isBettingRoundComplete(state)) {
            return advancePhase(state)
        }

        // Let AI act if it's their turn
        val currentPlayer = state.players.getOrNull(state.activePlayerIndex)
        return if (currentPlayer != null && !currentPlayer.isHuman && !currentPlayer.hasFolded) {
            val aiAction = getAIAction(state, state.activePlayerIndex)
            val updated = applyAction(state, state.activePlayerIndex, aiAction.first, aiAction.second)
            progressGame(updated)
        } else {
            state
        }
    }

    private fun isBettingRoundComplete(state: GameState): Boolean {
        val active = state.players.filter { !it.hasFolded && !it.isAllIn }
        if (active.isEmpty()) return true
        val allMatched = active.all { it.currentBet == state.currentBet }
        val allActed = active.all { it.lastAction != null }
        return allMatched && allActed
    }

    private fun advancePhase(state: GameState): GameState {
        val resetPlayers = state.players.map { p ->
            p.copy(currentBet = 0, lastAction = null)
        }
        val base = state.copy(players = resetPlayers, currentBet = 0)
        val firstToAct = firstActiveAfterDealer(resetPlayers, base.dealerIndex)

        return when (state.phase) {
            BettingRound.PREFLOP -> {
                val flop = deck.deal(3)
                val next = base.copy(
                    communityCards = flop,
                    phase = BettingRound.FLOP,
                    activePlayerIndex = firstToAct
                )
                progressGame(next)
            }
            BettingRound.FLOP -> {
                val turn = deck.deal(1)
                val next = base.copy(
                    communityCards = base.communityCards + turn,
                    phase = BettingRound.TURN,
                    activePlayerIndex = firstToAct
                )
                progressGame(next)
            }
            BettingRound.TURN -> {
                val river = deck.deal(1)
                val next = base.copy(
                    communityCards = base.communityCards + river,
                    phase = BettingRound.RIVER,
                    activePlayerIndex = firstToAct
                )
                progressGame(next)
            }
            BettingRound.RIVER -> showdown(base)
            BettingRound.SHOWDOWN -> base
        }
    }

    private fun showdown(state: GameState): GameState {
        val active = state.players.filter { !it.hasFolded }
        val winner = active.maxByOrNull { player ->
            HandEvaluator.evaluate(player.hand + state.communityCards)
        } ?: return state

        val msg = if (active.size == 1) {
            // Alle anderen sind ausgestiegen – keine Wertigkeit nötig.
            if (winner.isHuman) "Du gewinnst den Pot! +${state.pot} Chips"
            else "${winner.name} gewinnt den Pot."
        } else {
            val winnerHand = HandEvaluator.evaluate(winner.hand + state.communityCards)
            if (winner.isHuman) "Du gewinnst mit ${winnerHand.handRank.displayName}! +${state.pot} Chips"
            else "${winner.name} gewinnt mit ${winnerHand.handRank.displayName}."
        }

        val updatedPlayers = state.players.map { p ->
            if (p.id == winner.id) p.copy(chips = p.chips + state.pot) else p
        }

        val human = updatedPlayers.firstOrNull { it.isHuman }
        val humanBroke = (human?.chips ?: 0) <= 0
        val opponentsBroke = updatedPlayers.filter { !it.isHuman }.all { it.chips <= 0 }
        val matchOver = humanBroke || opponentsBroke

        return state.copy(
            players = updatedPlayers,
            phase = BettingRound.SHOWDOWN,
            winnerMessage = msg,
            isHandOver = true,
            isGameOver = matchOver,
            pot = 0
        )
    }

    /** Next index after [from] whose player still has chips, or -1 if none. */
    private fun nextWithChips(players: List<Player>, from: Int): Int {
        val n = players.size
        if (n == 0) return -1
        var idx = ((from.coerceAtLeast(0)) + 1) % n
        repeat(n) {
            if (players[idx].chips > 0) return idx
            idx = (idx + 1) % n
        }
        return -1
    }

    private fun nextActivePlayer(players: List<Player>, current: Int): Int {
        var next = (current + 1) % players.size
        var count = 0
        while ((players[next].hasFolded || players[next].isAllIn) && count < players.size) {
            next = (next + 1) % players.size
            count++
        }
        return next
    }

    private fun firstActiveAfterDealer(players: List<Player>, dealerIndex: Int): Int {
        var idx = (dealerIndex + 1) % players.size
        repeat(players.size) {
            if (!players[idx].hasFolded && !players[idx].isAllIn) return idx
            idx = (idx + 1) % players.size
        }
        return dealerIndex
    }

    private fun getAIAction(state: GameState, playerIndex: Int): Pair<PlayerAction, Int> {
        return when (state.difficulty) {
            Difficulty.EASY -> EasyAI.decide(state, playerIndex)
            Difficulty.MEDIUM -> MediumAI.decide(state, playerIndex)
            Difficulty.HARD -> HardAI.decide(state, playerIndex)
        }
    }
}
