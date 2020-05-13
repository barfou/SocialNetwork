package fr.barfou.socialnetwork.ui.utils

import android.content.Context
import android.location.Geocoder
import fr.barfou.socialnetwork.data.model.ConvertedLocation

fun convertLatLongToLocation(context: Context, lat : Double, long: Double) : ConvertedLocation {
    lateinit var myLocation : ConvertedLocation
    val geocoder : Geocoder = Geocoder(context)

    val addresses = geocoder.getFromLocation(lat, long, 1)
    val address = addresses[0]

    val city = address.locality.toString()
    val country = address.countryName.toString()

    myLocation = ConvertedLocation(lat, long, city, country)

    return myLocation
}