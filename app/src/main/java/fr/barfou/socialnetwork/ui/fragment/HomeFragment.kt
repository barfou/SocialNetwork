package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.MeetingAdapter
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : Fragment(), OnMeetingClickListener {

    private lateinit var meetingAdapter: MeetingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.app_name)
            this.setDisplayHomeAsUpEnabled(false)
        }

        meetingAdapter = MeetingAdapter(this)
        recycler_view_1.apply {
            adapter = meetingAdapter
            if (itemDecorationCount == 0) addItemDecoration(MeetingAdapter.OffsetDecoration())
        }
        loadAdapter()
    }

    private fun loadAdapter() {
        var listMeeting = mutableListOf<Meeting>()
        listMeeting.add(Meeting("1", "1", "1", "Bowling", "22/08/2088", 0.0, 0.0, ""))
        listMeeting.add(Meeting("1", "1", "1", "Plage", "19/03/2020", 0.0, 0.0, ""))
        listMeeting.add(Meeting("1", "1", "1", "Karting", "14/05/2021", 0.0, 0.0, ""))
        listMeeting.add(Meeting("1", "1", "1", "Escalade", "22/01/2010", 0.0, 0.0, ""))
        listMeeting.add(Meeting("1", "1", "1", "Chess-Boxing", "22/08/2088", 0.0, 0.0, ""))
        listMeeting.add(Meeting("1", "1", "1", "Lancer de nain", "14/04/2018", 0.0, 0.0, ""))
        meetingAdapter.submitList(listMeeting)
    }

    // Click listener implementation
    override fun invoke(view: View, meeting: Meeting) {

    }
}