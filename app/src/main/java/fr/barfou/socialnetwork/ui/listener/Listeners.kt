package fr.barfou.socialnetwork.ui.listener

import android.location.Location
import android.view.View
import fr.barfou.socialnetwork.data.model.ConvertedLocation
import fr.barfou.socialnetwork.data.model.Meeting
import fr.barfou.socialnetwork.data.model.User
import fr.barfou.socialnetwork.ui.fragment.FilterFragment

typealias OnMeetingClickListener = (view: View, meeting: Meeting) -> Unit

typealias OnUserClickListener = (view: View, user: User) -> Unit

typealias OnLocationResult = (Location) -> Unit

interface OnSearchValueChangeListener {

     fun onSearchValueChange(newText: String)
}

interface OnFilterChangeListener {

     fun onFilterChange(filter: FilterFragment.FilterMode)
}