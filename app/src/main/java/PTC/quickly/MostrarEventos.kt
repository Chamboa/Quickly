package PTC.quickly

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerView.AdMostrarEvento
import com.example.ptc1.modelo.DTEvento
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class MostrarEventos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mostrar_eventos)
        supportActionBar?.hide()


        // Configurar el padding para el sistema de barras de la pantalla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar el RecyclerView
        val rcvwMostrarEvento = findViewById<RecyclerView>(R.id.rcvMostrarEventos)
        rcvwMostrarEvento.layoutManager = LinearLayoutManager(this)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtrasea)
        btnAtras.setOnClickListener {
            finish()
        }

        // Iniciar la corutina para cargar los datos de los eventos
        CoroutineScope(Dispatchers.IO).launch {
            val listaEventos = obtenerEventos() // Obtiene la lista de eventos
            withContext(Dispatchers.Main) {
                if (listaEventos.isNotEmpty()) {
                    val adaptador = AdMostrarEvento(listaEventos) // Asigna la lista al adaptador
                    rcvwMostrarEvento.adapter = adaptador
                } else {
                    // Opcional: manejar la situación cuando no hay eventos
                    Log.d("MostrarEventos", "No se encontraron eventos.")
                }
            }
        }

    }

    // Función suspendida para obtener la lista de eventos desde la base de datos
    private suspend fun obtenerEventos(): List<DTEvento> {
        return withContext(Dispatchers.IO) {
            val lista = mutableListOf<DTEvento>()
            val objConexion = ClaseConexion().cadenaConexion()

            objConexion?.use { connection ->
                val query = "Select * from Eventos"
                val statement = connection.prepareStatement(query)
                val resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val UUID_Evento = resultSet.getString("UUID_Evento")
                    val UUID_Usuario = resultSet.getString("UUID_Usuario")
                    val lugar = resultSet.getString("lugar")
                    val descripcion = resultSet.getString("descripcion")
                    val nombre = resultSet.getString("nombre")
                    val fecha = resultSet.getString("fecha")
                    val hora = resultSet.getString("hora")
                    val evento = DTEvento(UUID_Evento, UUID_Usuario, lugar, descripcion, nombre, fecha, hora)
                    lista.add(evento)

                    Log.d("Evento", "Evento agregado: $nombre en $lugar el $fecha a las $hora")
                }
            }

            lista // Devuelve la lista de eventos

        }
    }
}
