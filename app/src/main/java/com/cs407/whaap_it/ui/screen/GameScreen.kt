package com.cs407.whaap_it.ui.screen

import android.graphics.Canvas
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Dialog
import com.cs407.whaap_it.ui.screen.TiledLogoBackground
import com.cs407.whaap_it.R

// How many actions performed before the next difficulty level
private const val ACTIONS_BEFORE_FAST = 10
private const val ACTIONS_BEFORE_VERY_FAST = 25
private const val ACTIONS_BEFORE_SUPER_FAST = 45

/**
 * Determines current game difficulty
 */
private fun getDifficultyForAction(actionIndex: Int): DifficultyLevel {
    return when {
        actionIndex >= ACTIONS_BEFORE_SUPER_FAST -> DifficultyLevel.SUPER_FAST
        actionIndex >= ACTIONS_BEFORE_VERY_FAST -> DifficultyLevel.VERY_FAST
        actionIndex >= ACTIONS_BEFORE_FAST -> DifficultyLevel.FAST
        else -> DifficultyLevel.NORMAL
    }
}

/**
 * Storing different difficulty levels (NORMAL, FAST, VERY FAST)
 */
enum class DifficultyLevel(
    val displayName: String,
    val actionTime: Float,
    val gapTime: Float,
    val pointsMultiplier: Float = 1f
) {
    NORMAL("Normal", 3f, 1f, 1f),
    FAST("Fast", 2f, 0.7f, 1.2f),
    VERY_FAST("Very Fast", 1.5f, 0.5f, 1.5f),
    SUPER_FAST("Super Fast", 2f, 0.5f, 2f)
}

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
    val isInCountdown: Boolean = true,
    val isPaused: Boolean = false,
    val lastPerformedAction: GameAction? = null,
    val currentDifficulty: DifficultyLevel = DifficultyLevel.NORMAL,
    val isInIntermission: Boolean = false,
    val intermissionCountdown: Int = 0
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
        val nextDifficulty = getDifficultyForAction(nextIndex)

        if (nextDifficulty != currentState.currentDifficulty && !currentState.isInIntermission) {
            onStateUpdate(
                currentState.copy(
                    isInIntermission = true,
                    intermissionCountdown = 5,
                    currentAction = null,
                    currentDifficulty = nextDifficulty
                )
            )
        } else {
            nextAction.playSound()

            onStateUpdate(
                currentState.copy(
                    currentAction = nextAction,
                    currentActionIndex = nextIndex,
                    actions = nextActions,
                    actionTimeRemaining = nextDifficulty.actionTime,
                    isInActionGap = false,
                    gapTimeRemaining = 0f,
                    currentDifficulty = nextDifficulty
                )
            )
        }
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
        val basePoints = performedAction.points
        val multipledPoints = (basePoints * currentState.currentDifficulty.pointsMultiplier).toInt()
        val newScore = currentState.score + multipledPoints
        currentAction.playActionSound()
        onStateUpdate(
            currentState.copy(
                score = newScore,
                isInActionGap = true,
                gapTimeRemaining = 1f,
                currentAction = null,
                lastPerformedAction = performedAction
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

/**
 * This is the overlay that is displayed when the game is in intermission (between difficulty levels)
 */
@Composable
fun IntermissionOverlay(
    timeRemaining: Int,
    nextDifficulty: DifficultyLevel,
    isVisible: Boolean
) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.4f))
                .pointerInput(Unit) {
                    detectTapGestures { }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Get Ready for",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${nextDifficulty.displayName} Speed!",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Relax: ${timeRemaining}s",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Countdown overlay displayed before the start of the game and when resuming a game from the pause menu.
 * It shows a countdown timer (3->2->1)
 */
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

@Composable
fun PauseMenuDialog(
    onResume: () -> Unit,
    onBackToHome: () -> Unit,
) {
    Dialog(
        onDismissRequest = {}
    ) {
        Card(
            modifier = Modifier
                .wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .width(200.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Game Paused",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onResume,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Resume Game",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Button(
                        onClick = onBackToHome,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to menu",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Exit to Menu",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WhaapItButton(
    isEnabled: Boolean,
    currentAction: GameAction?,
    gameState: GameState,
    onActionPerformed: () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 1000f
        ),
        label = "whaapButtonScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 1000f
        ),
        label = "whaapButtonElevation"
    )

    // Red Circle - Whaap It (clickable, overlaps both areas)
    Box(
        modifier = Modifier
            .size(300.dp)
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                clip = false
            )
            .background(Color.Red, CircleShape)
            .pointerInput(isEnabled) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                    },
                    onTap = {
                        onActionPerformed()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (gameState.isInActionGap && gameState.lastPerformedAction == GameAction.WHAAP) {
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
}

@Composable
fun SpringVisualization(
    isPulled: Boolean,
    pullDistance: Float,
    modifier: Modifier = Modifier
) {
    val springColor = if (isPulled) Color.Yellow else Color.White
    val springWidth by animateDpAsState(
        targetValue = if (isPulled) 8.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "springWidth"
    )

    Canvas(modifier = modifier.height(60.dp).width(springWidth)) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val coilCount = 8
        val maxPull = 150f
        val tension = (pullDistance / maxPull).coerceIn(0f, 1f)

        val coilSpacing = canvasHeight / coilCount * (1 + tension * 0.5f)

        drawRect(
            color = springColor,
            topLeft = Offset(0f, 0f),
            size = Size(canvasWidth, canvasHeight)
        )

        for (i in 0 until coilCount) {
            val yPos = i * coilSpacing
            drawCircle(
                color = Color.Red,
                center = Offset(canvasWidth / 2, yPos),
                radius = if (i % 2 == 0) 3.dp.toPx() else 2.dp.toPx()
            )
        }
    }
}

@Composable
fun PullItSpring(
    isEnabled: Boolean,
    currentAction: GameAction?,
    gameState: GameState,
    onActionPerformed: () -> Unit
) {
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "springPull"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .offset(y = animatedOffsetY.dp)
            .background(Color.Blue)
            .pointerInput(isEnabled, currentAction) {
                if (isEnabled) {
                    detectDragGestures (
                        onDragStart = {
                        },
                        onDragEnd = {
                            offsetY = 0f

                            if (animatedOffsetY > 10) {
                                onActionPerformed()
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val (x, y) = dragAmount

                            if (y > 0) {
                                offsetY = y.coerceAtMost(500f)
                            }
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        /**
         *         SpringVisualization(
         *             isPulled = isDragging,
         *             pullDistance = animatedOffsetY,
         *             modifier = Modifier.align(Alignment.TopCenter)
         *         )
         */

        if (gameState.isInActionGap && gameState.lastPerformedAction == GameAction.PULL) {
            Text("Eeek!", fontSize = 60.sp, color = Color.Green)
        } else {
            Text(
                text = currentAction?.takeIf { it == GameAction.PULL }?.displayName ?: "Pull-it!",
                fontSize = 40.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun TwistItRotation(
    isEnabled: Boolean,
    currentAction: GameAction?,
    gameState: GameState,
    onActionPerformed: () -> Unit
) {
    var rotation by remember { mutableStateOf(0f) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "twistRotation"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            abs(animatedRotation) > 60f -> Color.Red
            abs(animatedRotation) > 30f -> Color(0xFFFFA000) // Orange
            else -> Color.Black
        },
        label = "iconColor"
    )

    val iconSize by animateDpAsState(
        targetValue = when {
            abs(animatedRotation) > 60f -> 80.dp
            abs(animatedRotation) > 30f -> 70.dp
            else -> 60.dp
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "iconSize"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .background(Color.Yellow)
            .rotate(animatedRotation)
            .pointerInput(isEnabled) {
                if (isEnabled) {
                    detectDragGestures(
                        onDragStart = { },
                        onDragEnd = {
                            rotation = 0f // Spring back to center
                            if (abs(animatedRotation) > 10) {
                                onActionPerformed()
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val (x, y) = dragAmount
                            if (abs(x) > abs(y)) { // Horizontal movement controls rotation
                                // Convert horizontal drag to rotation (more drag = more rotation)
                                rotation = (x).coerceIn(-90f, 90f)
                            }
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        //RotationIndicators(rotation = animatedRotation)

        if (gameState.isInActionGap && gameState.lastPerformedAction == GameAction.TWIST) {
            Text("Owh!", fontSize = 60.sp, color = Color.Green) // Success indicator
        } else {
            Icon(
                imageVector = Icons.Default.Refresh, // Circular arrow icon
                contentDescription = "Twist it",
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun RotationIndicators(rotation: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Left arrow (rotates with content)
        Text(
            "↶",
            modifier = Modifier.align(Alignment.CenterStart).padding(16.dp),
            fontSize = 30.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
        // Right arrow (rotates with content)
        Text(
            "↷",
            modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp),
            fontSize = 30.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun TiledLogoBackground(
    id: Int,
    tileDensity: Int = 4
) {
    val context = LocalContext.current
    val logoIMage = remember {
        ImageBitmap.imageResource(context.resources, id)
    }

    Canvas(modifier = Modifier
        .fillMaxSize()) {
        val tileWidth = size.width / tileDensity
        val tileHeight = tileWidth

        val horizontalTiles = (size.width / tileWidth).toInt() + 1
        val verticalTiles = (size.height / tileHeight).toInt() + 1

        for (x in 0..horizontalTiles) {
            for (y in 0..verticalTiles) {
                drawImage(
                    image = logoIMage,
                    dstOffset = IntOffset(
                        (x * tileWidth).toInt(),
                        (y * tileHeight).toInt()
                    ),
                    dstSize = IntSize(tileWidth.toInt(), tileHeight.toInt()),
                    alpha = 0.4f
                )
            }
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
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Transparent)
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF6A1B9A)) // Purple color
                .size(48.dp)
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF6A1B9A))
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Score: ${score}",
                modifier = Modifier
                    .align(Alignment.Center),
                fontSize = 20.sp,
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF6A1B9A))
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Time: ${totalTimeRemaining.toInt()}s",
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                fontSize = 20.sp,
                color = Color.White
            )
        }
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
            .background(Color.White)
    ) {
        TiledLogoBackground(id = R.drawable.logo, tileDensity = 6)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            TwistItRotation(
                isEnabled = !gameState.isInActionGap && !gameState.isInCountdown && !gameState.isPaused && !gameState.isInIntermission,
                currentAction = currentAction,
                gameState = gameState,
                onActionPerformed = {
                    currentAction?.let {
                        onActionPerformed(GameAction.TWIST)
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            PullItSpring(
                isEnabled = !gameState.isInActionGap && !gameState.isInCountdown && !gameState.isPaused && !gameState.isInIntermission,
                currentAction = currentAction,
                gameState = gameState,
                onActionPerformed = {
                    currentAction?.let {
                        onActionPerformed(GameAction.PULL)
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp), // Same size as the button
            contentAlignment = Alignment.Center
        ) {
            WhaapItButton(
                isEnabled = !gameState.isInActionGap && !gameState.isInCountdown && !gameState.isPaused && !gameState.isInIntermission,
                currentAction = currentAction,
                gameState = gameState,
                onActionPerformed = {
                    currentAction?.let {
                        onActionPerformed(GameAction.WHAAP)
                    }
                }
            )
        }

        IntermissionOverlay(
            timeRemaining = gameState.intermissionCountdown,
            nextDifficulty = getDifficultyForAction(gameState.currentActionIndex + 1),
            isVisible = gameState.isInIntermission
        )

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
    var showPauseMenu by remember { mutableStateOf(false) }

    LaunchedEffect(gameState.isInCountdown, gameState.isPaused) {
        if (gameState.isInCountdown && !gameState.isPaused) {
            for (i in gameState.countdown downTo 1) {
                delay(1000L)
                if (gameState.isInCountdown && !gameState.isPaused) {
                    gameState = gameState.copy(countdown = i - 1)
                    SoundManager.playButtonClick()
                }
            }
            delay(1000L)
            if (gameState.isInCountdown && !gameState.isPaused) {
                gameState = gameState.copy(isInCountdown = false)
                gameState.currentAction?.playSound()
            }
        }
    }

    LaunchedEffect(gameState.isInIntermission, gameState.isPaused) {
        if (gameState.isInIntermission && !gameState.isPaused) {
            var remainingTime = gameState.intermissionCountdown

            while (remainingTime > 0 && gameState.isInIntermission && !gameState.isPaused) {
                delay(1000L)
                if (gameState.isInIntermission && !gameState.isPaused) {
                    remainingTime--
                    gameState = gameState.copy(intermissionCountdown = remainingTime)
                }
            }

            if (gameState.isInIntermission && !gameState.isPaused) {
                gameState = gameState.copy(isInIntermission = false)
                SoundManager.playButtonClick()
                nextAction(gameState) { newState -> gameState = newState }
            }
        }
    }

    LaunchedEffect(gameState.isGameActive, gameState.isPaused, gameState.isInCountdown, gameState.isInIntermission) {
        if (!gameState.isGameActive) {
            MusicManager.stopGameplayMusic()
        }
        if (gameState.isGameActive && !gameState.isPaused && !gameState.isInCountdown) {
            while (gameState.isGameActive && gameState.totalTimeRemaining > 0 && !gameState.isPaused && !gameState.isInCountdown && !gameState.isInIntermission) {
                delay(16L)

                if (gameState.isInActionGap) {
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
            startGame { newState -> gameState = newState.copy(
                countdown = 3,
                isInCountdown = true,
                isPaused = false
            ) }
            MusicManager.startGameplayMusic(context)
        }
    }

    val onMenuClick = {
        if (gameState.isGameActive && !gameState.isInCountdown) {
            gameState = gameState.copy(isPaused = true)
            showPauseMenu = true
            MusicManager.pauseGameplayMusic()
        }
    }

    val onResumeGame = {
        showPauseMenu = false

        if (gameState.isInIntermission) {
            gameState = gameState.copy(isPaused = false)
        } else {
            gameState = gameState.copy(
                isPaused = false,
                isInCountdown = true,
                countdown = 3
            )
        }
        MusicManager.resumeGameplayMusic()
    }

    val onBackToHome = {
        showPauseMenu = false
        MusicManager.stopGameplayMusic()
        MusicManager.startMenuMusic(context)
        onNavigateToHome()
    }

    if (!gameState.isGameActive) {
        AlertDialog(
            onDismissRequest = {/* Don't allow dismiss by clicking outside */},
            title = { Text(
                "Game Over",
                style = MaterialTheme.typography.labelLarge
            ) },
            text = { Text(
                "Final Score: ${gameState.score}",
                style = MaterialTheme.typography.labelMedium
            ) },
            confirmButton = {
                Button(
                    onClick = {
                        MusicManager.startGameplayMusic(context)
                        startGame { newState -> gameState = newState.copy(
                            countdown = 3,
                            isInCountdown = true,
                            isPaused = false
                        ) }
                    }
                ) {
                    Text("Play Again",
                        style = MaterialTheme.typography.labelSmall)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        MusicManager.startMenuMusic(context)
                        onNavigateToHome()
                    }
                ) {
                    Text("Main Menu",
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        )
    }

    if (showPauseMenu) {
        PauseMenuDialog(
            onResume = onResumeGame,
            onBackToHome = onBackToHome
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        GameArea(
            gameState = gameState,
            onActionPerformed = { action ->
                handleAction(action, gameState) { newState -> gameState = newState }
            }
        )

        GameHeader(
            score = gameState.score,
            totalTimeRemaining = gameState.totalTimeRemaining,
            onMenuClick = onMenuClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}