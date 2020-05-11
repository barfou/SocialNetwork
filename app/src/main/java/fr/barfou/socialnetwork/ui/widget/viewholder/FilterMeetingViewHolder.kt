package fr.barfou.socialnetwork.ui.widget.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import kotlinx.android.synthetic.main.holder_filter_meeting.view.*
import kotlinx.android.synthetic.main.holder_meeting.view.*
import kotlinx.android.synthetic.main.holder_meeting.view.tv_date
import kotlinx.android.synthetic.main.holder_meeting.view.tv_name

class FilterMeetingViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(model: Meeting, onClick: OnMeetingClickListener) {
        itemView.apply {
            this.setOnClickListener { onClick(it, model) }
            tv_username.text = "username_test"
            tv_date_post.text = "date_poste_test"
            tv_name.text = model.name
            tv_date.text = model.dateCreation

            when (model.type) {
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