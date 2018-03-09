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
import com.scythe.developertools.LocalBinder


class DisplayToolsActivity : AppCompatActivity(), ScreenAlwaysOnService.ScreenAlwaysOnServiceListener {

    lateinit var screenAlwaysOnService : ScreenAlwaysOnService
    var screenAlwaysOnServiceBound : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_tools)
        screen_always_on_toggle.setOnCheckedChangeListener { _, isOn ->
            if (isOn) {
                startForegroundService(
                        Intent(this, ScreenAlwaysOnService::class.java))
                bindToService()
            } else {
                stopService(Intent(this, ScreenAlwaysOnService::class.java))
                unbindFromService()
            }
        }
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

    override fun screenAlwaysOnStateChanged(state: Boolean) {
        screen_always_on_toggle.isChecked = state
    }
}
