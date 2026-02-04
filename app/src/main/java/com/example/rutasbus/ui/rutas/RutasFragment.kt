package com.example.rutasbus.ui.rutas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rutasbus.adapter.BusAdapter
import com.example.rutasbus.data.RutaBus
import com.example.rutasbus.databinding.FragmentTurismoBinding
import com.google.firebase.firestore.FirebaseFirestore

class RutasFragment : Fragment() {

    private var _binding: FragmentTurismoBinding? = null
    private val binding get() = _binding!!

    private val items = mutableListOf<RutaBus>()
    private lateinit var adapter: BusAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTurismoBinding.inflate(inflater, container, false)

        adapter = BusAdapter(items) { rutaSeleccionada ->
            Toast.makeText(
                requireContext(),
                "Ruta seleccionada: ${rutaSeleccionada.nombre}",
                Toast.LENGTH_SHORT
            ).show()

        }

        binding.recyclerTurismo.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerTurismo.adapter = adapter

        FirebaseFirestore.getInstance()
            .collection("lineas")
            .get()
            .addOnSuccessListener { result ->
                items.clear()
                for (document in result) {
                    val ruta = document.toObject(RutaBus::class.java)
                    if (ruta != null) items.add(ruta)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al cargar líneas: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

