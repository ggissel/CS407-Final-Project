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
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.whaap_it.R
import com.cs407.whaap_it.ui.theme.WhaapitTheme
import com.cs407.whaap_it.ui.viewModels.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.whaap_it.ui.viewModels.SettingsViewModelFactory
import com.cs407.whaap_it.util.SoundManager

/**
 * The settings page is where the app settings can be configured
 */
@Composable
fun SettingsScreen(
    onNavigateToHome: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize(), // Fill entire screen
    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(vertical = 16.dp)
        ) {
            IconButton(onClick = {
                SoundManager.playButtonClick() // Play button click sound
                onNavigateToHome()
            }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back to Home",
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
                    text = stringResource(id = R.string.settings_page_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(48.dp))
        }


        Cardfolio(viewModel = viewModel)
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
fun Cardfolio(
    viewModel: SettingsViewModel
) {
    val settings by viewModel.settingsState.collectAsState()
    val gameplaySettings = listOf(
        SettingItem.ToggleItem(
            title = "Shake Feature",
            isChecked = settings.enableShake,
            onToggleChange = { enabled -> viewModel.toggleShake(enabled) }
        ),
        SettingItem.ToggleItem(
            title = "Flip Feature",
            isChecked = settings.enableFlip,
            onToggleChange = { enabled -> viewModel.toggleFlip(enabled) }
        ),
        SettingItem.ToggleItem(
            title = "Microphone",
            isChecked = settings.useMicrophone,
            onToggleChange = { enabled -> viewModel.toggleMic(enabled) }
        ),
        SettingItem.ToggleItem(
            title = "Haptic Feedback",
            isChecked = settings.hapticFeedback,
            onToggleChange = { enabled -> viewModel.toggleHaptic(enabled) }
        ),
        SettingItem.ToggleItem(
            title = "Pinch Gesture",
            isChecked = settings.pinchGesture,
            onToggleChange = { enabled -> viewModel.togglePinch(enabled) }
        ),
        SettingItem.ToggleItem(
            title = "Swipe Gesture",
            isChecked = settings.swipeGesture,
            onToggleChange = { enabled -> viewModel.toggleSwipe(enabled) }
        ),
        SettingItem.ToggleItem(
            title = "Double Tap Gesture",
            isChecked = settings.doubleTap,
            onToggleChange = { enabled -> viewModel.toggleDoubleTap(enabled) }
        ),
        SettingItem.ToggleItem(
            title = "Long Press Gesture",
            isChecked = settings.longPress,
            onToggleChange = { enabled -> viewModel.toggleLongPress(enabled) }
        ),
    )

    val musicAudioSettings = listOf(
        SettingItem.ToggleItem(
            title = "Toggle Music",
            isChecked = settings.enableMusic,
            onToggleChange = { enabled -> viewModel.toggleMusic(enabled) }
        ),
        SettingItem.SliderItem(
            title = "Music Volume",
            value = settings.musicVolume,
            valueRange = 0f..1f,
            onValueChange = { volume -> viewModel.setMusicVolume(volume) }
        ),
        SettingItem.SliderItem(
            title = "App Volume",
            value = settings.appVolume,
            valueRange = 0f..1f,
            onValueChange = { volume -> viewModel.setAppVolume(volume) }
        ),
        SettingItem.ToggleItem(
            title = "Groovy Mode",
            isChecked = settings.groovyMode,
            onToggleChange = { enabled -> viewModel.toggleGroovy(enabled) }
        ),
    )

    val tabItems = listOf(
        TabItem(
            title = "Gameplay"
        ),
        TabItem(
            title = "Music / Audio"
        ),
    )

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
            var selectedTabIndex by remember { mutableIntStateOf(0) }
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Different Tab Settings
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    tabItems.forEachIndexed { index, item ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium)
                            }
                        )
                    }
                }
                if (selectedTabIndex == 0) { // Select Gameplay Tab
                    SettingsTab(gameplaySettings)
                } else if (selectedTabIndex == 1) { // Select Music/Audio Tab
                    SettingsTab(musicAudioSettings)
                }
            }
        }
    }
}

/**
 * The LazyColumn of Settings options displayed
 */
@Composable
fun SettingsTab(
    settingsType: List<SettingItem>
) {
    LazyColumn( // Vertical scroll container
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(settingsType) { settings ->
            when (settings) {
                is SettingItem.ToggleItem -> SettingsToggleRow(
                    title = settings.title,
                    isChecked = settings.isChecked,
                    onCheckedChange = settings.onToggleChange
                )

                is SettingItem.SliderItem -> SettingsSliderRow(
                    title = settings.title,
                    value = settings.value,
                    onValueChange = settings.onValueChange,
                    valueRange = settings.valueRange
                )
            }
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
            .padding(vertical = 24.dp, horizontal = 32.dp)
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { newCheckedState ->
                if (!newCheckedState) {
                    SoundManager.playToggleOnSound()
                } else {
                    SoundManager.playToggleOffSound()
                }
                onCheckedChange(newCheckedState)
                              },
            colors = SwitchDefaults.colors() // TODO: change the toggle color
        )
    }
}

/**
 * Displays each setting option with a slider
 */
@Composable
fun SettingsSliderRow(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .padding(36.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Slider( // TODO: change slider style
                value = value,
                valueRange = valueRange,
                onValueChange = onValueChange
            )
        }
    }
}

sealed class SettingItem {
    data class ToggleItem(
        val title: String,
        val isChecked: Boolean,
        val onToggleChange: (Boolean) -> Unit
    ) : SettingItem()

    data class SliderItem(
        val title: String,
        val value: Float,
        val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
        val onValueChange: (Float) -> Unit
    ) : SettingItem()
}

data class TabItem(
    val title: String
)