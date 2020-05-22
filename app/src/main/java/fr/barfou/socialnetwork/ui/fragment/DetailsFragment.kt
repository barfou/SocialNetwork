package fr.barfou.socialnetwork.ui.fragment

import android.annotation.SuppressLint
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
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.UserAdapter
import fr.barfou.socialnetwork.ui.listener.OnUserClickListener
import fr.barfou.socialnetwork.ui.utils.convertLatLongToLocation
import fr.barfou.socialnetwork.ui.utils.hide
import fr.barfou.socialnetwork.ui.utils.show
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.details_fragment.*

class DetailsFragment : Fragment(), OnUserClickListener {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var userAdapter: UserAdapter

    private var joined: Boolean = false
    private lateinit var meetingId: String
    private lateinit var userId: String
    private lateinit var datePost: String
    private lateinit var dateEvent: String
    private lateinit var name: String
    private lateinit var type: String
    private lateinit var latitude:String
    private lateinit var longitude: String
    private lateinit var details: String

    companion object {
        const val MEETING_ID_KEY = "meeting_id_key"
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
        meetingId = arguments?.getString(MEETING_ID_KEY) ?: throw IllegalStateException("No ID found")
        joined = mainViewModel.isJoined(meetingId)
        userId = arguments?.getString(USER_ID_KEY) ?: throw IllegalStateException("No ID found")
        datePost = arguments?.getString(DATE_POST_KEY) ?: throw IllegalStateException("No Date found")
        dateEvent = arguments?.getString(DATE_EVENT_KEY) ?: throw IllegalStateException("No Date found")
        name = arguments?.getString(NAME_KEY) ?: throw IllegalStateException("No Name found")
        type = arguments?.getString(TYPE_KEY) ?: throw IllegalStateException("No Type found")
        latitude = arguments?.getString(LATITUDE_KEY) ?: throw IllegalStateException("No Latitude found")
        longitude = arguments?.getString(LONGITUDE_KEY) ?: throw IllegalStateException("No Longitude found")
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
            this.setTitle(R.string.title_fragment_details)
            this.setDisplayHomeAsUpEnabled(true)
        }

        (activity as? MainActivity)?.run {
            this.mode = MainActivity.Mode.DETAILS
        }

        customizeImageView()
        setupAdapter()
        loadData()
        customizeButton()

        btn_join.setOnClickListener {
            if (!joined) {
                mainViewModel.joinMeeting(meetingId) { handleResult(it) }
            } else {
                mainViewModel.exitMeeting(meetingId) { handleResult(it) }
            }
        }
    }

    private fun handleResult(result: Boolean) {
        if (result) {
            Toast.makeText(requireContext(), "Changement enregistré.", Toast.LENGTH_LONG).show()
            joined = !joined
            customizeButton()
            showUsers()
        } else {
            Toast.makeText(requireContext(), "Un problème a empêché le traitement des données.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        try {
            mainViewModel.getUserById(userId)?.run {
                container_2.show()
                tv_user_pseudo.text = this.getInitials()
                tv_date_upload.text = " le $datePost"
                tv_user_pseudo.setOnClickListener {
                    navigateToProfileFragment(userId)
                }
            }
            tv_meeting_name.text = name
            tv_meeting_type.text = type
            tv_date_meeting.text = "Aura lieu le $dateEvent"
            tv_details_meeting.text = details
            showUsers()
            showLocation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showLocation() {
        try {
            val location = convertLatLongToLocation(this.requireContext(), latitude.toDouble(), longitude.toDouble())
            val town = location.town
            val country = location.country
            tv_location_details.text = "$town, $country"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showUsers() {
        mainViewModel.getSubscribedUsers(meetingId).run {
            tv_nb_person.text = this.size.toString() + " participants"
            userAdapter.submitList(this)
        }
    }

    private fun customizeButton() {
        if (userId != MainActivity.userId) { // Si l'utilisateur courant n'est pas le créateur du meeting
            if (joined) {
                btn_join.setBackgroundResource(R.drawable.rounded_button_quit)
                btn_join.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_exit, 0, 0, 0)
                btn_join.text = resources.getString(R.string.quit)
            } else {
                btn_join.setBackgroundResource(R.drawable.rounded_button)
                btn_join.setCompoundDrawablesWithIntrinsicBounds(R.drawable.join, 0, 0, 0)
                btn_join.text = resources.getString(R.string.join)
            }
        } else {
            btn_join.hide()
        }
    }

    private fun setupAdapter() {
        userAdapter = UserAdapter(this)
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

    private fun navigateToProfileFragment(userId: String) {
        findNavController().navigate(
                R.id.action_to_profil_fragment,
                bundleOf(ProfilFragment.USER_ID_KEY to userId)
        )
    }

    // OnUserClickListener Implementation
    override fun invoke(view: View, user: User) {
        navigateToProfileFragment(user.firebaseId)
    }
}