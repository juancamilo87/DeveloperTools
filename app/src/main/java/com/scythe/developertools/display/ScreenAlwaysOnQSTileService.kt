package com.scythe.developertools.display

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.scythe.developertools.R

/**
 * Created by Camilo on 5/4/2018.
 */
class ScreenAlwaysOnQSTileService: TileService() {

    companion object {
        const val ACTION_UPDATE_TILE : String = "ACTION_UPDATE_TILE"
    }

    private val receiver: UpdateTileReceiver = UpdateTileReceiver()

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
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = getString(R.string.display_screen_always_on_tile_label_on)
        } else {
            stopService(Intent(this, ScreenAlwaysOnService::class.java))
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = getString(R.string.display_screen_always_on_tile_label_off)
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
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = getString(R.string.display_screen_always_on_tile_label_on)
        } else {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = getString(R.string.display_screen_always_on_tile_label_off)
        }
        qsTile.updateTile()
    }

    inner class UpdateTileReceiver: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            updateTile()
        }
    }
}