package PTC.quickly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Confirmar_Comite : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmar_comite)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }
        val btnCancel = findViewById<Button>(R.id.buttonCancel)
        val lblComite = findViewById<TextView>(R.id.lblComite)



        val comision = Unirse_Comite.Comision
        lblComite.text = comision

        btnCancel.setOnClickListener {
            val intent = Intent(this, Unirse_Comite::class.java)
            startActivity(intent)
        }
    }
}