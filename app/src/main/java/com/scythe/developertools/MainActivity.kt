package com.scythe.developertools

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.scythe.developertools.display.DisplayToolsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        display_tools_button.setOnClickListener {
            startActivity(Intent(this, DisplayToolsActivity::class.java))
        }
    }
}
