package com.example.a2ride

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.*

private var correoelectronico: String? = ""

class SeleccionarDia : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var spinnerDestino: Spinner
    private lateinit var spinnerHorario: Spinner
    private lateinit var buttonSeleccionarAsientos: Button
    private var destinoSeleccionado: String? = null
    private var fechaSeleccionada: String? = null
    private var horarioSeleccionado: String? = null
    private val horarios = listOf(
        "06:00", "08:00", "10:00",
        "12:00", "14:00", "16:00",
        "18:00", "20:00"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_dia)

        // Obtener el destino de la pantalla anterior
        destinoSeleccionado = intent.getStringExtra("DESTINO")
        correoelectronico = intent.getStringExtra("CORREO_ELECTRONICO")

        //Comprueba que exista un correo logeado, si no, lo obtiene
        if (correoelectronico.equals(null)){
            //Usuario Logeado
            val user = Firebase.auth.currentUser
            user?.let {
                val correoelec = it.email
                correoelectronico = correoelec.toString()
            }
        }

        calendarView = findViewById(R.id.calendarView)
        spinnerDestino = findViewById(R.id.spinnerDestino)
        spinnerHorario = findViewById(R.id.spinnerHorario)
        buttonSeleccionarAsientos = findViewById(R.id.buttonSeleccionarAsientos)

        // Configurar el mínimo del calendario (hoy)
        val today = Calendar.getInstance()
        calendarView.minDate = today.timeInMillis

        // Configurar el calendario para seleccionar fecha
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            fechaSeleccionada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)

            // Filtrar horarios si la fecha es hoy
            if (selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                val currentTime = SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date())
                val filteredHorarios = horarios.filter { it > currentTime }
                actualizarSpinnerHorarios(filteredHorarios)
            } else {
                actualizarSpinnerHorarios(horarios)
            }
        }

        // Configurar los destinos en el Spinner (excluyendo el destino actual)
        val destinos = mutableListOf("CDMX", "Guadalajara", "Monterrey", "Pachuca")
        destinoSeleccionado?.let { destinos.remove(it) }

        val destinoAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, destinos)
        destinoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDestino.adapter = destinoAdapter

        // Configurar los horarios iniciales en el Spinner
        actualizarSpinnerHorarios(horarios)

        // Configurar el botón de Seleccionar Asientos
        buttonSeleccionarAsientos.setOnClickListener {
            // Validar que se hayan seleccionado todos los campos
            if (fechaSeleccionada == null ||
                spinnerDestino.selectedItem == null ||
                horarioSeleccionado == null) {
                Toast.makeText(this,
                    "Por favor, complete todos los campos",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear intent para ir a la pantalla de selección de asientos
            // Nota: Asegúrate de tener creada la actividad SeleccionarAsientosActivity
            val intent = Intent(this, SeleccionarAsientos::class.java).apply {
                putExtra("DESTINO_ORIGEN", spinnerDestino.selectedItem.toString())
                putExtra("DESTINO_DESTINO", destinoSeleccionado)
                putExtra("FECHA", fechaSeleccionada)
                putExtra("HORARIO", horarioSeleccionado)
                putExtra("CORREO_ELECTRONICO", correoelectronico)
            }
            startActivity(intent)
        }
    }

    private fun actualizarSpinnerHorarios(horariosFiltrados: List<String>) {
        val horarioAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, horariosFiltrados)
        horarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHorario.adapter = horarioAdapter

        spinnerHorario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                horarioSeleccionado = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                horarioSeleccionado = null
            }
        }
    }

}