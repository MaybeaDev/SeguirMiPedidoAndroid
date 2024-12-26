package com.example.transportistaapp.ui.homeTransportista.fragments.verRutas.adapter

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.transportistaapp.R
import com.example.transportistaapp.databinding.ItemRutaBinding
import com.example.transportistaapp.domain.model.Ruta

class VerRutasViewHolder(
    view: View,
    private val context: Context,
    private val onRouteClick: (Ruta) -> Unit
) :
    RecyclerView.ViewHolder(view) {

    private val binding = ItemRutaBinding.bind(view)
    private val parent = binding.parent

    fun render(ruta: Ruta) {
        //animacion a los colores (aun no esta testeado)
        // (la idea es achicar tambien los colores)
        val colorFrom = Color.LTGRAY
        val colorTo = Color.GREEN
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
        Log.d("clarkKent AnimacionC", colorAnimation.toString())
        colorAnimation.duration = 1000 // Duración de 1 segundo
        colorAnimation.repeatCount = ValueAnimator.INFINITE
        colorAnimation.repeatMode = ValueAnimator.REVERSE

        colorAnimation.addUpdateListener { animator ->
            binding.parent.setBackgroundColor(animator.animatedValue as Int)
        }
        binding.tvRutaNombre.text = ruta.nombre
        binding.tvRutaEstado.text = if (ruta.completado || ruta.cargado) "Cargada" else "Pendiente"
        binding.tvCantidadPaquetes.text =
            context.getString(R.string.x_paquetes, ruta.paquetes.count().toString())
        if (ruta.completado || ruta.cargado) {
            colorAnimation.start()

        } else {
            colorAnimation.cancel()
            parent.setOnClickListener {
                onRouteClick(ruta) // Llamar al callback con la ruta seleccionada
            }
        }
    }

}