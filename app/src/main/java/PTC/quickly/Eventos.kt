package PTC.quickly

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class Eventos : AppCompatActivity() {

    private lateinit var selectedDate: String
    private val notificationRequestCode = 1001  // Código de solicitud para notificaciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)
        supportActionBar?.hide()

        // Crear el canal de notificación al iniciar la actividad
        createNotificationChannel()

        // Suscribirse al topic "all" para recibir notificaciones globales
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Suscrito al tema 'all' para notificaciones globales")
                } else {
                    println("Error al suscribirse al tema 'all'")
                }
            }

        selectedDate = intent.getStringExtra("selectedDate") ?: ""

        val txtNombreEventos = findViewById<EditText>(R.id.txtNombreEvento)
        val txtDescripcion = findViewById<EditText>(R.id.txtDescripcion)
        val txtLugar = findViewById<EditText>(R.id.txtLugar)
        val txtHora = findViewById<EditText>(R.id.txtHora)
        val txtFecha = findViewById<EditText>(R.id.txtFecha).apply {
            setText(selectedDate)
            isFocusable = false
            isClickable = false
        }

        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val btnAgregarEvento = findViewById<Button>(R.id.btnAgregarEvento)

        txtHora.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                txtHora.setText(formattedTime)
            }, hour, minute, true)

            timePickerDialog.show()
        }

        btnAgregarEvento.setOnClickListener {
            if (validarCampos(txtNombreEventos, txtDescripcion, txtLugar, txtHora)) {
                agregarEvento(txtNombreEventos, txtDescripcion, txtLugar, txtHora)
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun validarCampos(vararg campos: EditText): Boolean {
        var hayErrores = false
        for (campo in campos) {
            if (campo.text.isNullOrEmpty()) {
                campo.error = "Este campo no puede estar vacío"
                hayErrores = true
            } else {
                campo.error = null
            }
        }
        return !hayErrores
    }

    private fun agregarEvento(
        txtNombreEventos: EditText,
        txtDescripcion: EditText,
        txtLugar: EditText,
        txtHora: EditText
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                val addEvento = objConexion?.prepareStatement(
                    "INSERT INTO Eventos (UUID_Evento, UUID_Usuario, lugar, descripcion, nombre, fecha, hora) VALUES (?, ?, ?, ?, ?, ?, ?)"
                )!!

                addEvento.setString(1, UUID.randomUUID().toString())
                addEvento.setString(2, Login.userUUID)
                addEvento.setString(3, txtLugar.text.toString())
                addEvento.setString(4, txtDescripcion.text.toString())
                addEvento.setString(5, txtNombreEventos.text.toString())

                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(selectedDate)
                val sqlDate = java.sql.Date(date.time)

                addEvento.setDate(6, sqlDate)
                addEvento.setString(7, txtHora.text.toString())

                addEvento.executeUpdate()

                val query = "Commit"
                val statement = objConexion.prepareStatement(query)
                statement.executeUpdate()

                withContext(Dispatchers.Main) {
                    mostrarDialogoExito()
                    // Enviar notificación push a través de FCM
                    enviarNotificacionGlobal(
                        txtNombreEventos.text.toString(),
                        txtDescripcion.text.toString()
                    )
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarDialogoError(e.message ?: "Error desconocido")
                }
            }
        }
    }

    private fun mostrarDialogoExito() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_exito, null)

        builder.setView(dialogView)
            .setTitle("Evento agregado")
            .setMessage("Se ha agregado el evento exitosamente.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun mostrarDialogoError(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("No se pudo agregar el evento: $mensaje")
            .setPositiveButton("OK", null)
            .show()
    }

    // Nueva función para enviar la notificación global a través de Firebase Cloud Messaging
    private fun enviarNotificacionGlobal(titulo: String, mensaje: String) {
        // Aquí llamamos al backend para que envíe la notificación a todos los usuarios a través de FCM
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://tu-servidor.com/api/enviar_notificacion_global"
                val requestBody = mapOf("title" to titulo, "message" to mensaje)

                // Usa Retrofit, Volley o cualquier método de HTTP para enviar esta petición al backend
                // El backend debe manejar el envío a FCM utilizando el topic 'all'

                // Simulación de éxito en la respuesta del servidor
                withContext(Dispatchers.Main) {
                    println("Notificación enviada con éxito a todos los usuarios")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarDialogoError("Error al enviar notificación push: ${e.message}")
                }
            }
        }
    }

    // Crear el canal de notificación local para Android
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "global_notifications"
            val channelName = "Global Notifications"
            val channelDescription = "Notificaciones globales para eventos"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
