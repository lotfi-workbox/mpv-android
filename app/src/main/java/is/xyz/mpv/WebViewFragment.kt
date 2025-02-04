package `is`.xyz.mpv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class WebViewFragment : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var webView: WebView
    private lateinit var noConnectionLayout: View
    private lateinit var connectivityReceiver: ConnectivityReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_webview, container, false)

        // Initialize views
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout)
        webView = rootView.findViewById(R.id.webView)
        noConnectionLayout = rootView.findViewById(R.id.noConnectionLayout)

        // Initialize WebView settings
        webView.settings.javaScriptEnabled = true
        webView.settings.setDomStorageEnabled(true)
        webView.settings.setLoadsImagesAutomatically(true)

        // Load the initial URL
        webView.loadUrl("http://10.42.0.1")

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            webView.reload() // Reload the WebView on swipe
        }

        // Set WebViewClient to handle page loading events
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                swipeRefreshLayout.isRefreshing = true
                noConnectionLayout.visibility = View.GONE // Hide no connection background
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                swipeRefreshLayout.isRefreshing = false
                showNoConnection() // Show "no connection" background on error
            }
        }

        // Register the network connectivity receiver
        connectivityReceiver = ConnectivityReceiver()
        requireActivity().registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        return rootView
    }

    // Check if network is available
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Show no connection background
    private fun showNoConnection() {
        noConnectionLayout.visibility = View.VISIBLE
        webView.visibility = View.GONE
    }

    // BroadcastReceiver to listen for network changes
    private inner class ConnectivityReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (isNetworkAvailable()) {
                // Hide the no connection background and reload WebView
                noConnectionLayout.visibility = View.GONE
                webView.visibility = View.VISIBLE
                webView.reload() // Force a reload to reestablish the connection
            } else {
                // Show no connection background
                showNoConnection()
            }
        }
    }

    // Unregister the receiver when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().unregisterReceiver(connectivityReceiver)
    }
}

