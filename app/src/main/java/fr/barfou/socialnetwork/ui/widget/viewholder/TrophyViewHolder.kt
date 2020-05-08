package fr.barfou.socialnetwork.ui.widget.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.barfou.socialnetwork.R
import kotlinx.android.synthetic.main.holder_trophy.view.*

class TrophyViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(model: String) {
        itemView.apply {
            this.tvDateTrophy.text = model
        }
    }

    companion object {
        fun create(parent: ViewGroup): TrophyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.holder_trophy,
                parent,
                false
            )
            return TrophyViewHolder(view)
        }
    }
}