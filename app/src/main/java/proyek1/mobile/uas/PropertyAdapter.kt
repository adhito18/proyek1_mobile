package proyek1.mobile.uas

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso

class PropertyAdapter(private var propertyList: List<Property>) :
    RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    private var filteredList = propertyList.toMutableList()

    class PropertyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val propertyImage: ImageView = view.findViewById(R.id.propertyImage)
        val propertyName: TextView = view.findViewById(R.id.propertyName)
        val propertyPrice: TextView = view.findViewById(R.id.propertyPrice)
        val locationTag: TextView = view.findViewById(R.id.locationTag)
        val favoriteIcon: ImageView = view.findViewById(R.id.favoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property_card_vertical, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = filteredList[position]

        holder.propertyName.text = property.name
        holder.propertyPrice.text = "Rp ${property.price}"
        holder.locationTag.text = property.location

        val baseUrl = "https://myprop.my.id/storage/"
        Picasso.get()
            .load(baseUrl + property.photo)
            .placeholder(R.drawable.property_thumbnail_placeholder)
            .into(holder.propertyImage)

        // Tampilkan status favorit
        holder.favoriteIcon.setImageResource(
            if (property.isFavorite) R.drawable.ic_heart_red else R.drawable.ic_heart_filled
        )

        // Toggle favorit saat diklik
        holder.favoriteIcon.setOnClickListener {
            toggleFavorite(holder.itemView.context, property) {
                property.isFavorite = !property.isFavorite
                notifyItemChanged(position)
            }
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PropertyDetailActivity::class.java).apply {
                putExtra("id", property.id)
                putExtra("name", property.name)
                putExtra("description", property.description)
                putExtra("type", property.type)
                putExtra("location", property.location)
                putExtra("price", property.price)
                putExtra("size", property.size)
                putExtra("bedrooms", property.bedrooms)
                putExtra("bathrooms", property.bathrooms)
                putExtra("photo", property.photo)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            propertyList.toMutableList()
        } else {
            propertyList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.location.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun updateData(newList: List<Property>) {
        propertyList = newList
        filteredList = newList.toMutableList()
        notifyDataSetChanged()
    }

    private fun toggleFavorite(context: Context, property: Property, onSuccess: () -> Unit) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("TOKEN", null)

        if (token == null) {
            Toast.makeText(context, "Token tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "https://myprop.my.id/api/favorites/${property.id}/toggle"

        val request = object : StringRequest(Method.POST, url,
            { response ->
                Log.d("TOGGLE_FAVORITE", "Success response: $response")
                onSuccess()
            },
            { error ->
                Log.e("TOGGLE_FAVORITE", "Error: ${error.message}")
                Toast.makeText(context, "Gagal toggle favorite", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        com.android.volley.toolbox.Volley.newRequestQueue(context).add(request)
    }
}
