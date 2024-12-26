package com.example.transportistaapp.ui.pantallaReparto.fragments.entregarPaquete

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.transportistaapp.R
import com.example.transportistaapp.databinding.FragmentEntregarPaqueteBinding
import com.example.transportistaapp.domain.model.Paquete
import com.example.transportistaapp.ui.entregasfallos.FormularioEntregaFallosFragment
import com.example.transportistaapp.ui.pantallaReparto.RepartoActivity
import com.example.transportistaapp.ui.pantallaReparto.fragments.BarcodeScannerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EntregarPaqueteFragment : Fragment() {
    private val viewModel: EntregarPaqueteViewModel by viewModels()
    private var _binding: FragmentEntregarPaqueteBinding? = null
    private val binding get() = _binding!!
    private lateinit var paquete: Paquete

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntregarPaqueteBinding.inflate(layoutInflater, container, false)
        viewModel.obtenerPaquete(arguments?.getString("codigoPaquete") ?: "")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initUIState()
    }

    private fun initUIState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect {
                        when (it) {
                            EntregarPaqueteState.Loading -> {}
                            is EntregarPaqueteState.Success -> successState(it.paquete)
                            is EntregarPaqueteState.Error -> errorState(it.error)
                            EntregarPaqueteState.BackCajas -> {
                                binding.tvError.text = ""
                                parentFragmentManager.popBackStack()
                            }
                            is EntregarPaqueteState.FormularioInvalido -> formInvalid(it.error)
                        }
                    }
                }
            }
        }
    }

    private fun formInvalid(error: String) {
        binding.tvError.text = error
    }

    private fun successState(p: Paquete) {
        paquete = p
        binding.tvPaqueteCodigo.text = p.id
        binding.tvRecibeValue.text = p.receptor
        binding.tvTelefonoValue.text = p.contacto
        binding.tvReferencia.text = if (p.referencia == "") {"No hay referencia"} else {p.referencia}
    }

    private fun errorState(error: String) {
        Log.d("MaybeaLog", error)
    }

    private fun initListeners() {
        binding.btnValidar.setOnClickListener {
            val barcodeFragment = BarcodeScannerFragment().apply {
                arguments = Bundle().apply {
                    putString("successCode", paquete.id)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, barcodeFragment)
                .addToBackStack(null)
                .commit()
        }
        parentFragmentManager.setFragmentResultListener("BARCODE_SCAN", this) { _, bundle ->
            val barcodeResult = bundle.getString("BARCODE_RESULT")
            if (barcodeResult != null) {
                if (barcodeResult == paquete.id) {
                    binding.seccionValidada.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        "Código Validado: $barcodeResult",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.seccionValidada.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Codigo incorrecto: $barcodeResult",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.checkboxReceptor.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.editTextNombre.setText(paquete.receptor)
                binding.editTextTelefono.setText("")
                binding.editTextNombre.visibility = View.GONE
                binding.textViewNombre.visibility = View.GONE
                binding.editTextTelefono.visibility = View.GONE
                binding.textViewTelefono.visibility = View.GONE
            } else {
                binding.editTextNombre.setText("")
                binding.editTextNombre.visibility = View.VISIBLE
                binding.textViewNombre.visibility = View.VISIBLE
                binding.editTextTelefono.visibility = View.VISIBLE
                binding.textViewTelefono.visibility = View.VISIBLE
            }
        }
        binding.btnConfirmarEntrega.setOnClickListener {
            viewModel.registrarEntrega(
                binding.editTextNombre.text.toString(),
                binding.editTextRut.text.toString(),
                binding.editTextTelefono.text.toString(),
                paquete.id,
                binding.checkboxReceptor.isChecked
            )
        }
        binding.btnEntregaFallida.setOnClickListener{
            val destino = FormularioEntregaFallosFragment().apply {
                arguments = Bundle().apply {
                    putString("codigo", paquete.id)
                    putString("nombre", paquete.receptor)
                    putString("telefono", paquete.contacto)
                }
            }
            val fragmentContainerId =
                (requireActivity() as RepartoActivity).binding.fragmentContainer.id
            parentFragmentManager.beginTransaction()
                .replace(fragmentContainerId, destino)
                .addToBackStack(null) // Agregar a la pila para permitir volver atrás
                .commit()
        }
    }
}
