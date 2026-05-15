package com.marents.app.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.marents.app.R
import com.marents.app.User

class UsersAdapter(
    private var users: List<User>,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvRol: TextView = itemView.findViewById(R.id.tvRol)
        private val btnEdit: TextView = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: TextView = itemView.findViewById(R.id.btnDelete)

        fun bind(user: User) {
            tvNombre.text = "${user.nombres ?: ""} ${user.apellidos ?: ""}"
            tvEmail.text = user.email
            tvRol.text = user.rol ?: "usuario"

            btnEdit.setOnClickListener {
                onEditClick(user)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(user)
            }

            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "Seleccionado: ${user.nombres} ${user.apellidos}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
