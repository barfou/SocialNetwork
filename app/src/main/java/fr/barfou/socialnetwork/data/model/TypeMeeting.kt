package fr.barfou.socialnetwork.data.model

enum class Theme { CULTURE, SPORT, AUTRE }

fun getTheme(value: String): Theme {
    return when (value) {
        "SPORT" -> Theme.SPORT
        "CULTURE" -> Theme.CULTURE
        else -> Theme.AUTRE
    }
}

data class TypeMeeting(
        var firebaseId: String,
        var name: String
) {

    var theme: Theme = Theme.AUTRE

    constructor(firebaseId: String, name: String, theme: Theme) : this(firebaseId, name) {
        this.theme = theme
    }

    constructor(firebaseId: String, name: String, theme: String) : this(firebaseId, name) {
        when (theme) {
            "SPORT" -> this.theme = Theme.SPORT
            "CULTURE" -> this.theme = Theme.CULTURE
            else -> this.theme = Theme.AUTRE
        }
    }
}