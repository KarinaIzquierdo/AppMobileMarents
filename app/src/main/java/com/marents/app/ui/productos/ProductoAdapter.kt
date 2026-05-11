package com.marents.app.ui.productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.marents.app.R

class ProductoAdapter(
    private val onProductoClick: (ProductoUI) -> Unit,
    private val onFavoritoClick: (ProductoUI) -> Unit
) : ListAdapter<ProductoUI, ProductoAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_card, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProducto: ImageView = itemView.findViewById(R.id.ivProducto)
        private val ivFavorito: ImageView = itemView.findViewById(R.id.ivFavorito)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvSubcategoria: TextView = itemView.findViewById(R.id.tvSubcategoria)
        private val btnTallas: List<TextView> = listOf(
            itemView.findViewById(R.id.btnTalla1),
            itemView.findViewById(R.id.btnTalla2),
            itemView.findViewById(R.id.btnTalla3),
            itemView.findViewById(R.id.btnTalla4),
            itemView.findViewById(R.id.btnTalla5)
        )

        fun bind(producto: ProductoUI) {
            tvPrecio.text = producto.precio
            tvNombre.text = producto.nombre
            tvSubcategoria.text = producto.subcategoria

            // Configurar tallas
            btnTallas.forEachIndexed { index, textView ->
                if (index < producto.tallas.size) {
                    textView.text = producto.tallas[index]
                    textView.visibility = View.VISIBLE
                } else {
                    textView.visibility = View.GONE
                }
            }

            // Configurar imagen con Coil
            if (producto.imagenUrl != null) {
                ivProducto.load(producto.imagenUrl) {
                    placeholder(R.drawable.ic_shoe_placeholder)
                    error(R.drawable.ic_shoe_placeholder)
                }
            } else {
                ivProducto.setImageResource(R.drawable.ic_shoe_placeholder)
            }

            // Configurar favorito
            ivFavorito.setImageResource(
                if (producto.esFavorito) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )

            ivFavorito.setOnClickListener {
                onFavoritoClick(producto)
            }

            itemView.setOnClickListener {
                onProductoClick(producto)
            }
        }
    }

    class ProductoDiffCallback : DiffUtil.ItemCallback<ProductoUI>() {
        override fun areItemsTheSame(oldItem: ProductoUI, newItem: ProductoUI): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductoUI, newItem: ProductoUI): Boolean {
            return oldItem == newItem
        }
    }
}
