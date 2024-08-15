package PTC.quickly

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerView.AdChat
import com.example.ptc1.RecyclerView.AdMensaje
import com.example.ptc1.modelo.dcChat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Bandeja_chat : AppCompatActivity() {

    private var UUID_remitente: String? = null
    private lateinit var adapter: AdMensaje

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bandeja_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        UUID_remitente = Login.userUUID

        val rcvChat = findViewById<RecyclerView>(R.id.rcvMensajes)
        val txtmensaje = findViewById<EditText>(R.id.editTextText)
        val btnenviar = findViewById<ImageButton>(R.id.button)

        // Inicializa el adaptador y lo asigna al RecyclerView
        adapter = AdMensaje(mutableListOf())
        rcvChat.adapter = adapter

        btnenviar.setOnClickListener {
            val UUID_destinatario = AdChat.UUID
            val mensaje = txtmensaje.text.toString()

            if (UUID_remitente.isNullOrEmpty() || UUID_destinatario.isNullOrEmpty()) {
                println("Error: UUID remitente o destinatario es null o vacío")
                return@setOnClickListener
            }

            val horaEnvio = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().cadenaConexion()

                val stmt = objConexion?.createStatement()
                val resultSet = stmt?.executeQuery("SELECT secuencia_mensaje_id.NEXTVAL FROM dual")
                var id_mensaje = 0
                if (resultSet?.next() == true) {
                    id_mensaje = resultSet.getInt(1)
                }

                val insertarMensaje = objConexion?.prepareStatement(
                    "INSERT INTO Reclamo (id_mensaje, UUID_remitente, UUID_destinatario, mensaje, fecha) VALUES (?, ?, ?, ?, TO_DATE(?, 'HH24:MI:SS'))"
                )

                insertarMensaje?.setInt(1, id_mensaje)
                insertarMensaje?.setString(2, UUID_remitente)
                insertarMensaje?.setString(3, UUID_destinatario)
                insertarMensaje?.setString(4, mensaje)
                insertarMensaje?.setString(5, horaEnvio)

                insertarMensaje?.executeUpdate()

                // Agregar el mensaje al RecyclerView

                val nuevoMensaje = dcChat(id_mensaje = id_mensaje, mensaje = mensaje, fecha = horaEnvio)
                adapter.agregarMensaje(nuevoMensaje)
            }



            // Reiniciar los campos a blanco
            txtmensaje.text.clear()
        }
    }
}
