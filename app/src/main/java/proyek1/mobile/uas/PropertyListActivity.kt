package proyek1.mobile.uas

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


class PropertyListActivity : AppCompatActivity() {

    private lateinit var propertyRecyclerView: RecyclerView
    private val propertyList = mutableListOf<Property>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_main)

        val searchInput = findViewById<EditText>(R.id.searchInput)
        propertyRecyclerView = findViewById(R.id.propertyRecyclerView)
        propertyRecyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = PropertyAdapter(propertyList)
        propertyRecyclerView.adapter = adapter

        fetchPropertiesFromApi(adapter)

        // Realtime search
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun fetchPropertiesFromApi(adapter: PropertyAdapter) {
        val url = "https://myprop.my.id/api/catalog"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                propertyList.clear()
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    val property = Property(
                        id = item.getInt("id"),
                        name = item.getString("name"),
                        description = item.getString("description"),
                        location = item.getString("location"),
                        price = item.getString("price"),
                        size = item.getString("size"),
                        bedrooms = item.getString("bedrooms"),
                        bathrooms = item.getString("bathrooms"),
                        photo = item.getString("photo")
                    )
                    propertyList.add(property)
                }
                adapter.updateData(propertyList) // Gunakan adapter yang sudah ada
            },
            { error ->
                val statusCode = error.networkResponse?.statusCode ?: -1
                Toast.makeText(this, "Gagal memuat data (status: $statusCode)", Toast.LENGTH_LONG)
                    .show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }
}
