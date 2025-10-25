package com.cs407.whaap_it.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.whaap_it.R
import com.cs407.whaap_it.ui.theme.WhaapitTheme

/**
 * The settings page is where the app settings can be configured
 */
@Composable
fun SettingsScreen( // TODO: add navigation function back to home as param
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize(), // Fill entire screen
    )
    {
        // Title Header
        Text( // TODO: maintain styling consistency w/ rest of the app
            text = stringResource(id = R.string.settings_page_title),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(24.dp)
        )

        Cardfolio()
    }

}

/**
 * Use this to preview the SettingsScreen()
 */
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    WhaapitTheme {
        SettingsScreen()
    }
}

/**
 * This is the visual Card container that houses the settings options
 */
@Composable
fun Cardfolio() {
    Box( // Container
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ){
        ElevatedCard( // Card
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ){
            // TODO: add Tabs to navigate to different settings type
            SettingsTab()
        }
    }
}

/**
 * This will display all the settings options related to gameplay
 * such as toggles for tilt, microphone, etc.
 */
@Composable
fun SettingsTab() {
    // TODO: fix data class, toggle logic
    var settings by remember { mutableStateOf(GameSettings()) }

    LazyColumn( // Vertical scroll container
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SettingsToggleRow(
                title = "Tilt Feature",
                isChecked = settings.enableTilt,
                onCheckedChange = { isEnabled ->
                    settings = settings.copy(enableTilt = isEnabled)
                }
            )
        }
        item {
            SettingsToggleRow(
                title = "Microphone",
                isChecked = settings.enableTilt,
                onCheckedChange = { isEnabled ->
                    settings = settings.copy(enableTilt = isEnabled)
                }
            )
        }
    }
}

/**
 * Displays each setting option with a toggle button
 */
@Composable
fun SettingsToggleRow(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .padding(36.dp)
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors() // TODO: change the toggle color
        )
    }
}

/**
 * Holds the toggle state of each game settings option
 */
data class GameSettings(
    val enableTilt: Boolean = false,
    val useMicrophone: Boolean = false,
    val hapticFeedback: Boolean = true
)
