package com.cs407.whaap_it.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date

object LeaderboardRepository {
    private const val TAG = "LeaderboardRepository"
    private val db = FirebaseFirestore.getInstance()
    private val leaderboardCollection = db.collection("leaderboard")

    suspend fun submitScore(entry: LeaderboardEntry): Boolean {
        return try {
            // Check Firestore database if user has a score on leaderboard
            val existingEntry = getUserScore(entry.userId)

            // If no existing score OR new score is higher, update the Firestore
            if (existingEntry == null || entry.score > existingEntry.score) {
                leaderboardCollection.document(entry.userId)
                    .set(entry)
                    .await()
                Log.d(TAG, "Score updated for user: ${entry.displayName} - New score: ${entry.score}")
                true
            } else {
                // New score is not higher, don't update
                Log.d(TAG, "Score not updated for user: ${entry.displayName} - Existing score: ${existingEntry.score} is higher than new score: ${entry.score}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to submit score: ${e.message}")
            false
        }
    }

    suspend fun getTopScores(limit: Int = 50): List<LeaderboardEntry> {
        return try {
            val result = leaderboardCollection
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val entries = result.toObjects(LeaderboardEntry::class.java)
            Log.d(TAG, "Loaded ${entries.size} leaderboard entries")
            entries
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load leaderboard: ${e.message}")
            emptyList()
        }
    }

    suspend fun getUserScore(userId: String): LeaderboardEntry? {
        return try {
            leaderboardCollection.document(userId)
                .get()
                .await()
                .toObject(LeaderboardEntry::class.java)
        } catch (e: Exception) {
            null
        }
    }
}