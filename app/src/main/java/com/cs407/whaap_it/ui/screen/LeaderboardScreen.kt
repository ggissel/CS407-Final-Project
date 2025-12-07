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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    viewModel: LeaderboardViewModel,
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
                .padding(vertical = 16.dp)
        ) {
            IconButton(onClick = {
                SoundManager.playButtonClick() // Play button click sound
                onNavigateToHome()
            },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary)) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back to home",
                    tint = Color.White
                )
            }

            // Header Title
            Box(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp),
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

        val leaderboardState by viewModel.leaderboardState
        val currentList = leaderboardState.leaderboard
            .mapIndexed { index, pair ->
                LeaderboardEntry(index + 1, pair.first, pair.second)
            }

        LeaderboardCard(entries = currentList)


    }
}



/**
 * Card displaying the tabbed leaderboard content
 */
@Composable
fun LeaderboardCard(
    entries: List<LeaderboardEntry>
) {

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


                LeaderboardList(entries)

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