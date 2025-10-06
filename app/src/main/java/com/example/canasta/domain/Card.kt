// ABOUTME: Card domain model representing playing cards in Canasta
// ABOUTME: Handles regular cards with suits/ranks and jokers, with point calculations
package com.example.canasta.domain

sealed class Card {
    abstract val points: Int

    data object Joker : Card() {
        override val points = 30
    }

    data class Regular(val rank: Rank, val suit: Suit) : Card() {
        override val points: Int
            get() = when (rank) {
                Rank.TWO -> 15
                Rank.ACE -> 15
                Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING -> 10
                Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX, Rank.SEVEN -> 5
            }
    }
}

enum class Rank {
    TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
}

enum class Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}
