package com.invisibleink.explore.vote

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Contains methods for voting on notes.
 */
interface VoteApi {

    @POST("vote")
    fun uploadVote(@Body voteRequest: VoteRequest): Observable<VoteResponse>
}

data class VoteResponse(val success: Boolean, val error: String?)

data class VoteRequest(
    @SerializedName("NoteId")
    val noteId: String,
    @SerializedName("Rate")
    private val rate: Boolean
) {
    companion object {
        fun upvote(noteId: String) = VoteRequest(noteId, true)
        fun downvote(noteId: String) = VoteRequest(noteId, false)
    }

    fun isUpvote() = rate
    fun isDownvote() = !isUpvote()
}