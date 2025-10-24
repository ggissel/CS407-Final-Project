package com.cs407.whaap_it.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
        )

        // TODO: Create an ElevatedCard to hold all the toggle buttons, etc.
    }

}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    WhaapitTheme {
        SettingsScreen()
    }
}
