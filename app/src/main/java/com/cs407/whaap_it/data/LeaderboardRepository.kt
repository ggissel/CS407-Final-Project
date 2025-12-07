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
            // Use userId as document ID to prevent duplicates
            leaderboardCollection.document(entry.userId)
                .set(entry)
                .await()
            Log.d(TAG, "Score submitted successfully for user: ${entry.displayName}")
            true
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