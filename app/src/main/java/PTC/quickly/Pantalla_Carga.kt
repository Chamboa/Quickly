package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import modelo.ClaseConexion
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class Pantalla_Carga : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_carga) // Usa el layout pantalla_de_carga

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            // Aquí se verifica si hay un comité en la base de datos
            val hasComite = checkForComite()

            val intent = if (hasComite) {
                Intent(this, Unirse_Comite::class.java)
            } else {
                Intent(this, Login::class.java)
            }
            startActivity(intent)
            finish()
        }, 300) // 300 milisegundos de espera
    }

    private fun checkForComite(): Boolean {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        var hasComite = false

        try {
            connection = ClaseConexion().cadenaConexion()
            val query = "Select *from usuario where id_comite = ?"
            if (connection != null) {
                preparedStatement = connection.prepareStatement(query)
            }
            if (preparedStatement != null) {
                resultSet = preparedStatement.executeQuery()
            }

            if (resultSet != null) {
                if (resultSet.next()) {
                    val count = resultSet.getInt("count")
                    hasComite = count > 0
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                resultSet?.close()
                preparedStatement?.close()
                connection?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        return hasComite
    }
}