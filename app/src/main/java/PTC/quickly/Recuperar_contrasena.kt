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
            val correo = txtcorreocontraolvidada.text.toString()
            var hayerrores = false

            if(!correo.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))){
                txtcorreocontraolvidada.error = "El correo ha sido escrito incorrectamente"
                hayerrores = true
            }
            else{
                txtcorreocontraolvidada.error = null
            }
        }

        imgregresar.setOnClickListener {
            val pantallalogin = Intent (this, Login::class.java)
            startActivity(pantallalogin)
        }
    }
}