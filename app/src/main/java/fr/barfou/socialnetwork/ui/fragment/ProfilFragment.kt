package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.barfou.socialnetwork.ui.activity.LoginActivity
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.ui.adapter.TrophyAdapter
import kotlinx.android.synthetic.main.fragment_profil.*


class ProfilFragment: Fragment() {

    private lateinit var trophyAdapter: TrophyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? LoginActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.app_name)
            this.setDisplayHomeAsUpEnabled(false)
        }

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


        /*tvShowAllTrophys.setOnClickListener {
            findNavController().navigate(
                R.id.action_login_fragment_to_register_fragment
            )
        }*/
    }
}