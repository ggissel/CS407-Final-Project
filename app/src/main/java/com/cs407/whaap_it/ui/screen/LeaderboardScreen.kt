package com.cs407.whaap_it.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.whaap_it.R
import com.cs407.whaap_it.ui.theme.WhaapitTheme
import com.cs407.whaap_it.ui.viewModels.LeaderboardViewModel
import com.cs407.whaap_it.util.SoundManager

/**
 * Leaderboard main entry
 */
data class LeaderboardEntry(
    val rank: Int,
    val playerName: String,
    val score: Int
)
/**
 * Main screen for displaying leaderboards
 */
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top bar (same as SettingsScreen)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 16.dp)
        ) {
            IconButton(onClick = {
                SoundManager.playButtonClick() // Play button click sound
                onNavigateToHome()
            }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back to home",
                    tint = Color.White
                )
            }

            // Header Title
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.leaderboard_page_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(48.dp))  // same width as IconButton for symmetry
        }

        LeaderboardCard(viewModel)
    }
}

/**
 * Use this to preview the LeaderboardScreen()
 */
@Preview(showBackground = true)
@Composable
fun LeaderboardScreenPreview() {
    WhaapitTheme {
        LeaderboardScreen()
    }
}

/**
 * Card displaying the tabbed leaderboard content
 */
@Composable
fun LeaderboardCard(
    viewModel: LeaderboardViewModel = viewModel()
) {

    val leaderboardState by viewModel.leaderboardState

    // For adding tabs (maybe)
    // val tabItems = listOf("Local","Global")
    // var selectedTabIndex by remember { mutableIntStateOf(0) }

    viewModel.addScore("person1", 70)
    viewModel.addScore("person2", 65)
    viewModel.addScore("person3", 60)
    viewModel.addScore("person4", 55)
    viewModel.addScore("person5", 50)
    viewModel.addScore("person6", 45)
    viewModel.addScore("person7", 40)
    viewModel.addScore("person8", 35)
    viewModel.addScore("person9", 30)
    viewModel.addScore("person10", 30)
    viewModel.addScore("person11", 25)
    viewModel.addScore("person12", 20)
    viewModel.addScore("person13", 15)
    viewModel.addScore("person14", 10)
    viewModel.addScore("person15", 5)

    val currentList = leaderboardState.leaderboard
        .mapIndexed { index, pair ->
            LeaderboardEntry(
                rank = index + 1,
                playerName = pair.first,
                score = pair.second
            )
        }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {


                LeaderboardList(entries = currentList)

            }
        }
    }
}

/**
 * LazyColumn displaying leaderboard entries
 */
@Composable
fun LeaderboardList(entries: List<LeaderboardEntry>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        items(entries) { entry ->
            LeaderboardRow(entry)
        }
    }
}

/**
 * A single leaderboard row (player rank, name, and score)
 */
@Composable
fun LeaderboardRow(entry: LeaderboardEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Display Player Rank
        Text(
            text = "#${entry.rank}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        // Display Player Name
        Text(
            text = entry.playerName,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp)
        )
        // Display Player Score
        Text(
            text = entry.score.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}