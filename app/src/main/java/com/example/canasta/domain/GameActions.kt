// ABOUTME: GameActions contains all valid game operations
// ABOUTME: Implements draw, discard, meld, and show actions with validation
package com.example.canasta.domain

sealed class ActionResult {
    data class Success(val gameState: GameState) : ActionResult()
    data class Failure(val reason: String) : ActionResult()
}

object GameActions {

    fun drawFromPile(gameState: GameState): ActionResult {
        val (card, newDeck) = gameState.drawPile.draw()

        return if (card != null) {
            val updatedPlayer = gameState.currentPlayer().addCard(card)
            ActionResult.Success(
                gameState.updatePlayer(updatedPlayer).copy(drawPile = newDeck)
            )
        } else {
            ActionResult.Failure("Draw pile is empty")
        }
    }

    fun takeDiscardPile(gameState: GameState): ActionResult {
        if (gameState.discardPile.isEmpty()) {
            return ActionResult.Failure("Discard pile is empty")
        }

        val updatedPlayer = gameState.currentPlayer().addCards(gameState.discardPile)
        return ActionResult.Success(
            gameState.updatePlayer(updatedPlayer).copy(discardPile = emptyList())
        )
    }

    fun discard(gameState: GameState, card: Card): ActionResult {
        val player = gameState.currentPlayer()

        if (!player.hand.contains(card)) {
            return ActionResult.Failure("Card not in player's hand")
        }

        val updatedPlayer = player.removeCard(card)
        val updatedDiscardPile = gameState.discardPile + card

        return ActionResult.Success(
            gameState.updatePlayer(updatedPlayer).copy(discardPile = updatedDiscardPile)
        )
    }

    fun createMeld(gameState: GameState, cards: List<Card>): ActionResult {
        val player = gameState.currentPlayer()

        if (!cards.all { player.hand.contains(it) }) {
            return ActionResult.Failure("Some cards are not in player's hand")
        }

        val validation = when {
            cards.all { it is Card.Regular && (it as Card.Regular).rank == (cards.first() as? Card.Regular)?.rank } -> {
                val jokerCount = cards.count { it is Card.Joker || (it is Card.Regular && it.rank == Rank.TWO) }
                if (jokerCount == 0 && cards.size >= 7) {
                    Meld.validateHandle(cards)
                } else {
                    Meld.validateSet(cards)
                }
            }
            else -> Meld.validateSequence(cards)
        }

        return when (validation) {
            is MeldValidation.Valid -> {
                val updatedPlayer = player.removeCards(cards).addMeld(cards)
                ActionResult.Success(gameState.updatePlayer(updatedPlayer))
            }
            is MeldValidation.Invalid -> ActionResult.Failure(validation.reason)
        }
    }

    fun show(gameState: GameState, finalCard: Card): ActionResult {
        val player = gameState.currentPlayer()

        if (player.hand.size != 1 || player.hand.first() != finalCard) {
            return ActionResult.Failure("Must have exactly one card left for show")
        }

        if (finalCard is Card.Joker) {
            return ActionResult.Failure("Cannot show with a Joker")
        }

        if (finalCard is Card.Regular && finalCard.rank == Rank.TWO) {
            return ActionResult.Failure("Cannot show with a 2")
        }

        val teamIndex = if (player.id == 1 || player.id == 3) 0 else 1
        val team = gameState.teams[teamIndex]

        val teamMeldCount = team.player1.melds.size + team.player2.melds.size
        if (teamMeldCount < 2) {
            return ActionResult.Failure("Team must have at least 2 melds before showing")
        }

        return ActionResult.Success(gameState)
    }
}
