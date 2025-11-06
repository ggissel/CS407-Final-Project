package com.cs407.whaap_it.ui.screen

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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    userState: UserState?,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingName by remember { mutableStateOf(false) }
    var newDisplayName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isUpdating by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(vertical = 16.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(48.dp))
        }

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
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Icon",
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    when (userState) {
                        is UserState.Success -> {
                            if (isEditingName) {
                                // Edit mode
                                OutlinedTextField(
                                    value = newDisplayName,
                                    onValueChange = {
                                        newDisplayName = it
                                        errorMessage = ""
                                    },
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
                                        onClick = {
                                            isEditingName = false
                                            newDisplayName = ""
                                            errorMessage = ""
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        enabled = !isUpdating
                                    ) {
                                        Text("CANCEL")
                                    }

                                    Button(
                                        onClick = {
                                            if (newDisplayName.isBlank()) {
                                                errorMessage = "Name cannot be empty"
                                                return@Button
                                            }
                                            if (newDisplayName.length > 20) {
                                                errorMessage = "Name must be 20 characters or less"
                                                return@Button
                                            }

                                            isUpdating = true
                                            val user = FirebaseAuth.getInstance().currentUser
                                            val profileUpdates =
                                                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                                    .setDisplayName(newDisplayName)
                                                    .build()

                                            user?.updateProfile(profileUpdates)
                                                ?.addOnCompleteListener { task ->
                                                    isUpdating = false
                                                    if (task.isSuccessful) {
                                                        isEditingName = false
                                                        newDisplayName = ""
                                                        errorMessage = ""
                                                    } else {
                                                        errorMessage = "Failed to update name"
                                                    }
                                                }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        enabled = !isUpdating
                                    ) {
                                        Text(if (isUpdating) "SAVING..." else "SAVE")
                                    }
                                }
                            } else {
                                // Display mode
                                Text(
                                    text = FirebaseAuth.getInstance().currentUser?.displayName
                                        ?: userState.displayName,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = userState.email,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                OutlinedButton(
                                    onClick = {
                                        isEditingName = true
                                        newDisplayName =
                                            FirebaseAuth.getInstance().currentUser?.displayName
                                                ?: userState.displayName
                                    },
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

                            Spacer(modifier = Modifier.height(48.dp))

                            OutlinedButton(
                                onClick = onLogout,
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
