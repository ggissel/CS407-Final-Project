package com.cs407.whaap_it.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.whaap_it.auth.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ProfileViewModel : ViewModel() {

    // Firebase Authentication instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // UI State - immutable for external observers
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()


    fun initialize(userState: UserState?) {
        if (userState is UserState.Success) {
            _uiState.value = _uiState.value.copy(
                email = userState.email,
                displayName = auth.currentUser?.displayName ?: userState.displayName
            )
        }
    }


    fun startEditingName() {
        _uiState.value = _uiState.value.copy(
            isEditingName = true,
            newDisplayName = _uiState.value.displayName,
            errorMessage = ""
        )
    }


    fun cancelEditingName() {
        _uiState.value = _uiState.value.copy(
            isEditingName = false,
            newDisplayName = "",
            errorMessage = ""
        )
    }


    fun onDisplayNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            newDisplayName = name,
            errorMessage = "" // Clear error on new input
        )
    }


    fun saveDisplayName() {
        val currentState = _uiState.value
        val newName = currentState.newDisplayName

        // Validate input
        if (newName.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Name cannot be empty")
            return
        }
        if (newName.length > 20) {
            _uiState.value = currentState.copy(errorMessage = "Name must be 20 characters or less")
            return
        }

        // Start update process
        _uiState.value = currentState.copy(isUpdating = true)

        viewModelScope.launch {
            updateFirebaseDisplayName(newName) { success ->
                if (success) {
                    _uiState.value = ProfileUiState(
                        email = currentState.email,
                        displayName = newName,
                        isEditingName = false,
                        newDisplayName = "",
                        errorMessage = "",
                        isUpdating = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to update name",
                        isUpdating = false
                    )
                }
            }
        }
    }


    private fun updateFirebaseDisplayName(newName: String, onComplete: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onComplete(false)
            return
        }

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }


    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }
}


data class ProfileUiState(
    val email: String = "",
    val displayName: String = "",
    val isEditingName: Boolean = false,
    val newDisplayName: String = "",
    val errorMessage: String = "",
    val isUpdating: Boolean = false
)