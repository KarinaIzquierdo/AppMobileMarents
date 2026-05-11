package com.marents.app.ui.carrito

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.marents.app.*
import com.marents.app.databinding.FragmentCarritoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarritoFragment : Fragment() {

    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!
    private lateinit var carritoAdapter: CarritoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        cargarCarrito()
    }

    private fun setupRecyclerView() {
        carritoAdapter = CarritoAdapter(
            onDeleteClick = { item ->
                eliminarItemDelCarrito(item)
            }
        )

        binding.recyclerViewCarrito.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = carritoAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnIrAPagar.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad de pago en construcción", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarCarrito() {
        // TODO: Obtener el userId real del usuario logueado. Por ahora usamos 1 para pruebas.
        val userId = 1

        Log.d("CarritoFragment", "Cargando carrito para usuario $userId")
        binding.tvCarritoVacio.text = "Cargando carrito..."
        binding.tvCarritoVacio.visibility = View.VISIBLE

        RetrofitClient.apiService.getCart(userId).enqueue(object : Callback<List<CartItem>> {
            override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                if (!isAdded) return // Verificar que el fragment sigue activo
                Log.d("CarritoFragment", "Respuesta API: ${response.code()}, body: ${response.body()}")
                if (response.isSuccessful) {
                    val items = response.body() ?: emptyList()
                    Log.d("CarritoFragment", "Items recibidos: ${items.size}")
                    actualizarUI(items)
                } else {
                    Log.e("CarritoFragment", "Error API: ${response.code()}")
                    binding.tvCarritoVacio.text = "Error al cargar carrito"
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                if (!isAdded) return // Verificar que el fragment sigue activo
                Log.e("CarritoFragment", "Error de red: ${t.message}", t)
                binding.tvCarritoVacio.text = "Error de conexión"
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun actualizarUI(items: List<CartItem>) {
        Log.d("CarritoFragment", "Actualizando UI con ${items.size} items")
        if (items.isEmpty()) {
            Log.d("CarritoFragment", "Carrito vacío - mostrando mensaje vacío")
            binding.tvCarritoVacio.text = "Tu carrito está vacío"
            binding.tvCarritoVacio.visibility = View.VISIBLE
            binding.recyclerViewCarrito.visibility = View.GONE
            binding.tvCantidadProductos.text = "0 productos"
            binding.tvSubtotal.text = "$0"
            binding.tvTotal.text = "$0"
        } else {
            Log.d("CarritoFragment", "Carrito con items - ocultando mensaje vacío, mostrando RecyclerView")
            binding.tvCarritoVacio.visibility = View.GONE
            binding.recyclerViewCarrito.visibility = View.VISIBLE
            
            // Actualizar el RecyclerView con los items
            carritoAdapter.submitList(items)
            
            var total = 0
            var cantidadTotal = 0
            
            for (item in items) {
                cantidadTotal += item.cantidad
                // Limpiar el string de precio si tiene simbolos
                val precioLimpio = item.precio.replace("$", "").replace(".", "").replace(",", "").trim()
                total += (precioLimpio.toIntOrNull() ?: 0) * item.cantidad
            }
            
            binding.tvCantidadProductos.text = "$cantidadTotal productos"
            binding.tvSubtotal.text = "$${formatPrecio(total)}"
            binding.tvTotal.text = "$${formatPrecio(total)}"
        }
    }

    private fun eliminarItemDelCarrito(item: CartItem) {
        RetrofitClient.apiService.removeFromCart(item.itemId).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (!isAdded) return // Verificar que el fragment sigue activo
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Producto eliminado del carrito", Toast.LENGTH_SHORT).show()
                    // Recargar el carrito para actualizar la UI
                    cargarCarrito()
                } else {
                    Toast.makeText(requireContext(), "Error al eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                if (!isAdded) return // Verificar que el fragment sigue activo
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatPrecio(precio: Int): String {
        return String.format("%,d", precio).replace(',', '.')
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
