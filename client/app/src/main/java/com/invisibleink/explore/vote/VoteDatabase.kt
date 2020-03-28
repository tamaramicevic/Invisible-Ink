package com.invisibleink.explore.vote

import android.app.Application
import androidx.room.*

fun createVoteDatabase(application: Application): VoteDatabase =
    Room.databaseBuilder(
        application,
        VoteDatabase::class.java,
        "vote-database"
    ).fallbackToDestructiveMigration()
        .allowMainThreadQueries().build()

@Database(entities = [Vote::class], version = 1)
abstract class VoteDatabase : RoomDatabase() {
    abstract fun voteDao(): VoteDao
}


@Dao
interface VoteDao {

    @Query("SELECT * FROM vote")
    fun getAllVotes(): List<Vote>

    @Query("SELECT * FROM vote WHERE noteId = :noteId")
    fun getVote(noteId: String): Vote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vote: Vote)

    @Update
    fun updateVote(vote: Vote)

    @Delete
    fun removeVote(vote: Vote)
}