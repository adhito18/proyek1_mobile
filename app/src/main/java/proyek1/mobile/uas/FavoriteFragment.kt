package proyek1.mobile.uas

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class FavoriteFragment : Fragment() {

    private lateinit var favoriteRecyclerView: RecyclerView
    private lateinit var adapter: PropertyAdapter
    private val favoriteList = mutableListOf<Property>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("TOKEN", null) ?: ""

        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "Token tidak tersedia, silakan login ulang", Toast.LENGTH_LONG).show()
            return view
        }

        // Setup RecyclerView
        favoriteRecyclerView = view.findViewById(R.id.favoritePropertiesRecyclerView)
        favoriteRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PropertyAdapter(favoriteList)
        favoriteRecyclerView.adapter = adapter
        favoriteRecyclerView.adapter = adapter

        // Tombol hapus semua
        view.findViewById<Button>(R.id.clearAllFavoritesButton).setOnClickListener {
            clearAllFavorites()
        }

        // Tombol tambah (nanti bisa diarahkan ke katalog)
        view.findViewById<Button>(R.id.addFavoriteButton).setOnClickListener {
            Toast.makeText(context, "Tambah Favorit belum diimplementasikan", Toast.LENGTH_SHORT).show()
        }

        fetchFavoriteProperties()

        return view
    }

    private fun fetchFavoriteProperties() {
        val url = "https://myprop.my.id/api/favorites"
        val request = object : JsonObjectRequest(
            Method.GET, url, null,
            { response ->
                val favorites = response.getJSONArray("favorites")
                favoriteList.clear()

                for (i in 0 until favorites.length()) {
                    val item = favorites.getJSONObject(i)

                    val property = Property(
                        id = item.getInt("id"),
                        name = item.getString("name"),
                        description = item.getString("description"),
                        location = item.getString("location"),
                        price = item.getString("price"),
                        type = item.getString("type"),
                        size = item.getString("size"),
                        bedrooms = item.getString("bedrooms"),
                        bathrooms = item.getString("bathrooms"),
                        photo = item.getString("photo")
                    )
                    property.isFavorite = true
                    favoriteList.add(property)
                }

                adapter.updateData(favoriteList)
                Toast.makeText(requireContext(), "Total favorit: ${favoriteList.size}", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Log.e("FavoriteFragment", "Error: ${error.message}")
                Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf(
                    "Authorization" to "Bearer $token",
                    "Accept" to "application/json"
                )
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }


    private fun clearAllFavorites() {
        // Hapus dari server
        for (property in favoriteList) {
            val url = "https://myprop.my.id/api/favorites/${property.id}"
            val request = object : JsonObjectRequest(
                Method.DELETE, url, null,
                {
                    fetchFavoriteProperties()
                },
                {
                    Toast.makeText(context, "Gagal menghapus favorit", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    return hashMapOf(
                        "Authorization" to "Bearer $token",
                        "Accept" to "application/json"
                    )
                }
            }
            Volley.newRequestQueue(requireContext()).add(request)
        }

        Toast.makeText(context, "Semua favorit dihapus", Toast.LENGTH_SHORT).show()
    }
}
