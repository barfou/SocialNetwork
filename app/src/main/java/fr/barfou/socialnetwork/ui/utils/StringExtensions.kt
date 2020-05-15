package fr.barfou.socialnetwork.ui.utils

import java.text.Normalizer

private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun String.unAccent(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return REGEX_UNACCENT.replace(temp, "")
}

fun String.toCapital(): String {
    return split(" ")
            .toMutableList()
            .fold("") { acc, str -> acc + str.capitalize() }
            .trim()
}