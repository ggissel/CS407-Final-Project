package com.cs407.whaap_it

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.whaap_it.ui.screen.SettingsScreen
import com.cs407.whaap_it.ui.screen.GameScreen

import com.cs407.whaap_it.ui.theme.WhaapitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhaapitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    // Creates and remembers a NavController to manage navigation state
    val navController = rememberNavController()

    // NavHost sets up the navigation graph for the app
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Controller that handles navigation
        // First screen to display when app starts
        // Defines the "home" route and what UI to display there
        composable("home") {
            StartScreen(
                onNavigateToGameScreen = {navController.navigate("game")},
                onNavigateToSettings = { navController.navigate("settings") },
            ) // Displays the HomeScreen composable
        }
        composable("settings") {
            SettingsScreen(
                onNavigateToHome = { navController.navigate("home") }
            )
        }

        composable("game") {
            GameScreen(
                onNavigateToHome = {
                    navController.navigate("home")
                }

            )
        }

    }
}

@Composable
fun StartScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToGameScreen: () -> Unit = {},
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
            onClick = { onNavigateToGameScreen() },
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
            onClick = { /* TODO: Navigate to leaderboard screen */ },
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
            onClick = { onNavigateToSettings() },
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