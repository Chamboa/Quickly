package PTC.quickly

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.util.UUID
import android.app.AlertDialog
import android.view.LayoutInflater
import java.text.SimpleDateFormat
import java.util.Locale

class Eventos : AppCompatActivity() {

    private lateinit var selectedDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_eventos)

        selectedDate = intent.getStringExtra("selectedDate") ?: ""

        val txtNombreEventos = findViewById<EditText>(R.id.txtNombreEvento)
        val txtDescripcion = findViewById<EditText>(R.id.txtDescripcion)
        val txtLugar = findViewById<EditText>(R.id.txtLugar)
        val txtHora = findViewById<EditText>(R.id.txtHora)
        val txtFecha = findViewById<EditText>(R.id.txtFecha).apply {
            setText(selectedDate)
        }
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val btnAgregarEvento = findViewById<Button>(R.id.btnAgregarEvento)

        btnAgregarEvento.setOnClickListener {
            if (validarCampos(txtNombreEventos, txtDescripcion, txtLugar, txtHora)) {
                agregarEvento(txtNombreEventos, txtDescripcion, txtLugar, txtHora)
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun validarCampos(vararg campos: EditText): Boolean {
        var hayErrores = false
        for (campo in campos) {
            if (campo.text.isNullOrEmpty()) {
                campo.error = "Este campo no puede estar vacío"
                hayErrores = true
            } else {
                campo.error = null
            }
        }
        return !hayErrores
    }

    private fun agregarEvento(
        txtNombreEventos: EditText,
        txtDescripcion: EditText,
        txtLugar: EditText,
        txtHora: EditText
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                val addEvento = objConexion?.prepareStatement(
                    "INSERT INTO Eventos (UUID_Evento, UUID_Usuario, lugar, descripcion, nombre, fecha, hora) VALUES (?, ?, ?, ?, ?, ?, ?)"
                )!!

                addEvento.setString(1, UUID.randomUUID().toString())
                addEvento.setString(2, Login.userUUID)
                addEvento.setString(3, txtLugar.text.toString())
                addEvento.setString(4, txtDescripcion.text.toString())
                addEvento.setString(5, txtNombreEventos.text.toString())

                // Convertir la fecha a un tipo DATE para insertar en la base de datos
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(selectedDate)
                val sqlDate = java.sql.Date(date.time)




                addEvento.setDate(6, sqlDate)
                addEvento.setString(7, txtHora.text.toString())

                addEvento.executeUpdate()

                val query = "Commit"
                val statement = objConexion.prepareStatement(query)
                statement.executeUpdate()

                withContext(Dispatchers.Main) {
                    mostrarDialogoExito()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarDialogoError(e.message ?: "Error desconocido")
                }
            }
        }
    }

    private fun mostrarDialogoExito() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_exito, null)

        builder.setView(dialogView)
            .setTitle("Evento agregado")
            .setMessage("Se ha agregado el evento exitosamente.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun mostrarDialogoError(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("No se pudo agregar el evento: $mensaje")
            .setPositiveButton("OK", null)
            .show()
    }
}
