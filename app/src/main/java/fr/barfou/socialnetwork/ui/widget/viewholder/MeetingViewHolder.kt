package fr.barfou.socialnetwork.ui.widget.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import kotlinx.android.synthetic.main.holder_meeting_1.view.*

class MeetingViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(model: Meeting, onClick: OnMeetingClickListener) {
        itemView.apply {
            this.setOnClickListener { onClick(it, model) }
            tv_name.text = model.name
            tv_date.text = model.dateCreation
        }
    }

    companion object {
        fun create(parent: ViewGroup): MeetingViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.holder_meeting_1,
                parent,
                false
            )
            return MeetingViewHolder(view)
        }
    }
}