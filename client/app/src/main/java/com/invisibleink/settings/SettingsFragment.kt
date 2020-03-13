package com.invisibleink.settings

import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.invisibleink.R
import com.invisibleink.vote.*

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var voteDatabase: VoteDatabase
    private lateinit var voteDao: VoteDao

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        voteDatabase = createVoteDatabase(application = requireActivity().application)
        voteDao = voteDatabase.voteDao()

        findPreference<Preference>(requireContext().getString(R.string.pref_category_vote_db_clear_key))?.setOnPreferenceClickListener {
            clearVotes()
            true
        }

        findPreference<Preference>(requireContext().getString(R.string.pref_category_vote_db_upvote_key))?.setOnPreferenceClickListener {
            addUpvote()
            true
        }

        findPreference<Preference>(requireContext().getString(R.string.pref_category_vote_db_downvote_key))?.setOnPreferenceClickListener {
            addDownvote()
            true
        }

        findPreference<Preference>(requireContext().getString(R.string.pref_category_vote_db_load_all_key))?.setOnPreferenceClickListener {
            logAllVotes()
            true
        }
    }

    private fun clearVotes() = voteDatabase.clearAllTables()

    private fun addUpvote() = voteDao.insert(Vote.upvote((0..100000).random()))

    private fun addDownvote() = voteDao.insert(Vote.downvote((0..100000).random()))

    private fun logAllVotes() =
        Log.d("---- Vote Db -----\n\t", voteDao.getAllVotes().joinToString("\n\t"))
}
