package proyek1.mobile.uas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import proyek1.mobile.uas.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil token dari intent
        token = intent.getStringExtra("token") ?: ""

        // Sinkronisasi favorit dari API ke SQLite
        syncFavorites(token)

        // Set default fragment
        loadFragment(CatalogFragment())

        // Handle navigation item selection
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> loadFragment(CatalogFragment())
                R.id.navigation_favorite -> loadFragment(FavoriteFragment())
                R.id.navigation_history -> loadFragment(HistoryFragment())
                R.id.navigation_profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commit()
    }

    private fun syncFavorites(token: String) {
        val url = "https://myprop.my.id/api/favorites"

        val request = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                val favoritesArray = response.getJSONArray("favorites")
                val dbHelper = FavoriteDbHelper(this)
                dbHelper.clearFavorites()

                for (i in 0 until favoritesArray.length()) {
                    val item = favoritesArray.getJSONObject(i)
                    val favorite = Favorite(
                        id = item.getInt("id"),
                        userId = item.getInt("user_id"),
                        name = item.getString("name"),
                        description = item.getString("description"),
                        location = item.getString("location"),
                        price = item.getString("price"),
                        size = item.getString("size"),
                        bedrooms = item.getString("bedrooms"),
                        bathrooms = item.getString("bathrooms"),
                        photo = item.getString("photo"),
                        status = item.getString("status"),
                        type = item.getString("type")
                    )
                    dbHelper.insertFavorite(favorite)
                }

                Toast.makeText(this, "Favorit disinkronkan", Toast.LENGTH_SHORT).show()
            },
            {
                Toast.makeText(this, "Gagal sinkronisasi favorit", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf(
                    "Authorization" to "Bearer $token",
                    "Accept" to "application/json"
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}

