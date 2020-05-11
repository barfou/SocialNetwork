package fr.barfou.socialnetwork.ui.widget.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import kotlinx.android.synthetic.main.holder_meeting.view.*

class MeetingViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(model: Meeting, onClick: OnMeetingClickListener) {
        itemView.apply {
            this.setOnClickListener { onClick(it, model) }
            tv_name.text = model.name
            tv_date.text = model.dateCreation

            when (model.type) {
                "Karaoké" -> image_view.setImageResource(R.drawable.karaoke)
                "Pièce de Théâtre" -> image_view.setImageResource(R.drawable.theatre)
                "Exposition" -> image_view.setImageResource(R.drawable.exposition)
                "Visite Touristique" -> image_view.setImageResource(R.drawable.visite_touristique)
                "Concert" -> image_view.setImageResource(R.drawable.concert)
                "Dégustation" -> image_view.setImageResource(R.drawable.degustation)
                "Squash" -> image_view.setImageResource(R.drawable.squash)
                "Karting" -> image_view.setImageResource(R.drawable.carting)
                "Parapente" -> image_view.setImageResource(R.drawable.parapente)
                "Course" -> image_view.setImageResource(R.drawable.trail)
                "Paint Ball" -> image_view.setImageResource(R.drawable.paintball)
                "Accrobranche" -> image_view.setImageResource(R.drawable.accrobranche)
                "Bowling" -> image_view.setImageResource(R.drawable.bowling)
                "Escalade" -> image_view.setImageResource(R.drawable.escalade)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): MeetingViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.holder_meeting,
                parent,
                false
            )
            return MeetingViewHolder(view)
        }
    }
}