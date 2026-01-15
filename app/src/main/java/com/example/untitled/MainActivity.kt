package com.example.untitled
import com.example.untitled.network.RetrofitClient
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(this)

        setContentView(R.layout.activity_main)

        // Toolbar setup removed as requested to remove extra headers

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Top-level screens: Show Bottom Nav
                R.id.nav_dashboard,
                R.id.nav_finance,
                R.id.nav_lifestyle,
                R.id.nav_profile -> {
                    bottomNav.visibility = View.VISIBLE
                }
                // Auth/Splash screens: Hide Bottom Nav
                R.id.splashFragment,
                R.id.introFragment,
                R.id.loginFragment,
                R.id.signupFragment,
                R.id.otpFragment -> {
                    bottomNav.visibility = View.GONE
                }
                // All other screens: Hide Bottom Nav
                else -> {
                    bottomNav.visibility = View.GONE
                }
            }
        }
    }
}
