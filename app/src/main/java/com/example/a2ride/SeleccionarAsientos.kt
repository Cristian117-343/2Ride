package com.example.a2ride

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

private var correoelectronico: String = ""

class SeleccionarAsientos : AppCompatActivity() {
    private lateinit var textViewAsientosSeleccionados: TextView
    private lateinit var textViewPrecioTotal: TextView
    private lateinit var buttonConfirmarReserva: Button

    private var destinoOrigen: String = ""
    private var destinoDestino: String = ""
    private var fecha: String = ""
    private var horario: String = ""

    private val asientosSeleccionados = mutableSetOf<String>()
    private var precioTotal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_asientos)

        // Recuperar datos de los intent extras
        destinoOrigen = intent.getStringExtra("DESTINO_ORIGEN") ?: ""
        destinoDestino = intent.getStringExtra("DESTINO_DESTINO") ?: ""
        fecha = intent.getStringExtra("FECHA") ?: ""
        horario = intent.getStringExtra("HORARIO") ?: ""
        correoelectronico = intent.getStringExtra("CORREO_ELECTRONICO") ?: ""

        //Toast.makeText(this, "El correo logeado es $correoelectronico", Toast.LENGTH_SHORT).show()

        // Inicializar vistas
        textViewAsientosSeleccionados = findViewById(R.id.textViewAsientosSeleccionados)
        textViewPrecioTotal = findViewById(R.id.textViewPrecioTotal)
        buttonConfirmarReserva = findViewById(R.id.buttonConfirmarReserva)

        // Mostrar el mapa de asientos como una imagen estática
        val imagenAsientos = findViewById<ImageView>(R.id.imagenAsientos)
        imagenAsientos.setImageResource(R.drawable.mapa)

        // Configurar la selección de asientos
        configurarSeleccionAsientos()

        // Configurar botón de confirmación
        buttonConfirmarReserva.setOnClickListener {
            if (asientosSeleccionados.isNotEmpty()) {
                // Navegar a la pantalla de pago
                val intent = Intent(this, PagoActivity::class.java).apply {
                    putExtra("DESTINO_ORIGEN", destinoOrigen)
                    putExtra("DESTINO_DESTINO", destinoDestino)
                    putExtra("FECHA", fecha)
                    putExtra("HORARIO", horario)
                    putExtra("ASIENTOS_SELECCIONADOS",asientosSeleccionados.joinToString(separator = ","))
                    putExtra("PRECIO_TOTAL", precioTotal.toString())
                    putExtra("CORREO_ELECTRONICO", correoelectronico)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Selecciona al menos un asiento", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarSeleccionAsientos() {
        // Define filas de asientos
        val filas = listOf("A", "B", "C", "D", "E", "F", "G", "H")

        // Contenedor para los botones de asientos
        val contenedorAsientos: LinearLayout = findViewById(R.id.contenedorAsientos)
        contenedorAsientos.removeAllViews()

        // Crear botones de asientos
        filas.forEach { fila ->
            val numAsientosEnFila = when (fila) {
                "A", "B", "C", "D" -> 4
                "E", "F" -> 4
                "G" -> 4
                "H" -> 2
                else -> 4
            }

            val filaLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
            }

            (1..numAsientosEnFila).forEach { numero ->
                val nombreAsiento = "$fila$numero"
                val botonAsiento = Button(this).apply {
                    text = nombreAsiento
                    setBackgroundColor(Color.LTGRAY) // Fondo gris por defecto
                    setTextColor(Color.BLACK)       // Texto negro por defecto
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    ).apply {
                        setMargins(4, 0, 4, 0)
                    }
                    setOnClickListener { toggleAsientoSeleccion(nombreAsiento, this) }
                }
                filaLayout.addView(botonAsiento)
            }

            contenedorAsientos.addView(filaLayout)
        }
    }

    private fun toggleAsientoSeleccion(nombreAsiento: String, asientoView: Button) {
        if (asientosSeleccionados.contains(nombreAsiento)) {
            // Deseleccionar asiento
            asientosSeleccionados.remove(nombreAsiento)
            asientoView.setBackgroundColor(Color.LTGRAY) // Volver al gris
            asientoView.setTextColor(Color.BLACK)        // Texto negro
            precioTotal -= calcularPrecioAsiento()
        } else {
            // Seleccionar asiento
            if (asientosSeleccionados.size < 4) { // Limitar a 4 asientos
                asientosSeleccionados.add(nombreAsiento)
                asientoView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAsientoSeleccionado)) // Verde
                asientoView.setTextColor(Color.BLACK) // Texto negro
                precioTotal += calcularPrecioAsiento()
            } else {
                Toast.makeText(this, "Máximo 4 asientos", Toast.LENGTH_SHORT).show()
            }
        }

        // Actualizar textos
        actualizarTextos()
    }

    private fun calcularPrecioAsiento(): Int {
        // Definir rutas y precios
        val rutas = listOf(
            "CDMX a Pachuca" to 200,
            "CDMX a Guadalajara" to 700,
            "CDMX a Monterrey" to 1000,
            "Pachuca a Monterrey" to 800,
            "Pachuca a Guadalajara" to 600,
            "Guadalajara a Monterrey" to 900
        )

        // Buscar precio para la ruta actual
        val rutaActual = "$destinoOrigen a $destinoDestino"
        val rutaInversa = "$destinoDestino a $destinoOrigen"

        rutas.find { it.first == rutaActual || it.first == rutaInversa }?.let {
            return it.second
        }

        return 0
    }

    private fun actualizarTextos() {
        // Actualizar lista de asientos seleccionados
        textViewAsientosSeleccionados.text = "Asientos: ${asientosSeleccionados.joinToString(", ")}"

        // Actualizar precio total
        textViewPrecioTotal.text = "Precio total: $$precioTotal"
    }
}