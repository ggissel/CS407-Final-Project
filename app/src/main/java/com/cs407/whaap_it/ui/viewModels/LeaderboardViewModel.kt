package com.cs407.whaap_it.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.whaap_it.data.LeaderboardEntry
import com.cs407.whaap_it.data.LeaderboardRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {

    private val _leaderboardState = MutableStateFlow(LeaderboardState())
    val leaderboardState: StateFlow<LeaderboardState> = _leaderboardState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val auth = FirebaseAuth.getInstance()

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val entries = LeaderboardRepository.getTopScores(50)
                _leaderboardState.value = LeaderboardState(
                    entries = entries,
                    error = null
                )
            } catch (e: Exception) {
                _leaderboardState.value = LeaderboardState(
                    entries = emptyList(),
                    error = "Failed to load leaderboard: ${e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitScore(score: Int, onResult: (Boolean, Boolean) -> Unit = { _, _ -> }) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Guest mode, not needed,
            onResult(false, false)
            return
        }

        val entry = LeaderboardEntry(
            userId = currentUser.uid,
            displayName = currentUser.displayName ?: "Anonymous",
            score = score,
            email = currentUser.email ?: ""
        )

        viewModelScope.launch {
            val success = LeaderboardRepository.submitScore(entry)
            if (success) {
                // Refresh leaderboard after successful submission
                loadLeaderboard()
                onResult(true, true)
            } else {
                onResult(false, false)
            }
        }
    }

    fun refresh() {
        loadLeaderboard()
    }

    data class LeaderboardState(
        val entries: List<LeaderboardEntry> = emptyList(),
        val error: String? = null
    )
}