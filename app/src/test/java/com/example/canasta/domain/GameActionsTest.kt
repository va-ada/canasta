// ABOUTME: Tests for game actions (draw, meld, discard, show)
// ABOUTME: Validates core gameplay mechanics and turn flow
package com.example.canasta.domain

import org.junit.Assert.*
import org.junit.Test

class GameActionsTest {

    private fun createTestGame(): GameState {
        val players = listOf(
            Player(1, "Player 1", emptyList()),
            Player(2, "Player 2", emptyList()),
            Player(3, "Player 3", emptyList()),
            Player(4, "Player 4", emptyList())
        )
        return GameState.create(players).deal()
    }

    @Test
    fun drawFromDrawPile() {
        val game = createTestGame()
        val initialDrawPileSize = game.drawPile.cards.size
        val initialHandSize = game.currentPlayer().hand.size

        val result = GameActions.drawFromPile(game)

        assertTrue(result is ActionResult.Success)
        val updatedGame = (result as ActionResult.Success).gameState
        assertEquals(initialDrawPileSize - 1, updatedGame.drawPile.cards.size)
        assertEquals(initialHandSize + 1, updatedGame.currentPlayer().hand.size)
    }

    @Test
    fun takeDiscardPile() {
        val game = createTestGame()
        val initialDiscardSize = game.discardPile.size
        val initialHandSize = game.currentPlayer().hand.size

        val result = GameActions.takeDiscardPile(game)

        assertTrue(result is ActionResult.Success)
        val updatedGame = (result as ActionResult.Success).gameState
        assertEquals(0, updatedGame.discardPile.size)
        assertEquals(initialHandSize + initialDiscardSize, updatedGame.currentPlayer().hand.size)
    }

    @Test
    fun discardCard() {
        val game = createTestGame()
        val player = game.currentPlayer()
        val cardToDiscard = player.hand.first()

        val result = GameActions.discard(game, cardToDiscard)

        assertTrue(result is ActionResult.Success)
        val updatedGame = (result as ActionResult.Success).gameState
        assertTrue(updatedGame.discardPile.contains(cardToDiscard))
        assertFalse(updatedGame.currentPlayer().hand.contains(cardToDiscard))
    }

    @Test
    fun cannotDiscardCardNotInHand() {
        val game = createTestGame()
        val cardNotInHand = Card.Joker

        val result = GameActions.discard(game, cardNotInHand)

        assertTrue(result is ActionResult.Failure)
    }

    @Test
    fun createValidMeld() {
        val cards = List(7) { Card.Regular(Rank.KING, Suit.values()[it % 4]) }
        val player = Player(1, "Player 1", cards)
        val players = listOf(player, Player(2, "P2", emptyList()), Player(3, "P3", emptyList()), Player(4, "P4", emptyList()))
        val game = GameState.create(players)

        val result = GameActions.createMeld(game, cards)

        assertTrue(result is ActionResult.Success)
        val updatedGame = (result as ActionResult.Success).gameState
        assertEquals(1, updatedGame.currentPlayer().melds.size)
    }

    @Test
    fun cannotCreateInvalidMeld() {
        val cards = listOf(
            Card.Regular(Rank.FIVE, Suit.HEARTS),
            Card.Regular(Rank.SIX, Suit.HEARTS)
        )
        val player = Player(1, "Player 1", cards)
        val players = listOf(player, Player(2, "P2", emptyList()), Player(3, "P3", emptyList()), Player(4, "P4", emptyList()))
        val game = GameState.create(players)

        val result = GameActions.createMeld(game, cards)

        assertTrue(result is ActionResult.Failure)
    }
}
