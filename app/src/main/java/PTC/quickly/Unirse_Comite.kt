package PTC.quickly

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class Unirse_Comite : AppCompatActivity() {

    private lateinit var txtEstado: TextView
    private lateinit var connection: Connection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unirse_comite)

        txtEstado = findViewById(R.id.txtEstado)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.comisiones)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val connection = ClaseConexion().cadenaConexion()

        verificarCupos()
    }

    private fun verificarCupos() {
        lifecycleScope.launch {
            val cuposDisponibles = ConsultarLogistica()

            if (cuposDisponibles) {
                txtEstado.text = "Cupos disponibles"
            } else {
                txtEstado.text = "No hay cupos disponibles"
            }
        }
    }

    private suspend fun ConsultarLogistica(): Boolean {
        return withContext(Dispatchers.IO) {
            var hayCupos = false
            try {

                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT cupos FROM Comite WHERE id_comite = 3")// Verificar si hay resultados en el resultado

                if (resultSet.next()) {
                    val cupos = resultSet.getInt("cupos")
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
