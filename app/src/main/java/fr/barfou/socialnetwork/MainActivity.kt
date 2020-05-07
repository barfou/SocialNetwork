package fr.barfou.socialnetwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val activitiesRef = Firebase.database.reference.child("Activities")
        var idTask = activitiesRef.push().key!!
        activitiesRef.child(idTask).setValue(true)
    }
}
