package fr.barfou.socialnetwork.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import fr.barfou.socialnetwork.ui.activity.LoginActivity
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.ConvertedLocation
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.TrophyAdapter
import fr.barfou.socialnetwork.ui.utils.convertLatLongToLocation
import fr.barfou.socialnetwork.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.details_fragment.*
import kotlinx.android.synthetic.main.fragment_profil.*


class ProfilFragment: Fragment() {

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

        loadUserData()?.run {



            tvLoginUser.text = this.pseudo
            //tvNumberLevelUser.text = this.level

            val location: ConvertedLocation = convertLatLongToLocation(requireContext(), this.latitude.toDouble(), this.longitude.toDouble())

            tvTown.text = location.town
            tvCountry.text = location.country

            tvBio.text = this.about

            //tvNumberMeetingSuggest.text = this.countMeetingSuggest
            //tvNumberMeetingJoin.text = this.countMeetingJoin

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

            btnNavEditProfil.setOnClickListener {
                findNavController().navigate(
                    R.id.action_to_modify_profil_fragment
                )
            }
        }



        /*tvShowAllTrophys.setOnClickListener {
            findNavController().navigate(
                R.id.action_login_fragment_to_register_fragment
            )
        }*/
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