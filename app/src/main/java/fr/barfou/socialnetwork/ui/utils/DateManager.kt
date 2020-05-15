package fr.barfou.socialnetwork.ui.utils

import android.annotation.SuppressLint
import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("SimpleDateFormat")
fun getCurrentDate(): String {
    val date = Date()
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(date)
}

fun String.toDateTime(): LocalDate {
    var value = this
    return try {
        LocalDate.parse(this, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        e.printStackTrace()
        LocalDate.now()
    }
    /*catch (e: Exception) {
    LocalDate.of(2016, Month.OCTOBER, 31)
    e.printStackTrace()
}*/
}