package proyek1.mobile.uas

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import proyek1.mobile.uas.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Call your custom methods
        updateDateTime()
        applyGradientToOverlay()

        binding.signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.continueButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Updates the current time and date TextViews.
     * This is a custom method, so it does NOT use 'override'.
     */
    fun updateDateTime() { // REMOVE 'override' here
        val timeFormat = SimpleDateFormat("HH.mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        binding.currentTime.text = currentTime

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        binding.currentDate.text = currentDate
    }

    /**
     * Applies a gradient drawable to the gradientOverlay View.
     * This is a custom method, so it does NOT use 'override'.
     */
    private fun applyGradientToOverlay() { // REMOVE 'override' here
        val startColor = ContextCompat.getColor(this, android.R.color.transparent)
        val endColor = ContextCompat.getColor(this, android.R.color.black)

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(startColor, endColor)
        )
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        binding.gradientOverlay.background = gradientDrawable
    }
}