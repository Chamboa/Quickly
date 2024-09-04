package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ptc1.RecyclerView.AdVerExpedienteAlumnos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class VerExpediente : AppCompatActivity() {

    companion object {
        var UUID_USUARIO: String = ""
    }
    private var UUID_Alumno = AdVerExpedienteAlumnos.UUID_alumno

    var UUID_Login = Login.userUUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_expediente)

        val tableLayout = findViewById<TableLayout>(R.id.tableExpediente)
        val btnAtras = findViewById<ImageView>(R.id.btnAtrasa)

        btnAtras.setOnClickListener {
            finish()
        }
        Log.d("VerExpediente", "UUID_USUARIO: $UUID_USUARIO")

        lifecycleScope.launch {
            try {
                val expedientes = obtenerExpedientes()
                if (expedientes.isEmpty()) {
                    Log.d("VerExpediente", "No se encontraron expedientes para el usuario")
                    mostrarMensajeNoExpedientes()
                } else {
                    Log.d("VerExpediente", "Se encontraron ${expedientes.size} expedientes")
                    mostrarExpedientes(expedientes, tableLayout)
                }
            } catch (e: Exception) {
                Log.e("VerExpediente", "Error al obtener expedientes", e)
                mostrarError("Error al cargar los expedientes: ${e.message}")
            }
        }
    }

    private suspend fun obtenerExpedientes(): List<Expediente> {
        return withContext(Dispatchers.IO) {
            val lista = mutableListOf<Expediente>()
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    // Primero obtenemos el id_rol del usuario usando el UUID_Login
                    val queryRol = "SELECT id_rol FROM Usuario WHERE UUID_Usuario = ?"
                    val statementRol = connection.prepareStatement(queryRol)
                    statementRol.setString(1, UUID_Login)
                    val resultSetRol = statementRol.executeQuery()

                    if (resultSetRol.next()) {
                        val idRol = resultSetRol.getInt("id_rol")

                        // Consulta según el rol del usuario
                        val queryExpedientes = when (idRol) {
                            1 -> "SELECT nombre_evento, horas_agregadas FROM Expediente WHERE UUID_Usuario = ?"
                            2 -> "SELECT nombre_evento, horas_agregadas FROM Expediente WHERE UUID_Usuario IN (SELECT UUID_Usuario FROM Usuario WHERE UUID_Usuario = ?)"
                            3 -> "SELECT nombre_evento, horas_agregadas FROM Expediente WHERE UUID_Usuario IN (SELECT UUID_Usuario FROM Usuario WHERE UUID_Usuario = ?)"
                            else -> throw Exception("Rol no autorizado para ver expedientes")
                        }

                        val statementExpedientes = connection.prepareStatement(queryExpedientes)
                        if (idRol == 1) {
                            statementExpedientes.setString(1, UUID_Login)
                        }
                        if (idRol == 2 || idRol == 3) {
                            statementExpedientes.setString(1, UUID_Alumno)
                        }


                        val resultSetExpedientes = statementExpedientes.executeQuery()

                        while (resultSetExpedientes.next()) {
                            val expediente = Expediente(
                                resultSetExpedientes.getString("nombre_evento"),
                                resultSetExpedientes.getInt("horas_agregadas")
                            )
                            lista.add(expediente)
                        }
                    } else {
                        throw Exception("No se encontró el usuario con UUID $UUID_Login")
                    }
                } ?: throw Exception("No se pudo establecer la conexión a la base de datos")
            } catch (e: Exception) {
                Log.e("VerExpediente", "Error en obtenerExpedientes", e)
                throw e
            }
            lista
        }
    }

    private fun mostrarExpedientes(expedientes: List<Expediente>, tableLayout: TableLayout) {
        runOnUiThread {
            tableLayout.removeAllViews() // Limpia la tabla antes de agregar nuevas filas

            // Agrega la fila de encabezado
            val headerRow = TableRow(this)
            listOf("Nombre Evento", "Horas Agregadas").forEach { headerText ->
                headerRow.addView(TextView(this).apply {
                    text = headerText
                    setPadding(5, 5, 5, 5)
                    setBackgroundResource(android.R.color.darker_gray)
                })
            }
            tableLayout.addView(headerRow)

            for (expediente in expedientes) {
                val tableRow = TableRow(this)
                tableRow.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )

                listOf(expediente.nombreEvento, expediente.horasAgregadas.toString()).forEach { cellText ->
                    tableRow.addView(TextView(this).apply {
                        text = cellText
                        setPadding(5, 5, 5, 5)
                    })
                }

                tableLayout.addView(tableRow)
            }
        }
    }

    private fun mostrarMensajeNoExpedientes() {
        runOnUiThread {
            Toast.makeText(this, "No se encontraron expedientes para este usuario", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarError(mensaje: String) {
        runOnUiThread {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    data class Expediente(
        val nombreEvento: String,
        val horasAgregadas: Int
    )
}