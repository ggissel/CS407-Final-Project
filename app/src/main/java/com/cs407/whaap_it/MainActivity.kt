package com.cs407.whaap_it

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
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
import com.cs407.whaap_it.ui.screen.ProfileScreen
import com.cs407.whaap_it.ui.screen.SettingsScreen
import com.cs407.whaap_it.ui.screen.LeaderboardScreen
import com.cs407.whaap_it.ui.screen.GameScreen
import com.cs407.whaap_it.ui.theme.WhaapitTheme
import com.cs407.whaap_it.util.SoundManager
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SoundManager.init(this) // Initializes SoundManager singleton object to handle component sounds
        setContent {
            WhaapitTheme {
                AppNavigation()
            }
        }
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
        Text(
            text = "WHAAP IT!",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {
                SoundManager.playButtonClick() // Play button click sound
            /* TODO: Navigate to game screen */
                onNavigateToGameScreen()},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
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