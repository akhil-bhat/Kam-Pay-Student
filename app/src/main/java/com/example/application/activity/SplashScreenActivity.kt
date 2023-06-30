package com.example.application.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.application.R
import com.example.application.SignInActivity

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 2025

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}
