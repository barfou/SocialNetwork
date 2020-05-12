package fr.barfou.socialnetwork.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.barfou.socialnetwork.data.model.*
import fr.barfou.socialnetwork.ui.utils.unAccent
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

open class LoginViewModel(
) : ViewModel() {

    // Firebase Refs
    private val firebaseRef = Firebase.database.reference
    private val usersRef = firebaseRef.child("Users")

    // No password here
    fun insertUserDetails(user: User) {
        usersRef.child(user.pseudo).setValue(user)
    }

    companion object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LoginViewModel() as T
        }
    }
}