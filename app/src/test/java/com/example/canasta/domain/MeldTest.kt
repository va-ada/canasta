// ABOUTME: Tests for Meld validation logic
// ABOUTME: Validates Sets, Handles, Pure/Impure Sequences according to game rules
package com.example.canasta.domain

import org.junit.Assert.*
import org.junit.Test

class MeldTest {

    @Test
    fun validSetWith7CardsAnd1Joker() {
        val cards = listOf(
            Card.Regular(Rank.FIVE, Suit.HEARTS),
            Card.Regular(Rank.FIVE, Suit.DIAMONDS),
            Card.Regular(Rank.FIVE, Suit.CLUBS),
            Card.Regular(Rank.FIVE, Suit.SPADES),
            Card.Regular(Rank.FIVE, Suit.HEARTS),
            Card.Regular(Rank.FIVE, Suit.DIAMONDS),
            Card.Joker
        )

        val result = Meld.validateSet(cards)
        assertTrue(result is MeldValidation.Valid)
        assertEquals(100, (result as MeldValidation.Valid).bonus)
    }

    @Test
    fun invalidSetWithMoreThan1Joker() {
        val cards = listOf(
            Card.Regular(Rank.FIVE, Suit.HEARTS),
            Card.Regular(Rank.FIVE, Suit.DIAMONDS),
            Card.Regular(Rank.FIVE, Suit.CLUBS),
            Card.Regular(Rank.FIVE, Suit.SPADES),
            Card.Regular(Rank.FIVE, Suit.HEARTS),
            Card.Joker,
            Card.Joker
        )

        val result = Meld.validateSet(cards)
        assertTrue(result is MeldValidation.Invalid)
    }

    @Test
    fun invalidSetWithLessThan7Cards() {
        val cards = listOf(
            Card.Regular(Rank.FIVE, Suit.HEARTS),
            Card.Regular(Rank.FIVE, Suit.DIAMONDS),
            Card.Regular(Rank.FIVE, Suit.CLUBS),
            Card.Regular(Rank.FIVE, Suit.SPADES),
            Card.Joker
        )

        val result = Meld.validateSet(cards)
        assertTrue(result is MeldValidation.Invalid)
    }

    @Test
    fun validHandleWith7PureCards() {
        val cards = listOf(
            Card.Regular(Rank.KING, Suit.HEARTS),
            Card.Regular(Rank.KING, Suit.DIAMONDS),
            Card.Regular(Rank.KING, Suit.CLUBS),
            Card.Regular(Rank.KING, Suit.SPADES),
            Card.Regular(Rank.KING, Suit.HEARTS),
            Card.Regular(Rank.KING, Suit.DIAMONDS),
            Card.Regular(Rank.KING, Suit.CLUBS)
        )

        val result = Meld.validateHandle(cards)
        assertTrue(result is MeldValidation.Valid)
        assertEquals(200, (result as MeldValidation.Valid).bonus)
    }

    @Test
    fun invalidHandleWithJoker() {
        val cards = listOf(
            Card.Regular(Rank.KING, Suit.HEARTS),
            Card.Regular(Rank.KING, Suit.DIAMONDS),
            Card.Regular(Rank.KING, Suit.CLUBS),
            Card.Regular(Rank.KING, Suit.SPADES),
            Card.Regular(Rank.KING, Suit.HEARTS),
            Card.Regular(Rank.KING, Suit.DIAMONDS),
            Card.Joker
        )

        val result = Meld.validateHandle(cards)
        assertTrue(result is MeldValidation.Invalid)
    }

    @Test
    fun validPureSequence() {
        val cards = listOf(
            Card.Regular(Rank.FOUR, Suit.HEARTS),
            Card.Regular(Rank.FIVE, Suit.HEARTS),
            Card.Regular(Rank.SIX, Suit.HEARTS)
        )

        val result = Meld.validateSequence(cards)
        assertTrue(result is MeldValidation.Valid)
    }

    @Test
    fun validImpureSequenceWithJoker() {
        val cards = listOf(
            Card.Regular(Rank.FOUR, Suit.HEARTS),
            Card.Joker,
            Card.Regular(Rank.SIX, Suit.HEARTS)
        )

        val result = Meld.validateSequence(cards)
        assertTrue(result is MeldValidation.Valid)
    }

    @Test
    fun validSequenceWithAceLow() {
        val cards = listOf(
            Card.Regular(Rank.ACE, Suit.DIAMONDS),
            Card.Regular(Rank.TWO, Suit.DIAMONDS),
            Card.Regular(Rank.THREE, Suit.DIAMONDS)
        )

        val result = Meld.validateSequence(cards)
        assertTrue(result is MeldValidation.Valid)
    }

    @Test
    fun validSequenceWithAceHigh() {
        val cards = listOf(
            Card.Regular(Rank.QUEEN, Suit.SPADES),
            Card.Regular(Rank.KING, Suit.SPADES),
            Card.Regular(Rank.ACE, Suit.SPADES)
        )

        val result = Meld.validateSequence(cards)
        assertTrue(result is MeldValidation.Valid)
    }

    @Test
    fun invalidSequenceWithMixedSuits() {
        val cards = listOf(
            Card.Regular(Rank.FOUR, Suit.HEARTS),
            Card.Regular(Rank.FIVE, Suit.DIAMONDS),
            Card.Regular(Rank.SIX, Suit.HEARTS)
        )

        val result = Meld.validateSequence(cards)
        assertTrue(result is MeldValidation.Invalid)
    }

    @Test
    fun invalidSequenceWithLessThan3Cards() {
        val cards = listOf(
            Card.Regular(Rank.FOUR, Suit.HEARTS),
            Card.Regular(Rank.FIVE, Suit.HEARTS)
        )

        val result = Meld.validateSequence(cards)
        assertTrue(result is MeldValidation.Invalid)
    }
}
