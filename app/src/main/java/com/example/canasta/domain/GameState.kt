// ABOUTME: GameState manages the overall game flow and state
// ABOUTME: Handles dealing, turn progression, draw/discard piles, and round management
package com.example.canasta.domain

data class GameState(
    val players: List<Player>,
    val teams: List<Team>,
    val drawPile: Deck,
    val discardPile: List<Card>,
    val currentPlayerIndex: Int,
    val currentRound: Int,
    val roundScores: List<Pair<Int, Int>> = emptyList()
) {

    fun deal(): GameState {
        var deck = drawPile.shuffle()
        val updatedPlayers = mutableListOf<Player>()

        players.forEach { player ->
            val cards = mutableListOf<Card>()
            repeat(13) {
                val (card, remaining) = deck.draw()
                card?.let { cards.add(it) }
                deck = remaining
            }
            updatedPlayers.add(player.copy(hand = cards))
        }

        val (firstDiscard, remainingDeck) = deck.draw()
        val initialDiscardPile = firstDiscard?.let { listOf(it) } ?: emptyList()

        return copy(
            players = updatedPlayers,
            drawPile = remainingDeck,
            discardPile = initialDiscardPile
        )
    }

    fun nextTurn(): GameState {
        val nextIndex = if (currentPlayerIndex == 0) {
            players.size - 1
        } else {
            currentPlayerIndex - 1
        }
        return copy(currentPlayerIndex = nextIndex)
    }

    fun currentPlayer(): Player = players[currentPlayerIndex]

    fun updatePlayer(player: Player): GameState {
        val updatedPlayers = players.toMutableList()
        updatedPlayers[currentPlayerIndex] = player
        return copy(players = updatedPlayers)
    }

    companion object {
        fun create(playerList: List<Player>): GameState {
            require(playerList.size == 4) { "Game requires exactly 4 players" }

            val team1 = Team(
                id = 1,
                player1 = playerList[0],
                player2 = playerList[2],
                gaddis = emptyList()
            )
            val team2 = Team(
                id = 2,
                player1 = playerList[1],
                player2 = playerList[3],
                gaddis = emptyList()
            )

            return GameState(
                players = playerList,
                teams = listOf(team1, team2),
                drawPile = Deck.create(),
                discardPile = emptyList(),
                currentPlayerIndex = 0,
                currentRound = 1
            )
        }
    }
}
