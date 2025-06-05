package com.example.a2ride

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LogIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)

        //Comprobacion de incio de sesion
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null){
            //Usuario Logeado
            finish()
        }else{
            //Usuario no logeado
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed(){
                //Toast.makeText(this, "El gesto de regreso esta deshabilitado", Toast.LENGTH_SHORT).show()
            }
        })

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginbutton = findViewById<Button>(R.id.btnIniciarSesion)
        val txtRegistrarse = findViewById<TextView>(R.id.txtRegistrarse)
        val restablecer = findViewById<TextView>(R.id.login_txtReestablecerPassword)

        title = "Authentication"
        loginbutton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(),
                        passwordEditText.text.toString()).addOnCompleteListener{
                            if(it.isSuccessful){
                                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            }else{
                                showAlert()
                            }
                    }
                } else {
                    Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        txtRegistrarse.setOnClickListener{
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        restablecer.setOnClickListener{
            val intent = Intent(this, ReestablecerPassword::class.java)
            startActivity(intent)
        }

    }

    private fun showAlert(){
        Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()

    }

}