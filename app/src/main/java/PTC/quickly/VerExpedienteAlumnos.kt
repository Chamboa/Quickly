package PTC.quickly

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerView.AdVerExpedienteAlumnos
import com.example.ptc1.modelo.dcExpedientes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class VerExpedienteAlumnos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_expediente_alumnos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recyclerView = findViewById<RecyclerView>(R.id.rcvVerExpedientes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            val lista = obtenerDatos()
            withContext(Dispatchers.Main) {
                val adaptador = AdVerExpedienteAlumnos(lista)
                recyclerView.adapter = adaptador
            }
        }
    }

    private suspend fun obtenerDatos(): List<dcExpedientes> {
        return withContext(Dispatchers.IO) {
            val lista = mutableListOf<dcExpedientes>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val query = """
                SELECT e.UUID_Expediente, e.UUID_Usuario, e.nombre_evento, e.horas_agregadas, u.nombre as nombre_usuario
                FROM Expediente e
                JOIN Usuario u ON e.UUID_Usuario = u.UUID_Usuario
            """
                val statement = connection.prepareStatement(query)
                val resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val UUID = resultSet.getString("UUID_Expediente")
                    val UUID_Alumno = resultSet.getString("UUID_Usuario")
                    val nombreEvento = resultSet.getString("nombre_evento")
                    val horasAgregadas = resultSet.getInt("horas_agregadas")
                    val nombreUsuario = resultSet.getString("nombre_usuario")

                    val expediente = dcExpedientes(UUID, UUID_Alumno, nombreEvento, horasAgregadas, nombreUsuario)
                    lista.add(expediente)

                    println("UUID: $lista")
                }
            }
            lista
        }
    }}