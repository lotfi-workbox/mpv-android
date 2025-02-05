package `is`.xyz.mpv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress

class PlayerActivity : AppCompatActivity() {

    private lateinit var playerLauncher: ActivityResultLauncher<Intent>
    private var isPlaying = false // Track if media is playing

    @Suppress("DEPRECATION")
    private val retryHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player) // Inflate the activity layout (your XML file)

        playerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (isPlaying && result.resultCode == RESULT_CANCELED) {
                retryPlayFile()
            } else if (result.resultCode == RESULT_OK) {
                finish()
            }
        }

        // Call pingHost method to check the connectivity and play file
        pingHost(LOCAL_ADDRESS) { isReachable ->
            if (isReachable) {
                playFile(LOCAL_RTSP_ADDRESS)
            } else {
                // Show a toast if there is no route to the IP
                Toast.makeText(this, "No route to 10.42.0.1", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playFile(@Suppress("SameParameterValue") filepath: String) {
        val i: Intent
        if (filepath.startsWith("content://")) {
            i = Intent(Intent.ACTION_VIEW, Uri.parse(filepath))
        } else {
            i = Intent()
            i.putExtra("filepath", filepath)
        }
        i.setClass(this, MPVActivity::class.java)
        playerLauncher.launch(i)

        isPlaying = true // Set isPlaying flag to true when the file starts playing
    }

    private fun retryPlayFile() {
        retryHandler.postDelayed({
            playFile(LOCAL_RTSP_ADDRESS)
        }, 2000) // Retry after 2 seconds
    }

    // Method to ping the host and check if reachable (now using Coroutine)
    private fun pingHost(@Suppress("SameParameterValue") host: String, callback: (Boolean) -> Unit) {
        // Run the network check on a background thread
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val inetAddress = InetAddress.getByName(host)
                val isReachable = inetAddress.isReachable(2000) // Timeout after 2 seconds

                // Return result to the main thread
                withContext(Dispatchers.Main) {
                    callback(isReachable)
                }
            } catch (e: IOException) {
                e.printStackTrace()

                // Return false in case of error
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isPlaying = false
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        isPlaying = false
        finish()
    }


    companion object {
        const val LOCAL_ADDRESS = "10.42.0.1"
        const val LOCAL_RTSP_ADDRESS = "rtsp://${LOCAL_ADDRESS}:8554/stream0"
    }
}
