package PTC.quickly

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerView.AdChat
import com.example.ptc1.modelo.dcUsuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class elegirChat : AppCompatActivity() {

    private var Id_rol = Login.userRoleId
    private var UUID_usuario = Login.userUUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_elegir_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val rcvChat = findViewById<RecyclerView>(R.id.rcvcargar)

        rcvChat.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            val lista = obtenerDatos()
            withContext(Dispatchers.Main) {
                val adaptador = AdChat(lista)
                rcvChat.adapter = adaptador
            }
        }
    }

    private suspend fun obtenerDatos(): List<dcUsuario> {
        return withContext(Dispatchers.IO) {
            val lista = mutableListOf<dcUsuario>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val query = when (Id_rol) {
                    1 -> {
                        // Para usuarios con id_rol 1, mostrar coordinadores y administradores
                        "SELECT * FROM Usuario WHERE id_rol IN (2, 3)"
                    }

                    2, 3 -> {
                        // Para coordinadores y administradores, mostrar usuarios que han enviado mensajes
                        "SELECT * FROM Usuario WHERE id_rol IN (1)"
                    }

                    else -> return@withContext lista // Retorna lista vacía si el id_rol no es válido
                }

                val statement = connection.prepareStatement(query)


                if (Id_rol == 2 || Id_rol == 3) {
                    statement.setString(1, UUID_usuario)
                }

                val resultSet = statement.executeQuery()


                while (resultSet.next()) {
                    val id_usuario = resultSet.getString("UUID")
                    val nombre = resultSet.getString("nombre")
                    val id_rol = resultSet.getInt("id_rol")
                    val id_comite = resultSet.getInt("id_comite")

                    val usuario = dcUsuario(id_usuario, nombre, id_rol, id_comite)
                    lista.add(usuario)
                }
            }
            lista
        }
    }
}