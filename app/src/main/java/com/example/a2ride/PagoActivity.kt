package com.example.a2ride

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Random

private var destinoOrigen: String = ""
private var destinoDestino: String = ""
private var Fecha: String = ""
private var Horario: String = ""
private var Asientos: String =""
private var Total: String = ""
private var correoelectronico: String = ""

class PagoActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago)

        destinoOrigen = intent.getStringExtra("DESTINO_ORIGEN") ?: ""
        destinoDestino = intent.getStringExtra("DESTINO_DESTINO") ?: ""
        Fecha = intent.getStringExtra("FECHA") ?: ""
        Horario = intent.getStringExtra("HORARIO") ?: ""
        Asientos = intent.getStringExtra("ASIENTOS_SELECCIONADOS") ?: ""
        Total = intent.getStringExtra("PRECIO_TOTAL") ?: ""
        correoelectronico = intent.getStringExtra("CORREO_ELECTRONICO") ?: ""

        val titular = findViewById<EditText>(R.id.etxtTitular)
        val numtarjeta = findViewById<EditText>(R.id.etxtNumTarjeta)
        val vencimiento = findViewById<EditText>(R.id.etxtVencimiento)
        val cvv = findViewById<EditText>(R.id.etxtCVV)
        val buttonPagar = findViewById<Button>(R.id.btnPagar)


        val longitud = 10 //Define la longitud de la cadena
        val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random()
        val NoOrden = StringBuilder(longitud)

        for (i in 0 until longitud) {
            val index = random.nextInt(caracteres.length)
            NoOrden.append(caracteres[index])
        }

        //Establece la entrada unicamente de numeros
        numtarjeta.inputType = InputType.TYPE_CLASS_NUMBER
        vencimiento.inputType = InputType.TYPE_CLASS_NUMBER
        cvv.inputType = InputType.TYPE_CLASS_NUMBER

        //Limita el numero de caracteres
        val maxLengthTar = 19
        val maxLengthVen = 5
        val maxLengthCVV = 3

        val filtertar = InputFilter.LengthFilter(maxLengthTar)
        numtarjeta.filters = arrayOf(filtertar)
        val filtervenc = InputFilter.LengthFilter(maxLengthVen)
        vencimiento.filters = arrayOf(filtervenc)
        val filtercvv = InputFilter.LengthFilter(maxLengthCVV)
        cvv.filters = arrayOf(filtercvv)

        //Funciones para estableces formato de numeros
        // 1234 5678 1234 5678
        numtarjeta.addTextChangedListener(object : TextWatcher{
            var isUpdating = false
            val space = ' '

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(isUpdating) {
                    isUpdating = false
                    return
                }
                val filtered = s?.replace("\\D".toRegex(),"")
                val formatted = StringBuilder()
                for (i in filtered!!.indices){
                    if (i > 0 && i % 4 ==0){
                        formatted.append(space)
                    }
                    formatted.append(filtered[i])
                }
                isUpdating = true
                numtarjeta.setText(formatted.toString())
                numtarjeta.setSelection(formatted.length)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 12/34
        vencimiento.addTextChangedListener(object : TextWatcher{
            var isUpdating = false
            val slash = '/'

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(isUpdating){
                    isUpdating = false
                    return
                }
                val filtered = s?.replace("\\D".toRegex(),"")
                val formatted = StringBuilder()
                for (i in filtered!!.indices){
                    if (i == 2){
                        formatted.append(slash)
                    }
                    formatted.append(filtered[i])
                }
                isUpdating = true
                vencimiento.setText(formatted.toString())
                vencimiento.setSelection(formatted.length)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        //Configura boton de pagar
        buttonPagar.setOnClickListener {
            val titularbtn = titular.text.toString()
            val numtarbtn = numtarjeta.text.toString()
            val vencbtn = vencimiento.text.toString()
            val cvvbtn = cvv.text.toString()
            val noorden = NoOrden.toString()

            val origen = destinoOrigen
            val destino = destinoDestino
            val fecha = Fecha
            val horario = Horario
            val asientos = Asientos
            val total = Total
            val correo = correoelectronico

            if(titularbtn.isNotEmpty() && numtarbtn.isNotEmpty() && vencbtn.isNotEmpty()
                && cvvbtn.isNotEmpty()){
                if (numtarjeta.length() == 19 && vencimiento.length() == 5
                    && cvv.length() == 3){
                    val loadingLayout = layoutInflater.inflate(R.layout.layout_carga, null)
                    val rootLayout = findViewById<FrameLayout>(R.id.rootLayout)
                    rootLayout.addView(loadingLayout)
                    progressBar = loadingLayout.findViewById(R.id.progressBar)
                    progressBar.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({
                        progressBar.visibility = View.GONE
                    }, 5000)

                    db.collection("orden").document(noorden).set(
                        hashMapOf("Orden" to noorden,
                            "Origen" to origen,
                            "Destino" to destino,
                            "Fecha" to fecha,
                            "Horario" to horario,
                            "Asientos" to asientos,
                            "Total" to total,
                            "Tarjeta" to numtarbtn,
                            "Email" to correo)
                    )

                    val intent = Intent(this, Boleto::class.java).apply {
                        putExtra("NUMERO_ORDEN", NoOrden.toString())
                        putExtra("DESTINO_ORIGEN", origen)
                        putExtra("DESTINO_DESTINO", destino)
                        putExtra("FECHA", fecha)
                        putExtra("HORARIO", horario)
                        putExtra("ASIENTOS_SELECCIONADOS", asientos)
                        putExtra("PRECIO_TOTAL", total)
                        putExtra("CORREO_ELECTRONICO", correo)
                    }
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"Datos fuera de formato", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Por favor rellene los datos.",
                    Toast.LENGTH_SHORT).show()
            }
        }

    }
}
