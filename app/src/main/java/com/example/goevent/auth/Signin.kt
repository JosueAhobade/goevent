package com.example.goevent.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goevent.R
import com.example.goevent.api.BackendClient
import com.example.goevent.api.UserRequest
import com.example.goevent.api.UserResponse
import com.example.goevent.events.AccueilEvents
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Signin: AppCompatActivity() {
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.test_login)

        val signupButton = findViewById<TextView>(R.id.signup)

        signupButton.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        val emailInput = findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.btn_login)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val userLogin = UserRequest(email = email, password = password)

                BackendClient.instance.login(userLogin).enqueue(object : Callback<UserResponse> {
                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                        if (response.isSuccessful) {

                            val userResponse = response.body()

                            val editor = getSharedPreferences("user_session", Context.MODE_PRIVATE).edit()
                            editor.putString("username", userResponse?.username)
                            editor.putString("email", userResponse?.email)
                            editor.apply()

                            Toast.makeText(this@Signin, "Bienvenue ${userResponse?.username}", Toast.LENGTH_LONG).show()

                            // Redirige vers HomeActivity ou une autre
                            val intent = Intent(this@Signin, AccueilEvents::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@Signin, "Erreur de connexion : ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Toast.makeText(this@Signin, "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this@Signin, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

