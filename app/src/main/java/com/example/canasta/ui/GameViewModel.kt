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

enum class TurnPhase {
    DRAW,  // Must draw one card or take discard pile
    PLAY,  // Can create melds (optional)
    DISCARD  // Must discard one card to end turn
}

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _turnPhase = MutableStateFlow(TurnPhase.DRAW)
    val turnPhase: StateFlow<TurnPhase> = _turnPhase.asStateFlow()

    fun startNewGame() {
        val players = listOf(
            Player(1, "Player 1", emptyList()),
            Player(2, "Player 2", emptyList()),
            Player(3, "Player 3", emptyList()),
            Player(4, "Player 4", emptyList())
        )
        _gameState.value = GameState.create(players).deal()
        _turnPhase.value = TurnPhase.DRAW
        _message.value = "Game started! Player 1's turn - Draw a card"
    }

    fun drawFromPile() {
        val current = _gameState.value ?: return

        if (_turnPhase.value != TurnPhase.DRAW) {
            _message.value = "You must discard a card first!"
            return
        }

        when (val result = GameActions.drawFromPile(current)) {
            is ActionResult.Success -> {
                _gameState.value = result.gameState
                _turnPhase.value = TurnPhase.PLAY
                _message.value = "Drew a card. You can create melds or discard to end turn."
            }
            is ActionResult.Failure -> {
                _message.value = result.reason
            }
        }
    }

    fun takeDiscardPile() {
        val current = _gameState.value ?: return

        if (_turnPhase.value != TurnPhase.DRAW) {
            _message.value = "You must discard a card first!"
            return
        }

        when (val result = GameActions.takeDiscardPile(current)) {
            is ActionResult.Success -> {
                _gameState.value = result.gameState
                _turnPhase.value = TurnPhase.PLAY
                _message.value = "Took discard pile. You can create melds or discard to end turn."
            }
            is ActionResult.Failure -> {
                _message.value = result.reason
            }
        }
    }

    fun discard(card: Card) {
        val current = _gameState.value ?: return

        if (_turnPhase.value == TurnPhase.DRAW) {
            _message.value = "You must draw a card first!"
            return
        }

        when (val result = GameActions.discard(current, card)) {
            is ActionResult.Success -> {
                val nextTurn = result.gameState.nextTurn()
                _gameState.value = nextTurn
                _turnPhase.value = TurnPhase.DRAW
                _message.value = "${nextTurn.currentPlayer().name}'s turn - Draw a card"
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
