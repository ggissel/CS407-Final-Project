package com.cs407.whaap_it.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.whaap_it.auth.*
import com.google.firebase.auth.FirebaseAuth
import com.cs407.whaap_it.ui.theme.WhaapitTheme
import com.cs407.whaap_it.util.SoundManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.whaap_it.ui.screen.login.LoginViewModel


@Composable
private fun ErrorText(error: String?, modifier: Modifier = Modifier) {
    if (!error.isNullOrEmpty()) {
        Text(
            text = error,
            color = Color.Red,
            textAlign = TextAlign.Center,
            modifier = modifier
        )
    }
}

@Composable
private fun EmailField(
    modifier: Modifier = Modifier,
    email: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = onValueChange,
        label = { Text("Email") },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun PasswordField(
    modifier: Modifier = Modifier,
    password: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = password,
        onValueChange = onValueChange,
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun LoginSignUpButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isLoading
    ) {
        Text(
            text = if (isLoading) "SIGNING IN..." else "LOG IN / SIGN UP",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun LoginScreen(
    onLoginSuccess: (UserState) -> Unit,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        // Top app bar with back button
        LoginTopBar(onNavigateBack = onNavigateBack)

        // Main content card
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Title
                    Text(
                        text = "WHAAP IT!",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subtitle
                    Text(
                        text = "Login or Sign Up",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Error message display
                    ErrorText(error = uiState.errorMessage)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email input field
                    EmailField(
                        email = uiState.email,
                        onValueChange = viewModel::onEmailChange
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password input field
                    PasswordField(
                        password = uiState.password,
                        onValueChange = viewModel::onPasswordChange
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login/Sign up button
                    LoginSignUpButton(
                        isLoading = uiState.isLoading,
                        onClick = {
                            SoundManager.playButtonClick() // Play button click sound
                            viewModel.onLoginSignUp(onLoginSuccess)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginTopBar(onNavigateBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(vertical = 16.dp)
    ) {
        IconButton(onClick = {
            SoundManager.playButtonClick()
            onNavigateBack()
        }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back to Home",
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Login/Sign Up",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    WhaapitTheme {
        LoginScreen(onLoginSuccess = {})
    }
}
