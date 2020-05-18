package fr.barfou.socialnetwork.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.barfou.socialnetwork.data.model.*
import fr.barfou.socialnetwork.ui.activity.MainActivity
import fr.barfou.socialnetwork.ui.fragment.FilterFragment
import fr.barfou.socialnetwork.ui.utils.getDistanceFromLatLongInM
import fr.barfou.socialnetwork.ui.utils.toDateTime
import fr.barfou.socialnetwork.ui.utils.unAccent
import kotlinx.coroutines.launch
import java.time.LocalDate

open class MainViewModel(
) : ViewModel() {
    var storedData = false

    // Current User
    var currentUser: User? = null

    // Firebase Refs
    private val firebaseRef = Firebase.database.reference
    private val meetingsRef = firebaseRef.child("Meetings")
    private val usersRef = firebaseRef.child("Users")
    private val typeMeetingsRef = firebaseRef.child("TypesMeeting")
    private val userMeetingJoinRef = firebaseRef.child("UserMeetingJoin")

    // Data
    var listMeetings = mutableListOf<Meeting>()
    var listUsers = mutableListOf<User>()
    var listTypeMeeting = mutableListOf<TypeMeeting>()
    var listUserMeetingJoin = mutableListOf<UserMeetingJoin>()
    var popularityMap = mutableMapOf<String, Int>()

    // Locations
    private val villefranche = ConvertedLocation(45.988800048828125, 4.715638160705566, "Villefranche sur Saône", "France")
    private val bourgEnBresse = ConvertedLocation(46.199424743652344, 5.21480131149292, "Bourg en Bresse", "France")
    private val chatillon = ConvertedLocation(46.11964416503906, 4.957491874694824, "Châtillon sur Chalaronne", "France")
    private val macon = ConvertedLocation(46.3036683, 4.8322266, "Mâcon", "France")
    private val belleville = ConvertedLocation(46.11201477050781, 4.728860855102539, "Belleville sur Saône", "France")
    private val quincie = ConvertedLocation(46.1172922, 4.6139836, "Quincié-en-Beaujolais", "France")
    private val gleize = ConvertedLocation(45.9888277, 4.6971455, "Villefranche", "France")
    private val lyon = ConvertedLocation(45.7578137, 4.8320114, "Lyon", "France")

    fun addNewMeeting(meeting: Meeting) {
        val firebaseId = pushMeetingToFirebase(meeting)
        meeting.firebaseId = firebaseId
        listMeetings.add(meeting)
    }

    fun updateUser(user: User) {
        val position = getUserPosition(user.firebaseId)
        if (position > -1) {
            listUsers[position] = user
            usersRef.child(user.firebaseId).setValue(user)
            currentUser = user
        }
    }

    fun getMostPopularMeetings(): MutableList<Meeting> {
        updatePopularityMap()
        return popularityMap.toList().sortedByDescending { (_, value) -> value }
                .map { it.first }
                .filter { it !in getMeetingsJoined(MainActivity.userId).map { item -> item.firebaseId } }
                .take(5)
                .mapNotNull { getMeetingById(it) }
                .toMutableList()
    }

    fun isJoined(meetingId: String): Boolean {
        return if (currentUser != null) {
            var found = false
            var i = 0
            while (!found && i < listUserMeetingJoin.size) {
                if (listUserMeetingJoin[i].userId == currentUser!!.firebaseId && listUserMeetingJoin[i].meetingId == meetingId)
                    found = true
                else
                    i++
            }
            found
        } else {
            false
        }
    }

    fun exitMeeting(meetingId: String, onSuccess: OnSuccess<Boolean>) {
        if (currentUser != null) {
            val res = getUserMeetingJoin(currentUser!!.firebaseId, meetingId)
            if (res != null) {
                listUserMeetingJoin.removeAt(res.second)
                userMeetingJoinRef.child(res.first.firebaseId).removeValue()
                updatePopularityMap()
                onSuccess(true)
            } else
                onSuccess(false)
        } else
            onSuccess(false)
    }

    fun joinMeeting(meetingId: String, onSuccess: OnSuccess<Boolean>) {
        try {
            if (currentUser != null) {
                var userMeetingJoin = UserMeetingJoin("", currentUser!!.firebaseId, meetingId)
                listUserMeetingJoin.add(userMeetingJoin)
                pushUserMeetingJoin(userMeetingJoin)
                updatePopularityMap()
                onSuccess(true)
            } else {
                onSuccess(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onSuccess(false)
        }
    }

    fun getMeetingsJoined(userId: String): MutableList<Meeting> {
        return listUserMeetingJoin.filter { it.userId == userId }
                .map { it.meetingId }
                .mapNotNull { getMeetingById(it) }
                .toMutableList()
    }

    fun getMeetingsJoinedCount(userId: String) = getMeetingsJoined(userId).size

    fun getMeetingsSuggested(userId: String) = listMeetings.filter { it.userId == userId }.toMutableList()

    fun getMeetingsSuggestedCount(userId: String) = getMeetingsSuggested(userId).size

    fun getSubscribedUsers(meetingId: String): MutableList<User> {
        return listUserMeetingJoin.filter { it.meetingId == meetingId }
                .map { it.userId }
                .mapNotNull { getUserById(it) }
                .toMutableList()
    }

    fun updateCurrentUser(userId: String) {

        if (currentUser == null) {
            usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("FirebaseError", error.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        var user = dataSnapshot.value as HashMap<*, *>
                        val firebaseId = user["firebaseId"] as String
                        val mail = user["mail"] as String
                        val pseudo = user["pseudo"] as String
                        val imageUrl = user["imageUrl"] as String
                        val dateInscription = user["dateInscription"] as String
                        val about = user["about"] as String
                        val latitude = user["latitude"] as String
                        val longitude = user["longitude"] as String
                        currentUser = User(firebaseId, mail, pseudo, imageUrl, dateInscription, about, latitude, longitude)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }

    fun getUserById(userId: String): User? {
        var found = false
        var i = 0
        while (!found && i < listUsers.size) {
            if (listUsers[i].firebaseId == userId)
                found = true
            else
                i++
        }
        return if (found)
            listUsers[i]
        else
            null
    }

    fun filterMeetingByName(name: String, onSuccess: OnSuccess<List<Meeting>>) {
        viewModelScope.launch {
            listMeetings.byName(name)
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

    fun filterMeetingsWithNameAndFilter(name: String, filter: FilterFragment.FilterMode, onSuccess: OnSuccess<MutableList<Pair<Meeting, User?>>>) {
        return when (filter) {
            FilterFragment.FilterMode.BY_PROXIMITY -> {
                filterMeetingsByProximity().byName(name).pairWithUser().run(onSuccess)
            }
            FilterFragment.FilterMode.BY_DATE -> {
                filterMeetingsByDate().byName(name).pairWithUser().run(onSuccess)
            }
            FilterFragment.FilterMode.NONE -> {
                listMeetings.byName(name).pairWithUser().run(onSuccess)
            }
        }
    }

    fun retrieveData(onSuccess: OnSuccess<Boolean>) {
        viewModelScope.launch {
            if (!storedData) {
                firebaseRef.child("isInit").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("FirebaseError", error.message)
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            val isInit = dataSnapshot.value as Boolean
                            if (!isInit)
                                initData() {
                                    onSuccess(it)
                                    observeChanges()
                                }
                            else {
                                loadData {
                                    onSuccess(it)
                                    observeChanges()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            onSuccess(false)
                            observeChanges()
                        }
                    }
                })
            } else {
                updatePopularityMap()
                onSuccess(true)
            }
        }
    }

    private fun userExists(userId: String): Boolean {
        return listUsers.any { item -> item.firebaseId == userId }
    }

    private fun meetingExists(meetingId: String): Boolean {
        return listMeetings.any { item -> item.firebaseId == meetingId }
    }

    private fun userMeetingJoinExist(userMeetingJoinId: String): Boolean {
        return listUserMeetingJoin.any { item -> item.firebaseId == userMeetingJoinId }
    }

    private fun filterMeetingsByDate(): MutableList<Meeting> {
        return listMeetings.asSequence()
                .map { meeting -> meeting to meeting.dateEvent.toDateTime() }
                .filter { it.second.isAfter(LocalDate.now()) }
                .sortedBy { it.second }
                .map { it.first }
                .toMutableList()
    }

    private fun filterMeetingsByProximity(): MutableList<Meeting> {
        return try {
            listMeetings.asSequence()
                    .map { meeting -> meeting to getDistanceFromLatLongInM(currentUser!!.latitude.toDouble(), meeting.latitude.toDouble(), currentUser!!.longitude.toDouble(), meeting.longitude.toDouble()) }
                    .sortedBy { it.second }
                    .map { it.first }
                    .toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    private fun MutableList<Meeting>.byName(name: String): MutableList<Meeting> {
        return this.filter { it.name.unAccent().contains(name.unAccent(), ignoreCase = true) }
                .toMutableList()
    }

    private fun MutableList<Meeting>.pairWithUser(): MutableList<Pair<Meeting, User?>> {
        return this.map { meeting -> meeting to getUserById(meeting.userId) }
                .toMutableList()
    }

    private fun updatePopularityMap() {
        popularityMap = mutableMapOf()
        listUserMeetingJoin.forEach { userMeetingJoin ->
            if (popularityMap.containsKey(userMeetingJoin.meetingId)) {
                var current: Int = popularityMap[userMeetingJoin.meetingId] as Int
                current++
                popularityMap[userMeetingJoin.meetingId] = current
            } else
                popularityMap[userMeetingJoin.meetingId] = 1
        }
    }

    // Return Element & Position
    private fun getUserMeetingJoin(userId: String, meetingId: String): Pair<UserMeetingJoin, Int>? {
        var found = false
        var i = 0
        while (!found && i < listUserMeetingJoin.size) {
            if (listUserMeetingJoin[i].userId == currentUser!!.firebaseId && listUserMeetingJoin[i].meetingId == meetingId)
                found = true
            else
                i++
        }
        return if (found)
            Pair(listUserMeetingJoin[i], i)
        else
            null
    }

    private fun getUserMeetingJoinPosition(userMeetingJoinId: String): Int {
        var found = false
        var i = 0
        while (!found && i < listUserMeetingJoin.size) {
            if (listUserMeetingJoin[i].firebaseId == userMeetingJoinId)
                found = true
            else
                i++
        }
        return if (found)
            i
        else
            -1
    }

    private fun getMeetingById(meetingId: String): Meeting? {
        var found = false
        var i = 0
        while (!found && i < listMeetings.size) {
            if (listMeetings[i].firebaseId == meetingId)
                found = true
            else
                i++
        }
        return if (found)
            listMeetings[i]
        else
            null
    }

    private fun getMeetingPosition(meetingId: String): Int {
        var found = false
        var i = 0
        while (!found && i < listMeetings.size) {
            if (listMeetings[i].firebaseId == meetingId)
                found = true
            else
                i++
        }
        return if (found)
            i
        else
            -1
    }

    private fun getUserPosition(userId: String): Int {
        var found = false
        var i = 0
        while (!found && i < listUsers.size) {
            if (listUsers[i].firebaseId == userId)
                found = true
            else
                i++
        }
        return if (found)
            i
        else
            -1
    }

    private fun initData(onSuccess: OnSuccess<Boolean>) {
        return try {
            initUsers()
            initTypeMeetings()
            initMeetings()
            initUserMeetingJoin()
            updatePopularityMap()
            firebaseRef.child("isInit").setValue(true)
            storedData = true
            onSuccess(true)
        } catch (e: Exception) {
            e.printStackTrace()
            onSuccess(false)
        }
    }

    private fun loadData(onSuccess: OnSuccess<Boolean>) {

        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseError", error.message)
                onSuccess(false)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val data = dataSnapshot.value as HashMap<*, *>

                    // Get Users
                    val users = data["Users"] as HashMap<*, *>
                    users?.map { entry ->
                        val user = entry.value as HashMap<*, *>
                        val firebaseId = entry.key as String
                        val mail = user["mail"] as String
                        val pseudo = user["pseudo"] as String
                        val imageUrl = user["imageUrl"] as String
                        val dateInscription = user["dateInscription"] as String
                        val about = user["about"] as String
                        val latitude = user["latitude"] as String
                        val longitude = user["longitude"] as String
                        listUsers.add(User(firebaseId, mail, pseudo, imageUrl, dateInscription, about, latitude, longitude))
                    }

                    // Get Type Meeting
                    val typesMeetings = data["TypesMeeting"] as HashMap<*, *>
                    typesMeetings?.map { entry ->
                        val typeMeeting = entry.value as HashMap<*, *>
                        val firebaseId = entry.key as String
                        val name = typeMeeting["name"] as String
                        val theme = typeMeeting["theme"] as String
                        listTypeMeeting.add(TypeMeeting(firebaseId, name, theme))
                    }

                    // Get Meetings
                    val meetings = data["Meetings"] as HashMap<*, *>
                    meetings?.map { entry ->
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

                    // GetUserMeetingJoin
                    val userMeetingJoin = data["UserMeetingJoin"] as HashMap<*, *>
                    userMeetingJoin?.map { entry ->
                        val userMeetingJoin = entry.value as HashMap<*, *>
                        val firebaseId = entry.key as String
                        val userId = userMeetingJoin["userId"] as String
                        val meetingId = userMeetingJoin["meetingId"] as String
                        listUserMeetingJoin.add(UserMeetingJoin(firebaseId, userId, meetingId))
                    }

                    updatePopularityMap()
                    storedData = true
                    onSuccess(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onSuccess(false)
                }
            }
        })
    }

    private fun observeChanges() {
        observeUsersChanges()
        observeMeetingsChanges()
        observeUserMeetingJoinChanges()
    }

    private fun getUserWithSnapShot(dataSnapshot: DataSnapshot): User {
        val user = dataSnapshot.value as HashMap<*, *>
        val firebaseId = dataSnapshot.key as String
        val mail = user["mail"] as String
        val pseudo = user["pseudo"] as String
        val imageUrl = user["imageUrl"] as String
        val dateInscription = user["dateInscription"] as String
        val about = user["about"] as String
        val latitude = user["latitude"] as String
        val longitude = user["longitude"] as String
        return User(firebaseId, mail, pseudo, imageUrl, dateInscription, about, latitude, longitude)
    }

    private fun getMeetingWithSnapShot(dataSnapshot: DataSnapshot): Meeting {
        val meeting = dataSnapshot.value as HashMap<*, *>
        val firebaseId = dataSnapshot.key as String
        val userId = meeting["userId"] as String
        val typeId = meeting["typeId"] as String
        val type = getTypeWithId(typeId)
        val theme = getTheme(meeting["theme"] as String)
        val name = meeting["name"] as String
        val dateCreation = meeting["dateCreation"] as String
        val dateEvent = meeting["dateEvent"] as String
        val latitude = meeting["latitude"] as String
        val longitude = meeting["longitude"] as String
        val imageUrl = meeting["imageUrl"] as String
        val details = meeting["details"] as String
        return Meeting(firebaseId, userId, typeId, type, theme, name, dateCreation, dateEvent, latitude, longitude, imageUrl, details)
    }

    private fun getUserMeetingJoinWithSnapShot(dataSnapshot: DataSnapshot): UserMeetingJoin {
        val userMeetingJoin = dataSnapshot.value as HashMap<*, *>
        val firebaseId = dataSnapshot.key as String
        val userId = userMeetingJoin["userId"] as String
        val meetingId = userMeetingJoin["meetingId"] as String
        return UserMeetingJoin(firebaseId, userId, meetingId)
    }

    private fun observeUsersChanges() {

        usersRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val user = getUserWithSnapShot(dataSnapshot)
                    if (!userExists(user.firebaseId)) {
                        listUsers.add(user)
                    } else {
                        val position = getUserPosition(user.firebaseId)
                        listUsers[position] = user
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val user = getUserWithSnapShot(dataSnapshot)
                    val position = getUserPosition(user.firebaseId)
                    listUsers[position] = user
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                try {
                    val firebaseId = dataSnapshot.key as String
                    if (userExists(firebaseId)) {
                        val position = getUserPosition(firebaseId)
                        listUsers.removeAt(position)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun observeMeetingsChanges() {

        meetingsRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val meeting = getMeetingWithSnapShot(dataSnapshot)
                    if (!meetingExists(meeting.firebaseId)) {
                        listMeetings.add(meeting)
                    } else {
                        val position = getMeetingPosition(meeting.firebaseId)
                        listMeetings[position] = meeting
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val meeting = getMeetingWithSnapShot(dataSnapshot)
                    val position = getMeetingPosition(meeting.firebaseId)
                    listMeetings[position] = meeting
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                try {
                    val firebaseId = dataSnapshot.key as String
                    if (meetingExists(firebaseId)) {
                        val position = getMeetingPosition(firebaseId)
                        listMeetings.removeAt(position)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun observeUserMeetingJoinChanges() {

        userMeetingJoinRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val userMeetingJoin = getUserMeetingJoinWithSnapShot(dataSnapshot)
                    if (!userMeetingJoinExist(userMeetingJoin.firebaseId)) {
                        listUserMeetingJoin.add(userMeetingJoin)
                    } else {
                        val position = getUserMeetingJoinPosition(userMeetingJoin.firebaseId)
                        listUserMeetingJoin[position] = userMeetingJoin
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val userMeetingJoin = getUserMeetingJoinWithSnapShot(dataSnapshot)
                    val position = getUserMeetingJoinPosition(userMeetingJoin.firebaseId)
                    listUserMeetingJoin[position] = userMeetingJoin
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                try {
                    val firebaseId = dataSnapshot.key as String
                    if (userMeetingJoinExist(firebaseId)) {
                        val position = getUserMeetingJoinPosition(firebaseId)
                        listUserMeetingJoin.removeAt(position)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
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
        listUsers.add(User("", "john.lennon@gmail.com", "John Lennon", "", "22/05/2019", "Bonjour, bienvenue sur mon profil !", villefranche.lat.toString(), villefranche.long.toString()))
        listUsers.add(User("", "marie.curie@gmail.com", "Marie Currie", "", "20/04/2020", "Bonjour, bienvenue sur mon profil !", gleize.lat.toString(), gleize.long.toString()))
        listUsers.add(User("", "je.dlv@gmail.com", "Jean Edmond De la Villardière", "", "17/03/2010", "Bonjour, bienvenue sur mon profil !", quincie.lat.toString(), quincie.long.toString()))
        listUsers.add(User("", "samuel.urbanowicz@gmail.com", "Samuel Urbanowicz", "", "03/01/2017", "Bonjour, bienvenue sur mon profil !", macon.lat.toString(), macon.long.toString()))
        listUsers.add(User("", "angela.merkel@gmail.com", "Angela Merkel", "", "04/04/2018", "Bonjour, bienvenue sur mon profil !", bourgEnBresse.lat.toString(), bourgEnBresse.long.toString()))
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

        listMeetings.add(Meeting("", listUsers[0].firebaseId, listTypeMeeting[12].firebaseId, listTypeMeeting[12].name, Theme.SPORT, "Ludo Club", "05/05/2020", "20/05/2020", villefranche.lat.toString(), villefranche.long.toString(), "", "Pour amateurs de strikes et autres spares."))
        listMeetings.add(Meeting("", listUsers[1].firebaseId, listTypeMeeting[2].firebaseId, listTypeMeeting[2].name, Theme.CULTURE, "Expo Impressionniste", "01/05/2020", "25/05/2020", macon.lat.toString(), macon.long.toString(), "", "Sur les traces de Claude Monet..."))
        listMeetings.add(Meeting("", listUsers[0].firebaseId, listTypeMeeting[7].firebaseId, listTypeMeeting[7].name, Theme.SPORT, "Speed Karting", "26/04/2020", "22/05/2020", chatillon.lat.toString(), chatillon.long.toString(), "", "Gentleman drivers only."))
        listMeetings.add(Meeting("", listUsers[3].firebaseId, listTypeMeeting[13].firebaseId, listTypeMeeting[13].name, Theme.SPORT, "Escalade", "07/03/2020", "29/05/2020", gleize.lat.toString(), gleize.long.toString(), "", "Petite session grimpe au mur de Gleizé."))
        listMeetings.add(Meeting("", listUsers[3].firebaseId, listTypeMeeting[9].firebaseId, listTypeMeeting[9].name, Theme.SPORT, "UTMB", "08/04/2020", "09/06/2020", belleville.lat.toString(), belleville.long.toString(), "", "Remake de l'UTMB dans les monts du Beaujolais."))
        listMeetings.add(Meeting("", listUsers[0].firebaseId, listTypeMeeting[8].firebaseId, listTypeMeeting[8].name, Theme.SPORT, "The Sky Divers", "02/05/2020", "30/05/2020", bourgEnBresse.lat.toString(), bourgEnBresse.long.toString(), "", "Adrénaline en perspective"))
        listMeetings.add(Meeting("", listUsers[2].firebaseId, listTypeMeeting[4].firebaseId, listTypeMeeting[4].name, Theme.CULTURE, "Concert U2", "01/02/2020", "08/07/2020", quincie.lat.toString(), quincie.long.toString(), "", "U2 en tournée dans les petits villages."))
        listMeetings.add(Meeting("", listUsers[2].firebaseId, listTypeMeeting[1].firebaseId, listTypeMeeting[1].name, Theme.CULTURE, "Le misanthrope", "17/04/2020", "25/05/2020", gleize.lat.toString(), gleize.long.toString(), "", "Excellente pièce de Molière. Venez nombreux."))
        listMeetings.add(Meeting("", listUsers[1].firebaseId, listTypeMeeting[3].firebaseId, listTypeMeeting[3].name, Theme.CULTURE, "Le Vieux Lyon", "04/05/2020", "07/06/2020", lyon.lat.toString(), lyon.long.toString(), "", "A la découverte du Vieux Lyon et de Fourvière."))
        listMeetings.add(Meeting("", listUsers[2].firebaseId, listTypeMeeting[5].firebaseId, listTypeMeeting[5].name, Theme.CULTURE, "La fête du paradis", "02/03/2020", "26/05/2020", chatillon.lat.toString(), chatillon.long.toString(), "", "Toujours avec modération."))
        listMeetings.forEach {
            pushMeetingToFirebase(it)
        }
    }

    private fun initUserMeetingJoin() {
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[0].firebaseId, listMeetings[0].firebaseId))
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[0].firebaseId, listMeetings[1].firebaseId))
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[0].firebaseId, listMeetings[2].firebaseId))
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[0].firebaseId, listMeetings[5].firebaseId))
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[1].firebaseId, listMeetings[1].firebaseId))
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[1].firebaseId, listMeetings[2].firebaseId))
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[1].firebaseId, listMeetings[6].firebaseId))
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[1].firebaseId, listMeetings[8].firebaseId))
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[3].firebaseId, listMeetings[2].firebaseId))
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[3].firebaseId, listMeetings[3].firebaseId))
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[3].firebaseId, listMeetings[5].firebaseId))
        listUserMeetingJoin.add(UserMeetingJoin("", listUsers[3].firebaseId, listMeetings[7].firebaseId))
        listUserMeetingJoin.forEach {
            pushUserMeetingJoin(it)
        }
    }

    private fun pushUserToFirebase(user: User) {
        val firebaseId = usersRef.push().key!!
        user.firebaseId = firebaseId
        usersRef.child(firebaseId).setValue(user)
    }

    private fun pushTypeMeetingToFirebase(typeMeeting: TypeMeeting) {
        val firebaseId = usersRef.push().key!!
        typeMeeting.firebaseId = firebaseId
        typeMeetingsRef.child(firebaseId).setValue(typeMeeting)
    }

    private fun pushMeetingToFirebase(meeting: Meeting): String {
        val firebaseId = usersRef.push().key!!
        meeting.firebaseId = firebaseId
        meetingsRef.child(firebaseId).setValue(meeting)
        return firebaseId
    }

    private fun pushUserMeetingJoin(userMeetingJoin: UserMeetingJoin) {
        val firebaseId = userMeetingJoinRef.push().key!!
        userMeetingJoin.firebaseId = firebaseId
        userMeetingJoinRef.child(firebaseId).setValue(userMeetingJoin)
    }

    private fun test() {
        var test = getDistanceFromLatLongInM(belleville.lat, villefranche.lat, belleville.long, villefranche.long)
        var test2 = getDistanceFromLatLongInM(bourgEnBresse.lat, lyon.lat, bourgEnBresse.long, lyon.long)
        var test3 = getDistanceFromLatLongInM(quincie.lat, macon.lat, quincie.long, macon.long)
    }

    companion object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel() as T
        }
    }
}