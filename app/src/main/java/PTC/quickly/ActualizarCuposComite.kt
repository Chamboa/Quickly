package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        recyclerView = findViewById(R.id.rcvCupos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnAgregarComite = findViewById<ImageButton>(R.id.xddd)
        val btnAtras = findViewById<ImageView>(R.id.btnAtrasJ)
        btnAtras.setOnClickListener {
            finish()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val datos = obtenerDatosDesdeBaseDeDatos()

            withContext(Dispatchers.Main) {
                adaptador = AdActualizarCuposComite(datos)
                recyclerView.adapter = adaptador
            }
        }
        btnAgregarComite.setOnClickListener {
            val intent = Intent(this, AgregarComite::class.java)
            startActivity(intent)
        }
    }

    private fun obtenerDatosDesdeBaseDeDatos(): List<dcCuposComite> {
        val objConexion = ClaseConexion().cadenaConexion()
        val query = "SELECT * FROM Comite"
        val datos = mutableListOf<dcCuposComite>()
        try {
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery(query)!!
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
            val objConexion = ClaseConexion().cadenaConexion()
            val query = "DELETE FROM Comite WHERE id_comite = ?"
            try {
                val statement = objConexion?.prepareStatement(query)
                statement?.setInt(1, id_comite)
                val rowsDeleted = statement?.executeUpdate() ?: 0
                statement?.close()
                objConexion?.close()
                rowsDeleted > 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun actualizarComite(id_comite: Int, nuevoNombre: String, nuevaDescripcion: String, nuevosCupos: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            val query = "UPDATE Comite SET comite = ?, descripcion = ?, cupos = ? WHERE id_comite = ?"
            try {
                val statement = objConexion?.prepareStatement(query)
                statement?.setString(1, nuevoNombre)
                statement?.setString(2, nuevaDescripcion)
                statement?.setInt(3, nuevosCupos)
                statement?.setInt(4, id_comite)
                val rowsUpdated = statement?.executeUpdate() ?: 0
                statement?.close()
                objConexion?.close()
                rowsUpdated > 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
