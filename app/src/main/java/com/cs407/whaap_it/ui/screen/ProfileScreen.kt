package com.cs407.whaap_it.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.whaap_it.auth.UserState
import com.cs407.whaap_it.util.SoundManager
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip

@Composable
fun ProfileScreen(
    userState: UserState?,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    // Initialize ViewModel with user data
    LaunchedEffect(userState) {
        viewModel.initialize(userState)
    }

    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        // Top app bar with back button
        ProfileTopBar(onNavigateBack = onNavigateBack)

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
                    // Profile icon
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Icon",
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    when (userState) {
                        is UserState.Success -> {
                            if (uiState.isEditingName) {
                                // Edit mode
                                ProfileEditMode(
                                    displayName = uiState.newDisplayName,
                                    onDisplayNameChange = viewModel::onDisplayNameChange,
                                    errorMessage = uiState.errorMessage,
                                    isUpdating = uiState.isUpdating,
                                    onCancel = {
                                        SoundManager.playButtonClick()
                                        viewModel.cancelEditingName()
                                    },
                                    onSave = {
                                        SoundManager.playButtonClick()
                                        viewModel.saveDisplayName()
                                    }
                                )
                            } else {
                                // Display mode
                                ProfileDisplayMode(
                                    displayName = uiState.displayName,
                                    email = uiState.email,
                                    onEditName = {
                                        SoundManager.playButtonClick()
                                        viewModel.startEditingName()
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(48.dp))

                            // Logout button
                            OutlinedButton(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    onLogout()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text(
                                    text = "LOG OUT",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        else -> {
                            Text(text = "Error: No user data")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileTopBar(onNavigateBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        IconButton(onClick = {
            SoundManager.playButtonClick()
            onNavigateBack()
        },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary)) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

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
                text = "Profile",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun ProfileDisplayMode(
    displayName: String,
    email: String,
    onEditName: () -> Unit
) {
    Text(
        text = displayName,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = email,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedButton(
        onClick = onEditName,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(
            text = "CHANGE NAME",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ProfileEditMode(
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    errorMessage: String,
    isUpdating: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    OutlinedTextField(
        value = displayName,
        onValueChange = onDisplayNameChange,
        label = { Text("Display Name") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )

    if (errorMessage.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            fontSize = 14.sp
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            enabled = !isUpdating
        ) {
            Text("CANCEL")
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            enabled = !isUpdating
        ) {
            Text(if (isUpdating) "SAVING..." else "SAVE")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        userState = UserState.Success(
            email = "user@example.com",
            displayName = "Player"
        ),
        onNavigateBack = {},
        onLogout = {}
    )
}
