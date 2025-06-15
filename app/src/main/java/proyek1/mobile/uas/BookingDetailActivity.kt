package proyek1.mobile.uas

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class BookingDetailActivity : AppCompatActivity() {

    private lateinit var startDateText: TextView
    private lateinit var endDateText: TextView
    private lateinit var totalPriceText: TextView

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale("in", "ID"))
    private var selectedStart: Calendar? = null
    private var selectedEnd: Calendar? = null
    private var pricePerDay = 0.0
    private var propertyId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Ambil data dari Intent
        val propertyName = intent.getStringExtra("property_name") ?: ""
        val propertyPrice = intent.getStringExtra("property_price") ?: ""
        propertyId = intent.getIntExtra("property_id", 0).toString()
        Log.d("BookingDetailActivity", "Property ID: $propertyId")
        val propertyType = intent.getStringExtra("property_type") ?: ""
        val propertySize = intent.getStringExtra("property_size") ?: ""
        val bedrooms = intent.getStringExtra("property_bedrooms") ?: ""
        val bathrooms = intent.getStringExtra("property_bathrooms") ?: ""
        val description = intent.getStringExtra("property_description") ?: ""
        val photoPath = intent.getStringExtra("property_photo") ?: ""

        // Binding View
        val propertyTitle = findViewById<TextView>(R.id.propertyTitle)
        val pricePerDayText = findViewById<TextView>(R.id.propertyPricePerDay)
        val propertyTypeText = findViewById<TextView>(R.id.tipeValue)
        val sizeValue = findViewById<TextView>(R.id.ukuranValue)
        val bedroomsValue = findViewById<TextView>(R.id.kamarTidurValue)
        val bathroomsValue = findViewById<TextView>(R.id.kamarMandiValue)
        val descriptionValue = findViewById<TextView>(R.id.deskripsiValue)
        val propertyImage = findViewById<ImageView>(R.id.propertyImagePlaceholder)
        val fullNameAutoComplete = findViewById<AutoCompleteTextView>(R.id.nameAutoComplete)
        val continueButton = findViewById<Button>(R.id.continuePaymentButton)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        startDateText = findViewById(R.id.startDateValue)
        endDateText = findViewById(R.id.endDateValue)
        totalPriceText = findViewById(R.id.totalPriceValue)

        // Tampilkan data ke tampilan
        propertyTitle.text = propertyName
        pricePerDay = propertyPrice.toDoubleOrNull() ?: 0.0
        val formattedPrice = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(pricePerDay)
        pricePerDayText.text = "$formattedPrice / day"

        propertyTypeText.text = propertyType.replace("_", " ").replaceFirstChar { it.uppercase() }
        sizeValue.text = "$propertySize m²"
        bedroomsValue.text = bedrooms
        bathroomsValue.text = bathrooms
        descriptionValue.text = description

        val imageUrl = "https://myprop.my.id/storage/$photoPath"
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.property_thumbnail_placeholder)
            .into(propertyImage)

        // Get user data from SharedPreferences
        val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "") ?: ""

        // Set up AutoCompleteTextView with user's name
        val nameSuggestions = if (userName.isNotEmpty()) {
            arrayOf(userName)
        } else {
            arrayOf("")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nameSuggestions)
        fullNameAutoComplete.setAdapter(adapter)
        fullNameAutoComplete.threshold = 1

        backButton.setOnClickListener { onBackPressed() }

        startDateText.setOnClickListener { showDatePicker(true) }
        endDateText.setOnClickListener { showDatePicker(false) }

        continueButton.setOnClickListener {
            val fullName = fullNameAutoComplete.text.toString().trim()
            if (fullName.isEmpty()) {
                fullNameAutoComplete.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }
            if (selectedStart == null || selectedEnd == null) {
                Toast.makeText(this, "Pilih tanggal terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createTransaction(fullName)
        }
    }

    private fun showDatePicker(isStart: Boolean) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, { _, year, month, day ->
            val selected = Calendar.getInstance()
            selected.set(year, month, day)

            val formatted = dateFormat.format(selected.time)

            if (isStart) {
                selectedStart = selected
                startDateText.text = formatted
                Log.d("DatePicker", "Tanggal mulai dipilih: $formatted")
            } else {
                selectedEnd = selected
                endDateText.text = formatted
                Log.d("DatePicker", "Tanggal akhir dipilih: $formatted")
            }

            updateTotalPrice()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    private fun updateTotalPrice() {
        if (selectedStart != null && selectedEnd != null) {
            val days = ((selectedEnd!!.timeInMillis - selectedStart!!.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
            Log.d("TotalPrice", "Durasi hari: $days")
            if (days <= 0) {
                totalPriceText.text = "Durasi tidak valid"
                Log.w("TotalPrice", "Durasi tidak valid (<= 0)")
                return
            }
            val total = pricePerDay * days
            val formatted = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(total)
            totalPriceText.text = "Total: $formatted"
            Log.d("TotalPrice", "Harga per hari: $pricePerDay, Total: $total")
        }
    }

    private fun createTransaction(fullName: String) {
        val token = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            .getString("auth_token", "") ?: ""

        val days = ((selectedEnd!!.timeInMillis - selectedStart!!.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
        val total = pricePerDay * days
        val url = "https://myprop.my.id/api/properties/$propertyId/transactions"

        val postData = hashMapOf(
            "name" to fullName,
            "property_id" to propertyId,
            "lease_start" to dateFormat.format(selectedStart!!.time),
            "lease_end" to dateFormat.format(selectedEnd!!.time),
            "total_price" to total.toInt().toString()
        )

        Log.d("Transaction", "Payload: $postData")
        Log.d("Transaction", "Token: $token")

        val jsonBody = org.json.JSONObject(postData as Map<*, *>)

        val request = object : JsonObjectRequest(Request.Method.POST, url, jsonBody,
            { response ->
                try {
                    Log.d("Midtrans", "Response: $response")
                    val snapToken = response.getString("snap_token")
                    val paymentUrl = "https://app.sandbox.midtrans.com/snap/v2/vtweb/$snapToken"
                    Log.d("Midtrans", "Redirect to payment: $paymentUrl")
                    val intent = Intent(this, PaymentWebViewActivity::class.java)
                    intent.putExtra("payment_url", paymentUrl)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("Midtrans", "Parsing Error: ${e.message}")
                    Toast.makeText(this, "Format respons tidak valid", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Gagal membuat transaksi", Toast.LENGTH_SHORT).show()
                Log.e("Midtrans", "Volley Error: ${error.message}")
                error.networkResponse?.let {
                    Log.e("Midtrans", "Status Code: ${it.statusCode}")
                    Log.e("Midtrans", "Response Body: ${String(it.data)}")
                }
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf(
                    "Accept" to "application/json",
                    "Authorization" to "Bearer $token"
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}