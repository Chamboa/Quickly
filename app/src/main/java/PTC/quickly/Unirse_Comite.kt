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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unirse_comite)

        txtEstado = findViewById(R.id.txtEstado)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.comisiones)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        verificarCupos()
    }

    private fun verificarCupos() {
        lifecycleScope.launch {
            val cuposDisponibles = ConsultarLogistica()

            withContext(Dispatchers.Main) {
                if (cuposDisponibles > 0) {
                    txtEstado.text = "Cupos disponibles"
                } else {
                    txtEstado.text = "No hay cupos disponibles"
                    Toast.makeText(this@Unirse_Comite, "$cuposDisponibles", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun ConsultarLogistica(): Int {
        return withContext(Dispatchers.IO) {
            var hayCupos = 0
            var resultSet: ResultSet? = null
            var statement: PreparedStatement? = null

            try {
                val connection = ClaseConexion().cadenaConexion()
                statement = connection?.prepareStatement("SELECT cupos FROM Comite WHERE id_comite = 3")
                resultSet = statement?.executeQuery()

                println("Este es lo que me trae el select $resultSet")

                if (resultSet != null && resultSet.next()) {
                    hayCupos = resultSet.getInt("cupos")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Unirse_Comite, "Error al consultar la base de datos", Toast.LENGTH_LONG).show()
                }
            } finally {
                resultSet?.close()
                statement?.close()
            }

            hayCupos
        }
    }
}
