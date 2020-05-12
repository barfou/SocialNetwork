package fr.barfou.socialnetwork.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.UserAdapter
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.details_fragment.*

class DetailsFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var userAdapter: UserAdapter

    private lateinit var userId: String
    private lateinit var datePost: String
    private lateinit var dateEvent: String
    private lateinit var name: String
    private lateinit var type: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var details: String

    companion object {
        const val USER_ID_KEY = "user_id_key"
        const val DATE_POST_KEY = "date_post_key"
        const val DATE_EVENT_KEY = "date_event_key"
        const val NAME_KEY = "name_key"
        const val TYPE_KEY = "type_key"
        const val LATITUDE_KEY = "latitude_key"
        const val LONGITUDE_KEY = "longitude_key"
        const val DETAILS_KEY = "details_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            mainViewModel = ViewModelProvider(this, MainViewModel).get()
        } ?: throw IllegalStateException("Invalid Activity")
        userId = arguments?.getString(USER_ID_KEY) ?: throw IllegalStateException("No ID found")
        datePost = arguments?.getString(DATE_POST_KEY) ?: throw IllegalStateException("No Date found")
        dateEvent = arguments?.getString(DATE_EVENT_KEY) ?: throw IllegalStateException("No Date found")
        name = arguments?.getString(NAME_KEY) ?: throw IllegalStateException("No Name found")
        type = arguments?.getString(TYPE_KEY) ?: throw IllegalStateException("No Type found")
        latitude = arguments?.getDouble(LATITUDE_KEY) ?: throw IllegalStateException("No Latitude found")
        longitude = arguments?.getDouble(LONGITUDE_KEY) ?: throw IllegalStateException("No Longitude found")
        details = arguments?.getString(DETAILS_KEY) ?: throw IllegalStateException("No Details found")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.app_name)
            this.setDisplayHomeAsUpEnabled(false)
        }

        loadData()
        customizeImageView()
        setupAdapter()
        loadAdapter()
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        try {
            mainViewModel.getUserById(userId) { user ->
                tv_username.text = user.firstName + " " + user.lastName
            }
            tv_date_post.text = datePost
            tv_meeting_name.text = name
            tv_location.text = "$latitude $longitude"
            tv_date_meeting.text = dateEvent
            tv_details_meeting.text = details
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupAdapter() {
        userAdapter = UserAdapter()
        recycler_view.apply {
            adapter = userAdapter
            if (itemDecorationCount == 0) addItemDecoration(UserAdapter.OffsetDecoration())
        }
    }

    private fun customizeImageView() {
        when (type) {
            "Karaoké" -> image_meeting.setImageResource(R.drawable.karaoke)
            "Pièce de Théâtre" -> image_meeting.setImageResource(R.drawable.theatre)
            "Exposition" -> image_meeting.setImageResource(R.drawable.exposition)
            "Visite Touristique" -> image_meeting.setImageResource(R.drawable.visite_touristique)
            "Concert" -> image_meeting.setImageResource(R.drawable.concert)
            "Dégustation" -> image_meeting.setImageResource(R.drawable.degustation)
            "Squash" -> image_meeting.setImageResource(R.drawable.squash)
            "Karting" -> image_meeting.setImageResource(R.drawable.carting)
            "Parapente" -> image_meeting.setImageResource(R.drawable.parapente)
            "Course" -> image_meeting.setImageResource(R.drawable.trail)
            "Paint Ball" -> image_meeting.setImageResource(R.drawable.paintball)
            "Accrobranche" -> image_meeting.setImageResource(R.drawable.accrobranche)
            "Bowling" -> image_meeting.setImageResource(R.drawable.bowling)
            "Escalade" -> image_meeting.setImageResource(R.drawable.escalade)
        }
    }

    private fun loadAdapter() {
        var list = mutableListOf<User>()
        list.add(User("1", "Jack", "The Ripper", "", "12/05/2020", "Je m'appelle Jack", "0.0", "0.0"))
        list.add(User("1", "John", "Doe", "", "12/05/2020", "Je m'appelle John", "0.0", "0.0"))
        list.add(User("1", "Kurt", "Cobain", "", "12/05/2020", "Je m'appelle Kurt", "0.0", "0.0"))
        userAdapter.submitList(list)
    }
}