package fr.barfou.socialnetwork.data.model

data class TypeMeeting(
        override var firebaseId: String,
        var name: String
) : FirebaseItem