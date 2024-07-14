package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ptc1.PTC.quickly.enviarCorreo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Recuperar_contrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtcorreocontraolvidada = findViewById<EditText>(R.id.txtcorreocontraolvidada)
        val btnsolicitarcorreo = findViewById<Button>(R.id.btnsolicitarcorreo)
        val imgregresar = findViewById<ImageView>(R.id.imgregresar)

        btnsolicitarcorreo.setOnClickListener {
            val activity_correoconfirmacion = Intent(this, Correoconfirmacion::class.java)

            val correo = txtcorreocontraolvidada.text.toString()
            var hayerrores = false

            if(!correo.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))){
                txtcorreocontraolvidada.error = "El correo ha sido escrito incorrectamente"
                hayerrores = true
            }
            else{
                txtcorreocontraolvidada.error = null
            }

            CoroutineScope(Dispatchers.Main).launch {

                val Codigorecuperacion = (100000..999999).random()

                enviarCorreo(
                    "quicklyptc2024@gmail.com",
                    "Recuperación de constraseña",
                    "¡Hola! aquí está tu código de recuperación $Codigorecuperacion"
                )
        }

        imgregresar.setOnClickListener {
            val pantallalogin = Intent (this, Login::class.java)
            startActivity(pantallalogin)
        }
    }
    }
}