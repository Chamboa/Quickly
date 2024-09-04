import PTC.quickly.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ptc1.modelo.DTEvento
import kotlin.reflect.KFunction1

class EventosAdapter(
    private var eventos: MutableList<DTEvento>,
    private val onEdit: KFunction1<DTEvento, Unit>,
    private val onDelete: KFunction1<DTEvento, Unit>
) : RecyclerView.Adapter<EventosAdapter.EventoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_evento, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        holder.bind(evento, onEdit, onDelete)
    }

    override fun getItemCount(): Int = eventos.size

    fun updateList(newEventos: List<DTEvento>) {
        this.eventos = newEventos.toMutableList()
        notifyDataSetChanged()
    }

    class EventoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txNombreEvento: TextView = view.findViewById(R.id.txtNombreEvento)
        private val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        private val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)

        fun bind(evento: DTEvento, onEdit: KFunction1<DTEvento, Unit>, onDelete: KFunction1<DTEvento, Unit>) {
            txNombreEvento.text = evento.nombre.toString()
            btnEditar.setOnClickListener { onEdit(evento) }
            btnEliminar.setOnClickListener { onDelete(evento) }
        }
    }
}
 