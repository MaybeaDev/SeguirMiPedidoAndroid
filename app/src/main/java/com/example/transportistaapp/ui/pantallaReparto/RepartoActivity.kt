package com.example.transportistaapp.ui.pantallaReparto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.fragment.app.add
import com.example.transportistaapp.R
import com.example.transportistaapp.databinding.ActivityRepartoBinding
import com.example.transportistaapp.ui.pantallaReparto.fragments.listado.ListadoFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepartoActivity : AppCompatActivity() {

    lateinit var binding: ActivityRepartoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepartoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<ListadoFragment>(R.id.fragmentContainer)
            }
        }
    }
}