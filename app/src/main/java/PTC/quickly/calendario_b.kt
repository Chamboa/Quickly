package PTC.quickly

<<<<<<< HEAD
import EventosAdapter
=======
>>>>>>> master
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import android.widget.CalendarView
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.DTEvento
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
=======
import android.widget.ImageButton
>>>>>>> master

class calendario_b : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var rvEventos: RecyclerView
    private lateinit var eventosAdapter: EventosAdapter
    private val eventosList = mutableListOf<DTEvento>()
    private var selectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
<<<<<<< HEAD
        val view = inflater.inflate(R.layout.fragment_calendario_b, container, false)
        calendarView = view.findViewById(R.id.calendarView)
        rvEventos = view.findViewById(R.id.rvEventos)
        val btnAgregarEvento = view.findViewById<ImageButton>(R.id.btnAgregarEvento)

        eventosAdapter = EventosAdapter(eventosList, ::onEditEvento, ::onDeleteEvento)
        rvEventos.layoutManager = LinearLayoutManager(context)
        rvEventos.adapter = eventosAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val lista = obtenerDatos()
            withContext(Dispatchers.Main) {
                eventosList.addAll(lista)
                eventosAdapter.notifyDataSetChanged()
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
            loadEventsForDate(selectedDate!!)
        }

        btnAgregarEvento.setOnClickListener {
            val intent = Intent(activity, Eventos::class.java)
            intent.putExtra("selectedDate", selectedDate)
            startActivity(intent)
        }

        return view
=======
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendario_b, container, false)
>>>>>>> master
    }

    private fun loadEventsForDate(date: String) {
        val filteredEvents = eventosList.filter { it.fecha == date }
        eventosAdapter.updateList(filteredEvents)
    }

    private fun onEditEvento(evento: DTEvento) {
        // Implementar la edición del evento aquí
    }

    private fun onDeleteEvento(evento: DTEvento) {
        eventosList.remove(evento)
        eventosAdapter.updateList(eventosList)
    }

    private suspend fun obtenerDatos(): List<DTEvento> {
        return withContext(Dispatchers.IO) {
            val lista = mutableListOf<DTEvento>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val query = "SELECT * FROM Eventos"
                val statement = connection.prepareStatement(query)
                val resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val UUIDA = resultSet.getString("UUID_Evento")
                    val UUIDU = resultSet.getString("UUID_Usuario")
                    val nombre = resultSet.getString("nombre")
                    val hora = resultSet.getString("hora")
                    val descripcion = resultSet.getString("descripcion")
                    val lugar = resultSet.getString("lugar")
                    val fecha = resultSet.getString("fecha")

                    val evento = DTEvento(
                        UUID = UUIDA,
                        UUID_Usuario = UUIDU,
                        lugar = lugar,
                        descripcion = descripcion,
                        fecha = fecha,
                        hora = hora,
                        nombre = nombre
                    )
                    lista.add(evento)
                }
            }
<<<<<<< HEAD
            lista
        }
=======
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnEvnts = view.findViewById<ImageButton>(R.id.btnagregareventos)
        val idrol = Login.rol

        if (idrol != 2) {
            btnEvnts.visibility = View.GONE
        } else {
            btnEvnts.setOnClickListener {
                val intent = Intent(requireContext(), Eventos::class.java)
                startActivity(intent)
            }
        }

>>>>>>> master
    }
}
