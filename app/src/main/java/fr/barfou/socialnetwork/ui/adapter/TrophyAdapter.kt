package fr.barfou.socialnetwork.ui.adapter

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.barfou.socialnetwork.ui.utils.dp
import fr.barfou.socialnetwork.ui.widget.viewholder.TrophyViewHolder

class TrophyAdapter(
) : RecyclerView.Adapter<TrophyViewHolder>() {

    private var _data = emptyList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrophyViewHolder {
        return TrophyViewHolder.create(parent)
    }

    override fun getItemCount(): Int = _data.count()

    override fun onBindViewHolder(holder: TrophyViewHolder, position: Int) {
        holder.bind(_data[position])
    }

    /**
     * Set new data in the list and refresh it
     */
    fun submitList(data: List<String>) {
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
                    dp(4),
                    dp(4),
                    dp(4)
                )
            }
        }
    }
}