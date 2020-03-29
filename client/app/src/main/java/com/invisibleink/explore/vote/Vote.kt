package com.invisibleink.explore.vote

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vote(
    @PrimaryKey val noteId: String,
    val isUpvote: Boolean
) {

    companion object {
        fun upvote(noteId: String) = Vote(noteId, true)
        fun downvote(noteId: String) = Vote(noteId, false)
    }

    override fun toString(): String =
        "NOTE ID: $noteId -> ${if (isUpvote) "UP-VOTE" else "DOWN-VOTE"}"
}
