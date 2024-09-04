package PTC.quickly

import android.app.DatePickerDialog
<<<<<<< HEAD
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
=======
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
>>>>>>> master
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
<<<<<<< HEAD
import java.sql.PreparedStatement
import java.util.Calendar
import java.util.UUID
import android.widget.Toast
import com.example.ptc1.RecyclerView.AdMostrarEvento
import com.example.ptc1.RecyclerViewListAlumnos.AdaptadorAsistencia
import oracle.net.aso.e
=======
import java.util.Calendar
>>>>>>> master

class Agregar_Horas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_horas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

<<<<<<< HEAD
        val imgRegresar = findViewById<ImageView>(R.id.img_regresar)
=======
>>>>>>> master
        val txtnombre = findViewById<EditText>(R.id.txtNomEvento)
        val txtfecha = findViewById<EditText>(R.id.txtFechaHoras)
        val txtentrada = findViewById<EditText>(R.id.txtEntradaHoras)
        val txtsalida = findViewById<EditText>(R.id.txtHoraSalida)
        val txtcantidadhoras = findViewById<EditText>(R.id.txtCantidadhoras)
        val btnAgregarHora  = findViewById<Button>(R.id.btnAgregarHoras)
<<<<<<< HEAD


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

            val cantidad = txtcantidadhoras.text.toString().toInt()




            val UUIDevento = AdMostrarEvento.selectedUUID
            GlobalScope.launch (Dispatchers.IO){
                val objConexion  = ClaseConexion().cadenaConexion()
                var statement: PreparedStatement? = null
                var nombreEvento: String? = null

                try {
                    // Consultar el nombre del evento usando el UUID_evento
                    val sqlConsulta = "SELECT nombre FROM Eventos WHERE UUID_Evento = ?"
                    statement = objConexion?.prepareStatement(sqlConsulta)
                    statement?.setString(1, UUIDevento.toString())
                    val resultSet = statement?.executeQuery()

                    if (resultSet?.next() == true) {
                        nombreEvento = resultSet.getString("nombre")
                    }

                    resultSet?.close()
                    statement?.close()

                    if (nombreEvento != null) {
                        // Insertar los datos en la tabla Expediente
                        val uuidExpediente = UUID.randomUUID().toString()
                        val sqlInsercion = """
                    INSERT INTO Expediente (UUID_expediente, UUID_usuario, nombre_evento, horas_agregadas)
                    VALUES (?, ?, ?, ?)
                """.trimIndent()

                        statement = objConexion?.prepareStatement(sqlInsercion)
                        statement?.setString(1, uuidExpediente)
                        statement?.setString(2, AdaptadorAsistencia.selectedUUIDA)
                        statement?.setString(3, nombreEvento)
                        statement?.setInt(4, cantidad)

                        statement?.executeUpdate()

                        runOnUiThread {
                            Toast.makeText(this@Agregar_Horas, "Horas agregadas correctamente", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@Agregar_Horas, "Error: no se encontró el evento", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@Agregar_Horas, "Error al agregar horas", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    statement?.close()
                    objConexion?.close()
                }
            }
        }

=======
        val btnCancelarHora = findViewById<Button>(R.id.btnCancelarHoras)


        btnAgregarHora.setOnClickListener {
            val nombre = txtnombre.text.toString()
            val fecha = txtfecha.text.toString()
            val entrada = txtentrada.text.toString()
            val salida = txtsalida.text.toString()
            val cantidad = txtcantidadhoras.text.toString()

            var hayError = false

            //valido
            if (nombre.isEmpty()) {
                txtnombre.error = "El nombre es obligatorio"
                hayError = true
            } else {
                txtnombre.error = null
            }
            if (fecha.isEmpty()) {
                txtfecha.error = "Agregar una fecha es obligatorio"
                hayError = true
            } else {
                txtfecha.error = null
            }
            if (entrada.isEmpty()) {
                txtentrada.error = "Agregue hora de entrada es obligatorio"
                hayError = true
            } else {
                txtentrada.error = null
            }
            if (salida.isEmpty()) {
                txtsalida.error = "Agregue hora de entrada es obligatoria"
                hayError = true
            } else {
                txtsalida.error = null
            }
            if (cantidad.isEmpty()) {
                txtcantidadhoras.error = "Agregue la cantidad de horas es obligatorio"
                hayError = true
            } else {
                txtcantidadhoras.error = null
            }

        }

        //////////////////////////////////////////////////////////////////

>>>>>>> master
        txtfecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val fechaMaxima = Calendar.getInstance()
            fechaMaxima.set(anio, mes, dia + 10)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fechaSeleccionada = "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                    txtfecha.setText(fechaSeleccionada)
                },
                anio, mes, dia
            )

            datePickerDialog.datePicker.maxDate = fechaMaxima.timeInMillis

            datePickerDialog.show()
        }

        txtentrada.setOnClickListener {
            showTimePickerDialog(txtentrada)
        }
<<<<<<< HEAD
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
=======


/////////////////////////////////////////////////////////////////////////
        btnAgregarHora.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                //Se agrega los datos
                val objConexion = ClaseConexion().cadenaConexion()
            }
        }


    }

    private fun showTimePickerDialog(txtentrada: EditText?) {
>>>>>>> master

    }


}