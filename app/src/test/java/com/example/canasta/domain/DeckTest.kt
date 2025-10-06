// ABOUTME: Tests for Deck domain model
// ABOUTME: Validates deck creation with 2 standard decks (108 cards including jokers)
package com.example.canasta.domain

import org.junit.Assert.*
import org.junit.Test

class DeckTest {

    @Test
    fun deckHas108Cards() {
        val deck = Deck.create()
        assertEquals(108, deck.cards.size)
    }

    @Test
    fun deckHas4Jokers() {
        val deck = Deck.create()
        val jokerCount = deck.cards.count { it is Card.Joker }
        assertEquals(4, jokerCount)
    }

    @Test
    fun deckHas8OfEachRank() {
        val deck = Deck.create()
        Rank.entries.forEach { rank ->
            val count = deck.cards.count {
                it is Card.Regular && it.rank == rank
            }
            assertEquals(8, count)
        }
    }

    @Test
    fun shuffleChangesDeckOrder() {
        val deck1 = Deck.create()
        val deck2 = Deck.create()
        val shuffled = deck1.shuffle()

        assertNotEquals(deck1.cards, shuffled.cards)
        assertEquals(deck1.cards.size, shuffled.cards.size)
    }

    @Test
    fun drawRemovesCardFromDeck() {
        val deck = Deck.create()
        val (card, remaining) = deck.draw()

        assertNotNull(card)
        assertEquals(107, remaining.cards.size)
    }

    @Test
    fun drawFromEmptyDeckReturnsNull() {
        val emptyDeck = Deck(emptyList())
        val (card, _) = emptyDeck.draw()

        assertNull(card)
    }
}
