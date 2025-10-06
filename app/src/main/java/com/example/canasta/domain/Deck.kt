// ABOUTME: Deck domain model representing a collection of cards
// ABOUTME: Creates standard double deck (108 cards) with shuffle and draw operations
package com.example.canasta.domain

data class Deck(val cards: List<Card>) {

    fun shuffle(): Deck = Deck(cards.shuffled())

    fun draw(): Pair<Card?, Deck> {
        if (cards.isEmpty()) return null to this
        return cards.first() to Deck(cards.drop(1))
    }

    companion object {
        fun create(): Deck {
            val allCards = buildList {
                repeat(2) {
                    Rank.entries.forEach { rank ->
                        Suit.entries.forEach { suit ->
                            add(Card.Regular(rank, suit))
                        }
                    }
                    repeat(2) {
                        add(Card.Joker)
                    }
                }
            }
            return Deck(allCards)
        }
    }
}
