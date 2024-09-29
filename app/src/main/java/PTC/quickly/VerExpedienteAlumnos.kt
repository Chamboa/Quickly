package PTC.quickly

import android.os.Bundle
import android.widget.ImageButton
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
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recyclerView = findViewById<RecyclerView>(R.id.rcvVerExpedientes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnAtras = findViewById<ImageButton>(R.id.btnAtrase)

        CoroutineScope(Dispatchers.IO).launch {
            val lista = obtenerDatos()
            withContext(Dispatchers.Main) {
                val adaptador = AdVerExpedienteAlumnos(lista)
                recyclerView.adapter = adaptador
            }
        }
        btnAtras.setOnClickListener {
            finish()
        }
    }

    private suspend fun obtenerDatos(): List<dcExpedientes> {
        return withContext(Dispatchers.IO) {
            val lista = mutableListOf<dcExpedientes>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val query = """
                WITH RankedExpedientes AS (
                    SELECT 
                        e.UUID_expediente,
                        e.UUID_Usuario,
                        e.nombre_evento,
                        e.horas_agregadas,
                        u.nombre AS nombre_usuario,
                        g.grado,
                        r.tipo_rol,
                        c.comite,
                        ev.lugar AS lugar_evento,
                        ev.fecha AS fecha_evento,
                        ev.hora AS hora_evento,
                        ROW_NUMBER() OVER (PARTITION BY e.UUID_Usuario ORDER BY e.UUID_expediente) AS rn
                    FROM 
                        Expediente e
                        JOIN Usuario u ON e.UUID_Usuario = u.UUID_Usuario
                        LEFT JOIN Grado g ON u.id_grado = g.id_grado
                        LEFT JOIN Rol r ON u.id_rol = r.id_rol
                        LEFT JOIN Comite c ON u.id_comite = c.id_comite
                        LEFT JOIN Eventos ev ON e.nombre_evento = ev.nombre
                )
                SELECT 
                    UUID_expediente,
                    UUID_Usuario,
                    nombre_evento,
                    horas_agregadas,
                    nombre_usuario,
                    grado,
                    tipo_rol,
                    comite,
                    lugar_evento,
                    fecha_evento,
                    hora_evento
                FROM 
                    RankedExpedientes
                WHERE 
                    rn = 1
                ORDER BY 
                    UUID_Usuario, UUID_expediente
            """
                val statement = connection.prepareStatement(query)
                val resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val expediente = dcExpedientes(
                        resultSet.getObject("UUID_expediente")?.toString() ?: "",
                        resultSet.getObject("UUID_Usuario")?.toString() ?: "",
                        resultSet.getObject("nombre_evento")?.toString() ?: "",
                        resultSet.getObject("horas_agregadas")?.toString()?.toIntOrNull() ?: 0,
                        resultSet.getObject("nombre_usuario")?.toString() ?: "",
                        resultSet.getObject("grado")?.toString() ?: "",
                        resultSet.getObject("tipo_rol")?.toString() ?: "",
                        resultSet.getObject("comite")?.toString() ?: "",
                        resultSet.getObject("lugar_evento")?.toString() ?: "",
                        resultSet.getObject("fecha_evento")?.toString() ?: "",
                        resultSet.getObject("hora_evento")?.toString() ?: ""
                    )
                    lista.add(expediente)
                }
            }
            lista
        }
    }}