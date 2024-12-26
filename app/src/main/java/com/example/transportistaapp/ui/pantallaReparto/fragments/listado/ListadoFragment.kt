package com.example.transportistaapp.ui.pantallaReparto.fragments.listado

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.transportistaapp.databinding.FragmentListadoBinding
import com.example.transportistaapp.domain.model.Paquete
import com.example.transportistaapp.ui.homeTransportista.RutasActivity
import com.example.transportistaapp.ui.pantallaReparto.RepartoActivity
import com.example.transportistaapp.ui.pantallaReparto.fragments.entregarPaquete.EntregarPaqueteFragment
import com.example.transportistaapp.ui.pantallaReparto.fragments.listado.adapter.ListadoAdapter
import com.example.transportistaapp.ui.pantallaReparto.fragments.mapa.MapaFragment
import com.google.gson.Gson
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListadoFragment : Fragment() {
    private var _binding: FragmentListadoBinding? = null
    private val binding get() = _binding!!
    private val listadoViewModel: ListadoViewModel by viewModels()
    private lateinit var listadoAdapter: ListadoAdapter
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    pedirPermiso()
                } else {
                    Toast.makeText(
                        context,
                        "Debes habilitar el acceso a la Ubicación",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }
        }

    private var permissionsListener: PermissionsListener = object : PermissionsListener {
        override fun onExplanationNeeded(permissionsToExplain: List<String>) {}
        override fun onPermissionResult(granted: Boolean) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pedirPermiso()
        if (!PermissionsManager.areLocationPermissionsGranted(requireContext())) {
            val permissionsManager = PermissionsManager(permissionsListener)
            activity?.let { permissionsManager.requestLocationPermissions(it) }
        }
        _binding = FragmentListadoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onResume() {
        pedirPermiso()
        super.onResume()
        listadoAdapter.updateList(emptyList())
        actualizarMovimientos()
    }

    private fun initUI() {
        initPaquetes()
        initUIState()
        initListeners()
    }

    private fun initListeners() {
        binding.btnVerMapa.setOnClickListener {
            val paquetes: List<Paquete> = listadoViewModel.paquetes.value ?: emptyList()
            val coordenadas = paquetes.filter { it.estado == "En reparto" }.map { it.coordenadas }
            val jsonCoordenadas = Gson().toJson(coordenadas)
            val mapaFragment = MapaFragment().apply {
                arguments = Bundle().apply {
                    putString("coordenadas", jsonCoordenadas)
                }
            }
            val fragmentContainerId =
                (requireActivity() as RepartoActivity).binding.fragmentContainer.id
            parentFragmentManager.beginTransaction()
                .replace(fragmentContainerId, mapaFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun initPaquetes() {
        listadoAdapter = ListadoAdapter(
            onClickHandler = { dato ->
                val gmmIntentUri =
                    Uri.parse("geo:${dato.coordenadas[1]},${dato.coordenadas[0]}?q=${dato.coordenadas[1]},${dato.coordenadas[0]}(Paquete:+${dato.id})")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")  // Asegura que se abra con Google Maps
                val packageManager =
                    requireContext().packageManager  // Usamos requireContext() para obtener el contexto del Fragment
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Google Maps no está disponible",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, onEntregarClickHandler = { dato ->
                val entregarPaqueteFragment = EntregarPaqueteFragment().apply {
                    arguments = Bundle().apply {
                        putString("codigoPaquete", dato.id) // Pasar el ID de la ruta como argumento
                    }
                }
                val fragmentContainerId =
                    (requireActivity() as RepartoActivity).binding.fragmentContainer.id
                parentFragmentManager.beginTransaction()
                    .replace(fragmentContainerId, entregarPaqueteFragment)
                    .addToBackStack(null) // Agregar a la pila para permitir volver atrás
                    .commit()
            })
        binding.recyclerViewCajas.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = listadoAdapter
        }
    }

    private fun initUIState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    listadoViewModel.state.collect {
                        when (it) {
                            ListadoState.Loading -> {}
                            is ListadoState.Success -> successState(it.paquetes)
                            is ListadoState.Error -> errorState(it.error)
                            ListadoState.PaquetesEntregados -> {
                                listadoViewModel.terminarEntrega()
                                Toast.makeText(context, "Ruta terminada!!", Toast.LENGTH_SHORT)
                                    .show()

                            }

                            ListadoState.RutasTerminadas -> {
                                val intent = Intent(requireContext(), RutasActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun successState(paquetes: List<Paquete>) {
        listadoAdapter.updateList(paquetes)
    }

    private fun errorState(error: String) {
        Log.d("MaybeaLog", error)
    }


    private fun actualizarMovimientos() {
        listadoViewModel.cargarRutas()
    }

    private fun pedirPermiso() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }
}
