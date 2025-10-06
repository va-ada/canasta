// ABOUTME: Tests for Team domain model
// ABOUTME: Validates team composition and gaddi management
package com.example.canasta.domain

import org.junit.Assert.*
import org.junit.Test

class TeamTest {

    @Test
    fun teamHasTwoPlayers() {
        val player1 = Player(id = 1, name = "Player 1", hand = emptyList())
        val player2 = Player(id = 2, name = "Player 2", hand = emptyList())
        val team = Team(id = 1, player1 = player1, player2 = player2)

        assertEquals(player1, team.player1)
        assertEquals(player2, team.player2)
    }

    @Test
    fun teamStartsWithTwoGaddis() {
        val player1 = Player(id = 1, name = "Player 1", hand = emptyList())
        val player2 = Player(id = 2, name = "Player 2", hand = emptyList())
        val gaddi1 = listOf(Card.Regular(Rank.ACE, Suit.HEARTS))
        val gaddi2 = listOf(Card.Regular(Rank.KING, Suit.SPADES))
        val team = Team(
            id = 1,
            player1 = player1,
            player2 = player2,
            gaddis = listOf(gaddi1, gaddi2)
        )

        assertEquals(2, team.gaddis.size)
    }

    @Test
    fun teamCanUseOneGaddi() {
        val player1 = Player(id = 1, name = "Player 1", hand = emptyList())
        val player2 = Player(id = 2, name = "Player 2", hand = emptyList())
        val gaddi1 = listOf(Card.Regular(Rank.ACE, Suit.HEARTS))
        val gaddi2 = listOf(Card.Regular(Rank.KING, Suit.SPADES))
        val team = Team(
            id = 1,
            player1 = player1,
            player2 = player2,
            gaddis = listOf(gaddi1, gaddi2),
            gaddiUsed = false
        )

        val updatedTeam = team.useGaddi(0)
        assertTrue(updatedTeam.gaddiUsed)
    }
}
