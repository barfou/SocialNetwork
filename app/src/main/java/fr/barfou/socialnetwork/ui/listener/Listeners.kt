package fr.barfou.socialnetwork.ui.listener

import android.view.View
import fr.barfou.socialnetwork.data.model.Meeting

typealias OnMeetingClickListener = (view: View, meeting: Meeting) -> Unit