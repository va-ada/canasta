// ABOUTME: Main game screen UI with Jetpack Compose
// ABOUTME: Displays player hand, draw/discard piles, and game controls
package com.example.canasta.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canasta.domain.*

@Composable
fun GameScreen(
    gameState: GameState,
    onDrawFromPile: () -> Unit,
    onTakeDiscard: () -> Unit,
    onDiscard: (Card) -> Unit,
    onCreateMeld: (List<Card>) -> Unit,
    onShow: (Card) -> Unit
) {
    var selectedCards by remember { mutableStateOf(setOf<Card>()) }
    val currentPlayer = gameState.currentPlayer()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top: Other players info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            gameState.players.forEachIndexed { index, player ->
                if (index != gameState.currentPlayerIndex) {
                    PlayerInfo(player = player, isCurrentPlayer = false)
                }
            }
        }

        // Center: Draw and discard piles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Draw pile
            Button(
                onClick = onDrawFromPile,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Draw\n(${gameState.drawPile.cards.size})")
            }

            Spacer(modifier = Modifier.width(32.dp))

            // Discard pile
            Card(modifier = Modifier.padding(8.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Discard Pile")
                    Text("(${gameState.discardPile.size})")
                    if (gameState.discardPile.isNotEmpty()) {
                        Text(cardToString(gameState.discardPile.last()))
                    }
                }
            }

            Button(
                onClick = onTakeDiscard,
                enabled = gameState.discardPile.isNotEmpty(),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Take All")
            }
        }

        // Current player turn indicator
        Text(
            text = "${currentPlayer.name}'s Turn",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Bottom: Current player's hand and controls
        Column {
            Text(
                text = "Your Hand (${currentPlayer.hand.size} cards)",
                style = MaterialTheme.typography.titleMedium
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentPlayer.hand) { card ->
                    CardItem(
                        card = card,
                        isSelected = selectedCards.contains(card),
                        onClick = {
                            selectedCards = if (selectedCards.contains(card)) {
                                selectedCards - card
                            } else {
                                selectedCards + card
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (selectedCards.isNotEmpty()) {
                            onCreateMeld(selectedCards.toList())
                            selectedCards = emptySet()
                        }
                    },
                    enabled = selectedCards.size >= 3
                ) {
                    Text("Create Meld")
                }

                Button(
                    onClick = {
                        if (selectedCards.size == 1) {
                            onDiscard(selectedCards.first())
                            selectedCards = emptySet()
                        }
                    },
                    enabled = selectedCards.size == 1
                ) {
                    Text("Discard")
                }

                Button(
                    onClick = {
                        if (selectedCards.size == 1) {
                            onShow(selectedCards.first())
                        }
                    },
                    enabled = selectedCards.size == 1 && currentPlayer.hand.size == 1
                ) {
                    Text("Show")
                }
            }

            // Display melds
            if (currentPlayer.melds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Your Melds:", style = MaterialTheme.typography.titleSmall)
                currentPlayer.melds.forEach { meld ->
                    Text(
                        text = meld.joinToString(" ") { cardToString(it) },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerInfo(player: Player, isCurrentPlayer: Boolean) {
    Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = player.name,
                style = if (isCurrentPlayer) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.bodyMedium
                }
            )
            Text("Cards: ${player.hand.size}")
            Text("Melds: ${player.melds.size}")
        }
    }
}

@Composable
fun CardItem(
    card: Card,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        ),
        modifier = Modifier
            .width(80.dp)
            .height(100.dp)
    ) {
        Text(
            text = cardToString(card),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

fun cardToString(card: Card): String {
    return when (card) {
        is Card.Joker -> "🃏"
        is Card.Regular -> {
            val rank = when (card.rank) {
                Rank.TWO -> "2"
                Rank.THREE -> "3"
                Rank.FOUR -> "4"
                Rank.FIVE -> "5"
                Rank.SIX -> "6"
                Rank.SEVEN -> "7"
                Rank.EIGHT -> "8"
                Rank.NINE -> "9"
                Rank.TEN -> "10"
                Rank.JACK -> "J"
                Rank.QUEEN -> "Q"
                Rank.KING -> "K"
                Rank.ACE -> "A"
            }
            val suit = when (card.suit) {
                Suit.HEARTS -> "♥"
                Suit.DIAMONDS -> "♦"
                Suit.CLUBS -> "♣"
                Suit.SPADES -> "♠"
            }
            "$rank$suit"
        }
    }
}
