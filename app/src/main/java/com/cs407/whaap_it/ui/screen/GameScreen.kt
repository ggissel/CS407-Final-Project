package com.cs407.whaap_it.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cs407.whaap_it.util.SoundManager
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GameScreen(
    navController: NavController? = null,
    onNavigateToHome: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(Color.Yellow)
        ) {

            IconButton(
                onClick = {
                    SoundManager.playButtonClick() // Play button click sound
                    onNavigateToHome()
                          },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Home", tint = Color.Black)
            }
            Text(
                text = "Twist It!",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 0.dp),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Blue)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-225).dp)
                    .size(300.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.Red, CircleShape)
                    .clickable {
                        /*TODO*/
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Whaap It!",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Pull It!",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 200.dp),
                fontSize = 40.sp,
                color = Color.White
            )
        }
    }
}