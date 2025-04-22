package com.example.goevent.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.goevent.R

class Profile: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.profile)
        val isLoggedIn = false // à remplacer par ta future logique de session

        val userLayout = findViewById<LinearLayout>(R.id.user_info_layout)
        val loginButton = findViewById<Button>(R.id.login_button)

        if (isLoggedIn) {
            userLayout.visibility = View.VISIBLE
            loginButton.visibility = View.GONE
        } else {
            userLayout.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
        }

    }
}