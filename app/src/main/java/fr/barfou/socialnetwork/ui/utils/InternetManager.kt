package fr.barfou.socialnetwork.ui.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun isOnline(context: Context): Boolean {
    var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    cm?.run {
        cm.getNetworkCapabilities(cm.activeNetwork)?.run {
            if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                result = 2
            } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                result = 1
            }
        }
    }
    return result > 0
}