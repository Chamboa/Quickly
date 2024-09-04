package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
<<<<<<< HEAD
import androidx.lifecycle.lifecycleScope
=======
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ptc1.MainActivity
>>>>>>> master
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
<<<<<<< HEAD
=======
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
>>>>>>> master

        val btnCancel = findViewById<Button>(R.id.buttonCancel)
        val btnConfirm = findViewById<Button>(R.id.buttonAccept)
        val lblComite = findViewById<TextView>(R.id.lblComite)
<<<<<<< HEAD
        val btnAceptar = findViewById<Button>(R.id.buttonAccept)
=======
>>>>>>> master

        val comision = Unirse_Comite.Comision
        lblComite.text = comision

        btnAceptar.setOnClickListener {
            actualizarComiteUsuario(comision)
        }

        btnCancel.setOnClickListener {
            val intent = Intent(this, Unirse_Comite::class.java)
            startActivity(intent)
        }

        btnConfirm.setOnClickListener {
            actualizarComiteSeleccionado(comision)
        }
    }

<<<<<<< HEAD
    private fun actualizarComiteUsuario(comision: String) {
        lifecycleScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                var statement: PreparedStatement? = null
                var updateStatement: PreparedStatement? = null
                try {
                    val connection = ClaseConexion().cadenaConexion()
                    connection?.autoCommit = false // Inicia una transacción

                    val query = """
                    UPDATE Usuario 
                    SET id_comite = (SELECT id_comite FROM Comite WHERE comite = ?) 
                    WHERE UUID = ?
                """
                    statement = connection?.prepareStatement(query)
                    statement?.setString(1, comision)
                    statement?.setString(2, Login.userUUID) // Asumiendo que tienes el UUID del usuario actual almacenado

                    val filasAfectadas = statement?.executeUpdate() ?: 0

                    if (filasAfectadas > 0) {
                        val updateQuery = """
                        UPDATE Comite
                        SET cupos = cupos - 1
                        WHERE comite = ? AND cupos > 0
                    """
                        updateStatement = connection?.prepareStatement(updateQuery)
                        updateStatement?.setString(1, comision)

                        val cuposActualizados = updateStatement?.executeUpdate() ?: 0
                        if (cuposActualizados > 0) {
                            connection?.commit() // Confirma la transacción si ambas actualizaciones fueron exitosas
                            true
                        } else {
                            connection?.rollback() // Revierte la transacción si no se pudo actualizar los cupos
                            false
                        }
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                } finally {
                    statement?.close()
                    updateStatement?.close()
                }
            }

            if (resultado) {
                Toast.makeText(this@Confirmar_Comite, "Comité actualizado con éxito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@Confirmar_Comite, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@Confirmar_Comite, "Error al actualizar el comité", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
=======
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
>>>>>>> master
