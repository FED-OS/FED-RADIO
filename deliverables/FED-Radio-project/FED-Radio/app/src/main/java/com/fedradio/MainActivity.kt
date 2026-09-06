package com.fedradio

import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

/**
 * FED-Radio — MainActivity
 *
 * The phone / tablet / desktop view. Loads the LOCAL index.html "brain"
 * from app assets — no server, no external fetch, no third-party SDKs.
 *
 * Tapping a station card opens the YouTube link in the system browser /
 * YouTube app so the WebView itself stays 100% local content.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)
        webView.settings.apply {
            javaScriptEnabled = true          // needed for the tap-to-open script in index.html
            domStorageEnabled = true
        }
        webView.setBackgroundColor(resources.getColor(R.color.paper, theme))
        webView.webViewClient = LocalBrainClient()

        // Load the in-house HTML brain — the single source of truth.
        webView.loadUrl("file:///android_asset/index.html")
    }

    /**
     * Keeps every non-local navigation OUT of the WebView: station taps
     * redirect to YouTube externally, everything else stays on the local file.
     */
    private inner class LocalBrainClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            val url = request.url
            // file:// stays inside the app; anything else goes to the OS.
            if (url.scheme == "file") return false
            return try {
                startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, url))
                true
            } catch (e: android.content.ActivityNotFoundException) {
                true // swallow unhandleable links — never crash the dashboard
            }
        }
    }

    /** Back button: walk WebView history before exiting the app. */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
