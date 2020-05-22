package fr.barfou.socialnetwork.ui.activity

import android.content.Intent

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import fr.barfou.socialnetwork.R
import fr.barfou.socialnetwork.ui.utils.IntToDateString

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({

            startActivity(Intent(this, LoginActivity::class.java))

            finish()
        }, SPLASH_TIME_OUT)
    }
}