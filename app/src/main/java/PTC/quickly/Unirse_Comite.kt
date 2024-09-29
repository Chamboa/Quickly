package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class Unirse_Comite : AppCompatActivity() {

    private lateinit var txtLogistica: TextView
    private lateinit var txtSocialyProtocolo: TextView
    private lateinit var txtComunicaciones: TextView
    private lateinit var txtDeportivo: TextView
    private lateinit var txtCultural: TextView
    private lateinit var txtSeguridadYEmergencia: TextView
    private lateinit var txtVidaComunitaria: TextView
    private lateinit var txtMedioAmbiente: TextView
    private lateinit var txtTecnicoCientifico: TextView

    companion object variablesGlobalesLogin{
        var Comision = ""

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unirse_comite)
        supportActionBar?.hide()


        txtLogistica = findViewById(R.id.txtLogistica)
        txtSocialyProtocolo = findViewById(R.id.txtSocialYProtocolo)
        txtComunicaciones = findViewById(R.id.txtComunicaciones)
        txtDeportivo = findViewById(R.id.txtDeportivo)
        txtCultural = findViewById(R.id.txtCultural)
        txtSeguridadYEmergencia = findViewById(R.id.txtSeguridadYEmergencia)
        txtVidaComunitaria = findViewById(R.id.txtVidaComunitaria)
        txtMedioAmbiente = findViewById(R.id.txtMedioAmbiente)
        txtTecnicoCientifico = findViewById(R.id.txtTecnicoCientifico)

        val btnLogistica = findViewById<View>(R.id.btnLogistica)
        val btnComunicaciones = findViewById<View>(R.id.btnComunicaciones)
        val btnSocialProtocolo = findViewById<View>(R.id.btnSocialProtocolo)
        val btnTecnicoCientifico = findViewById<View>(R.id.btnTecnicoCientifico)
        val btnMedioAmbiente = findViewById<View>(R.id.btnMedioAmbiente)
        val btnVidaComunitaria = findViewById<View>(R.id.btnVidaComunitaria)
        val btnSeguridadEmergencia = findViewById<View>(R.id.btnSeguridadEmergencia)
        val btnCultural = findViewById<View>(R.id.btnCultural)
        val btnDeportivo = findViewById<View>(R.id.btnDeportivo)

        btnLogistica.setOnClickListener {
            Comision = "Logistica"
            val intent = Intent(this, Confirmar_Comite::class.java)
            startActivity(intent)
        }

        btnComunicaciones.setOnClickListener {
            Comision = "Comunicaciones"
            val intent = Intent(this, Confirmar_Comite::class.java)
            startActivity(intent)
        }

        btnSocialProtocolo.setOnClickListener {
            Comision = "Social y protocolo"
            val intent = Intent(this, Confirmar_Comite::class.java)
            startActivity(intent)
        }

        btnCultural.setOnClickListener {
            Comision = "Cultural"
            val intent = Intent(this, Confirmar_Comite::class.java)
            startActivity(intent)
        }

        btnDeportivo.setOnClickListener {
            Comision = "Deportivo"
            val intent = Intent(this, Confirmar_Comite::class.java)
            startActivity(intent)
        }
        btnMedioAmbiente.setOnClickListener {
            Comision = "Medio ambiente"
            val intent = Intent(this, Confirmar_Comite::class.java)
            startActivity(intent)

        }
        btnVidaComunitaria.setOnClickListener {
            Comision = "Vida comunitaria"
            val intent = Intent(this, Confirmar_Comite::class.java)
            startActivity(intent)
        }
        btnSeguridadEmergencia.setOnClickListener {
            Comision = "Seguridad y emergencia"
            val intent = Intent(this, Confirmar_Comite::class.java)
            startActivity(intent)
        }
        btnTecnicoCientifico.setOnClickListener {
            Comision = "Tecnico cientifico"
            val intent = Intent(this, Confirmar_Comite::class.java)
            startActivity(intent)
        }


        verificarCuposLogistica()
        verificarCuposSocialyProtocolo()
        verificarCuposComunicaciones()
        verificarCuposDeportivo()
        verificarCuposCultural()
        verificarCuposSeguridadYEmergencia()
        verificarCuposMedioAmbiente()
        verificarCuposVidaComunitaria()
        verificarCuposTecnicoCientifico()
    }

    // Logistica
    private fun verificarCuposLogistica() {
        lifecycleScope.launch {
            val cuposDisponibles = consultarLogistica()

            withContext(Dispatchers.Main) {
                if (cuposDisponibles > 0) {
                    txtLogistica.text = "Cupos disponibles"
                } else {
                    txtLogistica.text = "No hay cupos disponibles"
                    Toast.makeText(this@Unirse_Comite, "$cuposDisponibles", Toast.LENGTH_LONG).show()

                }
            }
        }
    }



    private suspend fun consultarLogistica(): Int {
        return withContext(Dispatchers.IO) {
            var hayCupos = 0
            var resultSet: ResultSet? = null
            var statement: PreparedStatement? = null

            try {
                val connection = ClaseConexion().cadenaConexion()
                statement = connection?.prepareStatement("SELECT cupos FROM Comite WHERE id_comite = 3")
                resultSet = statement?.executeQuery()

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

    // Social y protocolo
    private fun verificarCuposSocialyProtocolo() {
        lifecycleScope.launch {
            val cuposDisponibles = consultarSocialYProtocolo()

            withContext(Dispatchers.Main) {
                if (cuposDisponibles > 0) {
                    txtSocialyProtocolo.text = "Cupos disponibles"
                } else {
                    txtSocialyProtocolo.text = "No hay cupos disponibles"
                    Toast.makeText(this@Unirse_Comite, "$cuposDisponibles", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun consultarSocialYProtocolo(): Int {
        return withContext(Dispatchers.IO) {
            var hayCupos = 0
            var resultSet: ResultSet? = null
            var statement: PreparedStatement? = null

            try {
                val connection = ClaseConexion().cadenaConexion()
                statement = connection?.prepareStatement("SELECT cupos FROM Comite WHERE id_comite = 5")
                resultSet = statement?.executeQuery()


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

    // Comunicaciones
    private fun verificarCuposComunicaciones() {
        lifecycleScope.launch {
            val cuposDisponibles = consultarComunicaciones()

            withContext(Dispatchers.Main) {
                if (cuposDisponibles > 0) {
                    txtComunicaciones.text = "Cupos disponibles"
                } else {
                    txtComunicaciones.text = "No hay cupos disponibles"
                    Toast.makeText(this@Unirse_Comite, "$cuposDisponibles", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun consultarComunicaciones(): Int {
        return withContext(Dispatchers.IO) {
            var hayCupos = 0
            var resultSet: ResultSet? = null
            var statement: PreparedStatement? = null

            try {
                val connection = ClaseConexion().cadenaConexion()
                statement = connection?.prepareStatement("SELECT cupos FROM Comite WHERE id_comite = 4")
                resultSet = statement?.executeQuery()


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

    // Deportivo
    private fun verificarCuposDeportivo() {
        lifecycleScope.launch {
            val cuposDisponibles = consultarDeportivo()

            withContext(Dispatchers.Main) {
                if (cuposDisponibles > 0) {
                    txtDeportivo.text = "Cupos disponibles"
                } else {
                    txtDeportivo.text = "No hay cupos disponibles"
                    Toast.makeText(this@Unirse_Comite, "$cuposDisponibles", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun consultarDeportivo(): Int {
        return withContext(Dispatchers.IO) {
            var hayCupos = 0
            var resultSet: ResultSet? = null
            var statement: PreparedStatement? = null

            try {
                val connection = ClaseConexion().cadenaConexion()
                statement = connection?.prepareStatement("SELECT cupos FROM Comite WHERE id_comite = 2")
                resultSet = statement?.executeQuery()


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

    // Cultural
    private fun verificarCuposCultural() {
        lifecycleScope.launch {
            val cuposDisponibles = consultarCultural()

            withContext(Dispatchers.Main) {
                if (cuposDisponibles > 0) {
                    txtCultural.text = "Cupos disponibles"
                } else {
                    txtCultural.text = "No hay cupos disponibles"
                    Toast.makeText(this@Unirse_Comite, "$cuposDisponibles", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun consultarCultural(): Int {
        return withContext(Dispatchers.IO) {
            var hayCupos = 0
            var resultSet: ResultSet? = null
            var statement: PreparedStatement? = null

            try {
                val connection = ClaseConexion().cadenaConexion()
                statement = connection?.prepareStatement("SELECT cupos FROM Comite WHERE id_comite = 9")
                resultSet = statement?.executeQuery()


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
    // Seguridad y emergencia
    private fun verificarCuposSeguridadYEmergencia() {
        lifecycleScope.launch {
            val cuposDisponibles = consultarSeguridadYEmergencia()

            withContext(Dispatchers.Main) {
                if (cuposDisponibles > 0) {
                    txtSeguridadYEmergencia.text = "Cupos disponibles"
                } else {
                    txtSeguridadYEmergencia.text = "No hay cupos disponibles"
                    Toast.makeText(this@Unirse_Comite, "$cuposDisponibles", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun consultarSeguridadYEmergencia(): Int {
        return withContext(Dispatchers.IO) {
            var hayCupos = 0
            var resultSet: ResultSet? = null
            var statement: PreparedStatement? = null

            try {
                val connection = ClaseConexion().cadenaConexion()
                statement = connection?.prepareStatement("SELECT cupos FROM Comite WHERE id_comite = 1")
                resultSet = statement?.executeQuery()


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
    // Vida comunitaria
    private fun verificarCuposVidaComunitaria() {
        lifecycleScope.launch {
            val cuposDisponibles = consultarVidaComunitaria()

            withContext(Dispatchers.Main) {
                if (cuposDisponibles > 0) {
                    txtVidaComunitaria.text = "Cupos disponibles"
                } else {
                    txtVidaComunitaria.text = "No hay cupos disponibles"
                    Toast.makeText(this@Unirse_Comite, "$cuposDisponibles", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun consultarVidaComunitaria(): Int {
        return withContext(Dispatchers.IO) {
            var hayCupos = 0
            var resultSet: ResultSet? = null
            var statement: PreparedStatement? = null

            try {
                val connection = ClaseConexion().cadenaConexion()
                statement = connection?.prepareStatement("SELECT cupos FROM Comite WHERE id_comite = 8")
                resultSet = statement?.executeQuery()


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
    // Medio ambiente
    private fun verificarCuposMedioAmbiente() {
        lifecycleScope.launch {
            val cuposDisponibles = consultarMedioAmbiente()

            withContext(Dispatchers.Main) {
                if (cuposDisponibles > 0) {
                    txtMedioAmbiente.text = "Cupos disponibles"
                } else {
                    txtMedioAmbiente.text = "No hay cupos disponibles"
                    Toast.makeText(this@Unirse_Comite, "$cuposDisponibles", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun consultarMedioAmbiente(): Int {
        return withContext(Dispatchers.IO) {
            var hayCupos = 0
            var resultSet: ResultSet? = null
            var statement: PreparedStatement? = null

            try {
                val connection = ClaseConexion().cadenaConexion()
                statement = connection?.prepareStatement("SELECT cupos FROM Comite WHERE id_comite = 7")
                resultSet = statement?.executeQuery()


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
    //TecnicoCientifico

    private fun verificarCuposTecnicoCientifico() {
        lifecycleScope.launch {
            val cuposDisponibles = consultarTecnicoCientifico()

            withContext(Dispatchers.Main) {
                if (cuposDisponibles > 0) {
                    txtTecnicoCientifico.text = "Cupos disponibles"
                } else {
                    txtTecnicoCientifico.text = "No hay cupos disponibles"
                    Toast.makeText(this@Unirse_Comite, "$cuposDisponibles", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun consultarTecnicoCientifico(): Int {
        return withContext(Dispatchers.IO) {
            var hayCupos = 0
            var resultSet: ResultSet? = null
            var statement: PreparedStatement? = null

            try {
                val connection = ClaseConexion().cadenaConexion()
                statement = connection?.prepareStatement("SELECT cupos FROM Comite WHERE id_comite = 6")
                resultSet = statement?.executeQuery()


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
