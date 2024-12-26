package com.example.transportistaapp.ui.pantallaReparto.fragments.listado.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.transportistaapp.R
import com.example.transportistaapp.domain.model.Paquete

class ListadoAdapter(
    private var paquetes: List<Paquete> = emptyList(),
    private val onClickHandler: (Paquete) -> Unit,
    private val onEntregarClickHandler: (Paquete) -> Unit
) :
    RecyclerView.Adapter<ListadoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListadoViewHolder {
        return ListadoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_paquete, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListadoViewHolder, position: Int) {
        val paquete = paquetes[position]
        holder.render(paquete, onClickHandler, onEntregarClickHandler)

    }

    // Cantidad de elementos en la lista
    override fun getItemCount(): Int = paquetes.size

    // c actualizan la lista de rutas
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Paquete>) {
        paquetes = list
        notifyDataSetChanged()
    }
}
