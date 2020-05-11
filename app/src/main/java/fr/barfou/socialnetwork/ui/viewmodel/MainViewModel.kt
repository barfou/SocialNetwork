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

    fun filterMeetingByTheme(theme: Theme): MutableList<Meeting> {
        return when (theme) {
            Theme.CULTURE -> listMeetings.filter { it.theme == Theme.CULTURE }.toMutableList()
            Theme.SPORT -> listMeetings.filter { it.theme == Theme.SPORT }.toMutableList()
            Theme.AUTRE -> mutableListOf()
        }
    }

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
        listTypeMeeting.add(TypeMeeting("", "Karaoké", Theme.CULTURE)) // 0
        listTypeMeeting.add(TypeMeeting("", "Pièce de Théâtre", Theme.CULTURE)) // 1
        listTypeMeeting.add(TypeMeeting("", "Exposition", Theme.CULTURE)) // 2
        listTypeMeeting.add(TypeMeeting("", "Visite Touristique", Theme.CULTURE)) // 3
        listTypeMeeting.add(TypeMeeting("", "Concert", Theme.CULTURE)) // 4
        listTypeMeeting.add(TypeMeeting("", "Dégustation", Theme.CULTURE)) // 5
        listTypeMeeting.add(TypeMeeting("", "Squash", Theme.SPORT)) // 6
        listTypeMeeting.add(TypeMeeting("", "Karting", Theme.SPORT)) // 7
        listTypeMeeting.add(TypeMeeting("", "Parapente", Theme.SPORT)) // 8
        listTypeMeeting.add(TypeMeeting("", "Course", Theme.SPORT)) // 9
        listTypeMeeting.add(TypeMeeting("", "Paint Ball", Theme.SPORT)) // 10
        listTypeMeeting.add(TypeMeeting("", "Accrobranche", Theme.SPORT)) // 11
        listTypeMeeting.add(TypeMeeting("", "Bowling", Theme.SPORT)) // 12
        listTypeMeeting.add(TypeMeeting("", "Escalade", Theme.SPORT)) // 13
        listTypeMeeting.forEach {
            pushTypeMeetingToFirebase(it)
        }
    }

    private fun initMeetings() {
        listMeetings.add(Meeting("", listUsers[0].firebaseId, listTypeMeeting[12].firebaseId, listTypeMeeting[12].name, Theme.SPORT, "Ludo Club", "22/08/2088", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[1].firebaseId, listTypeMeeting[2].firebaseId, listTypeMeeting[2].name, Theme.CULTURE, "Expo Impressionniste", "19/03/2020", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[0].firebaseId, listTypeMeeting[7].firebaseId, listTypeMeeting[7].name, Theme.SPORT, "Speed Karting", "14/05/2021", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[3].firebaseId, listTypeMeeting[13].firebaseId, listTypeMeeting[13].name, Theme.SPORT, "Escalade", "22/01/2010", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[3].firebaseId, listTypeMeeting[9].firebaseId, listTypeMeeting[9].name, Theme.SPORT, "UTMB", "22/08/2088", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[0].firebaseId, listTypeMeeting[8].firebaseId, listTypeMeeting[8].name, Theme.SPORT, "The Sky Divers", "14/04/2018", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[2].firebaseId, listTypeMeeting[4].firebaseId, listTypeMeeting[4].name, Theme.CULTURE, "Concert U2", "14/04/2018", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[2].firebaseId, listTypeMeeting[1].firebaseId, listTypeMeeting[1].name, Theme.CULTURE, "Le misanthrope", "14/04/2018", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[1].firebaseId, listTypeMeeting[3].firebaseId, listTypeMeeting[3].name, Theme.CULTURE, "Le Vieux Lyon", "14/04/2018", 0.0, 0.0, ""))
        listMeetings.add(Meeting("", listUsers[2].firebaseId, listTypeMeeting[5].firebaseId, listTypeMeeting[5].name, Theme.CULTURE, "La fête du paradis", "14/04/2018", 0.0, 0.0, ""))
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