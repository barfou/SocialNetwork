package fr.barfou.socialnetwork.ui.adapter

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import fr.barfou.socialnetwork.ui.utils.dp
import fr.barfou.socialnetwork.ui.widget.viewholder.MeetingViewHolder

class MeetingAdapter(
    val fragment: OnMeetingClickListener
) : RecyclerView.Adapter<MeetingViewHolder>() {

    private var _data = emptyList<Meeting>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        return MeetingViewHolder.create(parent)
    }

    override fun getItemCount(): Int = _data.count()

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        holder.bind(_data[position], fragment)
    }

    /**
     * Set new data in the list and refresh it
     */
    fun submitList(data: List<Meeting>) {
        _data = data
        notifyDataSetChanged()
    }

    /**
     * Define how decorate an item
     */
    class OffsetDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            parent.run {
                outRect.set(
                    dp(8),
                    dp(4),
                    dp(8),
                    dp(4)
                )
            }
        }
    }
}