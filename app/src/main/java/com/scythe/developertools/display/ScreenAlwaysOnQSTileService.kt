package com.scythe.developertools.display

import android.content.*
import android.graphics.drawable.Icon
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.scythe.developertools.R

/**
 * Created by Camilo on 5/4/2018.
 */
class ScreenAlwaysOnQSTileService: TileService() {

    companion object {
        const val ACTION_UPDATE_TILE = "com.scythe.developertools.display.ACTION.UPDATE_TILE"
    }

    private val receiver = UpdateTileReceiver()

    override fun onBind(intent: Intent?): IBinder? {
        requestListeningState(applicationContext,
                ComponentName(this, ScreenAlwaysOnQSTileService::class.java))
        sendBroadcast(Intent(ACTION_UPDATE_TILE))
        return super.onBind(intent)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        if (qsTile.state == Tile.STATE_INACTIVE) {
            val screenAlwaysOnServiceIntent =
                    Intent(this, ScreenAlwaysOnService::class.java)
            startForegroundService(
                    screenAlwaysOnServiceIntent)
            switchOn()
        } else {
            stopService(Intent(this, ScreenAlwaysOnService::class.java))
            switchOff()
        }
        qsTile.updateTile()
    }

    override fun onStartListening() {
        registerReceiver(receiver, IntentFilter(ACTION_UPDATE_TILE))
        super.onStartListening()
        updateTile()
    }

    override fun onStopListening() {
        unregisterReceiver(receiver)
        super.onStopListening()
    }

    private fun updateTile() {
        val screenOnPrefs = getSharedPreferences(DisplayToolsActivity.SCREEN_ON_PREFERENCES,
                Context.MODE_PRIVATE)
        if (screenOnPrefs.getBoolean(DisplayToolsActivity.SCREEN_ALWAYS_ON, false)) {
            switchOn()
        } else {
            switchOff()
        }
        qsTile.updateTile()
    }

    private fun switchOn() {
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.label = getString(R.string.display_screen_always_on_tile_label_on)
        qsTile.icon = Icon.createWithResource(this, R.drawable.ic_android_bulb_on)
    }

    private fun switchOff() {
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.label = getString(R.string.display_screen_always_on_tile_label_off)
        qsTile.icon = Icon.createWithResource(this, R.drawable.ic_android_bulb_off)
    }

    inner class UpdateTileReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateTile()
        }
    }
}