package fr.barfou.socialnetwork.data.model

data class User(
        var firebaseId: String,
        var firstName: String,
        var lastName: String,
        var imageUrl: String,
        var dateInscription: String,
        var about: String,
        var latitude: String,
        var longitude: String
)