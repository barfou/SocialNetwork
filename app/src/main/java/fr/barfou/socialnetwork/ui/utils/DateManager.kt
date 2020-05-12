package fr.barfou.socialnetwork.ui.utils

import android.annotation.SuppressLint
import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("SimpleDateFormat")
fun getCurrentDate(): String {
    val date = Date()
    val formatter = SimpleDateFormat("dd-MM-yyyy")
    return formatter.format(date)
}

fun String.toDateTime(): LocalDateTime {
    return LocalDateTime.parse(this, DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm"))
}