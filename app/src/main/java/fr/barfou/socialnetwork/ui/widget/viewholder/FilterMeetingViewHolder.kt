package fr.barfou.socialnetwork.ui.widget.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import kotlinx.android.synthetic.main.holder_meeting.view.*

class FilterMeetingViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(model: Meeting, onClick: OnMeetingClickListener) {
        itemView.apply {
            this.setOnClickListener { onClick(it, model) }
            tv_name.text = model.name
            tv_date.text = model.dateCreation
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