package com.example.a2ride

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Registro : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val nombreetxt = findViewById<EditText>(R.id.etxtNombre)
        val emailedit = findViewById<EditText>(R.id.etxtEmail)
        val confirmaremail = findViewById<EditText>(R.id.etxtConfirmarEmail)
        val passwordedit = findViewById<EditText>(R.id.etxtPassword)
        val confirmarpassword = findViewById<EditText>(R.id.etxtConfirmarPassword)
        val mensajeemail = findViewById<TextView>(R.id.txtMensajeEmail)
        val mensajecontraseña = findViewById<TextView>(R.id.txtMensajePassword)
        val btnregistro = findViewById<Button>(R.id.btnRegistro)

        //--Comprueba coincidencia entre correos y contraseñas--
        emailedit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = emailedit.text.toString()
                val confirmEmail = confirmaremail.text.toString()
                if (email == confirmEmail) {
                    // Los emails coinciden
                    mensajeemail.setTextColor(Color.GREEN)
                    mensajeemail.setText("✅Los correos coindicen.✅")
                } else {
                    // Los emails no coinciden
                    mensajeemail.setTextColor(Color.RED)
                    mensajeemail.setText("❌Los correos no coinciden.❌")
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        confirmaremail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = emailedit.text.toString()
                val confirmEmail = confirmaremail.text.toString()
                if (email == confirmEmail) {
                    // Los emails coinciden
                    mensajeemail.setTextColor(Color.GREEN)
                    mensajeemail.setText("✅Los correos coindicen.✅")
                } else {
                    // Los emails no coinciden
                    mensajeemail.setTextColor(Color.RED)
                    mensajeemail.setText("❌Los correos no coindicen.❌")
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        passwordedit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pass = passwordedit.text.toString()
                val confirmPass = confirmarpassword.text.toString()

                if (pass.length >= 8 && confirmPass.length >= 8) {
                    if (pass == confirmPass) {
                        // Las contraseñas coinciden y tienen al menos 8 caracteres
                        mensajecontraseña.setTextColor(Color.GREEN)
                        mensajecontraseña.setText("✅Las contraseñas coinciden.✅")
                    } else {
                        // Las contraseñas no coinciden
                        mensajecontraseña.setTextColor(Color.RED)
                        mensajecontraseña.setText("❌Las contraseñas no coinciden.❌")
                    }
                } else {
                    // Las contraseñas tienen menos de 8 caracteres
                    mensajecontraseña.setTextColor(Color.RED)
                    mensajecontraseña.setText("❌Las contraseñas deben tener al menos 8 caracteres.❌")
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        confirmarpassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pass = passwordedit.text.toString()
                val confirmPass = confirmarpassword.text.toString()

                if (pass.length >= 8 && confirmPass.length >= 8) {
                    if (pass == confirmPass) {
                        // Las contraseñas coinciden y tienen al menos 8 caracteres
                        mensajecontraseña.setTextColor(Color.GREEN)
                        mensajecontraseña.setText("✅Las contraseñas coinciden.✅")
                    } else {
                        // Las contraseñas no coinciden
                        mensajecontraseña.setTextColor(Color.RED)
                        mensajecontraseña.setText("❌Las contraseñas no coinciden.❌")
                    }
                } else {
                    // Las contraseñas tienen menos de 8 caracteres
                    mensajecontraseña.setTextColor(Color.RED)
                    mensajecontraseña.setText("❌Las contraseñas deben tener al menos 8 caracteres.❌")
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        //--Comprueba que los campos esten llenos

        btnregistro.setOnClickListener{
            val nombre = nombreetxt.text.toString()
            val email = emailedit.text.toString()
            val emailconf = confirmaremail.text.toString()
            val contrase = passwordedit.text.toString()
            val passwordconf = confirmarpassword.text.toString()

            if(nombre.isNotEmpty() && email.isNotEmpty() && emailconf.isNotEmpty() &&
                contrase.isNotEmpty() && passwordconf.isNotEmpty()){
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(confirmaremail.text.toString(),
                        confirmarpassword.text.toString()).addOnCompleteListener{
                        if(it.isSuccessful){

                            db.collection("users").document(email).set(
                                hashMapOf("nombre" to nombreetxt.text.toString())
                            )

                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }
                    }
                }else{
                    Toast.makeText(this, "Correo electronico no valido", Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showAlert(){
        Toast.makeText(this, "Error al registrarse", Toast.LENGTH_SHORT).show()
    }

    private fun showHome(email: String, provider: ProviderType){
        /*val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email )
            putExtra("provider", provider.name)
        }
        startActivity(intent)*/
        finish()
    }

}