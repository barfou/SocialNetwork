package fr.barfou.socialnetwork.data.model

import com.google.firebase.database.Exclude

data class Meeting(
        var firebaseId: String,
        var userId: String,
        var typeId: String,
        @set:Exclude @get:Exclude
        var type: String,
        var theme: Theme,
        var name: String,
        var dateCreation: String,
        var dateEvent: String,
        var latitude: String,
        var longitude: String,
        var imageUrl: String,
        var details: String = ""
)