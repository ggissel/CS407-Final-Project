package com.cs407.whaap_it.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

        // TODO: Create an ElevatedCard to hold all the toggle buttons, etc.
        Cardfolio()
    }

}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    WhaapitTheme {
        SettingsScreen()
    }
}

@Composable
fun Cardfolio() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ){
        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ){

        }
    }
}
