package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Contrasena_Reestablecida : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contrasena_reestablecida)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnaceptarcontrasenarestablecida = findViewById<Button>(R.id.btnaceptarcontrasenarestablecida)

        btnaceptarcontrasenarestablecida.setOnClickListener {
            val pantallalogin = Intent (this, Login::class.java)
            startActivity(pantallalogin)
        }

    }
}