package proyek1.mobile.uas

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var signUpButton: Button

    private val apiUrl = "https://myprop.my.id/api/register" // Sesuaikan dengan Laravel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Hubungkan view
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        signUpButton = findViewById(R.id.signUpButton)

        // Klik tombol daftar
        signUpButton.setOnClickListener {
            performSignUp()
        }
    }

    private fun performSignUp() {
        val name = usernameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString().trim()

        // Validasi input kosong
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Validasi format email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Validasi panjang password
        if (password.length < 8) {
            Toast.makeText(this, "Password minimal 8 karakter", Toast.LENGTH_SHORT).show()
            return
        }

        // Validasi konfirmasi password
        if (password != confirmPassword) {
            Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        // Buat data JSON untuk dikirim
        val requestData = JSONObject().apply {
            put("name", name)
            put("email", email)
            put("password", password)
            put("password_confirmation", confirmPassword)
            put("phone_number", phoneNumber)
        }

        // Kirim request
        val request = JsonObjectRequest(
            Request.Method.POST, apiUrl, requestData,
            { response ->
                Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            },
            { error ->
                val errorBody = error.networkResponse?.data?.let {
                    String(it)
                } ?: error.message
                Toast.makeText(this, "Gagal: $errorBody", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}
