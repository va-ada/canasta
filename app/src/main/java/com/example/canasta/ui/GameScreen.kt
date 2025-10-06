// ABOUTME: Main game screen UI with Jetpack Compose
// ABOUTME: Displays player hand, draw/discard piles, and game controls
package com.example.canasta.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.domain.*

@Composable
fun GameScreen(
    gameState: GameState,
    turnPhase: TurnPhase,
    onDrawFromPile: () -> Unit,
    onTakeDiscard: () -> Unit,
    onDiscard: (Card) -> Unit,
    onCreateMeld: (List<Card>) -> Unit,
    onShow: (Card) -> Unit
) {
    var selectedCards by remember { mutableStateOf(setOf<Card>()) }
    var sortedHand by remember { mutableStateOf<List<Card>>(emptyList()) }
    var autoSort by remember { mutableStateOf(true) }
    val currentPlayer = gameState.currentPlayer()

    LaunchedEffect(currentPlayer.hand) {
        if (autoSort) {
            sortedHand = sortCardsBySuit(currentPlayer.hand)
        } else {
            sortedHand = currentPlayer.hand
        }
    }

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
                enabled = turnPhase == TurnPhase.DRAW,
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
                enabled = turnPhase == TurnPhase.DRAW && gameState.discardPile.isNotEmpty(),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Take All")
            }
        }

        // Current player turn indicator with phase
        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${currentPlayer.name}'s Turn",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = when (turnPhase) {
                    TurnPhase.DRAW -> "DRAW PHASE: Pick a card"
                    TurnPhase.PLAY -> "PLAY PHASE: Create melds or discard"
                    TurnPhase.DISCARD -> "DISCARD PHASE: Discard a card"
                },
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        // Bottom: Current player's hand and controls
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Hand (${currentPlayer.hand.size} cards)",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            autoSort = true
                            sortedHand = sortCardsBySuit(currentPlayer.hand)
                        },
                        enabled = !autoSort
                    ) {
                        Text("Sort", fontSize = 12.sp)
                    }

                    if (selectedCards.size == 1) {
                        val selectedCard = selectedCards.first()
                        val currentIndex = sortedHand.indexOf(selectedCard)

                        IconButton(
                            onClick = {
                                if (currentIndex > 0) {
                                    autoSort = false
                                    sortedHand = sortedHand.toMutableList().apply {
                                        removeAt(currentIndex)
                                        add(currentIndex - 1, selectedCard)
                                    }
                                }
                            },
                            enabled = currentIndex > 0
                        ) {
                            Text("◀", fontSize = 16.sp)
                        }

                        IconButton(
                            onClick = {
                                if (currentIndex < sortedHand.size - 1) {
                                    autoSort = false
                                    sortedHand = sortedHand.toMutableList().apply {
                                        removeAt(currentIndex)
                                        add(currentIndex + 1, selectedCard)
                                    }
                                }
                            },
                            enabled = currentIndex < sortedHand.size - 1
                        ) {
                            Text("▶", fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedHand) { card ->
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
                    enabled = turnPhase == TurnPhase.PLAY && selectedCards.size >= 3
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
                    enabled = turnPhase == TurnPhase.PLAY && selectedCards.size == 1
                ) {
                    Text("Discard")
                }

                Button(
                    onClick = {
                        if (selectedCards.size == 1) {
                            onShow(selectedCards.first())
                        }
                    },
                    enabled = turnPhase == TurnPhase.PLAY && selectedCards.size == 1 && currentPlayer.hand.size == 1
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
    val (cardColor, suitSymbol) = when (card) {
        is Card.Joker -> Color.Black to "JOKER"
        is Card.Regular -> when (card.suit) {
            Suit.HEARTS, Suit.DIAMONDS -> Color.Red to getSuitName(card.suit)
            Suit.CLUBS, Suit.SPADES -> Color.Black to getSuitName(card.suit)
        }
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .width(90.dp)
            .height(120.dp)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = getRankString(card),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = cardColor
            )

            Text(
                text = suitSymbol,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = cardColor
            )

            Text(
                text = getRankString(card),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = cardColor
            )
        }
    }
}

fun getRankString(card: Card): String {
    return when (card) {
        is Card.Joker -> "★"
        is Card.Regular -> when (card.rank) {
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
    }
}

fun getSuitName(suit: Suit): String {
    return when (suit) {
        Suit.HEARTS -> "HEARTS"
        Suit.DIAMONDS -> "DIAMONDS"
        Suit.CLUBS -> "CLUBS"
        Suit.SPADES -> "SPADES"
    }
}

fun cardToString(card: Card): String {
    return when (card) {
        is Card.Joker -> "JOKER"
        is Card.Regular -> "${getRankString(card)} ${getSuitName(card.suit)}"
    }
}

fun sortCardsBySuit(hand: List<Card>): List<Card> {
    val suitOrder = listOf(Suit.SPADES, Suit.HEARTS, Suit.DIAMONDS, Suit.CLUBS)
    val rankOrder = listOf(
        Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX,
        Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING
    )

    val jokers = hand.filterIsInstance<Card.Joker>()
    val regularCards = hand.filterIsInstance<Card.Regular>()

    val sortedRegular = regularCards.sortedWith(
        compareBy<Card.Regular>(
            { suitOrder.indexOf(it.suit) },
            { rankOrder.indexOf(it.rank) }
        )
    )

    return jokers + sortedRegular
}
