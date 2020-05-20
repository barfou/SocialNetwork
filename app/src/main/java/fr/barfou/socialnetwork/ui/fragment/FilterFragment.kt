package fr.barfou.socialnetwork.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.MeetingAdapterFilter
import fr.barfou.socialnetwork.ui.listener.OnFilterChangeListener
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import fr.barfou.socialnetwork.ui.listener.OnSearchValueChangeListener
import fr.barfou.socialnetwork.ui.utils.hide
import fr.barfou.socialnetwork.ui.utils.show
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_filter.*
import kotlin.system.exitProcess

class FilterFragment : Fragment(), OnMeetingClickListener, OnSearchValueChangeListener, OnFilterChangeListener {

    private lateinit var meetingAdapterFilter: MeetingAdapterFilter
    private var searchValue: String = ""
    private lateinit var mainViewModel: MainViewModel
    private var filterMode: FilterMode = FilterMode.NONE

    enum class FilterMode { NONE, BY_DATE, BY_PROXIMITY }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            mainViewModel = ViewModelProvider(this, MainViewModel).get()
        } ?: throw IllegalStateException("Invalid Activity")

        searchValue = arguments?.getString(SEARCH_VALUE_KEY)
                ?: throw IllegalStateException("No Value found")
        val temp = arguments?.getString(FILTER_VALUE_KEY)
                ?: throw IllegalStateException("No Value found")
        when (temp) {
            "BY_PROXIMITY" -> filterMode = FilterMode.BY_PROXIMITY
            "BY_DATE" -> filterMode = FilterMode.BY_DATE
            "NONE" -> filterMode = FilterMode.NONE
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            (activity as? MainActivity)?.closeSearch()
            findNavController().popBackStack()
        }
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
            this.setDisplayHomeAsUpEnabled(true)
        }

        (activity as? MainActivity)?.run {
            this.mode = MainActivity.Mode.FILTER
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
        searchValue = newText
        loadAdapter()
    }

    override fun onFilterChange(filter: FilterMode) {
        filterMode = filter
        loadAdapter()
    }

    private fun loadAdapter() {
        progress_bar.show()
        mainViewModel.filterMeetingsWithNameAndFilter(searchValue, filterMode) { list ->
            if (list.size > 0) {
                showRecycler()
                meetingAdapterFilter.submitList(list)
            } else {
                showNoResult()
            }
        }
    }

    private fun showNoResult() {
        progress_bar.hide()
        recycler_view_filter.hide()
        tv_no_result.show()
    }

    private fun showRecycler() {
        progress_bar.hide()
        tv_no_result.hide()
        recycler_view_filter.show()
    }

    companion object {
        const val SEARCH_VALUE_KEY = "search_value_key"
        const val FILTER_VALUE_KEY = "filter_value_key"
    }
}