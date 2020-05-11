package fr.barfou.socialnetwork.data.model

data class Meeting(
        var firebaseId: String,
        var userId: String,
        var themeId: String,
        var name: String,
        var dateCreation: String,
        var latitude: Double,
        var longitude: Double,
        var imageUrl: String
)