package fr.barfou.socialnetwork.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.adapter.UserAdapter
import kotlinx.android.synthetic.main.details_fragment.*

class DetailsFragment : Fragment() {

    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        customizeImageView()
        userAdapter = UserAdapter()
        recycler_view.apply {
            adapter = userAdapter
            if (itemDecorationCount == 0) addItemDecoration(UserAdapter.OffsetDecoration())
        }
        loadAdapter()
    }

    private fun customizeImageView() {
        when (model.type) {
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
        list.add(User("1", "Jack", "The Ripper", "", "12/05/2020", "Je m'appelle Jack", 0.0, 0.0))
        list.add(User("1", "John", "Doe", "", "12/05/2020", "Je m'appelle John", 0.0, 0.0))
        list.add(User("1", "Kurt", "Cobain", "", "12/05/2020", "Je m'appelle Kurt", 0.0, 0.0))
        userAdapter.submitList(list)
    }
}