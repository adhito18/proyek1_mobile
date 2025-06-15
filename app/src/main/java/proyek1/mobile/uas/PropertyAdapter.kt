package proyek1.mobile.uas

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
        val fullPhotoUrl = baseUrl + property.photo

        Picasso.get()
            .load(fullPhotoUrl)
            .placeholder(R.drawable.property_thumbnail_placeholder)
            .into(holder.propertyImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PropertyDetailActivity::class.java).apply {
                putExtra("name", property.name)
                putExtra("price", property.price)
                putExtra("location", property.location)
                putExtra("description", property.description)
                putExtra("size", property.size)
                putExtra("bedrooms", property.bedrooms)
                putExtra("bathrooms", property.bathrooms)
                putExtra("photo", property.photo)
            }
            holder.itemView.context.startActivity(intent)
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
}
