package com.example.rutasbus.ui.maps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.rutasbus.R
import com.example.rutasbus.databinding.FragmentMapsBinding
import com.google.firebase.firestore.FirebaseFirestore
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import kotlin.text.toDoubleOrNull

class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private lateinit var mapLibreMap: MapLibreMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapLibre.getInstance(
            requireContext(),
            "GA07xg3hP91Uj1tsjZhQ",
            WellKnownTileServer.MapTiler
        )

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)

        val mapStyleUrl = "https://api.maptiler.com/maps/streets-v2/style.json?key=GA07xg3hP91Uj1tsjZhQ"
        val db = FirebaseFirestore.getInstance()

        mapView.getMapAsync { map ->
            mapLibreMap = map
            mapLibreMap.setStyle(mapStyleUrl) {
                val posicionInicial = LatLng(-1.6675204, -78.6580192)
                mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionInicial, 12.5))

                // Icono para rutas (verde)
                val iconoRuta = IconFactory.getInstance(requireContext())
                    .fromBitmap(vectorToBitmap(requireContext(), R.drawable.ic_google_marker, 96))

                // Icono para restaurantes (rojo, usando setTint)
                val vectorDrawableRestaurante = ContextCompat.getDrawable(requireContext(), R.drawable.ic_google_marker)!!.mutate()
                vectorDrawableRestaurante.setTint(ContextCompat.getColor(requireContext(), R.color.marker_restaurante))
                val bitmapRestaurante = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888)
                val canvasRestaurante = Canvas(bitmapRestaurante)
                vectorDrawableRestaurante.setBounds(0, 0, 96, 96)
                vectorDrawableRestaurante.draw(canvasRestaurante)
                val iconoRestaurante = IconFactory.getInstance(requireContext()).fromBitmap(bitmapRestaurante)

                // RUTAS
                db.collection("rutas")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val nombre = document.getString("nombre") ?: "Sin nombre"
                            val descripcion = document.getString("descripcion") ?: "Sin descripción"
                            val latitud = when (val lat = document.get("latitud")) {
                                is Number -> lat.toDouble()
                                is String -> lat.toDoubleOrNull() ?: 0.0
                                else -> 0.0
                            }
                            val longitud = when (val lon = document.get("longitud")) {
                                is Number -> lon.toDouble()
                                is String -> lon.toDoubleOrNull() ?: 0.0
                                else -> 0.0
                            }
                            val ubicacion = LatLng(latitud, longitud)
                            mapLibreMap.addMarker(
                                MarkerOptions()
                                    .position(ubicacion)
                                    .title(nombre)
                                    .snippet(descripcion)
                                    .icon(iconoRuta)
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error al cargar rutas: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                // RESTAURANTES
                db.collection("restaurantes")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val nombre = document.getString("nombre") ?: "Sin nombre"
                            val descripcion = document.getString("descripcion") ?: "Sin descripción"
                            val latitud = when (val lat = document.get("latitud")) {
                                is Number -> lat.toDouble()
                                is String -> lat.toDoubleOrNull() ?: 0.0
                                else -> 0.0
                            }
                            val longitud = when (val lon = document.get("longitud")) {
                                is Number -> lon.toDouble()
                                is String -> lon.toDoubleOrNull() ?: 0.0
                                else -> 0.0
                            }
                            val ubicacion = LatLng(latitud, longitud)
                            mapLibreMap.addMarker(
                                MarkerOptions()
                                    .position(ubicacion)
                                    .title(nombre)
                                    .snippet(descripcion)
                                    .icon(iconoRestaurante)
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error al cargar restaurantes: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
        _binding = null
    }

    // Utilidad para convertir vector drawable a Bitmap
    private fun vectorToBitmap(context: Context, drawableId: Int, size: Int = 96): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)!!
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}