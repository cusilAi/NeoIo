package com.worldsim.app

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import java.io.ByteArrayInputStream

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = FrameLayout(this)
        root.setBackgroundColor(Color.rgb(14, 21, 32))

        val web = WebView(this)
        web.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = false
        web.settings.allowContentAccess = false

        web.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                val path = request.url.path ?: return null
                if (request.url.host != "worldsim.local") return null
                val asset = when (path) {
                    "/", "/index.html" -> "worldsim/index.html"
                    "/countries.geojson" -> "worldsim/countries.geojson"
                    else -> return null
                }
                return try {
                    WebResourceResponse(
                        if (asset.endsWith(".geojson")) "application/geo+json" else "text/html",
                        "UTF-8",
                        assets.open(asset)
                    )
                } catch (_: Exception) {
                    WebResourceResponse(
                        "text/plain", "UTF-8",
                        ByteArrayInputStream("Asset not found".toByteArray())
                    )
                }
            }
        }

        root.addView(web)
        setContentView(root)
        web.loadUrl("https://worldsim.local/index.html")
    }
}
