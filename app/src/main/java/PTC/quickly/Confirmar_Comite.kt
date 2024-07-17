package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ptc1.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.PreparedStatement

class Confirmar_Comite : AppCompatActivity() {

    companion object {
        val comisionToIdMap = mapOf(
            "Logistica" to 3,
            "Comunicaciones" to 4,
            "Social y protocolo" to 5,
            "Deportivo" to 2,
            "Cultural" to 9,
            "Seguridad y emergencia" to 1,
            "Vida comunitaria" to 8,
            "Medio ambiente" to 7,
            "Tecnico cientifico" to 6
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmar_comite)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnCancel = findViewById<Button>(R.id.buttonCancel)
        val btnConfirm = findViewById<Button>(R.id.buttonAccept)
        val lblComite = findViewById<TextView>(R.id.lblComite)

        val comision = Unirse_Comite.Comision
        lblComite.text = comision

        btnCancel.setOnClickListener {
            val intent = Intent(this, Unirse_Comite::class.java)
            startActivity(intent)
        }

        btnConfirm.setOnClickListener {
            actualizarComiteSeleccionado(comision)
        }
    }

    private fun actualizarComiteSeleccionado(comision: String) {
        lifecycleScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                var actualizado = false
                var statement: PreparedStatement? = null
                val uuidUsuario = Login.UUID

                try {
                    val connection = ClaseConexion().cadenaConexion()
                    val idComite = comisionToIdMap[comision] ?: 0

                    val sql = "UPDATE Usuario SET id_comite = ? WHERE UUID = ?"
                    statement = connection?.prepareStatement(sql)
                    statement?.setInt(1, idComite)
                    statement?.setString(2, uuidUsuario)

                    actualizado = statement?.executeUpdate() ?: 0 > 0
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    statement?.close()
                }

                actualizado
            }

            withContext(Dispatchers.Main) {
                if (resultado) {
                    Toast.makeText(this@Confirmar_Comite, "Comité seleccionado actualizado", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@Confirmar_Comite, Login::class.java)

                    startActivity(intent)
                } else {
                    Toast.makeText(this@Confirmar_Comite, "Error al actualizar el comité seleccionado", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
