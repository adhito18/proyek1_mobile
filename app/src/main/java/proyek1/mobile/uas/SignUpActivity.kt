package proyek1.mobile.uas // Ensure this matches your actual package name

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import proyek1.mobile.uas.databinding.ActivitySignUpBinding // This is the generated binding class

class SignUpActivity : AppCompatActivity() {

    // Declare the binding object
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using View Binding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        // Set the content view to the root of the inflated layout
        setContentView(binding.root)

        // --- Add any specific logic for your Sign Up form here ---

        // Example: Set a click listener for the Sign Up button
        binding.signUpButton.setOnClickListener {
            // Get values from the EditText fields
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()
            val phoneNumber = binding.phoneNumberEditText.text.toString().trim()

            // Basic validation example:
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // Here you would typically send data to a server, save to database, etc.
                val message = "Sign Up successful!\nUsername: $username\nEmail: $email\nPhone: $phoneNumber"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                // Optionally, navigate back to LoginActivity or to a new screen
                // finish() // This would close SignUpActivity and go back to the previous activity in the stack
            }
        }
    }
}