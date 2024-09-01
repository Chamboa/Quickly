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

        val imgRegresar = findViewById<ImageView>(R.id.img_regresar)
        val txtnombre = findViewById<EditText>(R.id.txtNomEvento)
        val txtfecha = findViewById<EditText>(R.id.txtFechaHoras)
        val txtentrada = findViewById<EditText>(R.id.txtEntradaHoras)
        val txtsalida = findViewById<EditText>(R.id.txtHoraSalida)
        val txtcantidadhoras = findViewById<EditText>(R.id.txtCantidadhoras)
        val btnAgregarHora  = findViewById<Button>(R.id.btnAgregarHoras)
        val btnCancelarHora = findViewById<Button>(R.id.btnCancelarHoras)




        imgRegresar.setOnClickListener {
            finish()
        }

        btnAgregarHora.setOnClickListener {
            val nombre = txtnombre.text.toString()
            val fecha = txtfecha.text.toString()
            val entrada = txtentrada.text.toString()
            val salida = txtsalida.text.toString()
            val cantidad = txtcantidadhoras.text.toString().toInt()

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
            if (cantidad == null) {
                txtcantidadhoras.error = "La cantidad de horas debe ser un número"
                hayError = true
            } else {
                txtcantidadhoras.error = null
            }
            if (hayError){
                GlobalScope.launch (Dispatchers.IO){
                    val objConexion  = ClaseConexion().cadenaConexion()
                    var statement: PreparedStatement? = null
                    try {
                        val uuidExpediente = UUID.randomUUID().toString()
                        val uuidEvento = UUID.randomUUID().toString() // Aquí debes poner el UUID del evento asociado

                        // Preparar la consulta SQL
                        val sql = """
                            INSERT INTO Expediente (UUID_expediente, UUID_evento, nombre_evento, horas_agregadas)
                            VALUES (?, ?, ?, ?)
                        """.trimIndent()

                        statement = objConexion?.prepareStatement(sql)
                        statement?.setString(1, uuidExpediente)
                        statement?.setString(2, uuidEvento)
                        statement?.setString(3, nombre)
                        statement?.setInt(4, cantidad ?: 0)

                        statement?.executeUpdate()

                        runOnUiThread {
                            Toast.makeText(this@Agregar_Horas, "Horas agregadas correctamente", Toast.LENGTH_SHORT).show()
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

        }

        //////////////////////////////////////////////////////////////////

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
        txtsalida.setOnClickListener {
            showTimePickerDialog(txtsalida)
        }


/////////////////////////////////////////////////////////////////////////



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


}