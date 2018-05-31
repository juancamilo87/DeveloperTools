package com.scythe.developertools.memory

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.scythe.developertools.R
import com.scythe.developertools.memory.MemoryFillingActivity.Companion.finishedFillingMemory
import kotlinx.android.synthetic.main.activity_memory_tools.*

class MemoryToolsActivity : AppCompatActivity() {

    companion object {
        var test = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO: Make services instead of activities
        //TODO: Monitor when android starts killing background processes
        //TODO: Monitor free memory
        setContentView(R.layout.activity_memory_tools)
        start_filling_memory_button.text = getString(R.string.start_filling_memory)
        start_filling_memory_button.setOnClickListener {
            start_filling_memory_button.text = getString(R.string.running_filling_memory)
            progress_bar.visibility = View.VISIBLE
            successful_result.visibility = View.GONE
            failed_result.visibility = View.GONE
            test = 5
            startActivity(Intent(this, MemoryFillingActivity::class.java))
        }
        if (intent.getBooleanExtra(finishedFillingMemory, false)) {
            val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.killBackgroundProcesses("com.scythe.developertools")
            progress_bar.visibility = View.GONE
            if (test == 1) {
                failed_result.visibility = View.GONE
                successful_result.visibility = View.VISIBLE
            } else {
                successful_result.visibility = View.GONE
                failed_result.visibility = View.VISIBLE
            }
        }
    }


}
