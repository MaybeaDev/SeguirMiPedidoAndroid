package com.example.transportistaapp.ui.homeTransportista

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import com.example.transportistaapp.databinding.ActivityRutasBinding
import com.example.transportistaapp.ui.homeTransportista.fragments.verRutas.VerRutasFragment
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager

@AndroidEntryPoint
class RutasActivity : AppCompatActivity() {
    lateinit var binding: ActivityRutasBinding
    private lateinit var permissionsManager: PermissionsManager
    private var permissionsListener: PermissionsListener = object : PermissionsListener{
        override fun onExplanationNeeded(permissionsToExplain: List<String>) {}
        override fun onPermissionResult(granted: Boolean) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRutasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
        } else {
            permissionsManager = PermissionsManager(permissionsListener)
            permissionsManager.requestLocationPermissions(this)
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<VerRutasFragment>(binding.fragmentContainer.id)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



}
