// ABOUTME: Tests for Card domain model
// ABOUTME: Validates card properties, suits, ranks, and point values
package com.example.canasta.domain

import org.junit.Assert.*
import org.junit.Test

class CardTest {

    @Test
    fun jokerHas30Points() {
        val joker = Card.Joker
        assertEquals(30, joker.points)
    }

    @Test
    fun twoHas15Points() {
        val two = Card.Regular(Rank.TWO, Suit.HEARTS)
        assertEquals(15, two.points)
    }

    @Test
    fun aceHas15Points() {
        val ace = Card.Regular(Rank.ACE, Suit.SPADES)
        assertEquals(15, ace.points)
    }

    @Test
    fun eightToKingHave10Points() {
        val eight = Card.Regular(Rank.EIGHT, Suit.DIAMONDS)
        val king = Card.Regular(Rank.KING, Suit.CLUBS)
        assertEquals(10, eight.points)
        assertEquals(10, king.points)
    }

    @Test
    fun fourToSevenHave5Points() {
        val four = Card.Regular(Rank.FOUR, Suit.HEARTS)
        val seven = Card.Regular(Rank.SEVEN, Suit.SPADES)
        assertEquals(5, four.points)
        assertEquals(5, seven.points)
    }

    @Test
    fun threeHas5Points() {
        val three = Card.Regular(Rank.THREE, Suit.DIAMONDS)
        assertEquals(5, three.points)
    }
}
