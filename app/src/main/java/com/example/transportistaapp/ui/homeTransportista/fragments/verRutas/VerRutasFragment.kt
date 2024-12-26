package com.example.transportistaapp.ui.homeTransportista.fragments.verRutas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.transportistaapp.databinding.FragmentVerRutasBinding
import com.example.transportistaapp.domain.model.Ruta
import com.example.transportistaapp.ui.homeTransportista.RutasActivity
import com.example.transportistaapp.ui.homeTransportista.fragments.cargarRuta.CargarRutaFragment
import com.example.transportistaapp.ui.homeTransportista.fragments.verRutas.adapter.VerRutasAdapter
import com.example.transportistaapp.ui.pantallaReparto.RepartoActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VerRutasFragment : Fragment() {
    private var _binding: FragmentVerRutasBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VerRutasViewModel by viewModels()
    private lateinit var verRutasAdapter : VerRutasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerRutasBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }
    override fun onResume() {
        super.onResume()
        verRutasAdapter.updateList(emptyList())
        obtenerRutas()
    }

    private fun obtenerRutas() {
        viewModel.obtenerRutasActivas()
    }

    private fun initUI() {
        initAdapter()
        initState()
        initListeners()
    }

    private fun initListeners() {
        binding.btnIniciarEntregas.setOnClickListener {
            viewModel.comenzarEntregas()
        }
        binding.btnActualizar.setOnClickListener{
            viewModel.actualizar()
        }
    }

    private fun initAdapter() {
        verRutasAdapter = VerRutasAdapter { ruta ->
            val detalleFragment = CargarRutaFragment().apply {
                arguments = Bundle().apply {
                    putString("rutaId", ruta.id) // Pasar el ID de la ruta como argumento
                }
            }
            val fragmentContainerId = (requireActivity() as RutasActivity).binding.fragmentContainer.id
            parentFragmentManager.beginTransaction()
                .replace(fragmentContainerId, detalleFragment)
                .addToBackStack(null) // Agregar a la pila para permitir volver atrás
                .commit()
        }
        binding.rvPaquetes.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = verRutasAdapter
        }
    }
    private fun initState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        VerRutasState.Loading -> {}
                        is VerRutasState.Error -> errorState(state.error)
                        is VerRutasState.Success -> successState(state.rutas)
                        VerRutasState.IrARepartir -> {
                            val intent = Intent(requireContext(), RepartoActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
    private fun successState(rutas:List<Ruta>) {
        if (rutas.any { it.enReparto }) {
            val intent = Intent(requireContext(), RepartoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        // Actualizar la lista de rutas en el RecyclerView
        verRutasAdapter.updateList(rutas)

        // Habilitar o deshabilitar el botón "Iniciar entregas" según las rutas
        binding.btnIniciarEntregas.isEnabled = rutas.any { it.completado || it.cargado }
    }

    private fun errorState(error:String) {
        Log.d("Maybealog RutasActivity errorState()", error)
    }
}