package com.scythe.developertools.memory

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.scythe.developertools.R
import com.scythe.developertools.setupToolbar
import kotlinx.android.synthetic.main.activity_memory_tools.*
import kotlinx.android.synthetic.main.fullscreen_dialog_toolbar.*
import androidx.core.util.Pair as UtilPair

class MemoryToolsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAnimations()
        //TODO: Add foreground service to monitor execution and not monitor execution in activity where user can leave easily
        //TODO: Fix strings organization
        setContentView(R.layout.activity_memory_tools)
        setupToolbar(title = getString(R.string.memory_feature_fill),
                backgroundColor = getColor(android.R.color.transparent),
                backDrawableResId = android.R.drawable.ic_menu_close_clear_cancel)
        toolbar_title.transitionName = "zombie_app_title"
        start_filling_memory_button.text = getString(R.string.start_filling_memory)
        start_filling_memory_button.setOnClickListener {
            MemoryFillingHelperService.test = 5
            toolbar.transitionName = "toolbar"
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    UtilPair.create<View, String>(toolbar, toolbar.transitionName))
            startActivity(Intent(this, MemoryFillingActivity::class.java),
                    options.toBundle())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun configureAnimations() {
        with(window) {
            exitTransition = Fade()
        }
    }
}
