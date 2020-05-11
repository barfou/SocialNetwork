package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        setupRecyclerviews()
        mainViewModel.retrieveData {
            if (it) {
                loadAdapters()
                progress_bar.hide()
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

    private fun loadAdapters() {
        meetingAdapter.submitList(mainViewModel.listMeetings)
        meetingAdapter2.submitList(mainViewModel.listMeetings)
        meetingAdapter3.submitList(mainViewModel.filterMeetingByTheme(Theme.SPORT))
        meetingAdapter4.submitList(mainViewModel.filterMeetingByTheme(Theme.CULTURE))
    }

    // Click listener implementation
    override fun invoke(view: View, meeting: Meeting) {
        findNavController().navigate(R.id.action_home_fragment_to_details_fragment)
    }
}