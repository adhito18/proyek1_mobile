package proyek1.mobile.uas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class PropertyDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_properti)

        // Get data from Intent
        val propertyId = intent.getIntExtra("id", 0)
        val name = intent.getStringExtra("name") ?: ""
        val price = intent.getStringExtra("price") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val type = intent.getStringExtra("type") ?: ""
        val size = intent.getStringExtra("size") ?: ""
        val bedrooms = intent.getStringExtra("bedrooms") ?: ""
        val bathrooms = intent.getStringExtra("bathrooms") ?: ""
        val photoPath = intent.getStringExtra("photo") ?: ""

        // Initialize views
        val nameText = findViewById<TextView>(R.id.propertyName)
        val imageView = findViewById<ImageView>(R.id.mainPropertyImage)
        val locationText = findViewById<TextView>(R.id.locationName)
        val priceText = findViewById<TextView>(R.id.propertyPrice)
        val descriptionText = findViewById<TextView>(R.id.descriptionContent)
        val sizeText = findViewById<TextView>(R.id.sizeText)
        val bedroomsText = findViewById<TextView>(R.id.bedroomText)
        val bathroomsText = findViewById<TextView>(R.id.bathroomText)

        // Set data to views
        nameText.text = name
        locationText.text = location
        priceText.text = "Rp. $price / Day"
        descriptionText.text = description
        sizeText.text = "$size m²"
        bedroomsText.text = bedrooms
        bathroomsText.text = bathrooms

        // Load property image
        val imageUrl = "https://myprop.my.id/storage/$photoPath"
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.property_thumbnail_placeholder)
            .into(imageView)

        // Back button
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Rent Now button - navigate to BookingConfirmationActivity
        findViewById<Button>(R.id.rentNowButton).setOnClickListener {
            val bookingIntent = Intent(this, BookingDetailActivity::class.java).apply {
                putExtra("property_id", propertyId)
                putExtra("property_name", name)
                putExtra("property_price", price)
                putExtra("property_type", type)
                putExtra("property_location", location)
                putExtra("property_size", size)
                putExtra("property_bedrooms", bedrooms)
                putExtra("property_bathrooms", bathrooms)
                putExtra("property_description", description)
                putExtra("property_photo", photoPath)
            }
            startActivity(bookingIntent)
        }
    }
}