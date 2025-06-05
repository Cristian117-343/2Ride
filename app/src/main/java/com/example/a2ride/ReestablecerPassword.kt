package com.example.a2ride

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ReestablecerPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reestablecer_password)

        val btnRecuperar = findViewById<Button>(R.id.btnRecuperarPassword)
        val txtEmail = findViewById<TextView>(R.id.login_emailEditText)

       btnRecuperar.setOnClickListener {
           val emailAddress = txtEmail.text.toString()

           Firebase.auth.sendPasswordResetEmail(emailAddress)
               .addOnCompleteListener { task ->
                   if (task.isSuccessful) {
                        Toast.makeText(this,"Email sent. ", Toast.LENGTH_SHORT).show()
                   }else{
                        Toast.makeText(this, "Error sending email.", Toast.LENGTH_SHORT).show()
                   }
               }
       }

    }
}