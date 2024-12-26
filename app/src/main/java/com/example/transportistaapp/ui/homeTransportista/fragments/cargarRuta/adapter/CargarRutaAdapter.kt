package com.example.transportistaapp.ui.homeTransportista.fragments.cargarRuta.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.transportistaapp.R
import com.example.transportistaapp.domain.model.Paquete

class CargarRutaAdapter (private var paquetes:List<Paquete> = emptyList()) :
    RecyclerView.Adapter<CargarRutaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CargarRutaViewHolder {
        return CargarRutaViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_box, parent, false)
        )
    }
    override fun onBindViewHolder(holder: CargarRutaViewHolder, position: Int) {
        val paquete = paquetes[position]
        holder.render(paquete)
    }

    override fun getItemCount(): Int = paquetes.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Paquete>) {
        paquetes = list
        notifyDataSetChanged()
    }
}