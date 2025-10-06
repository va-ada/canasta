// ABOUTME: ViewModel for managing game state and actions
// ABOUTME: Handles game logic and exposes state to UI layer
package com.example.canasta.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canasta.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun startNewGame() {
        val players = listOf(
            Player(1, "Player 1", emptyList()),
            Player(2, "Player 2", emptyList()),
            Player(3, "Player 3", emptyList()),
            Player(4, "Player 4", emptyList())
        )
        _gameState.value = GameState.create(players).deal()
        _message.value = "Game started! Player 1's turn"
    }

    fun drawFromPile() {
        val current = _gameState.value ?: return

        when (val result = GameActions.drawFromPile(current)) {
            is ActionResult.Success -> {
                _gameState.value = result.gameState
                _message.value = "Drew a card from pile"
            }
            is ActionResult.Failure -> {
                _message.value = result.reason
            }
        }
    }

    fun takeDiscardPile() {
        val current = _gameState.value ?: return

        when (val result = GameActions.takeDiscardPile(current)) {
            is ActionResult.Success -> {
                _gameState.value = result.gameState
                _message.value = "Took entire discard pile"
            }
            is ActionResult.Failure -> {
                _message.value = result.reason
            }
        }
    }

    fun discard(card: Card) {
        val current = _gameState.value ?: return

        when (val result = GameActions.discard(current, card)) {
            is ActionResult.Success -> {
                val nextTurn = result.gameState.nextTurn()
                _gameState.value = nextTurn
                _message.value = "${nextTurn.currentPlayer().name}'s turn"
            }
            is ActionResult.Failure -> {
                _message.value = result.reason
            }
        }
    }

    fun createMeld(cards: List<Card>) {
        val current = _gameState.value ?: return

        when (val result = GameActions.createMeld(current, cards)) {
            is ActionResult.Success -> {
                _gameState.value = result.gameState
                _message.value = "Meld created successfully!"
            }
            is ActionResult.Failure -> {
                _message.value = result.reason
            }
        }
    }

    fun show(card: Card) {
        val current = _gameState.value ?: return

        when (val result = GameActions.show(current, card)) {
            is ActionResult.Success -> {
                _message.value = "Round ended! ${current.currentPlayer().name} showed."
            }
            is ActionResult.Failure -> {
                _message.value = result.reason
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
