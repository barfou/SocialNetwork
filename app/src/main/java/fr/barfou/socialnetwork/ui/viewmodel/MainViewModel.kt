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

open class MainViewModel(
) : ViewModel() {

    private val storedData = false

    // Firebase Refs
    private val firebaseRef = Firebase.database.reference
    private val meetingsRef = firebaseRef.child("Meetings")
    private val usersRef = firebaseRef.child("Users")
    private val typeMetingsRef = firebaseRef.child("TypesMeeting")

    // Data
    var listMeetings = mutableListOf<Meeting>()
    var listUsers = mutableListOf<User>()
    var listTypeMeeting = mutableListOf<TypeMeeting>()

    fun retrieveData(onSuccess: OnSuccess<Boolean>) {
        if (!storedData) {
            firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("FirebaseError", error.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        val taskMap = dataSnapshot.value as? HashMap<*, *>
                        if (taskMap.isNullOrEmpty())
                            initData().run(onSuccess)
                        else {
                            loadData().run(onSuccess)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        } else {
            onSuccess(true)
        }
    }

    private fun initData(): Boolean {
        return try {
            initUsers()
            initTypeMeetings()
            initMeetings()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun loadData(): Boolean {
        return true
    }

    private fun initUsers() {
        listUsers.add(User("", "John", "Lennon", "", "22/05/2019", "", 0.0, 0.0))
        listUsers.add(User("", "Marie", "Currie", "", "20/04/2020", "", 0.0, 0.0))
        listUsers.add(User("", "Jean Edmond", "De la Villardière", "", "17/03/2010", "", 0.0, 0.0))
        listUsers.add(User("", "Samuel", "Urbanowicz", "", "03/01/2017", "", 0.0, 0.0))
        listUsers.add(User("", "Angela", "Merkel", "", "04/04/2018", "", 0.0, 0.0))
        listUsers.forEach {
            pushUserToFirebase(it)
        }
    }

    private fun initTypeMeetings() {
        listTypeMeeting.add(TypeMeeting("", "Spectacle", Theme.CULTURE))
        listTypeMeeting.add(TypeMeeting("", "Pièce de Théâtre", Theme.CULTURE))
        listTypeMeeting.add(TypeMeeting("", "Exposition", Theme.CULTURE))
        listTypeMeeting.add(TypeMeeting("", "Visite Touristique", Theme.CULTURE))
        listTypeMeeting.add(TypeMeeting("", "Apéro Quizz", Theme.CULTURE))
        listTypeMeeting.add(TypeMeeting("", "Opéra", Theme.CULTURE))
        listTypeMeeting.add(TypeMeeting("", "Squash", Theme.SPORT))
        listTypeMeeting.add(TypeMeeting("", "Karting", Theme.SPORT))
        listTypeMeeting.add(TypeMeeting("", "Base Jump", Theme.SPORT))
        listTypeMeeting.add(TypeMeeting("", "Chess Boxing", Theme.SPORT))
        listTypeMeeting.add(TypeMeeting("", "Paint Ball", Theme.SPORT))
        listTypeMeeting.add(TypeMeeting("", "Accrobranche", Theme.SPORT))
        listTypeMeeting.forEach {
            pushTypeMeetingToFirebase(it)
        }
    }

    private fun initMeetings() {
        listMeetings.add(Meeting("", listUsers[0].firebaseId, "1", "Bowling", "22/08/2088", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[1].firebaseId, "1", "Plage", "19/03/2020", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[0].firebaseId, "1", "Karting", "14/05/2021", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[3].firebaseId, "1", "Escalade", "22/01/2010", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[3].firebaseId, "1", "Chess-Boxing", "22/08/2088", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[0].firebaseId, "1", "Base Jump", "14/04/2018", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[2].firebaseId, "1", "Apéro Quizz", "14/04/2018", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[2].firebaseId, "1", "Pièce de théâtre", "14/04/2018", 0.0, 0.0, ""))
        listMeetings.forEach {
            pushMeetingToFirebase(it)
        }
    }

    private fun pushUserToFirebase(user: User) {
        var firebaseId = usersRef.push().key!!
        user.firebaseId = firebaseId
        usersRef.child(firebaseId).setValue(user)
    }

    private fun pushTypeMeetingToFirebase(typeMeeting: TypeMeeting) {
        var firebaseId = usersRef.push().key!!
        typeMeeting.firebaseId = firebaseId
        typeMetingsRef.child(firebaseId).setValue(typeMeeting)
    }

    private fun pushMeetingToFirebase(meeting: Meeting) {
        var firebaseId = usersRef.push().key!!
        meeting.firebaseId = firebaseId
        meetingsRef.child(firebaseId).setValue(meeting)
    }

    companion object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel() as T
        }
    }
}