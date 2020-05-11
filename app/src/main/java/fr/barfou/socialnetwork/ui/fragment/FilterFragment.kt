package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.MeetingAdapterFilter
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import fr.barfou.socialnetwork.ui.listener.OnSearchValueChangeListener
import kotlinx.android.synthetic.main.fragment_filter.*

class FilterFragment : Fragment(), OnMeetingClickListener, OnSearchValueChangeListener {

    private lateinit var meetingAdapterFilter: MeetingAdapterFilter
    private lateinit var searchValue: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchValue = arguments?.getString(SEARCH_VALUE_KEY) ?: throw IllegalStateException("No Value found")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.app_name)
            this.setDisplayHomeAsUpEnabled(false)
        }

        meetingAdapterFilter = MeetingAdapterFilter(this)
        recycler_view_filter.apply {
            adapter = meetingAdapterFilter
            if (itemDecorationCount == 0) addItemDecoration(MeetingAdapterFilter.OffsetDecoration())
        }
        loadAdapter()
    }

    override fun invoke(view: View, meeting: Meeting) {
        //
    }

    override fun onSearchValueChange(newText: String) {
        TODO("Not yet implemented")
    }

    private fun loadAdapter() {
        var listMeeting = mutableListOf<Meeting>()
        listMeeting.add(Meeting("1", "1", "1", "Bowling", "22/08/2088", 0.0, 0.0, ""))
        listMeeting.add(Meeting("1", "1", "1", "Plage", "19/03/2020", 0.0, 0.0, ""))
        listMeeting.add(Meeting("1", "1", "1", "Karting", "14/05/2021", 0.0, 0.0, ""))
        listMeeting.add(Meeting("1", "1", "1", "Escalade", "22/01/2010", 0.0, 0.0, ""))
        listMeeting.add(Meeting("1", "1", "1", "Chess-Boxing", "22/08/2088", 0.0, 0.0, ""))
        listMeeting.add(Meeting("1", "1", "1", "Lancer de nain", "14/04/2018", 0.0, 0.0, ""))
        meetingAdapterFilter.submitList(listMeeting)
    }

    companion object {
        const val SEARCH_VALUE_KEY = "search_value_key"
    }
}