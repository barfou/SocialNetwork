package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.MeetingAdapterFilter
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import fr.barfou.socialnetwork.ui.listener.OnSearchValueChangeListener
import fr.barfou.socialnetwork.ui.utils.hide
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_filter.*

class FilterFragment : Fragment(), OnMeetingClickListener, OnSearchValueChangeListener {

    private lateinit var meetingAdapterFilter: MeetingAdapterFilter
    private lateinit var initialSearchValue: String
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            mainViewModel = ViewModelProvider(this, MainViewModel).get()
        } ?: throw IllegalStateException("Invalid Activity")

        initialSearchValue = arguments?.getString(SEARCH_VALUE_KEY) ?: throw IllegalStateException("No Value found")
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

    override fun onSearchValueChange(newText: String) {
        mainViewModel.filterMeetingByName(newText) {
            meetingAdapterFilter.submitList(it)
        }
    }

    private fun loadAdapter() {

        mainViewModel.filterMeetingByName(initialSearchValue) {
            progress_bar.hide()
            meetingAdapterFilter.submitList(it)
        }
    }

    companion object {
        const val SEARCH_VALUE_KEY = "search_value_key"
    }
}