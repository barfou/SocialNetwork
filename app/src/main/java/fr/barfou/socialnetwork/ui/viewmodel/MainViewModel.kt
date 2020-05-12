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

open class MainViewModel(
) : ViewModel() {

    // Firebase Refs
    private val firebaseRef = Firebase.database.reference
    private val meetingsRef = firebaseRef.child("Meetings")
    private val usersRef = firebaseRef.child("Users")
    private val typeMetingsRef = firebaseRef.child("TypesMeeting")

    // Data
    var listMeetings = mutableListOf<Meeting>()
    var listUsers = mutableListOf<User>()
    var listTypeMeeting = mutableListOf<TypeMeeting>()

    fun getUserById(firebaseId: String, onSuccess: OnSuccess<User>) {
        viewModelScope.launch {
            var found = false
            var i = 0
            while (!found && i < listUsers.size) {
                if (listUsers[i].firebaseId == firebaseId)
                    found = true
                else
                    i++
            }
            if (found)
                onSuccess(listUsers[i])
        }
    }

    fun filterMeetingByName(name: String, onSuccess: OnSuccess<List<Meeting>>) {
        viewModelScope.launch {
            listMeetings.filter { it.name.unAccent().contains(name.unAccent(), ignoreCase = true) }
                    .toMutableList()
                    .run(onSuccess)
        }
    }

    fun filterMeetingByTheme(theme: Theme, onSuccess: OnSuccess<List<Meeting>>) {
        viewModelScope.launch {
            when (theme) {
                Theme.CULTURE -> listMeetings.filter { it.theme == Theme.CULTURE }.toMutableList().run(onSuccess)
                Theme.SPORT -> listMeetings.filter { it.theme == Theme.SPORT }.toMutableList().run(onSuccess)
                Theme.AUTRE -> mutableListOf<Meeting>().run(onSuccess)
            }
        }
    }

    fun retrieveData(onSuccess: OnSuccess<Boolean>) {
        viewModelScope.launch {
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
                            loadData {
                                onSuccess(it)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
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

    private fun loadData(onSuccess: OnSuccess<Boolean>) {
        loadUsers { usersOk ->
            if (usersOk) {
                loadTypesMeeting { typeMeetingsOk ->
                    if (typeMeetingsOk) {
                        loadMeetings { meetingsOk ->
                            if (meetingsOk) onSuccess(true)
                            else onSuccess(false)
                        }
                    } else onSuccess(false)
                }
            } else onSuccess(false)
        }
    }

    private fun loadUsers(onSuccess: OnSuccess<Boolean>) {

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseError", error.message)
                onSuccess(false)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val userMap = dataSnapshot.value as? HashMap<*, *>
                    userMap?.map { entry ->
                        val user = entry.value as HashMap<*, *>
                        val firebaseId = entry.key as String
                        val firstName = user["firstName"] as String
                        val lastName = user["lastName"] as String
                        val imageUrl = user["imageUrl"] as String
                        val dateInscription = user["dateInscription"] as String
                        val about = user["about"] as String
                        val latitude = user["latitude"] as String
                        val longitude = user["longitude"] as String
                        listUsers.add(User(firebaseId, firstName, lastName, imageUrl, dateInscription, about, latitude, longitude))
                    }
                    onSuccess(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onSuccess(false)
                }
            }
        })
    }

    private fun loadTypesMeeting(onSuccess: OnSuccess<Boolean>) {

        typeMetingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseError", error.message)
                onSuccess(false)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val typeMeetingMap = dataSnapshot.value as? HashMap<*, *>
                    typeMeetingMap?.map { entry ->
                        val typeMeeting = entry.value as HashMap<*, *>
                        val firebaseId = entry.key as String
                        val name = typeMeeting["name"] as String
                        val theme = typeMeeting["theme"] as String
                        listTypeMeeting.add(TypeMeeting(firebaseId, name, theme))
                    }
                    onSuccess(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onSuccess(false)
                }
            }
        })
    }

    private fun loadMeetings(onSuccess: OnSuccess<Boolean>) {

        meetingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseError", error.message)
                onSuccess(false)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {
                    val meetingsMap = dataSnapshot.value as? HashMap<*, *>
                    meetingsMap?.map { entry ->

                        val meeting = entry.value as HashMap<*, *>
                        val firebaseId = entry.key as String
                        val userId = meeting["userId"] as String
                        val typeId = meeting["typeId"] as String
                        val type = getTypeWithId(typeId)
                        val theme = meeting["theme"] as String
                        val name = meeting["name"] as String
                        val dateCreation = meeting["dateCreation"] as String
                        val dateEvent = meeting["dateEvent"] as String
                        val latitude = meeting["latitude"] as String
                        val longitude = meeting["longitude"] as String
                        val imageUrl = meeting["imageUrl"] as String
                        val details = meeting["details"] as String
                        listMeetings.add(Meeting(firebaseId, userId, typeId, type, getTheme(theme), name, dateCreation, dateEvent, latitude, longitude, imageUrl, details))
                    }
                    onSuccess(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onSuccess(false)
                }
            }
        })
    }

    private fun getTypeWithId(typeId: String): String {
        var found = false
        var i = 0
        while (!found && i < listTypeMeeting.size) {
            if (listTypeMeeting[i].firebaseId == typeId)
                found = true
            else
                i++
        }
        return if (found)
            listTypeMeeting[i].name
        else
            ""
    }

    private fun initUsers() {
        listUsers.add(User("", "John", "Lennon", "", "22/05/2019", "", "0.0", "0.0"))
        listUsers.add(User("", "Marie", "Currie", "", "20/04/2020", "", "0.0", "0.0"))
        listUsers.add(User("", "Jean Edmond", "De la Villardière", "", "17/03/2010", "", "0.0", "0.0"))
        listUsers.add(User("", "Samuel", "Urbanowicz", "", "03/01/2017", "", "0.0", "0.0"))
        listUsers.add(User("", "Angela", "Merkel", "", "04/04/2018", "", "0.0", "0.0"))
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
        listMeetings.add(Meeting("", listUsers[0].firebaseId, listTypeMeeting[12].firebaseId, listTypeMeeting[12].name, Theme.SPORT, "Ludo Club", "05/05/2020", "20/05/2020", "0.0", "0.0", ""))
        listMeetings.add(Meeting("", listUsers[1].firebaseId, listTypeMeeting[2].firebaseId, listTypeMeeting[2].name, Theme.CULTURE, "Expo Impressionniste", "01/05/2020", "25/05/2020", "0.0", "0.0", ""))
        listMeetings.add(Meeting("", listUsers[0].firebaseId, listTypeMeeting[7].firebaseId, listTypeMeeting[7].name, Theme.SPORT, "Speed Karting", "26/04/2020", "22/05/2020", "0.0", "0.0", ""))
        listMeetings.add(Meeting("", listUsers[3].firebaseId, listTypeMeeting[13].firebaseId, listTypeMeeting[13].name, Theme.SPORT, "Escalade", "07/03/2020", "29/05/2020", "0.0", "0.0", ""))
        listMeetings.add(Meeting("", listUsers[3].firebaseId, listTypeMeeting[9].firebaseId, listTypeMeeting[9].name, Theme.SPORT, "UTMB", "08/04/2020", "09/06/2020", "0.0", "0.0", ""))
        listMeetings.add(Meeting("", listUsers[0].firebaseId, listTypeMeeting[8].firebaseId, listTypeMeeting[8].name, Theme.SPORT, "The Sky Divers", "02/05/2020", "30/05/2020", "0.0", "0.0", ""))
        listMeetings.add(Meeting("", listUsers[2].firebaseId, listTypeMeeting[4].firebaseId, listTypeMeeting[4].name, Theme.CULTURE, "Concert U2", "01/02/2020", "08/07/2020", "0.0", "0.0", ""))
        listMeetings.add(Meeting("", listUsers[2].firebaseId, listTypeMeeting[1].firebaseId, listTypeMeeting[1].name, Theme.CULTURE, "Le misanthrope", "17/04/2020", "25/05/2020", "0.0", "0.0", ""))
        listMeetings.add(Meeting("", listUsers[1].firebaseId, listTypeMeeting[3].firebaseId, listTypeMeeting[3].name, Theme.CULTURE, "Le Vieux Lyon", "04/05/2020", "07/06/2020", "0.0", "0.0", ""))
        listMeetings.add(Meeting("", listUsers[2].firebaseId, listTypeMeeting[5].firebaseId, listTypeMeeting[5].name, Theme.CULTURE, "La fête du paradis", "02/03/2020", "26/05/2020", "0.0", "0.0", ""))
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