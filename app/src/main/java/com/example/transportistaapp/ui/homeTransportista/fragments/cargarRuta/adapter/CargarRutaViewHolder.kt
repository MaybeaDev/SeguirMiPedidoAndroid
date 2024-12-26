package com.example.transportistaapp.ui.homeTransportista.fragments.cargarRuta.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.transportistaapp.databinding.ItemBoxBinding
import com.example.transportistaapp.domain.model.Paquete

class CargarRutaViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {

    private val binding = ItemBoxBinding.bind(view)

    fun render(paquete: Paquete){
        binding.tvPaqueteID.text = paquete.id
    }

}