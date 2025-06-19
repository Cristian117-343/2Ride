package com.example.a2ride

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class ProviderType{
    BASIC
}

private var correoelectronico: String = ""
private var correoobtenido: String? = ""

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle
    private val db = FirebaseFirestore.getInstance()
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "MissingInflatedId", "ScheduleExactAlarm")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        drawer = findViewById(R.id.drawer_layout)
        toogle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toogle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        correoobtenido = intent.getStringExtra("email")
        //val txtcorreo = findViewById<TextView>(R.id.txt_nav_header)
        val headerView = navigationView.getHeaderView(0)
        val navHeaderText = headerView.findViewById<TextView>(R.id.txt_nav_header)

        //Comprobacion de incio de sesion
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null){
            //Usuario Logeado
            val user = Firebase.auth.currentUser
            user?.let {
                val correoelec = it.email
                correoelectronico = correoelec.toString()
            }
            navHeaderText.text = correoelectronico
            //Toast.makeText(this, "El correo logeado es $correoelectronico", Toast.LENGTH_SHORT).show()
        }else{
            //Usuario no logeado
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }

        //Notificacion de ofertas
        val titulos = arrayOf("Nueva Oferta","Nuevo Destino","Quieres Vacacionar?")
        val testo = arrayOf("Hasta 50% para estudiantes!!","Viaja a Monterrey directo!!","Te llevamos a Guadalajara!!")
        val numero = Random.nextInt(0, 3)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Ofertas"
            val descriptionText = "Nuevas ofertas"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("canal_id", name, importance).apply {
                description = descriptionText
            }
            // Registra el canal en el sistema
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


        val builder = NotificationCompat.Builder(this, "canal_id")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titulos[numero])
            .setContentText(testo[numero])
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(101, builder.build())
        }

        fab.setOnClickListener{
            val intent = Intent(this, SeleccionarDestino::class.java)
            intent.putExtra("CORREO_ELECTRONICO", correoelectronico)
            startActivity(intent)
        }

        val container = findViewById<LinearLayout>(R.id.container)
        db.collection("orden").whereEqualTo("Email", correoelectronico).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty){
                    val inflater = LayoutInflater.from(this)
                    for (document in documents){
                        val itemView = inflater.inflate(R.layout.item_layout,container, false)
                        val numeroOrden: TextView = itemView.findViewById(R.id.item_layout_numeroOrden)
                        val total: TextView = itemView.findViewById(R.id.item_layout_costo)
                        val destino: TextView = itemView.findViewById(R.id.item_layout_Destino)
                        val origen: TextView = itemView.findViewById(R.id.item_layout_Origen)
                        val fecha: TextView = itemView.findViewById(R.id.item_layout_Fecha)
                        val horario: TextView = itemView.findViewById(R.id.item_layout_Horario)
                        val asientos: TextView = itemView.findViewById(R.id.item_layout_Asientos)

                        numeroOrden.text = "No. Orden: "+document.getString("Orden") ?: ""
                        total.text = "Total: "+document.getString("Total") ?: ""
                        destino.text = "Destino: "+document.getString("Destino") ?: ""
                        origen.text = "Origen: "+document.getString("Origen") ?: ""
                        fecha.text = "Fecha: "+document.getString("Fecha") ?: ""
                        horario.text = "Horario: "+document.getString("Horario") ?: ""
                        asientos.text = "Asientos: "+document.getString("Asientos") ?: ""

                        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val fechaStr = document.getString("Fecha") + " " + document.getString("Horario")
                        val fechaFirebase = formatter.parse(fechaStr)
                        val fechaActual = Calendar.getInstance().time

                        if (fechaFirebase != null && fechaActual.before(fechaFirebase)){
                            itemView.setBackgroundColor(Color.argb(180,76,175,80))
                            itemView.setOnClickListener {
                                val intent = Intent(this, Boleto::class.java).apply {
                                    putExtra("NUMERO_ORDEN", document.getString("Orden"))
                                    putExtra("DESTINO_ORIGEN", document.getString("Origen"))
                                    putExtra("DESTINO_DESTINO", document.getString("Destino"))
                                    putExtra("FECHA", document.getString("Fecha"))
                                    putExtra("HORARIO", document.getString("Horario"))
                                    putExtra("ASIENTOS_SELECCIONADOS", document.getString("Asientos"))
                                    putExtra("PRECIO_TOTAL", document.getString("Total"))
                                    putExtra("CORREO_ELECTRONICO", document.getString("Email"))
                                }
                                startActivity(intent)
                            }
                        }else{
                            itemView.setBackgroundColor(Color.argb(180,255,0,0))
                            itemView.setOnClickListener{
                                Toast.makeText(this, "Viaje caducado.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        container.addView(itemView)
                    }
                }else{
                    val noDataTextView = TextView(this).apply {
                        layoutParams = RecyclerView.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        ).apply {
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        text = "No se encontraron viajes recientes"
                        textSize = 20f
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        setPadding(16,16,16,16)
                    }
                    container.addView(noDataTextView)
                }
            }
            .addOnFailureListener{ exception ->
                val noDataTextView = TextView(this).apply {
                    layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    text = "Error al obtener los datos: ${exception.message}"
                    textSize = 18f
                    setPadding(16,16,16,16)
                }
                container.addView(noDataTextView)
            }

        val navigationView1: NavigationView = findViewById(R.id.nav_view)
        val headerView1 = navigationView1.getHeaderView(0)
        val themeSwitchButton: ImageButton = headerView1.findViewById(R.id.themeSwitchButton)

        val sharedPref = getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        themeSwitchButton.setOnClickListener {
            val nightMode = AppCompatDelegate.getDefaultNightMode()
            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putInt("night_mode", AppCompatDelegate.MODE_NIGHT_YES)
            }
            editor.apply()
        }

        val savedNightMode = sharedPref.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setDefaultNightMode(savedNightMode)


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            /*R.id.nav_item_one -> {
                val intent = Intent(this, UserProfile::class.java)
                startActivity(intent)
            }
            R.id.nav_item_two -> {
                val intent = Intent(this, CardsActivity::class.java)
                startActivity(intent)
            }*/
            R.id.nav_item_three -> {
                FirebaseAuth.getInstance().signOut()
                onBackPressed()
                finish()
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toogle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toogle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toogle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
