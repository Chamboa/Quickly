package PTC.quickly
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.view.LayoutInflater
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.util.UUID
import android.app.AlertDialog

class Eventos : AppCompatActivity() {

    val nombre: Any
        get() {
            TODO()
        }
    val fecha: Any
        get() {
            TODO()
        }
    private lateinit var selectedDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_eventos)

        // Recibir la fecha seleccionada desde el intent
        selectedDate = intent.getStringExtra("selectedDate") ?: ""

        // Mandar a llamar a los elementos
        val txtNombreEventos = findViewById<EditText>(R.id.txtNombreEvento)
        val txtDescripcion = findViewById<EditText>(R.id.txtDescripcion)
        val txtLugar = findViewById<EditText>(R.id.txtLugar)
        val txtHora = findViewById<EditText>(R.id.txtHora)
        val txtFecha = findViewById<EditText>(R.id.txtFecha).apply {
            setText(selectedDate)  // Setear la fecha seleccionada por defecto
        }
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val btnAgregarEvento = findViewById<Button>(R.id.btnAgregarEvento)

        // Programar el botón de agregar evento
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
                // Crear un objeto de la clase conexión
                val objConexion = ClaseConexion().cadenaConexion()
                // Crear una variable que tenga un prepare statement
                val addEvento = objConexion?.prepareStatement(
                    "INSERT INTO Eventos (UUID, UUID_Usuario, lugar, descripcion, nombre, fecha, hora) VALUES (?, ?, ?, ?, ?, ?, ?)"
                )!!

                addEvento.setString(1, UUID.randomUUID().toString()) // UUID del evento
                addEvento.setString(2, Login.userUUID) // UUID del usuario que crea el evento
                addEvento.setString(3, txtLugar.text.toString())
                addEvento.setString(4, txtDescripcion.text.toString())
                addEvento.setString(5, txtNombreEventos.text.toString())
                addEvento.setString(6, selectedDate) // Usar la fecha seleccionada desde el calendario
                addEvento.setString(7, txtHora.text.toString())

                println("UUID del usuario: ${Login.userUUID}")
                addEvento.executeUpdate()

                // Mostrar el AlertDialog en el hilo principal
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
                finish() // Cerrar la actividad después de agregar el evento
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