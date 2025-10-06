// ABOUTME: Tests for GameState management
// ABOUTME: Validates game initialization, turn management, and round progression
package com.example.canasta.domain

import org.junit.Assert.*
import org.junit.Test

class GameStateTest {

    @Test
    fun gameInitializesWith4Players() {
        val players = listOf(
            Player(1, "Player 1", emptyList()),
            Player(2, "Player 2", emptyList()),
            Player(3, "Player 3", emptyList()),
            Player(4, "Player 4", emptyList())
        )
        val gameState = GameState.create(players)

        assertEquals(4, gameState.players.size)
        assertEquals(2, gameState.teams.size)
    }

    @Test
    fun eachPlayerGets13Cards() {
        val players = listOf(
            Player(1, "Player 1", emptyList()),
            Player(2, "Player 2", emptyList()),
            Player(3, "Player 3", emptyList()),
            Player(4, "Player 4", emptyList())
        )
        val gameState = GameState.create(players).deal()

        gameState.players.forEach { player ->
            assertEquals(13, player.hand.size)
        }
    }

    @Test
    fun gameStartsAtRound1() {
        val players = listOf(
            Player(1, "Player 1", emptyList()),
            Player(2, "Player 2", emptyList()),
            Player(3, "Player 3", emptyList()),
            Player(4, "Player 4", emptyList())
        )
        val gameState = GameState.create(players)

        assertEquals(1, gameState.currentRound)
    }

    @Test
    fun turnProgressesAnticlockwise() {
        val players = listOf(
            Player(1, "Player 1", emptyList()),
            Player(2, "Player 2", emptyList()),
            Player(3, "Player 3", emptyList()),
            Player(4, "Player 4", emptyList())
        )
        val gameState = GameState.create(players)

        assertEquals(0, gameState.currentPlayerIndex)
        val next = gameState.nextTurn()
        assertEquals(3, next.currentPlayerIndex)
    }

    @Test
    fun discardPileStartsWithOneCard() {
        val players = listOf(
            Player(1, "Player 1", emptyList()),
            Player(2, "Player 2", emptyList()),
            Player(3, "Player 3", emptyList()),
            Player(4, "Player 4", emptyList())
        )
        val gameState = GameState.create(players).deal()

        assertEquals(1, gameState.discardPile.size)
    }

    @Test
    fun drawPileDecreasesAfterDeal() {
        val players = listOf(
            Player(1, "Player 1", emptyList()),
            Player(2, "Player 2", emptyList()),
            Player(3, "Player 3", emptyList()),
            Player(4, "Player 4", emptyList())
        )
        val gameState = GameState.create(players).deal()

        val cardsDealt = 4 * 13 + 1
        assertEquals(108 - cardsDealt, gameState.drawPile.cards.size)
    }
}
