package com.example.transportistaapp.ui.pantallaReparto.fragments

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
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.transportistaapp.databinding.FragmentBarcodeScannerBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class BarcodeScannerFragment : Fragment() {
    private lateinit var barcodeScanner: BarcodeScanner
    private var _binding: FragmentBarcodeScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var successCode: String
    private var errorCount: Int = 0
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
        _binding = FragmentBarcodeScannerBinding.inflate(layoutInflater, container, false)
        barcodeScanner = BarcodeScanning.getClient()
        successCode = arguments?.getString("successCode") ?: ""
        checkCameraPermission()
        return binding.root
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera(binding.previewView)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> {
                // Muestra un mensaje explicativo
                Toast.makeText(
                    requireContext(),
                    "Necesitamos acceso a la cámara para escanear códigos",
                    Toast.LENGTH_LONG
                ).show()
                requestCameraPermission()
            }

            else -> {
                // El permiso ha sido denegado permanentemente
                showPermissionDeniedDialog()
            }
        }
    }

    private fun requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun showPermissionDeniedDialog() {
        Toast.makeText(
            requireContext(),
            "Permiso de cámara denegado permanentemente. Por favor habilítalo en la configuración.",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageAnalysis = ImageAnalysis.Builder().build().also { analysis ->
                analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    processImageProxy(imageProxy)
                }
            }
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    if (rawValue != null) {
                        if (rawValue == successCode || errorCount >= 100) {
                            val result = Bundle()
                            result.putString("BARCODE_RESULT", rawValue)
                            imageProxy.close()
                            // Verifica si el fragmento está agregado antes de hacer popBackStack
                            if (isAdded) {
                                parentFragmentManager.setFragmentResult("BARCODE_SCAN", result)
                                parentFragmentManager.popBackStack()
                            }
                            break
                        } else {
                            errorCount++
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e("BarcodeScanner", "Error al procesar el código", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        barcodeScanner.close()
    }

}
