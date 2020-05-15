package fr.barfou.socialnetwork.ui.listener

import android.view.View
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.data.model.User

typealias OnMeetingClickListener = (view: View, meeting: Meeting) -> Unit

typealias OnUserClickListener = (view: View, user: User) -> Unit

interface OnSearchValueChangeListener {

     fun onSearchValueChange(newText: String)
}