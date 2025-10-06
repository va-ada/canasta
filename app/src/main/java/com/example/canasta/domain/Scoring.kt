// ABOUTME: Scoring system for calculating points
// ABOUTME: Handles card values, meld bonuses, and round score calculation
package com.example.canasta.domain

object Scoring {

    fun calculateHandPoints(hand: List<Card>): Int {
        return hand.sumOf { it.points }
    }

    fun calculateMeldPoints(melds: List<List<Card>>): Int {
        return melds.sumOf { meld ->
            val cardPoints = meld.sumOf { it.points }
            val bonus = calculateMeldBonus(meld)
            cardPoints + bonus
        }
    }

    private fun calculateMeldBonus(meld: List<Card>): Int {
        if (meld.size < 7) return 0

        val jokerCount = meld.count { it is Card.Joker }
        val twoCount = meld.count { it is Card.Regular && it.rank == Rank.TWO }
        val totalWildCards = jokerCount + twoCount

        val regularCards = meld.filterIsInstance<Card.Regular>()
        val allSameRank = regularCards.map { it.rank }.distinct().size == 1

        return when {
            allSameRank && totalWildCards == 0 -> 200
            allSameRank && totalWildCards == 1 -> 100
            else -> 0
        }
    }

    fun calculateRoundScore(
        team1: Team,
        team2: Team,
        showingTeamId: Int
    ): Pair<Int, Int> {
        val (showingTeam, opponentTeam) = if (showingTeamId == team1.id) {
            team1 to team2
        } else {
            team2 to team1
        }

        val showingTeamMeldPoints = calculateMeldPoints(
            showingTeam.player1.melds + showingTeam.player2.melds
        )

        val showingTeamTeammateHandPoints = calculateHandPoints(
            if (showingTeam.player1.hand.isEmpty()) {
                showingTeam.player2.hand
            } else {
                showingTeam.player1.hand
            }
        )

        val opponentHandPoints = calculateHandPoints(
            opponentTeam.player1.hand + opponentTeam.player2.hand
        )

        val showingTeamScore = showingTeamMeldPoints - showingTeamTeammateHandPoints + opponentHandPoints

        val opponentMeldPoints = calculateMeldPoints(
            opponentTeam.player1.melds + opponentTeam.player2.melds
        )
        val opponentScore = opponentMeldPoints + calculateHandPoints(
            opponentTeam.player1.hand + opponentTeam.player2.hand
        )

        return if (showingTeamId == team1.id) {
            showingTeamScore to opponentScore
        } else {
            opponentScore to showingTeamScore
        }
    }
}
