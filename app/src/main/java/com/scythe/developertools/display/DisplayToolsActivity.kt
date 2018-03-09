package com.scythe.developertools.display

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.scythe.developertools.R
import kotlinx.android.synthetic.main.activity_display_tools.*
import android.content.ComponentName
import android.os.IBinder
import android.content.ServiceConnection
import android.view.View
import com.scythe.developertools.LocalBinder


class DisplayToolsActivity : AppCompatActivity(), ScreenAlwaysOnService.ScreenAlwaysOnServiceListener {

    companion object {
        const val SCREEN_ON_PREFERENCES : String = "SCREEN_ON_PREFERENCES"
        const val ALLOW_DIMMING : String = "ALLOW_DIMMING"
        const val STOP_WHEN_BATTERY_LOW : String = "STOP_WHEN_BATTERY_LOW"
    }

    private lateinit var screenAlwaysOnService : ScreenAlwaysOnService
    private var screenAlwaysOnServiceBound : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_tools)
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
                bindToService()
            } else {
                stopService(Intent(this, ScreenAlwaysOnService::class.java))
                unbindFromService()
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
    }

    override fun screenAlwaysOnStateChanged(state: Boolean, allowDimming: Boolean,
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
        if (screenAlwaysOnServiceBound) {
            screenAlwaysOnService.configurationChanges(allow_dimming_switch.isChecked,
                    battery_switch.isChecked)
        }
    }

    private fun saveSettingsToSharedPreferences() {
        val screenOnPrefs = getSharedPreferences(SCREEN_ON_PREFERENCES, Context.MODE_PRIVATE)
        val editor = screenOnPrefs.edit()
        editor.putBoolean(ALLOW_DIMMING, allow_dimming_switch.isChecked)
        editor.putBoolean(STOP_WHEN_BATTERY_LOW, battery_switch.isChecked)
        editor.apply()
    }

    override fun onStart() {
        super.onStart()
        bindToService()
    }

    override fun onStop() {
        super.onStop()
        unbindFromService()
    }

    private fun bindToService() {
        if (screenAlwaysOnServiceBound) {
            screenAlwaysOnService.registerListener(this)
        } else {
            val intent = Intent(this, ScreenAlwaysOnService::class.java)
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindFromService() {
        if (screenAlwaysOnServiceBound) {
            screenAlwaysOnService.unregisterListener(this)
            unbindService(mConnection)
            screenAlwaysOnServiceBound = false
        }
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocalBinder<*>
            screenAlwaysOnService = binder.getService() as ScreenAlwaysOnService
            screenAlwaysOnService.registerListener(this@DisplayToolsActivity)
            screenAlwaysOnServiceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            screenAlwaysOnServiceBound = false
        }
    }
}
