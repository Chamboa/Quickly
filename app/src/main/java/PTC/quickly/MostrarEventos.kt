package PTC.quickly

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerView.EventosAdapter
import com.example.ptc1.modelo.DTEvento
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class MostrarEventos : AppCompatActivity() {
    val comite = Login.id_comite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mostrar_eventos)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rcvwMostrarEvento = findViewById<RecyclerView>(R.id.rcvMostrarEventos)
        rcvwMostrarEvento.layoutManager = LinearLayoutManager(this)
        rcvwMostrarEvento.setHasFixedSize(true)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtrasea)
        btnAtras.setOnClickListener {
            finish()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val listaEventos = obtenerEventos()
            withContext(Dispatchers.Main) {
                if (listaEventos.isNotEmpty()) {
                    val adaptador = EventosAdapter(listaEventos, { evento ->
                        // Aquí implementas lo que sucede cuando se edita el evento
                        Log.d("Editar", "Editando el evento: ${evento.nombre}")
                    }, { evento ->
                        // Aquí implementas lo que sucede cuando se elimina el evento
                        Log.d("Eliminar", "Eliminando el evento: ${evento.nombre}")
                    })

                    rcvwMostrarEvento.adapter = adaptador
                } else {
                    Log.d("MostrarEventos", "No se encontraron eventos.")
                }
            }
        }
    }

    private suspend fun obtenerEventos(): List<DTEvento> {
        return withContext(Dispatchers.IO) {
            val lista = mutableListOf<DTEvento>()
            val objConexion = ClaseConexion().cadenaConexion()

            objConexion?.use { connection ->
                try {
                    val query = """
                    SELECT e.UUID_Evento, e.UUID_Usuario, e.lugar, e.descripcion, e.nombre, e.fecha, e.hora
                    FROM Eventos e
                    JOIN Usuario u ON e.UUID_Usuario = u.UUID_Usuario
                    WHERE u.id_comite = ?
                """.trimIndent()

                    val statement = connection.prepareStatement(query)
                    if (comite != null) {
                        statement.setInt(1, comite.toInt())
                    }
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
                    }
                } catch (e: Exception) {
                    Log.e("Error de consulta", e.message.toString())
                }
            }
            lista
        }
    }
}
