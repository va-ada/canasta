// ABOUTME: Player domain model representing a game participant
// ABOUTME: Manages player's hand, melds, and card operations
package com.example.canasta.domain

data class Player(
    val id: Int,
    val name: String,
    val hand: List<Card>,
    val melds: List<List<Card>> = emptyList()
) {
    fun addCard(card: Card): Player = copy(hand = hand + card)

    fun addCards(cards: List<Card>): Player = copy(hand = hand + cards)

    fun removeCard(card: Card): Player = copy(hand = hand - card)

    fun removeCards(cards: List<Card>): Player = copy(hand = hand - cards.toSet())

    fun addMeld(meld: List<Card>): Player = copy(melds = melds + listOf(meld))
}
