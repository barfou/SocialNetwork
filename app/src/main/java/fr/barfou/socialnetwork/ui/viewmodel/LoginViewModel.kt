package fr.barfou.socialnetwork.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

    fun getUserFirebase(userId: String, onSuccess: OnSuccess<User?>) {
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseError", error.message)
                onSuccess(null)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val user = dataSnapshot.value as HashMap<*, *>
                    val firebaseId = dataSnapshot.key as String
                    val mail = user["mail"] as String
                    val pseudo = user["pseudo"] as String
                    val imageUrl = user["imageUrl"] as String
                    val dateInscription = user["dateInscription"] as String
                    val about = user["about"] as String
                    val latitude = user["latitude"] as String
                    val longitude = user["longitude"] as String
                    val level = user["level"] as String
                    val boolTrend = user["boolTrend"] as Boolean
                    val promote = user["promote"] as String
                    val boolLocation = user["boolLocation"] as Boolean
                    val res = User(firebaseId, mail, pseudo, imageUrl, dateInscription, about, latitude, longitude, level, boolTrend, promote, boolLocation)
                    onSuccess(res)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onSuccess(null)
                }
            }
        })
    }

    companion object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LoginViewModel() as T
        }
    }
}