package com.invisibleink.vote

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vote(
    @PrimaryKey val noteId: Int,
    val isUpvote: Boolean
) {

    companion object {
        fun upvote(noteId: Int) = Vote(noteId, true)
        fun downvote(noteId: Int) = Vote(noteId, false)
    }

    override fun toString(): String =
        "NOTE ID: $noteId -> ${if (isUpvote) "UP-VOTE" else "DOWN-VOTE"}"
}
