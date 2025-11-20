package com.cs407.whaap_it.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.whaap_it.auth.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    // Firebase Authentication instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // UI State - immutable for external observers
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()


    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = "" // Clear error on new input
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = "" // Clear error on new input
        )
    }

    fun onLoginSignUp(onSuccess: (UserState) -> Unit) {
        val currentState = _uiState.value

        // Clear any existing error
        _uiState.value = currentState.copy(errorMessage = "")

        // Validate email
        val emailResult = checkEmail(currentState.email)
        when (emailResult) {
            EmailResult.Empty -> {
                _uiState.value = currentState.copy(errorMessage = "Email cannot be empty")
                return
            }
            EmailResult.Invalid -> {
                _uiState.value = currentState.copy(errorMessage = "Invalid email format")
                return
            }
            EmailResult.Valid -> {} // Continue
        }

        // Validate password
        val passwordResult = checkPassword(currentState.password)
        when (passwordResult) {
            PasswordResult.Empty -> {
                _uiState.value = currentState.copy(errorMessage = "Password cannot be empty")
                return
            }
            PasswordResult.Short -> {
                _uiState.value = currentState.copy(errorMessage = "Password must be at least 6 characters")
                return
            }
            PasswordResult.Invalid -> {
                _uiState.value = currentState.copy(
                    errorMessage = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
                )
                return
            }
            PasswordResult.Valid -> {} // Continue
        }

        // Perform authentication
        _uiState.value = currentState.copy(isLoading = true)

        viewModelScope.launch {
            signInOrCreateAccount(
                email = currentState.email,
                password = currentState.password,
                onComplete = { success, displayName ->
                    if (success) {
                        onSuccess(UserState.Success(currentState.email, displayName ?: "Player"))
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Authentication failed. Please try again."
                        )
                    }
                }
            )
        }
    }

    private fun signInOrCreateAccount(
        email: String,
        password: String,
        onComplete: (success: Boolean, displayName: String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    onComplete(true, user?.displayName)
                } else {
                    // If sign in fails, try to create a new account
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


    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)