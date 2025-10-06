// ABOUTME: Meld validation for Sets, Handles, and Sequences
// ABOUTME: Enforces game rules for valid card combinations and calculates bonuses
package com.example.canasta.domain

sealed class MeldValidation {
    data class Valid(val bonus: Int = 0) : MeldValidation()
    data class Invalid(val reason: String) : MeldValidation()
}

object Meld {

    fun validateSet(cards: List<Card>): MeldValidation {
        if (cards.size < 7) {
            return MeldValidation.Invalid("Set requires at least 7 cards")
        }

        val jokerCount = cards.count { it is Card.Joker }
        val twoAsJokerCount = cards.count {
            it is Card.Regular && it.rank == Rank.TWO
        }

        val totalJokers = jokerCount + twoAsJokerCount

        if (totalJokers > 1) {
            return MeldValidation.Invalid("Set can have at most 1 joker")
        }

        val regularCards = cards.filterIsInstance<Card.Regular>()
        val ranks = regularCards.map { it.rank }.distinct()

        if (ranks.size > 1) {
            return MeldValidation.Invalid("All cards must have the same rank")
        }

        return MeldValidation.Valid(bonus = 100)
    }

    fun validateHandle(cards: List<Card>): MeldValidation {
        if (cards.size < 7) {
            return MeldValidation.Invalid("Handle requires at least 7 cards")
        }

        if (cards.any { it is Card.Joker }) {
            return MeldValidation.Invalid("Handle must be pure (no jokers)")
        }

        val regularCards = cards.filterIsInstance<Card.Regular>()
        if (regularCards.any { it.rank == Rank.TWO }) {
            return MeldValidation.Invalid("Handle must be pure (no 2s as jokers)")
        }

        val ranks = regularCards.map { it.rank }.distinct()
        if (ranks.size > 1) {
            return MeldValidation.Invalid("All cards must have the same rank")
        }

        return MeldValidation.Valid(bonus = 200)
    }

    fun validateSequence(cards: List<Card>): MeldValidation {
        if (cards.size < 3) {
            return MeldValidation.Invalid("Sequence requires at least 3 cards")
        }

        val hasJoker = cards.any { it is Card.Joker }
        val regularCards = cards.filterIsInstance<Card.Regular>()

        if (!hasJoker) {
            val suits = regularCards.map { it.suit }.distinct()
            if (suits.size > 1) {
                return MeldValidation.Invalid("Pure sequence must have same suit")
            }

            if (!isConsecutive(regularCards.map { it.rank })) {
                return MeldValidation.Invalid("Cards must be consecutive")
            }
        } else {
            val nonJokerSuits = regularCards.filter { it.rank != Rank.TWO }.map { it.suit }.distinct()
            if (nonJokerSuits.size > 1) {
                return MeldValidation.Invalid("Sequence must have same suit (excluding jokers)")
            }
        }

        return MeldValidation.Valid()
    }

    private fun isConsecutive(ranks: List<Rank>): Boolean {
        val rankValuesLow = ranks.map { rankToValue(it, aceHigh = false) }.sorted()
        val rankValuesHigh = ranks.map { rankToValue(it, aceHigh = true) }.sorted()

        return isSequentialValues(rankValuesLow) || isSequentialValues(rankValuesHigh)
    }

    private fun isSequentialValues(values: List<Int>): Boolean {
        for (i in 0 until values.size - 1) {
            if (values[i + 1] - values[i] != 1) {
                return false
            }
        }
        return true
    }

    private fun rankToValue(rank: Rank, aceHigh: Boolean): Int = when (rank) {
        Rank.ACE -> if (aceHigh) 14 else 1
        Rank.TWO -> 2
        Rank.THREE -> 3
        Rank.FOUR -> 4
        Rank.FIVE -> 5
        Rank.SIX -> 6
        Rank.SEVEN -> 7
        Rank.EIGHT -> 8
        Rank.NINE -> 9
        Rank.TEN -> 10
        Rank.JACK -> 11
        Rank.QUEEN -> 12
        Rank.KING -> 13
    }
}
