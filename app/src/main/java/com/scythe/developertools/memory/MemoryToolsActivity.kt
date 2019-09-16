package com.scythe.developertools.memory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.scythe.developertools.R
import com.scythe.developertools.setupToolbar
import kotlinx.android.synthetic.main.activity_memory_tools.*
import kotlinx.android.synthetic.main.toolbar.*

class MemoryToolsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO: Add foreground service to monitor execution and not monitor execution in activity where user can leave easily
        //TODO: Fix strings organization
        setContentView(R.layout.activity_memory_tools)
        setupToolbar(getString(R.string.memory_feature_fill))
        toolbar_title.transitionName = "zombie_app_title"
        start_filling_memory_button.text = getString(R.string.start_filling_memory)
        start_filling_memory_button.setOnClickListener {
            MemoryFillingHelperService.test = 5
            startActivity(Intent(this, MemoryFillingActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
