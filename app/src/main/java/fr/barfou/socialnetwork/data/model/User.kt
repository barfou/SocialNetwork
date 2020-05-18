package fr.barfou.socialnetwork.data.model

data class User(
        var firebaseId: String,
        var mail: String,
        var pseudo: String,
        var imageUrl: String,
        var dateInscription: String,
        var about: String,
        var latitude: String,
        var longitude: String,
        var level: String = "1"
) {
    fun getInitials() = pseudo.split(" ").map { it.first() }.take(2).joinToString("").toUpperCase()
}