package PTC.quickly

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import java.util.Calendar

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

        val txtnombre = findViewById<EditText>(R.id.txtNomEvento)
        val txtfecha = findViewById<EditText>(R.id.txtFechaHoras)
        val txtentrada = findViewById<EditText>(R.id.txtEntradaHoras)
        val txtsalida = findViewById<EditText>(R.id.txtHoraSalida)
        val txtcantidadhoras = findViewById<EditText>(R.id.txtCantidadhoras)
        val btnAgregarHora  = findViewById<Button>(R.id.btnAgregarHoras)
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


/////////////////////////////////////////////////////////////////////////
        btnAgregarHora.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                //Se agrega los datos
                val objConexion = ClaseConexion().cadenaConexion()
            }
        }


    }

    private fun showTimePickerDialog(txtentrada: EditText?) {

    }


}