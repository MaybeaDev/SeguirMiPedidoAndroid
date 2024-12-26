package com.example.transportistaapp.ui.entregasfallos

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
import com.example.transportistaapp.databinding.FragmentFormularioEntregaFallosBinding
import com.example.transportistaapp.ui.pantallaReparto.RepartoActivity
import com.example.transportistaapp.ui.pantallaReparto.fragments.listado.ListadoFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FormularioEntregaFallosFragment : Fragment() {

    private var _binding: FragmentFormularioEntregaFallosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FormularioEntregaFallosViewModel by viewModels()
    private lateinit var codigo: String
    private lateinit var nombre: String
    private lateinit var telefono: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormularioEntregaFallosBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            codigo = it.getString("codigo") ?: ""
            nombre = it.getString("nombre") ?: ""
            telefono = it.getString("telefono") ?: ""
        }
        binding.tvRecibeValue.text = nombre
        binding.tvPaqueteCodigo.text = codigo
        binding.tvTelefonoValue.text = telefono

        binding.btnRegistrarFallo.setOnClickListener {
            viewModel.registrarFallo(codigo, binding.etMotivoFallo.text.toString())
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect {
                        when (it) {
                            is FormularioEntregaFallosState.Error -> {
                                Log.d("MaybeaLog", it.error)
                            }

                            FormularioEntregaFallosState.Loading -> {}
                            FormularioEntregaFallosState.Success -> {
                                val destino = ListadoFragment()
                                val fragmentContainerId =
                                    (requireActivity() as RepartoActivity).binding.fragmentContainer.id
                                parentFragmentManager.beginTransaction()
                                    .replace(fragmentContainerId, destino)
                                    .commit()
                            }
                        }
                    }
                }
            }
        }
    }
}
