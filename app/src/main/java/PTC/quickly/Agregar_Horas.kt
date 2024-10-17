package PTC.quickly

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import java.sql.PreparedStatement
import java.util.Calendar
import java.util.UUID
import android.widget.Toast
import com.example.ptc1.RecyclerView.AdMostrarEvento
import com.example.ptc1.RecyclerViewListAlumnos.AdaptadorAsistencia
import java.text.SimpleDateFormat
import java.util.Locale

class Agregar_Horas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_horas)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imgRegresar = findViewById<Button>(R.id.btnCancelarHoras)
        val txtnombre = findViewById<EditText>(R.id.txtNomEvento)
        val txtfecha = findViewById<EditText>(R.id.txtFechaHoras)
        val txtentrada = findViewById<EditText>(R.id.txtEntradaHoras)
        val txtsalida = findViewById<EditText>(R.id.txtHoraSalida)
        val txtcantidadhoras = findViewById<EditText>(R.id.txtCantidadhoras)
        val btnAgregarHora  = findViewById<Button>(R.id.btnAgregarHoras)

        txtnombre.setText(AdMostrarEvento.selectedNombre)

        val calendario = Calendar.getInstance()
        val anio = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val fechaActual = "$dia/$mes/$anio"
        txtfecha.setText(fechaActual)

        imgRegresar.setOnClickListener {
            finish()
        }

        btnAgregarHora.setOnClickListener {
            val nombreEvento = txtnombre.text.toString().trim()
            val fecha = txtfecha.text.toString().trim()
            val entrada = txtentrada.text.toString().trim()
            val salida = txtsalida.text.toString().trim()
            val cantidadHoras = txtcantidadhoras.text.toString().trim().toIntOrNull()

            // Validaciones
            if (nombreEvento.isEmpty()) {
                showToast("Por favor, ingresa el nombre del evento.")
                return@setOnClickListener
            }
            if (fecha.isEmpty()) {
                showToast("Por favor, selecciona la fecha.")
                return@setOnClickListener
            }
            if (entrada.isEmpty()) {
                showToast("Por favor, selecciona la hora de entrada.")
                return@setOnClickListener
            }
            if (salida.isEmpty()) {
                showToast("Por favor, selecciona la hora de salida.")
                return@setOnClickListener
            }
            if (cantidadHoras == null || cantidadHoras <= 0) {
                showToast("La cantidad de horas debe ser un número positivo.")
                return@setOnClickListener
            }

            // Validar que la hora de entrada sea anterior a la hora de salida
            val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
            val horaEntrada = formatoHora.parse(entrada)
            val horaSalida = formatoHora.parse(salida)

            if (horaEntrada != null && horaSalida != null && horaEntrada >= horaSalida) {
                showToast("La hora de entrada debe ser anterior a la hora de salida.")
                return@setOnClickListener
            }

            val UUIDevento = AdMostrarEvento.selectedUUID

            // Insertar en la base de datos
            GlobalScope.launch(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                var statement: PreparedStatement? = null
                try {
                    // Consultar el nombre del evento usando el UUID_evento
                    val sqlConsulta = "SELECT nombre FROM Eventos WHERE UUID_Evento = ?"
                    statement = objConexion?.prepareStatement(sqlConsulta)
                    statement?.setString(1, UUIDevento.toString())
                    val resultSet = statement?.executeQuery()

                    var nombreEventoBD: String? = null
                    if (resultSet?.next() == true) {
                        nombreEventoBD = resultSet.getString("nombre")
                    }

                    resultSet?.close()
                    statement?.close()

                    if (nombreEventoBD != null) {
                        // Insertar los datos en la tabla Expediente
                        val uuidExpediente = UUID.randomUUID().toString()
                        val sqlInsercion = """
                            INSERT INTO Expediente (UUID_expediente, UUID_usuario, nombre_evento, horas_agregadas)
                            VALUES (?, ?, ?, ?)
                        """.trimIndent()

                        statement = objConexion?.prepareStatement(sqlInsercion)
                        statement?.setString(1, uuidExpediente)
                        statement?.setString(2, AdaptadorAsistencia.selectedUUIDA)
                        statement?.setString(3, nombreEventoBD)
                        statement?.setInt(4, cantidadHoras)

                        statement?.executeUpdate()

                        runOnUiThread {
                            showToast("Horas agregadas correctamente")
                        }
                    } else {
                        runOnUiThread {
                            showToast("Error: no se encontró el evento")
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        showToast("Error al agregar horas")
                    }
                } finally {
                    statement?.close()
                    objConexion?.close()
                }
            }
            finish()
        }

        // Selector de fecha
        txtfecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val fechaMaxima = Calendar.getInstance()
            fechaMaxima.set(anio, mes, dia + 10)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fechaSeleccionada = "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                    txtfecha.setText(fechaSeleccionada)
                },
                anio, mes, dia
            )
            datePickerDialog.datePicker.maxDate = fechaMaxima.timeInMillis
            datePickerDialog.show()
        }

        // Selector de hora de entrada
        txtentrada.setOnClickListener {
            showTimePickerDialog(txtentrada)
        }

        // Selector de hora de salida
        txtsalida.setOnClickListener {
            showTimePickerDialog(txtsalida)
        }
    }

    private fun showTimePickerDialog(editText: EditText) {
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minuto = calendario.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, horaSeleccionada, minutoSeleccionado ->
                val tiempoSeleccionado = String.format("%02d:%02d", horaSeleccionada, minutoSeleccionado)
                editText.setText(tiempoSeleccionado)
            },
            hora, minuto, true
        )
        timePickerDialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
