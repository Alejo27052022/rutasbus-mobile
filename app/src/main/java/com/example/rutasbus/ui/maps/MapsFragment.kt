package com.example.rutasbus.ui.maps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.rutasbus.R
import com.example.rutasbus.databinding.FragmentMapsBinding
import com.google.firebase.firestore.FirebaseFirestore
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import kotlin.math.absoluteValue

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private lateinit var mapLibreMap: MapLibreMap

    // ✅ Se quedan dentro del Fragment
    private val lineasByNombre = mutableMapOf<String, LineaUi>()
    private val nombresLineas = mutableListOf<String>()

    data class LineaUi(
        val id: String,
        val nombre: String,
        val paradasOrdenadas: List<Map<*, *>>
    )

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

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)

        val mapStyleUrl =
            "https://api.maptiler.com/maps/streets-v2/style.json?key=GA07xg3hP91Uj1tsjZhQ"

        val db = FirebaseFirestore.getInstance()

        mapView.getMapAsync { map ->
            mapLibreMap = map
            mapLibreMap.setStyle(mapStyleUrl) {

                val posicionInicial = LatLng(-1.6675204, -78.6580192)
                mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionInicial, 12.5))

                // Icono genérico para paradas
                val iconoParada = IconFactory.getInstance(requireContext())
                    .fromBitmap(vectorToBitmap(requireContext(), R.drawable.ic_google_marker, 96))

                // Icono para restaurantes (rojo)
                val vectorDrawableRestaurante =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_google_marker)!!.mutate()
                vectorDrawableRestaurante.setTint(
                    ContextCompat.getColor(requireContext(), R.color.marker_restaurante)
                )
                val bitmapRestaurante = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888)
                val canvasRestaurante = Canvas(bitmapRestaurante)
                vectorDrawableRestaurante.setBounds(0, 0, 96, 96)
                vectorDrawableRestaurante.draw(canvasRestaurante)
                val iconoRestaurante =
                    IconFactory.getInstance(requireContext()).fromBitmap(bitmapRestaurante)

                // Paleta de colores (de colors.xml)
                val routeColors = listOf(
                    ContextCompat.getColor(requireContext(), R.color.route_blue),
                    ContextCompat.getColor(requireContext(), R.color.route_green),
                    ContextCompat.getColor(requireContext(), R.color.route_orange),
                    ContextCompat.getColor(requireContext(), R.color.route_red),
                    ContextCompat.getColor(requireContext(), R.color.route_purple),
                    ContextCompat.getColor(requireContext(), R.color.route_teal),
                    ContextCompat.getColor(requireContext(), R.color.route_indigo),
                    ContextCompat.getColor(requireContext(), R.color.route_brown),
                    ContextCompat.getColor(requireContext(), R.color.route_pink),
                    ContextCompat.getColor(requireContext(), R.color.route_gray),
                )

                // ✅ Primero: carga restaurantes (si quieres que siempre estén)
                cargarRestaurantes(db, iconoRestaurante)

                // ✅ Segundo: cargar lineas en Spinner (SIN dibujar rutas aún)
                cargarLineasEnSpinner(db)

                // ✅ Botón mostrar: limpia y dibuja solo la seleccionada
                binding.btnMostrar.setOnClickListener {
                    val nombreSeleccionado = binding.spinnerLineas.selectedItem?.toString()
                        ?: return@setOnClickListener

                    val linea = lineasByNombre[nombreSeleccionado]
                        ?: return@setOnClickListener

                    // Limpia todo (markers+polylines)
                    mapLibreMap.clear()

                    // Si quieres restaurantes siempre visibles, vuelve a pintarlos
                    cargarRestaurantes(db, iconoRestaurante)

                    // Dibuja solo la línea elegida
                    dibujarLinea(linea, iconoParada, routeColors)
                }
            }
        }

        return binding.root
    }

    // =======================
    //  Cargar líneas a Spinner
    // =======================
    private fun cargarLineasEnSpinner(db: FirebaseFirestore) {
        db.collection("lineas")
            .get()
            .addOnSuccessListener { result ->

                lineasByNombre.clear()
                nombresLineas.clear()

                for (doc in result) {
                    val nombreLinea = doc.getString("nombre") ?: doc.id

                    val paradasMap = doc.get("paradas") as? Map<*, *> ?: emptyMap<Any, Any>()

                    // Ordenar por claves numéricas "1","2","3"
                    val paradasOrdenadas = paradasMap.entries
                        .sortedBy { it.key.toString().toIntOrNull() ?: Int.MAX_VALUE }
                        .mapNotNull { it.value as? Map<*, *> }

                    val linea = LineaUi(
                        id = doc.id,
                        nombre = nombreLinea,
                        paradasOrdenadas = paradasOrdenadas
                    )

                    lineasByNombre[nombreLinea] = linea
                    nombresLineas.add(nombreLinea)
                }

                val adapterSpinner = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    nombresLineas
                )
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerLineas.adapter = adapterSpinner
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al cargar líneas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // =======================
    //  Dibujar línea seleccionada
    // =======================
    private fun dibujarLinea(
        linea: LineaUi,
        iconoParada: Icon,
        routeColors: List<Int>
    ) {
        val puntos = mutableListOf<LatLng>()
        val colorRuta = routeColors[linea.id.hashCode().absoluteValue % routeColors.size]

        for (paradaObj in linea.paradasOrdenadas) {
            val nombreParada = paradaObj["nombre"]?.toString() ?: "Parada"
            val tipoParada = paradaObj["tipo"]?.toString() ?: "PARADA"

            val lat = (paradaObj["lat"] as? Number)?.toDouble() ?: 0.0
            val lng = (paradaObj["lng"] as? Number)?.toDouble() ?: 0.0

            if (lat == 0.0 && lng == 0.0) continue

            val pos = LatLng(lat, lng)
            puntos.add(pos)

            mapLibreMap.addMarker(
                MarkerOptions()
                    .position(pos)
                    .title(nombreParada)
                    .snippet("${linea.nombre} • $tipoParada")
                    .icon(iconoParada)
            )
        }

        if (puntos.size >= 2) {
            mapLibreMap.addPolyline(
                PolylineOptions()
                    .addAll(puntos)
                    .color(colorRuta)
                    .width(5f)
            )
            mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(puntos.first(), 13.5))
        } else if (puntos.size == 1) {
            mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(puntos.first(), 14.0))
        }
    }

    // =======================
    //  Restaurantes
    // =======================
    private fun cargarRestaurantes(db: FirebaseFirestore, iconoRestaurante: Icon) {
        db.collection("restaurantes")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val descripcion = document.getString("descripcion") ?: "Sin descripción"

                    val lat = (document.get("latitud") as? Number)?.toDouble()
                        ?: document.getString("latitud")?.toDoubleOrNull()
                        ?: 0.0

                    val lng = (document.get("longitud") as? Number)?.toDouble()
                        ?: document.getString("longitud")?.toDoubleOrNull()
                        ?: 0.0

                    if (lat == 0.0 && lng == 0.0) continue

                    val ubicacion = LatLng(lat, lng)

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

    // =======================
    //  Ciclo de vida MapView
    // =======================
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

    private fun vectorToBitmap(context: Context, drawableId: Int, size: Int = 96): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)!!
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
