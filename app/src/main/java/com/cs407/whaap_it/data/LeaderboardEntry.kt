package com.cs407.whaap_it.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class LeaderboardEntry(
    @PropertyName("userId")
    val userId: String = "",

    @PropertyName("displayName")
    val displayName: String = "",

    @PropertyName("score")
    val score: Int = 0,

    @PropertyName("timestamp")
    val timestamp: Timestamp = Timestamp.now(),

    @PropertyName("email")
    val email: String = ""
) {
    constructor() : this("", "", 0, Timestamp.now(), "")
}