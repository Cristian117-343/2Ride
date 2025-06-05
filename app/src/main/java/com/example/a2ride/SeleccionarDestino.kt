package com.example.a2ride

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

private var correoelectronico: String = ""
class SeleccionarDestino : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_destino)

        correoelectronico = intent.getStringExtra("CORREO_ELECTRONICO") ?: ""

        //Configurar los botones para que todos naveguen a Pantalla2
        val buttonCDMX = findViewById<Button>(R.id.btnCDMX)
        val buttonGuadalajara = findViewById<Button>(R.id.btnGDL)
        val buttonPachuca = findViewById<Button>(R.id.btnHDO)
        val buttonMonterrey = findViewById<Button>(R.id.btnMTY)

        // Navegar a Pantalla2 al hacer clic en cada botón

        buttonCDMX.setOnClickListener {
            val intent = Intent(this, SeleccionarDia::class.java)
            intent.putExtra("DESTINO", "CDMX")
            intent.putExtra("CORREO_ELECTRONICO", correoelectronico)
            startActivity(intent)
        }

        buttonGuadalajara.setOnClickListener {
            val intent = Intent(this, SeleccionarDia::class.java)
            intent.putExtra("DESTINO", "Guadalajara")
            intent.putExtra("CORREO_ELECTRONICO", correoelectronico)
            startActivity(intent)
        }

        buttonPachuca.setOnClickListener {
            val intent = Intent(this, SeleccionarDia::class.java)
            intent.putExtra("DESTINO", "Pachuca")
            intent.putExtra("CORREO_ELECTRONICO", correoelectronico)
            startActivity(intent)
        }

        buttonMonterrey.setOnClickListener {
            val intent = Intent(this, SeleccionarDia::class.java)
            intent.putExtra("DESTINO", "Monterrey")
            intent.putExtra("CORREO_ELECTRONICO", correoelectronico)
            startActivity(intent)
        }

    }

}