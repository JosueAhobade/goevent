package com.example.goevent.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.goevent.R

class Profile: AppCompatActivity() {

    private lateinit var userInfoLayout: LinearLayout
    private lateinit var userNameText: TextView
    private lateinit var userEmailText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.profile)

        val userLayout = findViewById<LinearLayout>(R.id.user_info_layout)
        val loginButton = findViewById<Button>(R.id.login_button)

        userInfoLayout = findViewById(R.id.user_info_layout)
        userNameText = findViewById(R.id.user_name)
        userEmailText = findViewById(R.id.user_email)

        // Exemple de stockage local avec SharedPreferences
        val sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", null)
        val email = sharedPref.getString("email", null)

        if (username != null && email != null) {
            // L'utilisateur est connecté, afficher les infos
            userInfoLayout.visibility = View.VISIBLE
            loginButton.visibility = View.GONE
            userNameText.text = "Nom : $username"
            userEmailText.text = "Email : $email"
        } else {
            // L'utilisateur n'est pas connecté
            userInfoLayout.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
        }


        loginButton.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
        }

    }
}