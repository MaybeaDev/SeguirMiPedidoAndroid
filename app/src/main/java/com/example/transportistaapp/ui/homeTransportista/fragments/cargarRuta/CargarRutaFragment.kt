package com.example.transportistaapp.ui.homeTransportista.fragments.cargarRuta

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.transportistaapp.databinding.FragmentCargarRutaBinding
import com.example.transportistaapp.domain.model.Paquete
import com.example.transportistaapp.ui.homeTransportista.RutasActivity
import com.example.transportistaapp.ui.homeTransportista.fragments.cargarRuta.adapter.CargarRutaAdapter
import com.example.transportistaapp.ui.homeTransportista.fragments.verRutas.VerRutasFragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CargarRutaFragment : Fragment() {
    private var _binding: FragmentCargarRutaBinding? = null
    private val binding get() = _binding!!
    private val barcodeScannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_DATA_MATRIX,
            Barcode.FORMAT_AZTEC
        )
        .build()
    private val scanner: BarcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions)
    private var cameraProvider: ProcessCameraProvider? = null
    private val viewModel: CargarRutaViewModel by viewModels()
    private lateinit var cargarRutaAdapter: CargarRutaAdapter
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    context,
                    "Debes habilitar el acceso a la Cámara",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCargarRutaBinding.inflate(layoutInflater, container, false)
        pedirPermisos()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initAdapter()
        initState()
        initListeners()
    }

    private fun initListeners() {
        binding.btnEscanearCaja.setOnClickListener {
            binding.previewView.visibility = View.VISIBLE
            startCamera()
        }
    }


    override fun onResume() {
        super.onResume()
        cargarRutaAdapter.updateList(emptyList())
        obtenerRutas()
    }

    private fun obtenerRutas() {
        val ruta = arguments?.getString("rutaId") ?: ""
        viewModel.obtenerPaquetes(ruta)
    }

    private fun initState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        CargarRutaState.Loading -> {}
                        is CargarRutaState.Error -> errorState(state.error)
                        is CargarRutaState.Success -> successState(state.rutas)
                    }
                }
            }
        }
    }

    private fun successState(paquetes: List<Paquete>) {
        cargarRutaAdapter.updateList(paquetes)
    }

    private fun errorState(error: String) {
        Log.d("MaybeaLog CargarRutaFragment 63", "Error: $error")
    }

    private fun initAdapter() {
        Log.d("MaybeaLog CargarRutaFragment 86", "Init adapter 1")
        cargarRutaAdapter = CargarRutaAdapter()
        binding.recyclerViewCajas.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = cargarRutaAdapter
        }
        Log.d("MaybeaLog CargarRutaFragment 86", "Init adapter 2")
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.previewView.surfaceProvider
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                        processImageProxy(scanner, imageProxy)
                    }
                }

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Error al inicializar la cámara", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(scanner: BarcodeScanner, imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        if (rawValue != null) {
                            val validacion = viewModel.validarCodigo(rawValue)
                            if (validacion[0]) {
                                beep()
                                Toast.makeText(
                                    requireContext(),
                                    "Código: $rawValue encontrado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (validacion[1]) {
                                viewModel.validarRuta(arguments?.getString("rutaId") ?: "")
                                imageProxy.close()
                                stopCamera()
                                val verRutasFragment = VerRutasFragment()
                                val fragmentContainerId =
                                    (requireActivity() as RutasActivity).binding.fragmentContainer.id
                                parentFragmentManager.beginTransaction()
                                    .replace(fragmentContainerId, verRutasFragment)
                                    .addToBackStack(null) // Agregar a la pila para permitir volver atrás
                                    .commit()
                            }
                            imageProxy.close()
                            return@addOnSuccessListener
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("BarcodeScanner", "Error al procesar la imagen", it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }


    private fun stopCamera() {
        cameraProvider?.unbindAll() // Si estás usando CameraX
    }

    private fun pedirPermisos() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun beep() {
        Log.d("MaybeaLog beep cargarRutaFragment", "Se ejecuta el beep")
        val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100) // 100 es el volumen
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200) // 500ms de duración
    }
}