package com.cs407.whaap_it.ui.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.cs407.whaap_it.auth.saveHighscore
import com.cs407.whaap_it.auth.getHighscore

data class LeaderboardState(
    val leaderboard: List<Pair<String, Int>> = emptyList()
)

class LeaderboardViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _leaderboardState = mutableStateOf(LeaderboardState())
    val leaderboardState = _leaderboardState

    init {
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        db.collection("leaderboard")
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val name = doc.getString("displayName") ?: return@mapNotNull null
                        val score = (doc.getLong("score") ?: 0L).toInt()
                        name to score
                    }

                    _leaderboardState.value = LeaderboardState(list)
                }
            }
    }


    fun updateScore(score: Int) {
        val user = auth.currentUser ?: return
        val uid = user.uid
        val name = user.displayName ?: "Guest"

        val leaderboardRef = db.collection("leaderboard").document(uid)

        db.runTransaction { transaction ->
            val doc = transaction.get(leaderboardRef)
            val existingScore = doc.getLong("score")?.toInt() ?: 0

            // Only update if new score is higher
            if (score > existingScore) {
                val updated = mapOf(
                    "displayName" to name,
                    "score" to score
                )
                transaction.set(leaderboardRef, updated, SetOptions.merge())

                // also update personal saved highscore
                saveHighscore(uid, name, score) { success ->
                    Log.d("Leaderboard", "Personal highscore update result: $success")
                }
            }
        }.addOnSuccessListener {
            Log.d("Leaderboard", "Transaction successful")
        }.addOnFailureListener {
            Log.e("Leaderboard", "Transaction failed", it)
        }
    }



    fun updateLeaderboard(newLeaderboard: List<Pair<String, Int>>) {
        leaderboardState.value = LeaderboardState(
            leaderboard = newLeaderboard.sortedByDescending { it.second }
        )
    }

    fun addScore(name: String, score: Int) {
        val currentList = leaderboardState.value.leaderboard.toMutableList()

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
        val updatedList = leaderboardState.value.leaderboard.filterNot { it.first == name }
        // Update leaderboard
        updateLeaderboard(updatedList)
    }

    fun clearLeaderboard() {
        updateLeaderboard(emptyList())
    }

    fun getLeaderboard(): List<Pair<String, Int>> {
        return leaderboardState.value.leaderboard
    }

    fun getScore(name: String): Int {
        return leaderboardState.value.leaderboard.find { it.first == name }?.second ?: 0
    }

    fun getRank(name: String): Int {
        val sorted = leaderboardState.value.leaderboard
        return sorted.indexOfFirst { it.first == name }.let { index ->
            if (index != -1) index + 1 else 0
        }
    }

    fun getTopScores(limit: Int = 3): List<Pair<String, Int>> {
        return leaderboardState.value.leaderboard.take(limit)
    }
}