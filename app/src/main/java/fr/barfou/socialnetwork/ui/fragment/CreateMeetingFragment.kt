package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.ui.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_create_meeting.*

class CreateMeetingFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_meeting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.supportActionBar?.apply {
            this.setTitle(R.string.title_fragment_create_meeting)
            this.setDisplayHomeAsUpEnabled(false)
        }

        (activity as? MainActivity)?.run {
            this.mode = MainActivity.Mode.PROFILE
        }

        btnCancelCreateMeeting.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnConfirmCreateMeeting.setOnClickListener {
            //Traitement des données changées
            requireActivity().onBackPressed()
        }

    }
}