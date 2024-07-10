package PTC.quickly

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.Connection
import java.sql.ResultSet

class Unirse_Comite : AppCompatActivity() {
    private lateinit var circuloVerde: View
    private lateinit var circuloRojo: View
    private lateinit var connection: Connection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_unirse_comite)

        circuloVerde = findViewById(R.id.circulo_verde)
        circuloRojo = findViewById(R.id.circulo_rojo)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.comisiones)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        connection = ClaseConexion().cadenaConexion() ?: run {
            Toast.makeText(this, "No se pudo establecer la conexión con la base de datos", Toast.LENGTH_LONG).show()
            return
        }

        verificarCupos()
    }

    private fun verificarCupos() {
        lifecycleScope.launch {
            val cuposDisponibles = consultarCuposEnBaseDeDatos()

            if (cuposDisponibles) {
                circuloVerde.visibility = View.VISIBLE
                circuloRojo.visibility = View.GONE
            } else {
                circuloVerde.visibility = View.GONE
                circuloRojo.visibility = View.VISIBLE
            }
        }
    }

    private suspend fun consultarCuposEnBaseDeDatos(): Boolean {
        return withContext(Dispatchers.IO) {
            var hayCupos = false
            try {
                val statement = connection.createStatement()
                val resultSet: ResultSet = statement.executeQuery("SELECT cupos FROM Comite WHERE id_comite = 1")

                if (resultSet.next()) {
                    val cupos = resultSet.getInt("cupos")
                    hayCupos = cupos > 0
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@Unirse_Comite, "Error al consultar la base de datos", Toast.LENGTH_LONG).show()
                }
            }

            hayCupos
        }
    }
}
