package proyek1.mobile.uas

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaymentWebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_payment)

        webView = findViewById(R.id.webView)
        val url = intent.getStringExtra("payment_url") ?: ""

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                Log.d("WebView", "Redirecting to: $url")

                if (url.contains("example.com")) {
                    handleTransactionResult(url)
                    return true
                }

                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Loaded: $url")

                if (url != null && url.contains("example.com")) {
                    handleTransactionResult(url)
                }
            }

            private fun handleTransactionResult(url: String) {
                val uri = Uri.parse(url)
                val status = uri.getQueryParameter("transaction_status") ?: "unknown"

                val message = when (status) {
                    "settlement", "capture" -> "Pembayaran berhasil"
                    "pending" -> "Pembayaran masih pending"
                    "deny", "cancel", "expire", "failure" -> "Pembayaran gagal/dibatalkan"
                    else -> "Status tidak diketahui: $status"
                }

                Toast.makeText(this@PaymentWebViewActivity, message, Toast.LENGTH_SHORT).show()

                // Kembali ke PropertyListActivity
                val intent = Intent(this@PaymentWebViewActivity, PropertyListActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        webView.loadUrl(url)
    }
}
