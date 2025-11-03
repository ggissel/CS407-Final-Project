package com.cs407.whaap_it.ui.screen

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.cs407.whaap_it.R
import com.cs407.whaap_it.ui.theme.WhaapitTheme

/**
 * The login/signup page where users can either create a new account or login to an existing one
 */
@Composable
fun LoginSignupScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val buttonClickSound: MediaPlayer = MediaPlayer.create(context, R.raw.ui_button_click_1)

    Scaffold(modifier) {
        innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Button(
                onClick = {
                    buttonClickSound.seekTo(0)
                    buttonClickSound.start()
                }
            ) {
                Text(stringResource(R.string.login_button))
            }
            Button(
                onClick = {
                    buttonClickSound.seekTo(0)
                    buttonClickSound.start()
                }
            ) {
                Text(stringResource(R.string.signup_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginSignupScreenPreview() {
    WhaapitTheme {
        LoginSignupScreen()
    }
}
