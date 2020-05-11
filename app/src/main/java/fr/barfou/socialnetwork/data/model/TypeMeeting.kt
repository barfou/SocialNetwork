package fr.barfou.socialnetwork.data.model

enum class Theme { CULTURE, SPORT, AUTRE }

data class TypeMeeting(
        var firebaseId: String,
        var name: String,
        var theme: Theme = Theme.AUTRE
)