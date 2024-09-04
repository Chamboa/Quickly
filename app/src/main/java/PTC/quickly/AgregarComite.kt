package PTC.quickly

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.Connection

class AgregarComite : AppCompatActivity() {

    private lateinit var editNombreComite: EditText
    private lateinit var editDescripcionComite: EditText
    private lateinit var editCuposComite: EditText
    private lateinit var btnAgregarComite: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_comite)

        // Vinculación de vistas
        editNombreComite = findViewById(R.id.editNombreComite)
        editDescripcionComite = findViewById(R.id.editDescripcionComite)
        editCuposComite = findViewById(R.id.editCuposComite)
        btnAgregarComite = findViewById(R.id.btnAgregarComite)
        val btnAtras = findViewById<Button>(R.id.btnAtrassadl)

        btnAtras.setOnClickListener {
            finish()
        }

        // Configuración de padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración del botón para agregar un nuevo comité
        btnAgregarComite.setOnClickListener {
            val nombre = editNombreComite.text.toString().trim()
            val descripcion = editDescripcionComite.text.toString().trim()
            val cupos = editCuposComite.text.toString().trim().toIntOrNull()

            if (nombre.isNotEmpty() && descripcion.isNotEmpty() && cupos != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    val resultado = agregarNuevoComite(nombre, descripcion, cupos)
                    if (resultado) {
                        Toast.makeText(this@AgregarComite, "Comité agregado con éxito", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la actividad y vuelve a la anterior
                    } else {
                        Toast.makeText(this@AgregarComite, "Error al agregar el comité", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun agregarNuevoComite(nombre: String, descripcion: String, cupos: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            // Paso 1: Obtener el próximo id_comite disponible
            val nextId = obtenerProximoIdComite(objConexion)

            // Paso 2: Insertar el nuevo comité con el id_comite obtenido
            val query = "INSERT INTO Comite (id_comite, comite, descripcion, cupos) VALUES (?, ?, ?, ?)"
            try {
                val statement = objConexion?.prepareStatement(query)
                statement?.setInt(1, nextId)
                statement?.setString(2, nombre)
                statement?.setString(3, descripcion)
                statement?.setInt(4, cupos)
                val rowsInserted = statement?.executeUpdate() ?: 0
                statement?.close()
                objConexion?.close()
                rowsInserted > 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private suspend fun obtenerProximoIdComite(conexion: Connection?): Int {
        return withContext(Dispatchers.IO) {
            val query = "SELECT NVL(MAX(id_comite), 0) + 1 AS next_id FROM Comite"
            var nextId = 1
            try {
                val statement = conexion?.createStatement()
                val resultSet = statement?.executeQuery(query)
                if (resultSet?.next() == true) {
                    nextId = resultSet.getInt("next_id")
                }
                resultSet?.close()
                statement?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            nextId
        }
    }
}
