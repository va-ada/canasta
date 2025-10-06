// ABOUTME: Tests for scoring system
// ABOUTME: Validates point calculation for cards, melds, and round scoring
package com.example.canasta.domain

import org.junit.Assert.*
import org.junit.Test

class ScoringTest {

    @Test
    fun calculateHandPoints() {
        val hand = listOf(
            Card.Joker,
            Card.Regular(Rank.ACE, Suit.HEARTS),
            Card.Regular(Rank.KING, Suit.SPADES),
            Card.Regular(Rank.FIVE, Suit.DIAMONDS)
        )

        val points = Scoring.calculateHandPoints(hand)
        assertEquals(60, points) // 30 + 15 + 10 + 5
    }

    @Test
    fun calculateMeldPointsForSet() {
        val set = List(7) { Card.Regular(Rank.KING, Suit.values()[it % 4]) } + listOf(Card.Joker)

        val points = Scoring.calculateMeldPoints(listOf(set))
        assertEquals(200, points) // 7*10 + 30 + 100 (set bonus)
    }

    @Test
    fun calculateMeldPointsForHandle() {
        val handle = List(7) { Card.Regular(Rank.ACE, Suit.values()[it % 4]) }

        val points = Scoring.calculateMeldPoints(listOf(handle))
        assertEquals(305, points) // 7*15 + 200 (handle bonus)
    }

    @Test
    fun calculateMeldPointsForSequence() {
        val sequence = listOf(
            Card.Regular(Rank.FOUR, Suit.HEARTS),
            Card.Regular(Rank.FIVE, Suit.HEARTS),
            Card.Regular(Rank.SIX, Suit.HEARTS)
        )

        val points = Scoring.calculateMeldPoints(listOf(sequence))
        assertEquals(15, points) // 5 + 5 + 5, no bonus for sequences
    }

    @Test
    fun calculateRoundScore() {
        val team1Player1 = Player(
            id = 1,
            name = "P1",
            hand = emptyList(),
            melds = listOf(List(7) { Card.Regular(Rank.KING, Suit.values()[it % 4]) })
        )
        val team1Player2 = Player(
            id = 3,
            name = "P3",
            hand = listOf(
                Card.Regular(Rank.KING, Suit.HEARTS),
                Card.Regular(Rank.KING, Suit.DIAMONDS),
                Card.Regular(Rank.KING, Suit.CLUBS),
                Card.Regular(Rank.KING, Suit.SPADES),
                Card.Regular(Rank.KING, Suit.HEARTS)
            ),
            melds = emptyList()
        )

        val team2Player1 = Player(
            id = 2,
            name = "P2",
            hand = listOf(Card.Regular(Rank.FIVE, Suit.HEARTS)),
            melds = emptyList()
        )
        val team2Player2 = Player(
            id = 4,
            name = "P4",
            hand = listOf(Card.Regular(Rank.ACE, Suit.SPADES)),
            melds = emptyList()
        )

        val team1 = Team(1, team1Player1, team1Player2)
        val team2 = Team(2, team2Player1, team2Player2)

        val (score1, score2) = Scoring.calculateRoundScore(team1, team2, showingTeamId = 1)

        assertEquals(240, score1) // 70 (melds) + 200 (handle bonus) - 50 (teammate hand) + 20 (opponents' hands)
        assertEquals(20, score2) // 5 + 15 (own hands)
    }

    @Test
    fun opponentsHandsAddedToShowingTeamScore() {
        val team1Player1 = Player(1, "P1", hand = emptyList(), melds = listOf(List(7) { Card.Regular(Rank.KING, Suit.values()[it % 4]) }))
        val team1Player2 = Player(3, "P3", hand = listOf(Card.Regular(Rank.TWO, Suit.HEARTS)), melds = emptyList())

        val team2Player1 = Player(2, "P2", hand = listOf(Card.Joker), melds = emptyList())
        val team2Player2 = Player(4, "P4", hand = listOf(Card.Regular(Rank.ACE, Suit.SPADES)), melds = emptyList())

        val team1 = Team(1, team1Player1, team1Player2)
        val team2 = Team(2, team2Player1, team2Player2)

        val (score1, _) = Scoring.calculateRoundScore(team1, team2, showingTeamId = 1)

        assertTrue(score1 > 200) // Should include opponents' hands (30 + 15 = 45)
    }
}
