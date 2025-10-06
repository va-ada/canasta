// ABOUTME: Tests for Player domain model
// ABOUTME: Validates player hand management and meld operations
package com.example.canasta.domain

import org.junit.Assert.*
import org.junit.Test

class PlayerTest {

    @Test
    fun playerStartsWith13Cards() {
        val cards = List(13) { Card.Regular(Rank.FIVE, Suit.HEARTS) }
        val player = Player(id = 1, name = "Player 1", hand = cards)

        assertEquals(13, player.hand.size)
    }

    @Test
    fun addCardToHand() {
        val player = Player(id = 1, name = "Player 1", hand = emptyList())
        val card = Card.Regular(Rank.ACE, Suit.SPADES)
        val updatedPlayer = player.addCard(card)

        assertEquals(1, updatedPlayer.hand.size)
        assertEquals(card, updatedPlayer.hand.first())
    }

    @Test
    fun removeCardFromHand() {
        val card = Card.Regular(Rank.ACE, Suit.SPADES)
        val player = Player(id = 1, name = "Player 1", hand = listOf(card))
        val updatedPlayer = player.removeCard(card)

        assertEquals(0, updatedPlayer.hand.size)
    }

    @Test
    fun playerCanCreateMeld() {
        val cards = List(7) { Card.Regular(Rank.KING, Suit.values()[it % 4]) }
        val player = Player(id = 1, name = "Player 1", hand = cards, melds = emptyList())

        val meld = cards.take(7)
        val updatedPlayer = player.addMeld(meld)

        assertEquals(1, updatedPlayer.melds.size)
        assertEquals(7, updatedPlayer.melds.first().size)
    }
}
