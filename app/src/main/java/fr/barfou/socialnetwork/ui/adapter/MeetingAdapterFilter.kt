package fr.barfou.socialnetwork.ui.adapter

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.listener.OnMeetingClickListener
import fr.barfou.socialnetwork.ui.utils.dp
import fr.barfou.socialnetwork.ui.widget.viewholder.FilterMeetingViewHolder

class MeetingAdapterFilter(
        var fragment: OnMeetingClickListener
) : RecyclerView.Adapter<FilterMeetingViewHolder>() {

    private var _data = emptyList<Pair<Meeting, User?>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterMeetingViewHolder {
        return FilterMeetingViewHolder.create(parent)
    }

    override fun getItemCount(): Int = _data.count()

    override fun onBindViewHolder(holder: FilterMeetingViewHolder, position: Int) {
        holder.bind(_data[position], fragment)
    }

    /**
     * Set new data in the list and refresh it
     */
    fun submitList(data: List<Pair<Meeting, User?>>) {
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
                        dp(4),
                        dp(8),
                        dp(4),
                        dp(8)
                )
            }
        }
    }
}