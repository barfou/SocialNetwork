package fr.barfou.socialnetwork.data.model

data class UserMeetingJoin(
        var firebaseId: String,
        var userId: String,
        var meetingId: String
)