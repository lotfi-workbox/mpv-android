package `is`.xyz.mpv

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var homeFragment: HomeFragment
    private var doubleBackToExitPressedOnce = false // Flag to track if back button was pressed twice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setTitle(R.string.mpv_activity)

        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, homeFragment)
                .commit()
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // If no media is playing, show a toast message
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed() // Exit the app
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()

        // Reset the flag after 2 seconds to allow for next back press
        android.os.Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}
