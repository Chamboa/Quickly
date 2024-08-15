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

        rcvChat.layoutManager = LinearLayoutManager( this)

        fun obtenerDatos(): List<dcUsuario> {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()!!
            val resultSet = statement.executeQuery("SELECT * FROM Usuario")
            val lista = mutableListOf<dcUsuario>()

            while (resultSet.next()) {
                val id_usuario = resultSet.getString("UUID")
                val nombre = resultSet.getString("nombre")
                val id_rol = resultSet.getInt("id_rol")
                val id_comite = resultSet.getInt("id_comite")

                // Filtro para mostrar solo los usuarios con id_rol igual a 2 o 3
                if (id_rol == 2 || id_rol == 3) {
                    val valoresJuntos = dcUsuario(id_usuario, nombre, id_rol, id_comite)
                    lista.add(valoresJuntos)
                }
            }
            return lista
        }

        CoroutineScope(Dispatchers.IO).launch {
            val lista = obtenerDatos()
            withContext(Dispatchers.Main) {
                val adaptador = AdChat(lista)
                rcvChat.adapter = adaptador
            }
        }
    }
}