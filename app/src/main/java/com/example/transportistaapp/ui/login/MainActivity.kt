package com.example.transportistaapp.ui.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.transportistaapp.R
import com.example.transportistaapp.databinding.ActivityMainBinding
import com.example.transportistaapp.ui.homeTransportista.RutasActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val multiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permission = it.key
                val isGranted = it.value
                if (!isGranted) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        when (permission) {
                            Manifest.permission.CAMERA -> {
                                Toast.makeText(
                                    this,
                                    "Necesitamos acceso a la cámara para escanear los codigos de los paquetes",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            Manifest.permission.ACCESS_COARSE_LOCATION -> {
                                Toast.makeText(
                                    this,
                                    "Necesitamos acceso a la ubicacion para visualizar el mapa",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        when (permission) {
                            Manifest.permission.CAMERA -> {
                                Toast.makeText(
                                    this,
                                    "Debes habilitar el acceso a la Cámara",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            Manifest.permission.ACCESS_COARSE_LOCATION -> {
                                Toast.makeText(
                                    this,
                                    "Debes habilitar el acceso a la ubicación",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            loginViewModel.login(email, password)
        }
        initUI()
    }

    private fun initUI() {
        initState()
    }

    private fun initState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginState.collect { state ->
                    when (state) {
                        LoginState.Idle -> {
                            binding.tvStatus.text = ""
                            binding.tvStatus.visibility = TextView.GONE
                        }

                        LoginState.Loading -> {
                            binding.tvStatus.text = getString(R.string.CARGANDO)
                            binding.tvStatus.visibility = TextView.VISIBLE
                        }

                        is LoginState.Success -> {
                            navigateToDashboard()
                        }

                        is LoginState.Failure -> {
                            binding.tvStatus.text = state.error
                            binding.tvStatus.visibility = TextView.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, RutasActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (permissionsToRequest.isNotEmpty()) {
            multiplePermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}

