package com.example.untitled

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup the Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Hide title in the Toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Define top-level destinations (no back arrow)
        val appBarConfiguration = androidx.navigation.ui.AppBarConfiguration(
            setOf(
                R.id.nav_dashboard,
                R.id.nav_finance,
                R.id.nav_lifestyle,
                R.id.nav_profile
            )
        )
        
        // Setup ActionBar with NavController to handle back button
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Ensure title stays hidden after navigation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.setDisplayShowTitleEnabled(false)
            
            when (destination.id) {
                R.id.splashFragment,
                R.id.introFragment,
                R.id.signupFragment,
                R.id.loginFragment,
                R.id.otpFragment -> {
                    bottomNav.visibility = View.GONE
                    supportActionBar?.hide()
                }
                else -> {
                    bottomNav.visibility = View.VISIBLE
                    supportActionBar?.show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
