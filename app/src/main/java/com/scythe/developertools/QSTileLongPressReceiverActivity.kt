package com.scythe.developertools

import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.scythe.developertools.display.DisplayToolsActivity
import com.scythe.developertools.display.ScreenAlwaysOnQSTileService

/**
 * Created by Camilo on 5/4/2018.
 */
class QSTileLongPressReceiverActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent.getParcelableExtra<ComponentName>(Intent.EXTRA_COMPONENT_NAME)?.className) {
            ScreenAlwaysOnQSTileService::class.java.name -> {
                val stackBuilder = TaskStackBuilder.create(this)
                stackBuilder.addNextIntent(Intent(this, MainActivity::class.java))
                stackBuilder.addNextIntent(Intent(this, DisplayToolsActivity::class.java))
                stackBuilder.startActivities()
            }
            else -> startInfoPage()
        }
        finish()
    }

    private fun startInfoPage() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }
}