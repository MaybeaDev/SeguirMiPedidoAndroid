package com.example.transportistaapp.ui.pantallaReparto.fragments.mapa

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.transportistaapp.R
import com.example.transportistaapp.databinding.FragmentMapaBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.OverviewViewportStateOptions
import com.mapbox.maps.plugin.viewport.viewport
import kotlinx.coroutines.launch


class MapaFragment : Fragment() {
    private var _binding: FragmentMapaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MapaViewModel by viewModels()
    private var mapView: MapView? = null
    private var locationAccess: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtener el JSON desde los argumentos
        arguments?.getString("coordenadas")?.let { json ->
            val coordenadasType = object : TypeToken<List<List<Double>>>() {}.type
            val coordenadas: List<List<Double>> = Gson().fromJson(json, coordenadasType)
            viewModel.setCoordenadas(coordenadas)  // Pasamos las coordenadas al ViewModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapaBinding.inflate(layoutInflater, container, false)
        mapView = binding.mapView
        if (locationAccess) enableLocationComponent()
        mapView?.mapboxMap?.let { mapboxMap ->
            mapboxMap.loadStyle(
                Style.MAPBOX_STREETS
            ) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.state.collect { state ->
                        when (state) {
                            is MapaState.Success -> {
                                addAnnotationsToMap(state.coordenadas)
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
        return binding.root
    }

    private fun enableLocationComponent() {
        with(mapView!!) {
            location.locationPuck = createDefault2DPuck(withBearing = true)
            location.enabled = true
            location.puckBearing = PuckBearing.COURSE
            location.puckBearingEnabled = true
            viewport.transitionTo(
                targetState = viewport.makeOverviewViewportState(
                    OverviewViewportStateOptions.Builder().build()
                ),
                transition = viewport.makeImmediateViewportTransition()
            )
        }
    }

    private fun addAnnotationsToMap(coordenadas: List<List<Double>>) {
        coordenadas.forEach { coord ->
            val point = Point.fromLngLat(coord[0], coord[1])
            bitmapFromDrawableRes(requireContext(), R.drawable.pin_24)?.let {
                val annotationApi = mapView?.annotations
                val pointAnnotationManager = annotationApi?.createPointAnnotationManager()
                val pointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(point)
                    .withIconAnchor(IconAnchor.BOTTOM)
                    .withIconImage(it)
                pointAnnotationManager?.create(pointAnnotationOptions)
            }
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}
