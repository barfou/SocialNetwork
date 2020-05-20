package fr.barfou.socialnetwork.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.TrophyAdapter
import fr.barfou.socialnetwork.ui.utils.convertLatLongToLocation
import fr.barfou.socialnetwork.ui.utils.toCapital
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_profil.*


class ProfilFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var trophyAdapter: TrophyAdapter

    private lateinit var userId: String

    companion object {
        const val USER_ID_KEY = "user_id_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            mainViewModel = ViewModelProvider(this, MainViewModel).get()
        } ?: throw IllegalStateException("Invalid Activity")
        userId = arguments?.getString(USER_ID_KEY) ?: throw IllegalStateException("No ID found")
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.title_fragment_profil)
            this.setDisplayHomeAsUpEnabled(true)
        }

        (activity as? MainActivity)?.run {
            this.mode = MainActivity.Mode.PROFILE
        }

        if(userId != mainViewModel.currentUser!!.firebaseId){
            btnNavPreferences.visibility = View.GONE
            btnNavEditProfil.visibility = View.GONE
        }

        loadUserData()?.run {
            tvProfil.text = this.getInitials()
            tvLoginUser.text = this.pseudo.toCapital()
            tvNumberLevelUser.text = this.level
            showLocation(this.latitude, this.longitude)
            tvBio.text = this.about

            tvMeetingSuggest.text = resources.getText(R.string.text_suggest).toString() + " " + mainViewModel.getMeetingsSuggestedCount(userId).toString() + " " + resources.getText(R.string.text_activity).toString()
            tvMeetingJoin.text = resources.getText(R.string.text_join).toString() + " " + mainViewModel.getMeetingsJoinedCount(userId).toString() + " " + resources.getText(R.string.text_activity).toString()

            trophyAdapter = TrophyAdapter()
            rvTrophys.apply {
                adapter = trophyAdapter
                if (itemDecorationCount == 0) addItemDecoration(TrophyAdapter.OffsetDecoration())
            }

            val listTrophy = mutableListOf<String>()
            listTrophy.add("23 Avr 2019")
            listTrophy.add("05 Mai 2019")
            listTrophy.add("06 Déc 2019")
            listTrophy.add("22 Avr 2020")
            listTrophy.add("17 Mar 2020")
            listTrophy.add("11 Mai 2020")

            trophyAdapter.submitList(listTrophy)

        }

        btnNavEditProfil.setOnClickListener {
            findNavController().navigate(
                    R.id.action_to_modify_profil_fragment
            )
        }

        btnNavPreferences.setOnClickListener {
            findNavController().navigate(
                    R.id.action_to_preferences_fragment
            )
        }

        btnDetailSuggested.setOnClickListener {
            findNavController().navigate(
                    R.id.action_to_display_list_fragment,
                    bundleOf(
                            DisplayListFragment.USER_ID_KEY to userId,
                            DisplayListFragment.MODE_KEY to DisplayListFragment.Mode.CREATED.toString()
                    )
            )
        }

        btnDetailSubscribed.setOnClickListener {
            findNavController().navigate(
                    R.id.action_to_display_list_fragment,
                    bundleOf(
                            DisplayListFragment.USER_ID_KEY to userId,
                            DisplayListFragment.MODE_KEY to DisplayListFragment.Mode.SUBSCRIBED.toString()
                    )
            )
        }

        /*tvShowAllTrophys.setOnClickListener {
            findNavController().navigate(
                R.id.action_login_fragment_to_register_fragment
            )
        }*/
    }

    @SuppressLint("SetTextI18n")
    private fun showLocation(latitude: String, longitude: String) {
        try {
            val location = convertLatLongToLocation(this.requireContext(), latitude.toDouble(), longitude.toDouble())
            val town = location.town
            val country = location.country
            tv_location_profile.text = "$town, $country"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadUserData(): User? {
        var user: User? = null
        try {
            mainViewModel.getUserById(userId)?.run {
                user = this
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return user
    }
}