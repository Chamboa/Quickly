package PTC.quickly

import android.os.Bundle
import android.util.Log
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ptc1.RecyclerView.AdVerExpedienteAlumnos
import com.example.ptc1.modelo.dcExpedientes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class VerExpediente : AppCompatActivity() {

    var UUID_USUARIO = AdVerExpedienteAlumnos.UUID_alumno

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_expediente)

        // Verificar el valor de UUID_USUARIO
        if (UUID_USUARIO.isNullOrEmpty()) {
            Log.e("VerExpediente", "UUID_USUARIO está vacío o es nulo.")
            Toast.makeText(this, "UUID del usuario no disponible", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("VerExpediente", "UUID_USUARIO: $UUID_USUARIO")

        val tabla = findViewById<TableLayout>(R.id.tableExpediente)

        lifecycleScope.launch {
            try {
                val expedienteList = obtenerDatosExpediente()
                Log.d("VerExpediente", "Expedientes obtenidos: ${expedienteList.size}")

                if (expedienteList.isNotEmpty()) {
                    mostrarDatosEnTabla(expedienteList, tabla)
                } else {
                    Toast.makeText(this@VerExpediente, "No se encontraron datos para mostrar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("VerExpediente", "Error al obtener datos: ${e.message}")
                Toast.makeText(this@VerExpediente, "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun obtenerDatosExpediente(): List<dcExpedientes> = withContext(Dispatchers.IO) {
        val expedienteList = mutableListOf<dcExpedientes>()
        try {
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val query = "SELECT * FROM Expediente WHERE UUID_Usuario = ?"
                connection.prepareStatement(query).use { statement ->
                    statement.setString(1, UUID_USUARIO)
                    statement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            val expediente = dcExpedientes(
                                resultSet.getString("UUID_expediente"),
                                resultSet.getString("UUID_Usuario"),
                                resultSet.getString("nombre_evento"),
                                resultSet.getInt("horas_agregadas"),
                                resultSet.getString("nombre_usuario")
                            )
                            expedienteList.add(expediente)
                            Log.d("VerExpediente", "Expediente añadido: $expediente")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("VerExpediente", "Error en obtenerDatosExpediente: ${e.message}")
            throw e
        }
        Log.d("VerExpediente", "Total de expedientes obtenidos: ${expedienteList.size}")
        expedienteList
    }

    private fun mostrarDatosEnTabla(expedienteList: List<dcExpedientes>, tableLayout: TableLayout) {
        // Limpiar la tabla, excepto la fila de encabezado
        for (i in tableLayout.childCount - 1 downTo 1) {
            tableLayout.removeViewAt(i)
        }

        for (expediente in expedienteList) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
            tableRow.setPadding(5, 5, 5, 5)

            // Columna "UUID Expediente"
            val tvUUID = TextView(this)
            tvUUID.text = expediente.UUID.take(8) // Mostramos solo los primeros 8 caracteres del UUID
            tableRow.addView(tvUUID)

            // Columna "Nombre Evento"
            val tvEvento = TextView(this)
            tvEvento.text = expediente.nombreEvento
            tableRow.addView(tvEvento)

            // Columna "Horas Agregadas"
            val tvHoras = TextView(this)
            tvHoras.text = expediente.horasAgregadas.toString()
            tableRow.addView(tvHoras)

            tableLayout.addView(tableRow)
        }
        Log.d("VerExpediente", "Tabla actualizada con ${expedienteList.size} filas")
    }
}
