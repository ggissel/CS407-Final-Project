package com.cs407.whaap_it.ui.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class LeaderboardState(
    val leaderboard: List<Pair<String, Int>> = emptyList()
)

class LeaderboardViewModel {
    private val _leaderboardState = mutableStateOf(LeaderboardState())
    val leaderboardState: MutableState<LeaderboardState> = _leaderboardState

    fun updateLeaderboard(newLeaderboard: List<Pair<String, Int>>) {
        _leaderboardState.value = LeaderboardState(
            leaderboard = newLeaderboard.sortedByDescending { it.second }
        )
    }

    fun addScore(name: String, score: Int) {
        val currentList = _leaderboardState.value.leaderboard.toMutableList()

        // Check if player exists
        val existingIndex = currentList.indexOfFirst { it.first == name }

        if (existingIndex != -1) {
            // If existing, update score
            currentList[existingIndex] = name to score
        } else {
            // Else add new player
            currentList.add(name to score)
        }

        // Update leaderboard
        updateLeaderboard(currentList)
    }

    fun removeScore(name: String) {
        val updatedList = _leaderboardState.value.leaderboard.filterNot { it.first == name }
        // Update leaderboard
        updateLeaderboard(updatedList)
    }

    fun clearLeaderboard() {
        updateLeaderboard(emptyList())
    }

    fun getLeaderboard(): List<Pair<String, Int>> {
        return _leaderboardState.value.leaderboard
    }

    fun getScore(name: String): Int {
        return _leaderboardState.value.leaderboard.find { it.first == name }?.second ?: 0
    }

    fun getRank(name: String): Int {
        val sorted = _leaderboardState.value.leaderboard
        return sorted.indexOfFirst { it.first == name }.let { index ->
            if (index != -1) index + 1 else 0
        }
    }

    fun getTopScores(limit: Int = 3): List<Pair<String, Int>> {
        return _leaderboardState.value.leaderboard.take(limit)
    }
}