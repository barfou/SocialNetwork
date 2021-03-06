package fr.barfou.socialnetwork.ui.widget.viewholder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import fr.barfou.socialnetwork.ui.utils.toCapital
import kotlinx.android.synthetic.main.holder_filter_meeting.view.*
import kotlinx.android.synthetic.main.holder_meeting.view.tv_date
import kotlinx.android.synthetic.main.holder_meeting.view.tv_name

class FilterMeetingViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @SuppressLint("SetTextI18n")
    fun bind(model: Pair<Meeting, User?>, onClick: OnMeetingClickListener) {
        itemView.apply {
            this.setOnClickListener { onClick(it, model.first) }
            tv_username.text = "username_test"
            tv_date_post.text = "date_poste_test"
            tv_name.text = model.first.name
            tv_date.text = "Aura lieu le " + model.first.dateEvent
            tv_type_filter.text = model.first.type
            model.second?.run {
                tv_user_pseudo.text = this.getInitials()
                tv_username.text = "Créé par" + this.pseudo.toCapital()
            }

            tv_date_post.text = "le " + model.first.dateCreation

            when (model.first.type) {
                "Karaoké" -> image_view_filter.setImageResource(R.drawable.karaoke)
                "Pièce de Théâtre" -> image_view_filter.setImageResource(R.drawable.theatre)
                "Exposition" -> image_view_filter.setImageResource(R.drawable.exposition)
                "Visite Touristique" -> image_view_filter.setImageResource(R.drawable.visite_touristique)
                "Concert" -> image_view_filter.setImageResource(R.drawable.concert)
                "Dégustation" -> image_view_filter.setImageResource(R.drawable.degustation)
                "Squash" -> image_view_filter.setImageResource(R.drawable.squash)
                "Karting" -> image_view_filter.setImageResource(R.drawable.carting)
                "Parapente" -> image_view_filter.setImageResource(R.drawable.parapente)
                "Course" -> image_view_filter.setImageResource(R.drawable.trail)
                "Paint Ball" -> image_view_filter.setImageResource(R.drawable.paintball)
                "Accrobranche" -> image_view_filter.setImageResource(R.drawable.accrobranche)
                "Bowling" -> image_view_filter.setImageResource(R.drawable.bowling)
                "Escalade" -> image_view_filter.setImageResource(R.drawable.escalade)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): FilterMeetingViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.holder_filter_meeting,
                parent,
                false
            )
            return FilterMeetingViewHolder(view)
        }
    }
}