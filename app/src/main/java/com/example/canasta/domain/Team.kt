// ABOUTME: Team domain model representing a pair of players
// ABOUTME: Manages team members, gaddis (reserve piles), and team score
package com.example.canasta.domain

data class Team(
    val id: Int,
    val player1: Player,
    val player2: Player,
    val gaddis: List<List<Card>> = emptyList(),
    val gaddiUsed: Boolean = false,
    val score: Int = 0
) {
    fun useGaddi(index: Int): Team {
        require(index in gaddis.indices) { "Invalid gaddi index" }
        require(!gaddiUsed) { "Team has already used a gaddi this round" }
        return copy(gaddiUsed = true)
    }

    fun updatePlayer1(player: Player): Team = copy(player1 = player)

    fun updatePlayer2(player: Player): Team = copy(player2 = player)

    fun addScore(points: Int): Team = copy(score = score + points)
}
