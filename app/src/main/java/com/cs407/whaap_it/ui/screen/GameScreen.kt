package com.cs407.whaap_it.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cs407.whaap_it.util.SoundManager
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random
import com.cs407.whaap_it.util.MusicManager
import androidx.compose.ui.platform.LocalContext


/**
 * Keeps track of the overall game state, such as the current score, action, time remaining.
 */
data class GameState(
    val score: Int = 0,
    val currentAction: GameAction? = null,
    val actionTimeRemaining: Float = 0f,
    val totalTimeRemaining: Float = 60f,
    val isGameActive: Boolean = false,
    val actions: List<GameAction> = emptyList(),
    val currentActionIndex: Int = 0,
    val isInActionGap: Boolean = false,
    val gapTimeRemaining: Float = 0f,
    val countdown: Int = 3,
    val isInCountdown: Boolean = true
)

/**
 * Enum class that stores all the possible game actions and their points value
 */
enum class GameAction(val displayName: String, val points: Int, val playSound: () -> Unit, val playActionSound: () -> Unit) {
    WHAAP("Whaap-it!", 10, {SoundManager.playBopIt()}, {SoundManager.zipperSquealSound()}),
    PULL("Pull-it!", 10, {SoundManager.playPullIt()}, {SoundManager.swipeSound()}),
    TWIST("Twist-it!", 10, {SoundManager.playTwistIt()}, {SoundManager.cartoonJumpSound()}),
    // Add more Game Actions in the future...
}

/**
 * Random action generator. Returns a random Game Action.
 */
private fun generateRandomAction(): GameAction {
    return when (Random.nextInt(3)) {
        0 -> GameAction.WHAAP
        1 -> GameAction.PULL
        else -> GameAction.TWIST
    }
}

/**
 * Generates first 10 Game Actions of a game session
 */
private fun generateInitialActions(): List<GameAction> {
    return List(10) { generateRandomAction() } // Start with 10 actions
}

/**
 * Used to initiate the start of a game. Initializes the game state.
 */
private fun startGame(onStateUpdate: (GameState) -> Unit) {
    val actions = generateInitialActions()
    val firstAction = actions.firstOrNull()

    onStateUpdate(
        GameState(
            isGameActive = true,
            actions = actions,
            currentAction = firstAction,
            actionTimeRemaining = 3f,
            totalTimeRemaining = 60f,
            countdown = 3,
            isInCountdown = true
        )
    )

    //firstAction?.playSound()
}

/**
 * Gets the next game action from the generated list of actions. If there are no more actions left,
 * this function ends the game
 */
private fun nextAction(currentState: GameState, onStateUpdate: (GameState) -> Unit) {
    if (currentState.totalTimeRemaining <= 0) {
        onStateUpdate(currentState.copy(isGameActive = false, currentAction = null))
        return
    }

    val nextIndex = currentState.currentActionIndex + 1
    var nextActions = currentState.actions
    var nextAction = nextActions.getOrNull(nextIndex)

    if (nextIndex >= nextActions.size) {
        val newActions = List(5) { generateRandomAction() }
        nextActions = nextActions + newActions
        nextAction = nextActions.getOrNull(nextIndex)
    }

    if (nextAction != null) {
        nextAction.playSound()

        onStateUpdate(
            currentState.copy(
                currentAction = nextAction,
                currentActionIndex = nextIndex,
                actions = nextActions,
                actionTimeRemaining = 3f,
                isInActionGap = false,
                gapTimeRemaining = 0f
            )
        )
    } else {
        onStateUpdate(
            currentState.copy(
                isGameActive = false,
                currentAction = null
            )
        )
    }
}

/**
 * Handles when an action is performed. If that action performed is the same as the current action,
 * points will be added to the user's score
 */
private fun handleAction(
    performedAction: GameAction,
    currentState: GameState,
    onStateUpdate: (GameState) -> Unit
) {
    val currentAction = currentState.currentAction

    if (currentAction == performedAction) {
        val newScore = currentState.score + performedAction.points
        currentAction.playActionSound()
        onStateUpdate(
            currentState.copy(
                score = newScore,
                isInActionGap = true,
                gapTimeRemaining = 1f,
                currentAction = null
            )
        )
        SoundManager.playButtonClick()
    } else {
        if (currentState.isGameActive) {
            SoundManager.playWeakGameOver()
            onStateUpdate(
                currentState.copy(
                    isGameActive = false,
                    currentAction = null
                )
            )
        }
    }
}

@Composable
fun CountdownOverlay(
    countdown: Int,
    isVisible: Boolean
) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (countdown > 0) countdown.toString() else "Go!",
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * The Game Header shows the user's current score and the time remaining for the game.
 * It also currently holds the arrowback button to return to the home screen.
 */
@Composable
fun GameHeader(
    score: Int,
    totalTimeRemaining: Float,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.DarkGray)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
            text = "Score: ${score}",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            fontSize = 20.sp,
            color = Color.White
        )
        Text(
            text = "Time: ${totalTimeRemaining.toInt()}s",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            fontSize = 20.sp,
            color = Color.White
        )
    }
}

/**
 * This is the main game area which comprises of the visual layout of the
 * Whaap-it, Pull-it, Twist-it action buttons
 */
@Composable
fun GameArea(
    gameState: GameState,
    onActionPerformed: (GameAction) -> Unit
) {
    val currentAction = gameState.currentAction

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Yellow Top Half - Twist It (clickable)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(Color.Yellow)
                .pointerInput(gameState.isInActionGap, gameState.isInCountdown) {
                    if (!gameState.isInActionGap && !gameState.isInCountdown) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val (x, y) = dragAmount
                            // Detect horizontal swipe (left or right)
                            if (abs(x) > abs(y) && abs(x) > 50) {
                                currentAction?.let {
                                    onActionPerformed(GameAction.TWIST)
                                }
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (gameState.isInActionGap) {
                Text("Owh!", fontSize = 60.sp, color = Color.Green) // Success indicator
            } else {
                Text(
                    text = currentAction?.takeIf { it == GameAction.TWIST }?.displayName ?: "Twist-it!",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // Blue Bottom Half - Pull It (clickable)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(Color.Blue)
                .pointerInput(gameState.isInActionGap, gameState.isInCountdown) {
                    if (!gameState.isInActionGap && !gameState.isInCountdown) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val (x, y) = dragAmount
                            // Detect downward swipe
                            if (y > abs(x) && y > 50) {
                                currentAction?.let {
                                    onActionPerformed(GameAction.PULL)
                                }
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (gameState.isInActionGap) {
                Text("Eeek!", fontSize = 60.sp, color = Color.Green)
            } else {
                Text(
                    text = currentAction?.takeIf { it == GameAction.PULL }?.displayName ?: "Pull-it!",
                    fontSize = 40.sp,
                    color = Color.White
                )
            }
        }

        // Red Circle - Whaap It (clickable, overlaps both areas)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp)
                .shadow(8.dp, CircleShape)
                .background(Color.Red, CircleShape)
                .clickable(enabled = !gameState.isInActionGap && !gameState.isInCountdown) {
                    currentAction?.let {
                            onActionPerformed(GameAction.WHAAP)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (gameState.isInActionGap) {
                Text("Ouch!", fontSize = 80.sp, color = Color.Green)
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentAction?.takeIf { it == GameAction.WHAAP }?.displayName ?: "Whaap-it!",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (currentAction != null) {
                        Text(
                            text = "${gameState.actionTimeRemaining.toInt()}s",
                            color = Color.White,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        CountdownOverlay(
            countdown = gameState.countdown,
            isVisible = gameState.isInCountdown
        )
    }
}

@Composable
fun GameScreen(
    navController: NavController? = null,
    onNavigateToHome: () -> Unit = {}
) {
    val context = LocalContext.current
    var gameState by remember { mutableStateOf(GameState()) }

    LaunchedEffect(gameState.isGameActive) {
        if (!gameState.isGameActive) {
            MusicManager.stopGameplayMusic()
        }
        if (gameState.isGameActive) {
            while (gameState.isGameActive && gameState.totalTimeRemaining > 0) {
                delay(16L)

                if (gameState.isInCountdown) {
                    if (gameState.countdown > 0) {
                        delay(1000L)
                        gameState = gameState.copy(countdown = gameState.countdown - 1)
                        SoundManager.playButtonClick()
                    } else {
                        gameState = gameState.copy(isInCountdown = false)
                        gameState.currentAction?.playSound()
                    }
                } else if (gameState.isInActionGap) {
                    val newGapTime = (gameState.gapTimeRemaining - 0.016f).coerceAtLeast(0f)

                    gameState = gameState.copy(gapTimeRemaining = newGapTime)

                    if (newGapTime <= 0) {
                        gameState = gameState.copy(isInActionGap = false)
                        nextAction(gameState) { newState -> gameState = newState }
                    }
                } else {
                    gameState = gameState.copy(
                        actionTimeRemaining = (gameState.actionTimeRemaining - 0.016f).coerceAtLeast(0f),
                        totalTimeRemaining = (gameState.totalTimeRemaining - 0.016f).coerceAtLeast(0f)
                    )

                    // Has current action time expired?
                    if (gameState.actionTimeRemaining <= 0) {
                        SoundManager.tooSlowGameOver()
                        gameState = gameState.copy(isGameActive = false)
                    }
                }

                // Has game time expired?
                if (gameState.totalTimeRemaining <= 0) {
                    gameState = gameState.copy(isGameActive = false)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!gameState.isGameActive && gameState.currentAction == null) {
            startGame { newState -> gameState = newState }
            MusicManager.startGameplayMusic(context)
        }
    }

    if (!gameState.isGameActive) {
        AlertDialog(
            onDismissRequest = {/* Don't allow dismiss by clicking outside */},
            title = { Text("Game Over!") },
            text = { Text("Final Score: ${gameState.score}") },
            confirmButton = {
                Button(
                    onClick = {
                        MusicManager.startGameplayMusic(context)
                        startGame { newState -> gameState = newState.copy(
                            countdown = 3,
                            isInCountdown = true
                        ) }
                    }
                ) {
                    Text("Play Again")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        MusicManager.startMenuMusic(context)
                        onNavigateToHome()
                    }
                ) {
                    Text("Main Menu")
                }
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        GameHeader(
            score = gameState.score,
            totalTimeRemaining = gameState.totalTimeRemaining,
            onBackClick = {
                MusicManager.stopGameplayMusic()
                MusicManager.startMenuMusic(context)
                onNavigateToHome()
            }
        )

        GameArea(
            gameState = gameState,
            onActionPerformed = { action ->
                handleAction(action, gameState) { newState -> gameState = newState }
            }
        )
    }
}