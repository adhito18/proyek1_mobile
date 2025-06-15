package proyek1.mobile.uas

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class PropertyDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_properti)

        // Ambil data dari Intent
        val name = intent.getStringExtra("name")
        val price = intent.getStringExtra("price")
        val location = intent.getStringExtra("location")
        val description = intent.getStringExtra("description")
        val size = intent.getStringExtra("size")
        val bedrooms = intent.getStringExtra("bedrooms")
        val bathrooms = intent.getStringExtra("bathrooms")
        val photoPath = intent.getStringExtra("photo")

        // Ganti dengan domain server Laravel kamu
        val imageUrl = "https://myprop.my.id/storage/$photoPath"

        // Inisialisasi view
        val nameText = findViewById<TextView>(R.id.propertyName)
        nameText.text = name
        val imageView = findViewById<ImageView>(R.id.mainPropertyImage)
        val locationText = findViewById<TextView>(R.id.locationName)
        val priceText = findViewById<TextView>(R.id.propertyPrice)
        val descriptionText = findViewById<TextView>(R.id.descriptionContent)
        val sizeText = findViewById<TextView>(R.id.sizeText)
        val bedroomsText = findViewById<TextView>(R.id.bedroomText)
        val bathroomsText = findViewById<TextView>(R.id.bathroomText)

        locationText.text = location
        priceText.text = "Rp. $price / Day"
        descriptionText.text = description
        sizeText.text = "$size m²"
        bedroomsText.text = bedrooms
        bathroomsText.text = bathrooms

        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.property_thumbnail_placeholder)
            .into(imageView)

        // Tombol kembali
        val backButton = findViewById<Button>(R.id.backToCatalogButton)
        backButton.setOnClickListener {
            finish()
        }

        val rentButton = findViewById<Button>(R.id.rentNowButton)
        rentButton.setOnClickListener {
            // TODO: Arahkan ke form booking
        }
    }
}
