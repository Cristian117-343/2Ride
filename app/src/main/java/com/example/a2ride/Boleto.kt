package com.example.a2ride

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import org.w3c.dom.Text
import kotlin.random.Random
import java.io.IOException

private var correoelectronico: String = ""
private var numeroorden: String = ""
private var destinoOrigen: String = ""
private var destinoDestino: String = ""
private var fecha: String = ""
private var horario: String = ""
private var asientos: String =""
private var total: String = ""

class Boleto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_boleto)

        correoelectronico = intent.getStringExtra("CORREO_ELECTRONICO") ?: ""
        numeroorden = intent.getStringExtra("NUMERO_ORDEN") ?: ""
        destinoOrigen = intent.getStringExtra("DESTINO_ORIGEN") ?: ""
        destinoDestino = intent.getStringExtra("DESTINO_DESTINO") ?: ""
        fecha = intent.getStringExtra("FECHA") ?: ""
        horario = intent.getStringExtra("HORARIO") ?: ""
        asientos = intent.getStringExtra("ASIENTOS_SELECCIONADOS") ?: ""
        total = intent.getStringExtra("PRECIO_TOTAL") ?: ""

        //Toast.makeText(this, "El correo logeado es $correoelectronico", Toast.LENGTH_SHORT).show()

        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }*/

        val generateButton = findViewById<Button>(R.id.generateButton)
        val qrImageView = findViewById<ImageView>(R.id.qrImageView)
        val txtOrden = findViewById<TextView>(R.id.txtNoOrden)
        val txtOrigen = findViewById<TextView>(R.id.txtOrigen)
        val txtDestino = findViewById<TextView>(R.id.txtDestino)
        val txtFecha = findViewById<TextView>(R.id.txtFecha)
        val txtHorario = findViewById<TextView>(R.id.txtHorario)
        val txtAsientos = findViewById<TextView>(R.id.txtAsientos)
        val txtTotal = findViewById<TextView>(R.id.txtTotal)
        val txtCorreo = findViewById<TextView>(R.id.txtCorreo)

        txtOrden.setText("Numero de orden: $numeroorden")
        txtCorreo.setText("Correo asociado: $correoelectronico")
        txtOrigen.setText("Origen: $destinoOrigen")
        txtDestino.setText("Destino: $destinoDestino")
        txtFecha.setText("Fecha: $fecha")
        txtHorario.setText("Horario: $horario")
        txtAsientos.setText("Asientos: $asientos")
        txtTotal.setText("Total: $total")

        if (!numeroorden.equals("")) {
            val bitmap = generateQRCode(numeroorden.toString())
            qrImageView.setImageBitmap(bitmap)
            /*if (bitmap != null){
                saveQRCodeToFile(bitmap, "$numeroorden"+"QR.png")
            }*/
        }

        generateButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

    }

    private fun generateQRCode(text: String): Bitmap? {
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix: BitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 1024, 1024)
            val width: Int = bitMatrix.width
            val height: Int = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    private fun saveQRCodeToFile(bitmap: Bitmap, fileName: String) {
        val context: Context = this
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName) // Nombre del archivo
            put(MediaStore.Images.Media.MIME_TYPE, "image/png") // Tipo MIME
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/2Ride") // Ruta relativa
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    Toast.makeText(this, "Archivo guardado en: ${uri.path}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al guardar el archivo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error al crear el URI para el archivo", Toast.LENGTH_SHORT).show()
        }
    }

}