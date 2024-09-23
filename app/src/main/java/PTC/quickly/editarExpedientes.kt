package PTC.quickly

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ptc1.modelo.DTEvento
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.PreparedStatement

class editarExpedientes : AppCompatActivity() {

    private lateinit var horasAgregadasTextView: TextView
    private var eventoSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_expedientes)

        horasAgregadasTextView = findViewById(R.id.horasAgregadas)
        val btnGuardar = findViewById<TextView>(R.id.btnGuardar)
        val spEventos = findViewById<Spinner>(R.id.spEventosxd)

        val uuidExpediente = intent.getStringExtra("uuidExpediente") ?: ""
        val nombreEvento = intent.getStringExtra("nombreEvento") ?: ""
        val horasAgregadas = intent.getIntExtra("horasAgregadas", 0)

        horasAgregadasTextView.text = horasAgregadas.toString()

        // Lanzar corrutina para obtener los eventos
        CoroutineScope(Dispatchers.Main).launch {
            val eventos = obtenerEventos().map { it.nombre }
            Log.d("editarExpedientes", "Eventos obtenidos: $eventos")  // Log de eventos obtenidos

            if (eventos.isNotEmpty()) {
                // Configurar el adaptador para el Spinner
                val adaptador = ArrayAdapter(this@editarExpedientes, android.R.layout.simple_spinner_dropdown_item, eventos)
                spEventos.adapter = adaptador
            } else {
                Log.d("editarExpedientes", "No se obtuvieron eventos.")
            }
        }

        // Listener para seleccionar un evento del Spinner
        spEventos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                eventoSeleccionado = parent?.getItemAtPosition(position).toString()
                Log.d("editarExpedientes", "Evento seleccionado: $eventoSeleccionado")  // Log de evento seleccionado
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Botón para guardar los cambios
        btnGuardar.setOnClickListener {
            val nuevasHorasAgregadas = horasAgregadasTextView.text.toString().toIntOrNull()
            if (eventoSeleccionado != null && nuevasHorasAgregadas != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val resultado = actualizarExpediente(uuidExpediente, eventoSeleccionado!!, nuevasHorasAgregadas)
                    withContext(Dispatchers.Main) {
                        if (resultado > 0) {
                            Toast.makeText(this@editarExpedientes, "Expediente actualizado correctamente", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@editarExpedientes, "Error al actualizar el expediente", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    // Método para actualizar el expediente
    private suspend fun actualizarExpediente(uuidExpediente: String, nombreEvento: String, horasAgregadas: Int): Int {
        return withContext(Dispatchers.IO) {
            var filasAfectadas = 0
            val conexion = ClaseConexion().cadenaConexion()
            val query = "UPDATE Expediente SET horas_agregadas = ?, nombre_evento = ? WHERE UUID_Expediente = ?"
            var statement: PreparedStatement? = null

            try {
                statement = conexion?.prepareStatement(query)
                statement?.setInt(1, horasAgregadas)
                statement?.setString(2, nombreEvento)
                statement?.setString(3, uuidExpediente)

                filasAfectadas = statement?.executeUpdate() ?: 0
                Log.d("editarExpedientes", "Filas afectadas: $filasAfectadas")
            } catch (e: Exception) {
                Log.e("editarExpedientes", "Error al actualizar expediente", e)
            } finally {
                statement?.close()
                conexion?.close()
            }
            filasAfectadas
        }
    }

    // Método para obtener los eventos desde la base de datos
    private suspend fun obtenerEventos(): List<DTEvento> {
        return withContext(Dispatchers.IO) {
            val eventos = mutableListOf<DTEvento>()
            val conexion = ClaseConexion().cadenaConexion()

            val query = "SELECT nombre FROM Eventos"
            val statement = conexion?.prepareStatement(query)
            val resultSet = statement?.executeQuery()

            // Si el resultSet tiene datos, agregarlos a la lista de eventos
            while (resultSet?.next() == true) {
                val evento = DTEvento(nombre = resultSet.getString("nombre"))
                eventos.add(evento)
            }

            resultSet?.close()
            statement?.close()
            conexion?.close()

            eventos
        }
    }
}
