package PTC.quickly

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerViewListAlumnos.AdaptadorAsistencia
import com.example.ptc1.modelo.tbAsistencia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.SQLException

class Asistencia_Alumno : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asistencia_alumno)

        val txtBuscarAlumnos = findViewById<EditText>(R.id.txtBuscarAlumnos)
        val rcvAsistencia = findViewById<RecyclerView>(R.id.rcvAsistencia)
        val imgBuscar = findViewById<ImageView>(R.id.imgBuscar)
        val imgAtrasflecha = findViewById<ImageView>(R.id.imgAtrasflecha)
        val imageView12 = findViewById<ImageView>(R.id.imageView12)
        val imageView13 = findViewById<ImageView>(R.id.imageView13)


        CoroutineScope(Dispatchers.IO).launch {
            val lista = obtenerUsuarios()
            withContext(Dispatchers.Main) {
                val adapter  = AdaptadorAsistencia(lista)
                rcvAsistencia.adapter = adapter
            }
        }

    }


    private fun obtenerUsuarios(): List<tbAsistencia> {
        val conexion = ClaseConexion().cadenaConexion()
        val listaUsuarios = mutableListOf<tbAsistencia>()
        try {
            val statement = conexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM Usuario where id_rol = 1")!!

            while (resultSet.next()) {
                val nombre = resultSet.getString("nombre")
                val id_comite = resultSet.getInt("id_comite")
                listaUsuarios.add(tbAsistencia(nombre, id_comite))


            }


        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {

            conexion?.close()
        }

        return listaUsuarios
    }


}
