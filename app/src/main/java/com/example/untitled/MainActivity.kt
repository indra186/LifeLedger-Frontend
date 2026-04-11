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

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // ✅ CORRECT WAY
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.visibility =
                if (destination.id in listOf(
                        R.id.nav_dashboard,
                        R.id.nav_finance,
                        R.id.nav_lifestyle,
                        R.id.nav_profile
                    )
                ) View.VISIBLE else View.GONE
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            android.util.Log.d("NAV_DEST", destination.label.toString())
        }

    }
}



