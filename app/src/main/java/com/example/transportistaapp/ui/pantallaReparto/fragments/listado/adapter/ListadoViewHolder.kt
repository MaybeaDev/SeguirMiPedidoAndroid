package com.example.transportistaapp.ui.pantallaReparto.fragments.listado.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.transportistaapp.databinding.ItemPaqueteBinding
import com.example.transportistaapp.domain.model.Paquete

class ListadoViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {

    private val binding = ItemPaqueteBinding.bind(view)

    fun render(
        paquete: Paquete,
        onClickHandler: (Paquete) -> Unit,
        onEntregarClickHandler: (Paquete) -> Unit
    ) {
        binding.tvEstado.text = paquete.estado
        binding.tvCodigoPaquete.text = paquete.id
        binding.tvDireccion.text = paquete.direccion
        binding.btnMapa.setOnClickListener {
            onClickHandler(paquete)
        }
        binding.leftHalf.setOnClickListener {
            onEntregarClickHandler(paquete)
        }
    }

}