package com.scythe.developertools.display

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.scythe.developertools.R
import com.scythe.developertools.setupToolbar
import kotlinx.android.synthetic.main.activity_display_tools.*
import kotlinx.android.synthetic.main.toolbar.*

class DisplayToolsActivity : AppCompatActivity() {

    companion object {
        const val SCREEN_ON_PREFERENCES : String = "SCREEN_ON_PREFERENCES"
        const val ALLOW_DIMMING : String = "ALLOW_DIMMING"
        const val STOP_WHEN_BATTERY_LOW : String = "STOP_WHEN_BATTERY_LOW"
        const val SCREEN_ALWAYS_ON : String = "SCREEN_ALWAYS_ON"
        const val ACTION_SCREEN_ALWAYS_ON_CONFIGURATION_CHANGE : String =
                "ACTION_SCREEN_ALWAYS_ON_CONFIGURATION_CHANGE"
    }

    private val receiver: ScreenAlwaysOnConfigurationChange = ScreenAlwaysOnConfigurationChange()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_tools)
        setupToolbar(title = getString(R.string.display_feature_lock),
                backgroundColor = getColor(R.color.card_background),
                backDrawableResId = android.R.drawable.ic_menu_close_clear_cancel)
        toolbar_title.transitionName = "lock_screen_title"
        initDefaults()
        screen_always_on_toggle.setOnCheckedChangeListener { _, isOn ->
            if (isOn) {
                val screenAlwaysOnServiceIntent =
                        Intent(this, ScreenAlwaysOnService::class.java)
                screenAlwaysOnServiceIntent.putExtra(ScreenAlwaysOnService.ALLOW_DIMMING_TOGGLE,
                        allow_dimming_switch.isChecked)
                screenAlwaysOnServiceIntent.putExtra(
                        ScreenAlwaysOnService.STOP_WHEN_BATTERY_LOW_TOGGLE,
                        battery_switch.isChecked)

                startForegroundService(
                        screenAlwaysOnServiceIntent)
            } else {
                stopService(Intent(this, ScreenAlwaysOnService::class.java))
            }
        }
        allow_dimming_switch.setOnCheckedChangeListener { _, _ ->
            notifyScreenAlwaysOnServiceOfConfigurationChange()
        }

        battery_switch.setOnCheckedChangeListener { _, _ ->
            notifyScreenAlwaysOnServiceOfConfigurationChange()
        }
    }

    private fun initDefaults() {
        val screenOnPrefs = getSharedPreferences(SCREEN_ON_PREFERENCES, Context.MODE_PRIVATE)
        allow_dimming_switch.isChecked = screenOnPrefs.getBoolean(ALLOW_DIMMING, false)
        battery_switch.isChecked = screenOnPrefs.getBoolean(STOP_WHEN_BATTERY_LOW, false)
        screen_always_on_toggle.isChecked = screenOnPrefs.getBoolean(SCREEN_ALWAYS_ON, false)
    }

    fun screenAlwaysOnStateChanged(state: Boolean, allowDimming: Boolean,
                                            stopWhenBatteryLow: Boolean, paused: Boolean) {
        screen_always_on_toggle.isChecked = state
        allow_dimming_switch.isChecked = allowDimming
        battery_switch.isChecked = stopWhenBatteryLow
        screen_always_on_paused.visibility = if(paused) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun notifyScreenAlwaysOnServiceOfConfigurationChange() {
        saveSettingsToSharedPreferences()
        sendBroadcast(Intent(ACTION_SCREEN_ALWAYS_ON_CONFIGURATION_CHANGE))
    }

    private fun saveSettingsToSharedPreferences() {
        val screenOnPrefs = getSharedPreferences(SCREEN_ON_PREFERENCES, Context.MODE_PRIVATE)
        val editor = screenOnPrefs.edit()
        editor.putBoolean(ALLOW_DIMMING, allow_dimming_switch.isChecked)
        editor.putBoolean(STOP_WHEN_BATTERY_LOW, battery_switch.isChecked)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver,
                IntentFilter(ScreenAlwaysOnService.ACTION_SCREEN_ALWAYS_ON_SERVICE_CONFIGURATION_CHANGE))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    inner class ScreenAlwaysOnConfigurationChange: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            screenAlwaysOnStateChanged(p1?.getBooleanExtra(ScreenAlwaysOnService.RUNNING, false) ?: false,
                    p1?.getBooleanExtra(ALLOW_DIMMING, false) ?: false,
                    p1?.getBooleanExtra(STOP_WHEN_BATTERY_LOW, false) ?: false,
                    p1?.getBooleanExtra(ScreenAlwaysOnService.PAUSED, false) ?: false)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
