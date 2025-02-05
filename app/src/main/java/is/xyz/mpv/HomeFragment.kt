package `is`.xyz.mpv

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragmentContainer: FrameLayout

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
        @Suppress("DEPRECATION")
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(WebViewFragment()) // Load Home fragment
                    true
                }
                R.id.nav_player -> {
                    val intent = Intent(requireContext(), PlayerActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
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
}
