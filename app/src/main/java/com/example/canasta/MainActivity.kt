package com.example.canasta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.canasta.ui.GameScreen
import com.example.canasta.ui.GameViewModel
import com.example.canasta.ui.theme.CanastaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanastaTheme {
                CanastaApp()
            }
        }
    }
}

@Composable
fun CanastaApp() {
    val viewModel: GameViewModel = viewModel()
    val gameState by viewModel.gameState.collectAsState()
    val message by viewModel.message.collectAsState()
    val turnPhase by viewModel.turnPhase.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = gameState) {
                null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Canasta - Gaddi Edition",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = { viewModel.startNewGame() }) {
                            Text("Start New Game")
                        }
                    }
                }
                else -> {
                    GameScreen(
                        gameState = state,
                        turnPhase = turnPhase,
                        onDrawFromPile = { viewModel.drawFromPile() },
                        onTakeDiscard = { viewModel.takeDiscardPile() },
                        onDiscard = { card -> viewModel.discard(card) },
                        onCreateMeld = { cards -> viewModel.createMeld(cards) },
                        onShow = { card -> viewModel.show(card) }
                    )
                }
            }

            message?.let { msg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearMessage() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(msg)
                }
            }
        }
    }
}