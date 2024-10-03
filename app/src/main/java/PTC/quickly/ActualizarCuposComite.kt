package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerView.AdActualizarCuposComite
import com.example.ptc1.modelo.dcCuposComite
import modelo.ClaseConexion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActualizarCuposComite : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptador: AdActualizarCuposComite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actualizar_cupos_comite)
        supportActionBar?.hide()

        recyclerView = findViewById(R.id.rcvCupos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnAgregarComite = findViewById<ImageButton>(R.id.xddd)
        val btnAtras = findViewById<ImageView>(R.id.btnAtrasJ)
        btnAtras.setOnClickListener {
            finish()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val datos = obtenerDatosDesdeBaseDeDatos()

            if (datos.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ActualizarCuposComite, "No hay comités disponibles", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    adaptador = AdActualizarCuposComite(datos)
                    recyclerView.adapter = adaptador
                }
            }
        }

        btnAgregarComite.setOnClickListener {

                val intent = Intent(this, AgregarComite::class.java)
                startActivity(intent)

        }
    }

    private fun obtenerDatosDesdeBaseDeDatos(): List<dcCuposComite> {
        val objConexion = ClaseConexion().cadenaConexion()
        if (objConexion == null) {
            Log.e("Error", "No se pudo conectar a la base de datos")
            runOnUiThread {
                Toast.makeText(this, "No se pudo conectar a la base de datos", Toast.LENGTH_SHORT).show()
            }
            return emptyList()
        }

        val datos = mutableListOf<dcCuposComite>()
        val query = "SELECT * FROM Comite"
        try {
            val statement = objConexion.createStatement()
            val resultSet = statement.executeQuery(query)
            while (resultSet.next()) {
                val cupos = resultSet.getInt("cupos")
                val comite = resultSet.getString("comite")
                val id_comite = resultSet.getInt("id_comite")
                val descripcion = resultSet.getString("descripcion")

                val item = dcCuposComite(id_comite, comite, descripcion, cupos)
                datos.add(item)
            }
            resultSet.close()
            statement.close()
            objConexion.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return datos
    }

    suspend fun eliminarComite(id_comite: Int): Boolean {
        return withContext(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                AlertDialog.Builder(this@ActualizarCuposComite)
                    .setTitle("Confirmación")
                    .setMessage("¿Estás seguro de que deseas eliminar este comité?")
                    .setPositiveButton("Eliminar") { dialog, which ->
                        val objConexion = ClaseConexion().cadenaConexion()
                        val query = "DELETE FROM Comite WHERE id_comite = ?"
                        try {
                            val statement = objConexion?.prepareStatement(query)
                            statement?.setInt(1, id_comite)
                            val rowsDeleted = statement?.executeUpdate() ?: 0
                            statement?.close()
                            objConexion?.close()
                            if (rowsDeleted > 0) {
                                Toast.makeText(this@ActualizarCuposComite, "Comité eliminado", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@ActualizarCuposComite, "No se pudo eliminar el comité", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            true
        }
    }

    suspend fun actualizarComite(id_comite: Int, nuevoNombre: String, nuevaDescripcion: String, nuevosCupos: Int): Boolean {
        if (nuevoNombre.isBlank() || nuevaDescripcion.isBlank()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ActualizarCuposComite, "Por favor, ingresa un nombre y descripción válidos", Toast.LENGTH_SHORT).show()
            }
            return false
        }

        return withContext(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            val query = "UPDATE Comite SET comite = ?, descripcion = ?, cupos = ? WHERE id_comite = ?"
            try {
                val statement = objConexion?.prepareStatement(query)
                statement?.setString(1, nuevoNombre)
                statement?.setString(2, nuevaDescripcion)
                statement?.setInt(3, nuevosCupos)  // Permitimos cupos en 0
                statement?.setInt(4, id_comite)
                val rowsUpdated = statement?.executeUpdate() ?: 0
                statement?.close()
                objConexion?.close()
                withContext(Dispatchers.Main) {
                    if (rowsUpdated > 0) {
                        Toast.makeText(this@ActualizarCuposComite, "Comité actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ActualizarCuposComite, "No se pudo actualizar el comité", Toast.LENGTH_SHORT).show()
                    }
                }
                rowsUpdated > 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }


}
