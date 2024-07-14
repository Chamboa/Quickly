package PTC.quickly

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerViewListAlumnos.AdaptadorAsistencia
import com.example.ptc1.modelo.tbAsistencia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.Date

class Asistencia(
    id_Asistencia: Int,
    Hora_de_entrada: Date,
    Hora_de_salida: Date,
    Asistencia_total: Int
) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_asistencia)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rcvAsistencia = findViewById<RecyclerView>(R.id.rcvAsistencia)

        //Agrego el layaut al RecyclerView
        rcvAsistencia.layoutManager = LinearLayoutManager(this)

        ///Espero mostrar datos

        fun obtenerAlumnos(): List<tbAsistencia> {

            //1-Creo conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2-Creo un statement
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM Asistencia")!!

            val listaAsistencia = mutableListOf<tbAsistencia>()

            while (resultSet.next()){
                val id_Asistencia = resultSet.getInt("id_Asistencia")
                val Hora_de_entrada = resultSet.getDate("Hora_de_entrada")
                val Hora_de_salida = resultSet.getDate("Hora_de_salida")
                val Asistencia_total = resultSet.getInt("Asistencia_total")

                val valoresJuntos = Asistencia(id_Asistencia, Hora_de_entrada, Hora_de_salida, Asistencia_total)

                 listaAsistencia.add()

            }
            return listaAsistencia


        }
        CoroutineScope(Dispatchers.IO).launch {
            val AsistenciaDB = obtenerAlumnos()
            withContext(Dispatchers.Main){
                val adapter = AdaptadorAsistencia(AsistenciaDB)
                rcvAsistencia.adapter = adapter
            }
        }




    }
}