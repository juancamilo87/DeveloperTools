package com.scythe.developertools

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = findNavController(R.id.nav_fragment)
        navigation_bar.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp() =
            findNavController(R.id.nav_fragment).navigateUp()
}
