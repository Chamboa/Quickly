package PTC.quickly

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerView.AdChat
import com.example.ptc1.RecyclerView.AdMensaje
import com.example.ptc1.modelo.dcChat
import kotlinx.coroutines.*
import modelo.ClaseConexion
import java.text.SimpleDateFormat
import java.util.*

class Bandeja_chat : AppCompatActivity() {

    private var UUID_remitente: String? = null
    private lateinit var adapter: AdMensaje
    private lateinit var rcvChat: RecyclerView
    private lateinit var txtmensaje: EditText
    private lateinit var btnenviar: ImageButton
    private var UUID_destinatario: String? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bandeja_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var ID_Rol = Login.userRoleId
        UUID_remitente = Login.userUUID
        UUID_destinatario = AdChat.UUID

        rcvChat = findViewById(R.id.rcvMensajes)
        txtmensaje = findViewById(R.id.editTextText)
        btnenviar = findViewById(R.id.button)

        rcvChat.layoutManager = LinearLayoutManager(this)
        adapter = AdMensaje(mutableListOf())
        rcvChat.adapter = adapter

        // Iniciar la actualización periódica de mensajes
        iniciarActualizacionPeriodica()

        btnenviar.setOnClickListener {
            val mensaje = txtmensaje.text.toString().trim()
            if (mensaje.isNotEmpty()) {
                enviarMensaje(mensaje)
            } else {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun iniciarActualizacionPeriodica() {
        coroutineScope.launch {
            while (isActive) {
                actualizarMensajes()
                delay(5000) // Esperar 5 segundos antes de la próxima actualización
            }
        }
    }

    private suspend fun actualizarMensajes() {
        if (UUID_remitente.isNullOrEmpty() || UUID_destinatario.isNullOrEmpty()) {
            println("Error: UUID remitente o destinatario es null o vacío")
            return
        }

        val mensajes = obtenerDatos(UUID_remitente!!, UUID_destinatario!!)
        withContext(Dispatchers.Main) {
            adapter.actualizarMensajes(mensajes)
            rcvChat.scrollToPosition(adapter.itemCount - 1)
        }
    }

    private suspend fun obtenerDatos(UUID_remitente: String, UUID_destinatario: String): List<dcChat> {
        return withContext(Dispatchers.IO) {
            val lista = mutableListOf<dcChat>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { conexion ->
                val statement = conexion.prepareStatement(
                    "SELECT * FROM Reclamo WHERE (UUID_Remitente = ? AND UUID_Destinatario = ?) OR (UUID_Remitente = ? AND UUID_Destinatario = ?) ORDER BY fecha"
                )
                statement.setString(1, UUID_remitente)
                statement.setString(2, UUID_destinatario)
                statement.setString(3, UUID_destinatario)
                statement.setString(4, UUID_remitente)
                val resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val id_mensaje = resultSet.getInt("id_mensaje")
                    val mensaje = resultSet.getString("mensaje")
                    val fecha = resultSet.getString("fecha")
                    lista.add(dcChat(id_mensaje, mensaje, fecha))
                }
            }
            lista
        }
    }

    private fun enviarMensaje(mensaje: String) {
        if (UUID_remitente.isNullOrEmpty() || UUID_destinatario.isNullOrEmpty()) {
            println("Error: UUID remitente o destinatario es null o vacío")
            return
        }

        val horaEnvio = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        coroutineScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { conexion ->
                val stmt = conexion.createStatement()
                val resultSet = stmt.executeQuery("SELECT secuencia_mensaje_id.NEXTVAL FROM dual")
                var id_mensaje = 0
                if (resultSet.next()) {
                    id_mensaje = resultSet.getInt(1)
                }

                val insertarMensaje = conexion.prepareStatement(
                    "INSERT INTO Reclamo (id_mensaje, UUID_remitente, UUID_destinatario, mensaje, fecha) VALUES (?, ?, ?, ?, TO_DATE(?, 'HH24:MI:SS'))"
                )

                insertarMensaje.setInt(1, id_mensaje)
                insertarMensaje.setString(2, UUID_remitente)
                insertarMensaje.setString(3, UUID_destinatario)
                insertarMensaje.setString(4, mensaje)
                insertarMensaje.setString(5, horaEnvio)

                insertarMensaje.executeUpdate()

                withContext(Dispatchers.Main) {
                    txtmensaje.text.clear()
                    val nuevoMensaje = dcChat(id_mensaje = id_mensaje, mensaje = mensaje, fecha = horaEnvio)
                    adapter.agregarMensaje(nuevoMensaje)
                    rcvChat.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancelar todas las corrutinas al destruir la actividad
    }
}