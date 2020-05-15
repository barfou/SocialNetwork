package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.data.model.Theme
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.MeetingAdapter
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import fr.barfou.socialnetwork.ui.utils.hide
import fr.barfou.socialnetwork.ui.utils.show
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : Fragment(), OnMeetingClickListener {

    private lateinit var mainViewModel: MainViewModel

    private lateinit var meetingAdapter: MeetingAdapter
    private lateinit var meetingAdapter2: MeetingAdapter
    private lateinit var meetingAdapter3: MeetingAdapter
    private lateinit var meetingAdapter4: MeetingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            mainViewModel = ViewModelProvider(this, MainViewModel).get()
        } ?: throw IllegalStateException("Invalid Activity")
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

        (activity as? MainActivity)?.run {
            this.mode = MainActivity.Mode.HOMEPAGE
        }

        setupRecyclerviews()
        mainViewModel.retrieveData {
            if (it) {
                mainViewModel.updateCurrentUser(MainActivity.userId)
                loadAdaptersByTheme()
                progress_bar.hide()
            } else {
                progress_bar.hide()
                Toast.makeText(requireContext(), "Problem while loading data.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerviews() {
        meetingAdapter = MeetingAdapter(this)
        recycler_view_1.apply {
            adapter = meetingAdapter
            if (itemDecorationCount == 0) addItemDecoration(MeetingAdapter.OffsetDecoration())
        }
        meetingAdapter2 = MeetingAdapter(this)
        recycler_view_2.apply {
            adapter = meetingAdapter2
            if (itemDecorationCount == 0) addItemDecoration(MeetingAdapter.OffsetDecoration())
        }
        meetingAdapter3 = MeetingAdapter(this)
        recycler_view_3.apply {
            adapter = meetingAdapter3
            if (itemDecorationCount == 0) addItemDecoration(MeetingAdapter.OffsetDecoration())
        }
        meetingAdapter4 = MeetingAdapter(this)
        recycler_view_4.apply {
            adapter = meetingAdapter4
            if (itemDecorationCount == 0) addItemDecoration(MeetingAdapter.OffsetDecoration())
        }
    }

    private fun loadAdaptersByTheme() {
        // Recycler View 1
        mainViewModel.getMeetingsJoined(MainActivity.userId).run {
            if (this.size > 0) {
                tv_my_activities.show()
                recycler_view_1.show()
                meetingAdapter.submitList(this)
                spacer.show()
            } else {
                recycler_view_1.hide()
                tv_my_activities.hide()
                spacer.hide()
            }
        }

        // Recycler View 2
        mainViewModel.getMostPopularMeetings().run {
            tv_trending.show()
            meetingAdapter2.submitList(this)
        }

        // Recycler View 3
        mainViewModel.filterMeetingByTheme(Theme.SPORT) {
            tv_sport.show()
            meetingAdapter3.submitList(it)
        }

        // Recycler View 4
        mainViewModel.filterMeetingByTheme(Theme.CULTURE) {
            tv_culture.show()
            meetingAdapter4.submitList(it)
        }
    }

    // Click listener implementation
    override fun invoke(view: View, meeting: Meeting) {
        findNavController().navigate(R.id.action_to_details_fragment,
                bundleOf(
                        DetailsFragment.MEETING_ID_KEY to meeting.firebaseId,
                        DetailsFragment.USER_ID_KEY to meeting.userId,
                        DetailsFragment.DATE_POST_KEY to meeting.dateCreation,
                        DetailsFragment.DATE_EVENT_KEY to meeting.dateEvent,
                        DetailsFragment.NAME_KEY to meeting.name,
                        DetailsFragment.TYPE_KEY to meeting.type,
                        DetailsFragment.LATITUDE_KEY to meeting.latitude,
                        DetailsFragment.LONGITUDE_KEY to meeting.longitude,
                        DetailsFragment.DETAILS_KEY to meeting.details
                ))
    }
}