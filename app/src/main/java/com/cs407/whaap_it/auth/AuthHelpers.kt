package com.cs407.whaap_it.auth

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

/**
 * Authentication Helper
 * Contains all business logic for Firebase authentication
 */

// ============================================
// User State
// ============================================

sealed class UserState {
    data class Success(
        val email: String,
        val displayName: String = "Player"
    ) : UserState()

    data class Error(val message: String) : UserState()

    object Loading : UserState()

    object Idle : UserState()
}

// ============================================
// Email Validation
// ============================================

enum class EmailResult {
    Valid,
    Empty,
    Invalid
}

/**
 * Validate email format
 * 1. Username of email should only contain "0-9, a-z, _, A-Z, ."
 * 2. There is one and only one "@" between username and server address
 * 3. There are multiple domain names with at least one top-level domain
 * 4. Domain name "0-9, a-z, -, A-Z" (could not have "_" but "-" is valid)
 * 5. Multiple domains separate with '.'
 * 6. Top level domain should only contain letters and at least 2 letters
 */
fun checkEmail(email: String): EmailResult {
    if (email.isEmpty()) {
        return EmailResult.Empty
    }

    val pattern = Regex("^[\\w.]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")

    return if (pattern.containsMatchIn(email)) {
        EmailResult.Valid
    } else {
        EmailResult.Invalid
    }
}

// ============================================
// Password Validation
// ============================================

enum class PasswordResult {
    Valid,
    Empty,
    Short,
    Invalid
}

/**
 * Validate password strength
 * 1. Password should contain at least one uppercase letter, lowercase letter, one digit
 * 2. Minimum length: 6 characters (changed from 5 to match Firebase requirement)
 */
fun checkPassword(password: String): PasswordResult {
    if (password.isEmpty()) {
        return PasswordResult.Empty
    }

    if (password.length < 6) {
        return PasswordResult.Short
    }

    val hasDigit = Regex("\\d+").containsMatchIn(password)
    val hasLowercase = Regex("[a-z]+").containsMatchIn(password)
    val hasUppercase = Regex("[A-Z]+").containsMatchIn(password)

    return if (hasDigit && hasLowercase && hasUppercase) {
        PasswordResult.Valid
    } else {
        PasswordResult.Invalid
    }
}

// ============================================
// Firebase Authentication Functions
// ============================================

/**
 * Sign in existing user with email and password
 * If sign-in fails, automatically attempts to create new account
 */
fun signIn(
    email: String,
    password: String,
    onSuccess: (UserState.Success) -> Unit,
    onFailure: (String) -> Unit
) {
    val auth = Firebase.auth
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail: success")
                val user = auth.currentUser
                if (user != null) {
                    onSuccess(UserState.Success(
                        email = user.email ?: email,
                        displayName = user.displayName ?: "Player"
                    ))
                } else {
                    onFailure("User data not available")
                }
            } else {
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                // Sign in failed, try creating account
                createAccount(email, password, onSuccess, onFailure)
            }
        }
}

/**
 * Create new Firebase account with email and password
 */
fun createAccount(
    email: String,
    password: String,
    onSuccess: (UserState.Success) -> Unit,
    onFailure: (String) -> Unit
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { result ->
            Log.d(TAG, "createUserWithEmail: success")
            val user = auth.currentUser
            if (user != null) {
                onSuccess(UserState.Success(
                    email = user.email ?: email,
                    displayName = user.displayName ?: "Player"
                ))
            } else {
                onFailure("User data not available")
            }
        }
        .addOnFailureListener { exception ->
            Log.w(TAG, "createUserWithEmail:failure", exception)
            onFailure(exception.message ?: "Failed to create account")
        }
}

/**
 * Update Firebase Auth displayName
 */
fun updateName(
    name: String,
    onSuccess: () -> Unit = {},
    onFailure: (Exception?) -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    if (user != null) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("updateName", "User profile updated successfully")
                    onSuccess()
                } else {
                    Log.w("updateName", "Failed to update user profile", task.exception)
                    onFailure(task.exception)
                }
            }
    } else {
        Log.w("updateName", "No authenticated user found")
        onFailure(null)
    }
}

/**
 * Sign out current user
 */
fun signOut() {
    FirebaseAuth.getInstance().signOut()
    Log.d(TAG, "User signed out")
}

/**
 * Get current authenticated user
 */
fun getCurrentUser(): UserState {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    return if (user != null) {
        UserState.Success(
            email = user.email ?: "",
            displayName = user.displayName ?: "Player"
        )
    } else {
        UserState.Idle
    }
}