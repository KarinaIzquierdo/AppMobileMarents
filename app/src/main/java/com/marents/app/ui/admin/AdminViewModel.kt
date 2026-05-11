package com.marents.app.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marents.app.RetrofitClient
import kotlinx.coroutines.launch

data class AdminStats(
    val pedidosPendientes: Int = 0,
    val pedidosCompletados: Int = 0,
    val totalVentas: Int = 0,
    val stockBajo: Int = 0
)

data class ProductoVendido(
    val nombre: String,
    val cantidad: Int
)

data class PedidoReciente(
    val id: String,
    val cliente: String,
    val total: String,
    val estado: String
)

class AdminViewModel : ViewModel() {

    private val _stats = MutableLiveData<AdminStats>()
    val stats: LiveData<AdminStats> = _stats

    private val _productosVendidos = MutableLiveData<List<ProductoVendido>>()
    val productosVendidos: LiveData<List<ProductoVendido>> = _productosVendidos

    private val _pedidosRecientes = MutableLiveData<List<PedidoReciente>>()
    val pedidosRecientes: LiveData<List<PedidoReciente>> = _pedidosRecientes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarEstadisticas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Simular datos mientras creamos los endpoints reales
                // TODO: Reemplazar con llamadas reales a la API
                val statsMock = AdminStats(
                    pedidosPendientes = 12,
                    pedidosCompletados = 58,
                    totalVentas = 2500000,
                    stockBajo = 5
                )

                val productosMock = listOf(
                    ProductoVendido("Cool", 25),
                    ProductoVendido("Bolichero", 18),
                    ProductoVendido("Bigotes", 12)
                )

                val pedidosMock = listOf(
                    PedidoReciente("#001", "Juan", "$120.000", "Pendiente"),
                    PedidoReciente("#002", "Ana", "$85.000", "Completado")
                )

                _stats.value = statsMock
                _productosVendidos.value = productosMock
                _pedidosRecientes.value = pedidosMock

            } catch (e: Exception) {
                _error.value = "Error al cargar estadísticas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
