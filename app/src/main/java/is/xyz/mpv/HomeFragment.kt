package `is`.xyz.mpv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var playerLauncher: ActivityResultLauncher<Intent>
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragmentContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // we don't care about the result but remember that we've been here
//            returningFromPlayer = true
//            Log.v(TAG, "returned from player")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Set up Bottom Navigation View and Fragment Container
        bottomNavigationView = view.findViewById(R.id.bottom_navigation)
        fragmentContainer = view.findViewById(R.id.fragment_container)

        // Set default fragment to Home
        if (savedInstanceState == null) {
            loadFragment(WebViewFragment()) // Load the Home fragment by default
        }

        // Handle Bottom Navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(WebViewFragment()) // Load Home fragment
                    true
                }
                R.id.nav_player -> {
                    playFile("rtsp://10.42.0.1:8554/stream0")
                    false
                }
                else -> false
            }
        }

        return view
    }

    // Function to replace current fragment with the selected one
    private fun loadFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun playFile(filepath: String) {
        val i: Intent
        if (filepath.startsWith("content://")) {
            i = Intent(Intent.ACTION_VIEW, Uri.parse(filepath))
        } else {
            i = Intent()
            i.putExtra("filepath", filepath)
        }
        i.setClass(requireContext(), MPVActivity::class.java)
        playerLauncher.launch(i)
    }
}