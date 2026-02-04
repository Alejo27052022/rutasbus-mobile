package com.example.rutasbus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rutasbus.R
import com.example.rutasbus.data.RutaBus

class BusAdapter(
    private val items: List<RutaBus>,
    private val onItemClick: (RutaBus) -> Unit
) : RecyclerView.Adapter<BusAdapter.LineaViewHolder>() {

    inner class LineaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.txtTitulo)
        val descripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        val imagen: ImageView = itemView.findViewById(R.id.imgTurismo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_turismo, parent, false)
        return LineaViewHolder(view)
    }

    override fun onBindViewHolder(holder: LineaViewHolder, position: Int) {
        val item = items[position]

        holder.titulo.text = item.nombre
        holder.descripcion.text = "Paradas: ${item.paradas.size}"

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
