package com.example.goevent.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goevent.R
import com.example.goevent.api.BackendClient
import com.example.goevent.api.UserRequest
import com.example.goevent.api.UserResponse
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Signup: AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.signup)

        val signinButton = findViewById<TextView>(R.id.login)

        signinButton.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
        }

        val emailInput = findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password)
        val passwordConfirmInput = findViewById<TextInputEditText>(R.id.confirm_password)
        val usernameInput = findViewById<TextInputEditText>(R.id.username)
        val signupButton = findViewById<Button>(R.id.btn_signup)

        signupButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val passwordConfirm = passwordConfirmInput.text.toString().trim()

            if(password == passwordConfirm) {
                val user = UserRequest(email = email, password = password, username = username)
                BackendClient.instance.signup(user).enqueue(object : Callback<UserResponse> {
                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                        if (response.isSuccessful) {
                            val userResponse = response.body()
                            Toast.makeText(this@Signup, "Bienvenue ${userResponse?.username}", Toast.LENGTH_LONG).show()

                            // Redirige vers login ou home
                            val intent = Intent(this@Signup, Signin::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@Signup, "Erreur : ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Toast.makeText(this@Signup, "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                    Toast.makeText(this@Signup, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            }


        }


    }
}