package fr.barfou.socialnetwork.ui.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@SuppressLint("SimpleDateFormat")
fun getCurrentDate(): String {
    val date = Date()
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(date)
}

@SuppressLint("SimpleDateFormat")
fun getDate(dateString: String): String {
    return try {
        LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        "Error"
    }
}

fun String.toDateTime(): LocalDate {
    return try {
        LocalDate.parse(this, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        e.printStackTrace()
        LocalDate.now()
    }
}

@SuppressLint("SimpleDateFormat")
fun IntToDateString(day: Int, month: Int, year: Int): String {
    return try {
        val date = LocalDate.of(year, month, day)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return date.format(formatter)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}