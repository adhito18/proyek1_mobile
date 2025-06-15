package proyek1.mobile.uas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FavoriteAdapter(private val list: MutableList<Favorite>) :
    RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.propertyName)
        val location = itemView.findViewById<TextView>(R.id.propertyLocation)
        val price = itemView.findViewById<TextView>(R.id.propertyPrice)
        val icon = itemView.findViewById<ImageView>(R.id.propertyIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_property, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name
        holder.location.text = item.location
        holder.price.text = "Rp ${item.price}"

        // Placeholder image, bisa pakai Glide/Picasso
        holder.icon.setImageResource(R.drawable.ic_house_placeholder)

        // ✅ Tambahkan Picasso di sini
        Picasso.get()
            .load("https://myprop.my.id/storage/" + item.photo)
            .placeholder(R.drawable.ic_house_placeholder)
            .error(R.drawable.ic_house_placeholder) // opsional, jika gagal load
            .into(holder.icon)
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newData: List<Favorite>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }

}

