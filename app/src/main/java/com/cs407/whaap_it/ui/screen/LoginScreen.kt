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


@Composable
fun ErrorText(error: String?, modifier: Modifier = Modifier) {
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
fun EmailField(
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
fun PasswordField(
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
fun LoginSignUpButton(
    email: String,
    password: String,
    onErrorChange: (String) -> Unit,
    onSuccess: (UserState) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

    Button(
        onClick = {
            onErrorChange("")

            val emailResult = checkEmail(email)
            if (emailResult == EmailResult.Empty) {
                onErrorChange("Email cannot be empty")
                return@Button
            } else if (emailResult == EmailResult.Invalid) {
                onErrorChange("Invalid email format")
                return@Button
            }

            val passwordResult = checkPassword(password)
            if (passwordResult == PasswordResult.Empty) {
                onErrorChange("Password cannot be empty")
                return@Button
            } else if (passwordResult == PasswordResult.Short) {
                onErrorChange("Password must be at least 6 characters")
                return@Button
            } else if (passwordResult == PasswordResult.Invalid) {
                onErrorChange("Password must contain at least one uppercase letter, one lowercase letter, and one digit")
                return@Button
            }

            if (emailResult == EmailResult.Valid && passwordResult == PasswordResult.Valid) {
                isLoading = true
                signInWithCallback(email, password) { success, displayName ->
                    isLoading = false
                    if (success) {
                        onSuccess(UserState.Success(email, displayName ?: "Player"))
                    } else {
                        onErrorChange("Authentication failed. Please try again.")
                    }
                }
            }
        },
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

fun signInWithCallback(
    email: String,
    password: String,
    onComplete: (success: Boolean, displayName: String?) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                onComplete(true, user?.displayName)
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val user = auth.currentUser
                        onComplete(true, user?.displayName)
                    }
                    .addOnFailureListener {
                        onComplete(false, null)
                    }
            }
        }
}

@Composable
fun LoginScreen(
    onLoginSuccess: (UserState) -> Unit,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

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
                    Text(
                        text = "WHAAP IT!",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Login or Sign Up",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    ErrorText(errorMsg)

                    Spacer(modifier = Modifier.height(8.dp))

                    EmailField(email = email, onValueChange = { email = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    PasswordField(password = password, onValueChange = { password = it })
                    Spacer(modifier = Modifier.height(24.dp))

                    LoginSignUpButton(
                        email = email,
                        password = password,
                        onErrorChange = { errorMsg = it },
                        onSuccess = onLoginSuccess
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    WhaapitTheme {
        LoginScreen(onLoginSuccess = {})
    }
}
