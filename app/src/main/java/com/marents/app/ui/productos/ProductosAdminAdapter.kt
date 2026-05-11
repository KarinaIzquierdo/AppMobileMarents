package com.marents.app.ui.productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import com.marents.app.Producto
import com.marents.app.R

class ProductosAdminAdapter(
    private val onEditClick: (Producto) -> Unit,
    private val onDeleteClick: (Producto) -> Unit
) : ListAdapter<Producto, ProductosAdminAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_tabla, parent, false)
        return ProductoViewHolder(view, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductoViewHolder(
        itemView: View,
        private val onEditClick: (Producto) -> Unit,
        private val onDeleteClick: (Producto) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private fun formatMoney(value: String): String {
            val num = value.replace(".", "").replace(",", "").toIntOrNull() ?: 0
            return when {
                num >= 1000000 -> "$${num / 1000000}M"
                num >= 1000 -> "$${num / 1000}k"
                else -> "$${num}"
            }
        }

        private fun getColorForName(colorName: String): Int {
            return when (colorName.lowercase()) {
                "negro", "black" -> 0xFF1F2937.toInt()
                "rojo", "red" -> 0xFFEF4444.toInt()
                "azul", "blue" -> 0xFF3B82F6.toInt()
                "verde", "green" -> 0xFF10B981.toInt()
                "blanco", "white" -> 0xFF9CA3AF.toInt()
                "cafe", "marrón", "brown" -> 0xFF92400E.toInt()
                "amarillo", "yellow" -> 0xFFF59E0B.toInt()
                "rosa", "pink" -> 0xFFEC4899.toInt()
                "gris", "gray" -> 0xFF6B7280.toInt()
                else -> 0xFF374151.toInt()
            }
        }

        private val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvColor: TextView = itemView.findViewById(R.id.tvColor)
        private val tvTallas: TextView = itemView.findViewById(R.id.tvTallas)
        private val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        private val tvCosto: TextView = itemView.findViewById(R.id.tvCosto)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvGanancia: TextView = itemView.findViewById(R.id.tvGanancia)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(producto: Producto) {
            // Categoría
            tvCategoria.text = producto.modelo?.categoria?.nombre ?: "Sin categoría"

            // Nombre
            tvNombre.text = producto.modelo?.nombre ?: "Sin nombre"

            // Variaciones (color y tallas)
            val variaciones = producto.variaciones
            val primeraVariacion = variaciones?.firstOrNull()
            
            // Color
            val color = primeraVariacion?.colorPrimario?.nombre ?: "Sin color"
            tvColor.text = "● $color"
            tvColor.setTextColor(getColorForName(color))
            
            // Tallas
            val tallasText = variaciones?.take(3)?.joinToString(", ") { variacion ->
                val talla = variacion.talla?.numero?.toString() ?: "-"
                val stock = variacion.stock ?: 0
                "T$talla ($stock)"
            } ?: "Sin variaciones"
            tvTallas.text = tallasText

            // Stock total
            val stockTotal = variaciones?.sumOf { it.stock ?: 0 } ?: 0
            tvStock.text = stockTotal.toString()

            // Costo y Precio (promedio de todas las variaciones)
            val costos = variaciones?.mapNotNull { it.costo?.toDoubleOrNull() } ?: emptyList()
            val precios = variaciones?.mapNotNull { it.precio?.toDoubleOrNull() } ?: emptyList()
            
            val costoPromedio = if (costos.isNotEmpty()) costos.average() else 0.0
            val precioPromedio = if (precios.isNotEmpty()) precios.average() else 0.0
            
            tvCosto.text = formatMoney(costoPromedio.toInt().toString())
            tvPrecio.text = formatMoney(precioPromedio.toInt().toString())
            
            // Ganancia
            val ganancia = (precioPromedio - costoPromedio).toInt()
            tvGanancia.text = formatMoney(ganancia.toString())
            tvGanancia.setTextColor(
                if (ganancia > 0) 0xFF10B981.toInt() else 0xFFEF4444.toInt()
            )

            // Botón editar
            btnEdit.setOnClickListener {
                onEditClick(producto)
            }

            // Botón eliminar
            btnDelete.setOnClickListener {
                onDeleteClick(producto)
            }
        }
    }

    class ProductoDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }
}
