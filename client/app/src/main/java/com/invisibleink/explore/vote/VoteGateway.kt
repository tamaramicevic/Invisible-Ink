package com.invisibleink.explore.vote

import io.reactivex.Observable
import retrofit2.Retrofit
import javax.inject.Inject

enum class VoteResult {
    SUCCESS, FAILURE, DUPLICATE
}

class VoteGateway @Inject constructor(
    retrofit: Retrofit
) {

    lateinit var voteDatabase: VoteDatabase

    private val voteApi = retrofit.create(VoteApi::class.java)

    fun upVoteNote(noteId: String): Observable<VoteResult> = vote(VoteRequest.upvote(noteId))

    fun downvoteNote(noteId: String): Observable<VoteResult> = vote(VoteRequest.downvote(noteId))

    fun clearDatabase() = voteDatabase.clearAllTables()

    fun printNotes(): String = voteDatabase.voteDao().getAllVotes().toString()

    private fun vote(voteRequest: VoteRequest): Observable<VoteResult> {
        if (isDuplicateVote(voteRequest)) return Observable.just(VoteResult.DUPLICATE)

        return voteApi.uploadVote(voteRequest)
            .map {
                if (it.success) {
                    updateVoteDatabase(voteRequest)
                    VoteResult.SUCCESS
                } else {
                    VoteResult.FAILURE
                }
            }
    }

    private fun isDuplicateVote(voteRequest: VoteRequest): Boolean {
        val previousVote = voteDatabase.voteDao().getVote(voteRequest.noteId)

        return when {
            previousVote == null -> false
            previousVote.isUpvote != voteRequest.isUpvote() -> false
            else -> true
        }
    }

    private fun updateVoteDatabase(successfulVoteRequest: VoteRequest) {
        val previousVote = voteDatabase.voteDao().getVote(successfulVoteRequest.noteId)
        val newVote = Vote(
            successfulVoteRequest.noteId,
            successfulVoteRequest.isUpvote()
        )

        when {
            previousVote == null -> voteDatabase.voteDao().insert(newVote)
            previousVote.isUpvote != newVote.isUpvote -> voteDatabase.voteDao().removeVote(
                previousVote
            )
            else -> voteDatabase.clearAllTables()
        }
    }
}