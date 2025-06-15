package proyek1.mobile.uas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import proyek1.mobile.uas.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val baseUrl = "https://myprop.my.id/api/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener {
            val email = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val jsonBody = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            val request = object : JsonObjectRequest(
                Method.POST, baseUrl, jsonBody,
                JsonObjectRequest@{ response ->
                    val token = response.optString("access_token", null)
                    val user = response.optJSONObject("user")
                    val name = user?.optString("name", "Pengguna") ?: "Pengguna"

                    if (token == null) {
                        Toast.makeText(this, "Login gagal: token tidak ditemukan", Toast.LENGTH_SHORT).show()
                        return@JsonObjectRequest
                    }

                    Toast.makeText(this, "Selamat datang, $name", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, PropertyListActivity::class.java)
                    intent.putExtra("token", token)
                    startActivity(intent)
                    finish()
                },
                { error ->
                    val message = try {
                        val data = error.networkResponse?.data
                        val responseBody = data?.let { String(it) } ?: ""

                        when {
                            responseBody.startsWith("<!DOCTYPE") || responseBody.startsWith("<html") -> {
                                "Email atau password salah"
                            }
                            responseBody.isNotEmpty() -> {
                                val json = JSONObject(responseBody)
                                when {
                                    json.has("message") -> json.getString("message")
                                    json.has("error") -> json.getString("error")
                                    else -> "Terjadi kesalahan login"
                                }
                            }
                            else -> "Tidak ada respons dari server"
                        }
                    } catch (e: Exception) {
                        "Email atau password salah"
                    }

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    return headers
                }
            }

            Volley.newRequestQueue(this).add(request)
        }
    }
}
