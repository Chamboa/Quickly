import PTC.quickly.R
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ptc1.modelo.dcRoles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class alert_dialog : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_dialog)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinner = findViewById<Spinner>(R.id.spinnerComite)

        CoroutineScope(Dispatchers.Main).launch {
            val listaRoles = cargarRoles()
            val roles = listaRoles.map { it.tipo_rol }
            val adaptador = ArrayAdapter(
                this@alert_dialog,
                android.R.layout.simple_spinner_dropdown_item,
                roles
            )
            spinner.adapter = adaptador
        }
    }

    private suspend fun cargarRoles(): List<dcRoles> {
        return withContext(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()!!
            val resultSet = statement.executeQuery("SELECT * FROM Rol")
            val lista = mutableListOf<dcRoles>()
            while (resultSet.next()) {
                val id_rol = resultSet.getInt("id_Rol")
                val nombreRol = resultSet.getString("tipo_rol")
                val valoresJuntos = dcRoles(id_rol, nombreRol)
                lista.add(valoresJuntos)
            }
            return@withContext lista
        }
    }
}
