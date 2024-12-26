package com.example.transportistaapp.ui.homeTransportista.fragments.verRutas.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.transportistaapp.R
import com.example.transportistaapp.domain.model.Ruta

class VerRutasAdapter(private var rutas:List<Ruta> = emptyList(), private val onRouteClick: (Ruta) -> Unit) :
    RecyclerView.Adapter<VerRutasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerRutasViewHolder {
        return VerRutasViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ruta, parent, false), parent.context, onRouteClick
        )
    }

    override fun onBindViewHolder(holder: VerRutasViewHolder, position: Int) {
        val ruta = rutas[position]
        holder.render(ruta)

    }

    // Cantidad de elementos en la lista
    override fun getItemCount(): Int = rutas.size

    // c actualizan la lista de rutas
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Ruta>) {
        rutas = list
        notifyDataSetChanged()
    }
}
