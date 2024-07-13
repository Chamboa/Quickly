package PTC.quickly

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import java.util.UUID

class Eventos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_eventos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1- Mandar a llamar a los elementos
        val txtNombreEventos = findViewById<EditText>(R.id.txtNombreEvento)
        val txtDescripcion = findViewById<EditText>(R.id.txtDescripcion)
        val txtLugar = findViewById<EditText>(R.id.txtLugar)
        val txtHora = findViewById<EditText>(R.id.txtHora)
        val txtFecha = findViewById<EditText>(R.id.txtFecha)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val btnAgregarEvento = findViewById<Button>(R.id.btnAgregarEvento)

        // Programar el botón de agregar evento
        btnAgregarEvento.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                // Crear un objeto de la clase conexión
                val objConexion = ClaseConexion().cadenaConexion()
                // Crear una variable que tenga un prepare statement
                val addEvento = objConexion?.prepareStatement(
                    "INSERT INTO Eventos (uuid,  lugar, descripcion, nombre, fecha, hora) VALUES ( ?, ?, ?, ?, ?, ?)")!!

                 addEvento.setString(1,UUID.randomUUID().toString())
                addEvento.setString(2,txtNombreEventos.text.toString())
                addEvento.setString(3, txtLugar.text.toString())
                addEvento.setString(4, txtDescripcion.text.toString())
                addEvento.setString(5, txtFecha.text.toString())
                addEvento.setString(6, txtHora.text.toString())


                addEvento.executeUpdate()
            }
        }
    }
}