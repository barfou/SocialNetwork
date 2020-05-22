package fr.barfou.socialnetwork.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.barfou.socialnetwork.data.model.*

open class LoginViewModel(
) : ViewModel() {

    // Firebase Refs
    private val firebaseRef = Firebase.database.reference
    private val usersRef = firebaseRef.child("Users")

    // No password here
    fun insertUserDetails(user: User) {
        usersRef.child(user.firebaseId).setValue(user)
    }

    fun updateUserLocation(userId: String, newLatitude: Double, newLongitude: Double) {
        usersRef.child(userId).child("latitude").setValue(newLatitude.toString())
        usersRef.child(userId).child("longitude").setValue(newLongitude.toString())
    }

    companion object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LoginViewModel() as T
        }
    }
}