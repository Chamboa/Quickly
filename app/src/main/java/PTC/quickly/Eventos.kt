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

        // Crear el canal de notificación al iniciar la actividad
        createNotificationChannel()

        selectedDate = intent.getStringExtra("selectedDate") ?: ""

        val txtNombreEventos = findViewById<EditText>(R.id.txtNombreEvento)
        val txtDescripcion = findViewById<EditText>(R.id.txtDescripcion)
        val txtLugar = findViewById<EditText>(R.id.txtLugar)
        val txtHora = findViewById<EditText>(R.id.txtHora)
        val txtFecha = findViewById<EditText>(R.id.txtFecha).apply {
            setText(selectedDate)
            isFocusable = false  // Desactiva la capacidad de obtener foco
            isClickable = false  // Desactiva los clics en el campo
        }
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val btnAgregarEvento = findViewById<Button>(R.id.btnAgregarEvento)

        txtHora.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                // Formatear la hora seleccionada como 00:00 y mostrarla en el EditText
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

                // Convertir la fecha a un tipo DATE para insertar en la base de datos
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(selectedDate)
                val sqlDate = java.sql.Date(date.time)

                addEvento.setDate(6, sqlDate)
                addEvento.setString(7, txtHora.text.toString())

                addEvento.executeUpdate()

                val query = "Commit"
                val statement = objConexion.prepareStatement(query)
                statement.executeUpdate()

                // Mostrar la notificación después de agregar el evento
                withContext(Dispatchers.Main) {
                    mostrarDialogoExito()
                    checkNotificationPermissionAndSend(
                        txtNombreEventos.text.toString(),
                        txtDescripcion.text.toString(),
                        txtLugar.text.toString(),
                        txtHora.text.toString(),
                        selectedDate
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

    // Verificar si el permiso para notificaciones está otorgado y enviar la notificación si es posible
    private fun checkNotificationPermissionAndSend(
        nombreEvento: String,
        descripcion: String,
        lugar: String,
        hora: String,
        fecha: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permiso concedido, enviar la notificación
                enviarNotificacion(nombreEvento, descripcion, lugar, hora, fecha)
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    notificationRequestCode
                )
            }
        } else {
            // Para versiones anteriores a Android 13 no es necesario solicitar el permiso
            enviarNotificacion(nombreEvento, descripcion, lugar, hora, fecha)
        }
    }

    // Método para enviar la notificación
    private fun enviarNotificacion(nombreEvento: String, descripcion: String, lugar: String, hora: String, fecha: String) {
        val channelId = "event_channel"
        val intent = Intent(this, Eventos::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.imgquickly)
            .setContentTitle("Nuevo Evento: $nombreEvento")
            .setContentText("Descripción: $descripcion\nLugar: $lugar\nFecha: $fecha\nHora: $hora")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Descripción: $descripcion\nLugar: $lugar\nFecha: $fecha\nHora: $hora"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(this)) {
                notify(UUID.randomUUID().hashCode(), builder.build())
            }
        } catch (e: SecurityException) {
            mostrarDialogoError("No se puede enviar la notificación debido a un problema de permisos.")
        }
    }

    // Manejo de la respuesta del usuario al solicitar permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == notificationRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, enviar la notificación
                enviarNotificacion(
                    "Nombre del Evento",
                    "Descripción del Evento",
                    "Lugar del Evento",
                    "Hora del Evento",
                    selectedDate
                )
            } else {
                // El permiso fue rechazado, manejar el caso
                mostrarDialogoError("Permiso de notificaciones denegado.")
            }
        }
    }

    // Método para crear el canal de notificación
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "event_channel"
            val channelName = "Eventos"
            val channelDescription = "Notificaciones de eventos"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
