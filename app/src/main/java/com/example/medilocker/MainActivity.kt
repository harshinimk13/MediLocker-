package com.example.medilocker

import android.content.Intent
import android.widget.Button
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {

                etEmail.error = "Please enter your email"
                etEmail.requestFocus()

            } else if (password.isEmpty()) {

                etPassword.error = "Please enter your password"
                etPassword.requestFocus()

            } else {

                Toast.makeText(
                    this,
                    "Login successful! Welcome to MediLocker",
                    Toast.LENGTH_SHORT
                ).show()

                // Open Dashboard
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)

                // Prevent returning to login using Back button
                finish()
            }
        }
    }
}