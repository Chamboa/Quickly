package PTC.quickly

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.RecyclerView.EventosAdapter
import com.example.ptc1.modelo.DTEvento
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.text.SimpleDateFormat
import java.util.*

class calendario_b : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var rvEventos: RecyclerView
    private lateinit var eventosAdapter: EventosAdapter
    private val eventosList = mutableListOf<DTEvento>()
    private var selectedDate: String? = null
    val id_comite = Login.id_comite
    var id_rol = Login.userRoleId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            loadEventsForDate(selectedDate!!)
        }
        if (id_rol == 2 || id_rol == 3) {
            btnAgregarEvento.visibility = View.VISIBLE
            btnAgregarEvento.setOnClickListener {
                val intent = Intent(activity, Eventos::class.java)
                intent.putExtra("selectedDate", selectedDate)
                startActivity(intent)
            }
        } else {
            btnAgregarEvento.visibility = View.GONE
        }


        return view
    }

    private fun loadEventsForDate(date: String) {
        val filteredEvents = eventosList.filter { it.fecha == date }
        eventosAdapter.updateList(filteredEvents)
    }

    private fun onEditEvento(evento: DTEvento) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_dialog_editar_evento, null)

        val etFechaEvento = dialogView.findViewById<EditText>(R.id.etFechaEvento)
        val etHoraEvento = dialogView.findViewById<EditText>(R.id.etHoraEvento)

        etFechaEvento.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    etFechaEvento.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        etHoraEvento.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                    etHoraEvento.setText(formattedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

        builder.setView(dialogView)
            .setTitle("Editar Evento")
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = dialogView.findViewById<EditText>(R.id.etNombreEvento).text.toString()
                val nuevaDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcionEvento).text.toString()
                val nuevaFecha = etFechaEvento.text.toString()
                val nuevaHora = etHoraEvento.text.toString()
                val nuevoLugar = dialogView.findViewById<EditText>(R.id.etLugarEvento).text.toString()

                evento.nombre = nuevoNombre
                evento.descripcion = nuevaDescripcion
                evento.fecha = nuevaFecha
                evento.hora = nuevaHora
                evento.lugar = nuevoLugar

                CoroutineScope(Dispatchers.IO).launch {
                    actualizarEventoEnBaseDeDatos(evento)
                    withContext(Dispatchers.Main) {
                        eventosAdapter.notifyDataSetChanged()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)

        dialogView.findViewById<EditText>(R.id.etNombreEvento).setText(evento.nombre)
        dialogView.findViewById<EditText>(R.id.etDescripcionEvento).setText(evento.descripcion)
        etFechaEvento.setText(evento.fecha)
        etHoraEvento.setText(evento.hora)
        dialogView.findViewById<EditText>(R.id.etLugarEvento).setText(evento.lugar)

        val dialog = builder.create()
        dialog.show()
    }

    private suspend fun actualizarEventoEnBaseDeDatos(evento: DTEvento) {
        withContext(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val query = """
                    UPDATE Eventos SET 
                    nombre = ?, descripcion = ?, fecha = TO_DATE(?, 'YYYY-MM-DD'), hora = ?, lugar = ?
                    WHERE UUID_Evento = ?
                """
                val statement = connection.prepareStatement(query)
                statement.setString(1, evento.nombre)
                statement.setString(2, evento.descripcion)
                statement.setString(3, evento.fecha)
                statement.setString(4, evento.hora)
                statement.setString(5, evento.lugar)
                statement.setString(6, evento.UUID)
                statement.executeUpdate()
            }
        }
    }

    private fun onDeleteEvento(evento: DTEvento) {
        CoroutineScope(Dispatchers.IO).launch {
            eliminarEventoDeBaseDeDatos(evento.UUID)
            withContext(Dispatchers.Main) {
                eventosList.remove(evento)
                eventosAdapter.updateList(eventosList)
            }
        }
    }

    private suspend fun eliminarEventoDeBaseDeDatos(uuidEvento: String) {
        withContext(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val query = "DELETE FROM Eventos WHERE UUID_Evento = ?"
                val statement = connection.prepareStatement(query)
                statement.setString(1, uuidEvento)
                statement.executeUpdate()
            }
        }
    }

    private suspend fun obtenerDatos(): List<DTEvento> {
        return withContext(Dispatchers.IO) {
            val lista = mutableListOf<DTEvento>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                if (id_rol == 3) {
                    // Mostrar todos los eventos si el rol es 3
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
                        val fecha = resultSet.getDate("fecha")
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val fechaStr = sdf.format(fecha)

                        val evento = DTEvento(
                            UUID = UUIDA,
                            UUID_Usuario = UUIDU,
                            lugar = lugar,
                            descripcion = descripcion,
                            fecha = fechaStr,
                            hora = hora,
                            nombre = nombre
                        )
                        lista.add(evento)
                    }
                } else {
                    // Verifica que id_comite no sea null antes de ejecutar la consulta
                    if (id_comite != null) {
                        // Mostrar solo los eventos del mismo comité si el rol es 1 o 2
                        val query = """
                        SELECT e.UUID_Evento, e.UUID_Usuario, e.lugar, e.descripcion, e.nombre, e.fecha, e.hora
                        FROM Eventos e
                        JOIN Usuario u ON e.UUID_Usuario = u.UUID_Usuario
                        WHERE u.id_comite = ?
                    """
                        val statement = connection.prepareStatement(query)
                        statement.setInt(1, id_comite!!)  // Se asegura de que id_comite no es null
                        val resultSet = statement.executeQuery()

                        while (resultSet.next()) {
                            val UUIDA = resultSet.getString("UUID_Evento")
                            val UUIDU = resultSet.getString("UUID_Usuario")
                            val nombre = resultSet.getString("nombre")
                            val hora = resultSet.getString("hora")
                            val descripcion = resultSet.getString("descripcion")
                            val lugar = resultSet.getString("lugar")
                            val fecha = resultSet.getDate("fecha")
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val fechaStr = sdf.format(fecha)

                            val evento = DTEvento(
                                UUID = UUIDA,
                                UUID_Usuario = UUIDU,
                                lugar = lugar,
                                descripcion = descripcion,
                                fecha = fechaStr,
                                hora = hora,
                                nombre = nombre
                            )
                            lista.add(evento)
                        }
                    } else {
                        // Maneja el caso en que id_comite sea null
                        throw IllegalStateException("id_comite no puede ser null para este rol")
                    }
                }
            }
            lista
        }
    }
}
