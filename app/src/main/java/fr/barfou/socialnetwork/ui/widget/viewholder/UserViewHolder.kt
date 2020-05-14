package fr.barfou.socialnetwork.ui.widget.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.data.model.User
import kotlinx.android.synthetic.main.holder_user.view.*

class UserViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(model: User) {
        itemView.apply {
            tv_user_pseudo.text = model.getInitials()
        }
    }

    companion object {
        fun create(parent: ViewGroup): UserViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.holder_user,
                parent,
                false
            )
            return UserViewHolder(view)
        }
    }
}