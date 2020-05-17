package fr.barfou.socialnetwork.ui.utils

import android.content.Context
import android.location.Geocoder
import fr.barfou.socialnetwork.data.model.ConvertedLocation
import java.util.*
import kotlin.math.*

fun convertLatLongToLocation(context: Context, lat : Double, long: Double) : ConvertedLocation {
    lateinit var myLocation : ConvertedLocation
    val geocoder : Geocoder = Geocoder(context)

    val addresses = geocoder.getFromLocation(lat, long, 1)
    val address = addresses[0]

    val city = address.locality.toString()
    val country = address.countryName.toString().toUpperCase(Locale.ROOT)

    myLocation = ConvertedLocation(lat, long, city, country)

    return myLocation
}

fun getDistanceFromLatLongInM(lat1: Double, lat2: Double, lon1: Double, lon2: Double, el1: Double = 0.0, el2: Double = 0.0): Double {
    val r = 6371 // Radius of the earth
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = (sin(latDistance / 2) * sin(latDistance / 2)
            + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            * sin(lonDistance / 2) * sin(lonDistance / 2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    var distance = r * c * 1000 // convert to meters
    val height: Double = el1 - el2
    distance = distance.pow(2.0) + height.pow(2.0)
    return sqrt(distance)
}