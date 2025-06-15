package proyek1.mobile.uas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import proyek1.mobile.uas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Declare the binding variable
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding and set it as the content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the click listener for the Signup button
        binding.signupButton.setOnClickListener {
            // Create an Intent to start SignupActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Set the click listener for the "Continue" (Login) button
        binding.continueButton.setOnClickListener {
            // Create an Intent to start LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
