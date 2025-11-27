package com.cs407.whaap_it

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cs407.whaap_it.auth.UserState
import com.cs407.whaap_it.ui.screen.LoginScreen
import com.cs407.whaap_it.ui.screen.profile.ProfileScreen
import com.cs407.whaap_it.ui.screen.SettingsScreen
import com.cs407.whaap_it.ui.screen.LeaderboardScreen
import com.cs407.whaap_it.ui.screen.GameScreen
import com.cs407.whaap_it.ui.theme.WhaapitTheme
import com.cs407.whaap_it.util.SoundManager
import com.google.firebase.auth.FirebaseAuth
import com.cs407.whaap_it.util.MusicManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        SoundManager.init(this)  // Initializes SoundManager singleton object to handle component sounds
        MusicManager.startMenuMusic(this) //start stolen_menu_music

        setContent {
            WhaapitTheme {
                AppNavigation()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        MusicManager.pauseMenuMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicManager.stopMenuMusic()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var currentUser by remember { mutableStateOf<UserState?>(null) }
    val auth = FirebaseAuth.getInstance()

    // Check if user is already logged in
    if (auth.currentUser != null) {
        currentUser = UserState.Success(
            email = auth.currentUser?.email ?: "",
            displayName = auth.currentUser?.displayName ?: "Player"
        )
    }

    // Always start at home - guest mode by default
    val startDestination = "home"

    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentRoute == "home") {
                TopAppBar(
                    title = { },
                    actions = {
                        IconButton(onClick = {
                            SoundManager.playButtonClick() // Play button click sound
                            // Navigate to profile if logged in, otherwise to login
                            if (currentUser != null) {
                                navController.navigate("profile")
                            } else {
                                navController.navigate("login")
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { userState ->
                        currentUser = userState
                        // Navigate back to previous screen or home
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("home") {
                StartScreen(
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToGameScreen = { navController.navigate("game") },
                    onNavigateToLeaderboard = { navController.navigate("Leaderboard") }
                )
            }

            composable("game") {
                GameScreen(
                    onNavigateToHome = {
                        navController.navigate("home")
                    }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onNavigateToHome = { navController.navigate("home") }
                )
            }

            composable("Leaderboard") {
                LeaderboardScreen(
                    onNavigateToHome = { navController.navigate("home") }
                )
            }

            composable("profile") {
                // If no user is logged in, this route shouldn't be accessible
                // Navigation logic in the profile button handles redirecting to login
                if (currentUser != null) {
                    ProfileScreen(
                        userState = currentUser,
                        onNavigateBack = { navController.popBackStack() },
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            currentUser = null
                            navController.navigate("home") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    // Press animations
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.5f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    val buttonModifier = modifier
        .scale(scale)
        .offset(y = floatOffset.dp)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    try {
                        awaitRelease()
                    } finally {
                        isPressed = false
                    }
                },
                onTap = { onClick() }
            )
        }

    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = buttonModifier,
            shape = RoundedCornerShape(12.dp)
        ) {
            content()
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            shape = RoundedCornerShape(12.dp)
        ) {
            content()
        }
    }
}

@Composable
fun StartScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToGameScreen: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "whaap it logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(400.dp)
            )
        }

        /**
         *         Button(
         *             onClick = {
         *                 SoundManager.playButtonClick() // Play button click sound
         *                 MusicManager.stopMenuMusic() //stop menu music when entering GameScreen
         *                 onNavigateToGameScreen()},
         *             modifier = Modifier
         *                 .fillMaxWidth()
         *                 .height(56.dp)
         *         ) {
         *             Icon(
         *                 imageVector = Icons.Default.PlayArrow,
         *                 contentDescription = "Play",
         *                 modifier = Modifier.size(24.dp)
         *             )
         *             Spacer(modifier = Modifier.width(8.dp))
         *             Text(
         *                 text = "PLAY",
         *                 fontSize = 20.sp,
         *                 fontWeight = FontWeight.Bold
         *             )
         *         }
         */

        // Play Button (Primary)
        SimpleFloatingButton (
            onClick = {
                SoundManager.playButtonClick()
                MusicManager.stopMenuMusic()
                onNavigateToGameScreen()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            isPrimary = true
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PLAY",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                SoundManager.playButtonClick() // Play button click sound
                onNavigateToLeaderboard()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "Leaderboard",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "LEADERBOARD",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                SoundManager.playButtonClick() // Play button click sound
                onNavigateToSettings()
                      },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SETTINGS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    WhaapitTheme {
        StartScreen()
    }
}