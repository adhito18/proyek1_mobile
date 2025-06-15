package proyek1.mobile.uas

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class CatalogFragment : Fragment() {

    private lateinit var propertyRecyclerView: RecyclerView
    private val propertyList = mutableListOf<Property>()
    private lateinit var adapter: PropertyAdapter
    private lateinit var locationNameTextView: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dashboard_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val searchInput = view.findViewById<EditText>(R.id.searchInput)
        val imgFilter = view.findViewById<ImageButton>(R.id.imgFilter)
        locationNameTextView = view.findViewById(R.id.locationName)

        propertyRecyclerView = view.findViewById(R.id.propertyRecyclerView)
        propertyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PropertyAdapter(propertyList)
        propertyRecyclerView.adapter = adapter

        fetchPropertiesFromApi()

        // Realtime search
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        // Filter Popup
        imgFilter.setOnClickListener { showFilterPopup(it) }

        // Lokasi
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLocation()
    }

    private fun fetchPropertiesFromApi() {
        val url = "https://myprop.my.id/api/catalog"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                propertyList.clear()
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    val property = Property(
                        id = item.getInt("id"),
                        name = item.getString("name"),
                        type = item.getString("type"),
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
                adapter.updateData(propertyList)
            },
            { error ->
                val statusCode = error.networkResponse?.statusCode ?: -1
                Toast.makeText(requireContext(), "Gagal memuat data (status: $statusCode)", Toast.LENGTH_LONG).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    private fun showFilterPopup(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.filter_catalog, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.popup_sort_newest_first -> {
                    propertyList.sortByDescending { it.id }
                }
                R.id.popup_sort_oldest_first -> {
                    propertyList.sortBy { it.id }
                }
                R.id.popup_sort_price_high_to_low -> {
                    propertyList.sortByDescending { it.price.toIntOrNull() ?: 0 }
                }
                R.id.popup_sort_price_low_to_high -> {
                    propertyList.sortBy { it.price.toIntOrNull() ?: 0 }
                }
                else -> return@setOnMenuItemClickListener false
            }
            adapter.updateData(propertyList)
            true
        }

        popupMenu.show()
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                try {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val locality = address.locality
                        val subAdmin = address.subAdminArea
                        val admin = address.adminArea

                        val readableLocation = when {
                            !locality.isNullOrEmpty() -> "$locality, $admin"
                            !subAdmin.isNullOrEmpty() -> "$subAdmin, $admin"
                            !admin.isNullOrEmpty() -> admin
                            else -> "Lokasi tidak diketahui"
                        }

                        locationNameTextView.text = readableLocation
                    } else {
                        locationNameTextView.text = "Lokasi tidak ditemukan"
                    }

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Gagal memuat lokasi: ${e.message}", Toast.LENGTH_SHORT).show()
                    locationNameTextView.text = "Error Location"
                }
            } else {
                locationNameTextView.text = "Location not available"
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Gagal mengambil lokasi", Toast.LENGTH_SHORT).show()
            locationNameTextView.text = "Error Location"
        }
    }
}
