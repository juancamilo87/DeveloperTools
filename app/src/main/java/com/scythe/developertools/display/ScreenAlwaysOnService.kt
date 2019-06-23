package com.scythe.developertools.display

import android.app.*
import android.content.*
import android.graphics.drawable.Icon
import android.os.Binder
import android.os.IBinder
import android.os.Build
import android.os.PowerManager
import com.scythe.developertools.LocalBinder
import com.scythe.developertools.R
import android.os.BatteryManager
import android.service.quicksettings.TileService


class ScreenAlwaysOnService : Service() {

    companion object{
        var running : Boolean = false
        const val ALLOW_DIMMING_TOGGLE : String = "ALLOW_DIMMING_TOGGLE"
        const val STOP_WHEN_BATTERY_LOW_TOGGLE : String = "STOP_WHEN_BATTERY_LOW_TOGGLE"
        const val TURN_OFF_ACTION : String = "TURN_OFF"
        const val PAUSED : String = "SCREEN_ALWAYS_ON_SERVICE_PAUSED"
        const val RUNNING : String = "SCREEN_ALWAYS_ON_SERVICE_RUNNING"
        const val ACTION_SCREEN_ALWAYS_ON_SERVICE_CONFIGURATION_CHANGE : String =
                "ACTION_SCREEN_ALWAYS_ON_SERVICE_CONFIGURATION_CHANGE"
    }

    private val notificationId : Int = 204

    private val CHANNEL_ID : String = "alwaysOn.Notifications"

    private lateinit var wakeLock : PowerManager.WakeLock

    private var allowDimming: Boolean = false
    private var stopWhenBatteryLow: Boolean = false

    private var paused : Boolean = false
    private val receiver: ConfigurationChangeReceiver = ConfigurationChangeReceiver()

    override fun onCreate() {
        super.onCreate()
        stopRunning()
        val screenOnPrefs = getSharedPreferences(DisplayToolsActivity.SCREEN_ON_PREFERENCES,
                Context.MODE_PRIVATE)
        allowDimming = screenOnPrefs.getBoolean(DisplayToolsActivity.ALLOW_DIMMING, false)
        stopWhenBatteryLow = screenOnPrefs.getBoolean(DisplayToolsActivity.STOP_WHEN_BATTERY_LOW,
                false)
        registerReceiver(receiver,
                IntentFilter(DisplayToolsActivity.ACTION_SCREEN_ALWAYS_ON_CONFIGURATION_CHANGE))
    }

    override fun onBind(p0: Intent?): IBinder? {
        return object : Binder(), LocalBinder<ScreenAlwaysOnService> {
            override fun getService() : ScreenAlwaysOnService {
                return this@ScreenAlwaysOnService
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            TURN_OFF_ACTION -> {
                stopRunning()
                if(!paused) {
                    releaseWakelock()
                }
                paused = false
                notifyOfChanges()
                stopForeground(true)
                stopSelf()
            }
            ALLOW_DIMMING_TOGGLE -> {
                configurationChanges(allowDimming = !allowDimming)
            }
            STOP_WHEN_BATTERY_LOW_TOGGLE -> {
                configurationChanges(stopWhenBatteryLow = !stopWhenBatteryLow)
            }
            else -> {
                configurationChanges(intent?.getBooleanExtra(ALLOW_DIMMING_TOGGLE,
                        getDimmingFromSharedPreferences()) ?: getDimmingFromSharedPreferences(),
                        intent?.getBooleanExtra(STOP_WHEN_BATTERY_LOW_TOGGLE,
                                getBatterySettingFromSharedPreferences()) ?:
                        getBatterySettingFromSharedPreferences())
                startRunning()
                paused = false
                createNotificationChannels()
                startService()
                postStartService()
            }
        }
        return START_STICKY
    }

    private fun notifyOfChanges() {
        saveSettingsToSharedPreferences()
        val broadcastIntent = Intent(ACTION_SCREEN_ALWAYS_ON_SERVICE_CONFIGURATION_CHANGE)
        broadcastIntent.putExtra(RUNNING, running)
        broadcastIntent.putExtra(DisplayToolsActivity.ALLOW_DIMMING, allowDimming)
        broadcastIntent.putExtra(DisplayToolsActivity.STOP_WHEN_BATTERY_LOW, stopWhenBatteryLow)
        broadcastIntent.putExtra(PAUSED, paused)
        sendBroadcast(broadcastIntent)
    }

    private fun saveSettingsToSharedPreferences() {
        val screenOnPrefs = getSharedPreferences(DisplayToolsActivity.SCREEN_ON_PREFERENCES, Context.MODE_PRIVATE)
        val editor = screenOnPrefs.edit()
        editor.putBoolean(DisplayToolsActivity.ALLOW_DIMMING, allowDimming)
        editor.putBoolean(DisplayToolsActivity.STOP_WHEN_BATTERY_LOW, stopWhenBatteryLow)
        editor.apply()
    }

    private fun getDimmingFromSharedPreferences() : Boolean {
        val screenOnPrefs = getSharedPreferences(DisplayToolsActivity.SCREEN_ON_PREFERENCES, Context.MODE_PRIVATE)
        return screenOnPrefs.getBoolean(DisplayToolsActivity.ALLOW_DIMMING, false)
    }

    private fun getBatterySettingFromSharedPreferences() : Boolean {
        val screenOnPrefs = getSharedPreferences(DisplayToolsActivity.SCREEN_ON_PREFERENCES, Context.MODE_PRIVATE)
        return screenOnPrefs.getBoolean(DisplayToolsActivity.STOP_WHEN_BATTERY_LOW, false)
    }

    private fun makeServiceForeground() {
        val intent = Intent(this, DisplayToolsActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(intent)
        val pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val turnOffIntent = Intent(this, ScreenAlwaysOnService::class.java)
        turnOffIntent.action = TURN_OFF_ACTION
        val turnOffPendingIntent = PendingIntent.getService(this, 0,
                turnOffIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val allowDimmingIntent = Intent(this, ScreenAlwaysOnService::class.java)
        allowDimmingIntent.action = ALLOW_DIMMING_TOGGLE
        val allowDimmingPendingIntent = PendingIntent.getService(this, 0,
                allowDimmingIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val allowDimmingActionString = if (allowDimming) {
            getString(R.string.screen_always_on_action_block_dimming)
        } else {
            getString(R.string.screen_always_on_action_allow_dimming)
        }

        val stopWhenBatteryLowIntent = Intent(this, ScreenAlwaysOnService::class.java)
        stopWhenBatteryLowIntent.action = STOP_WHEN_BATTERY_LOW_TOGGLE
        val stopWhenBatteryLowPendingIntent = PendingIntent.getService(this, 0,
                stopWhenBatteryLowIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val stopWhenBatterLowActionString = if (stopWhenBatteryLow) {
            getString(R.string.screen_always_on_action_battery_saver_off)
        } else {
            getString(R.string.screen_always_on_action_battery_saver_on)
        }

        val mainText = getString(if(paused) {
            R.string.screen_always_on_notification_text_paused
        } else {
            R.string.screen_always_on_notification_text
        })

        val allowDimmingText = getString(if(allowDimming) {
            R.string.screen_always_on_notification_text_allow_dimming_on
        } else {
            R.string.screen_always_on_notification_text_allow_dimming_off
        })

        val batterySaverText = getString(if(stopWhenBatteryLow) {
            R.string.screen_always_on_notification_text_battery_saver_on
        } else {
            R.string.screen_always_on_notification_text_battery_saver_off
        })

        val title = getString(R.string.display_tools_notification_title)

        val notification = Notification.Builder(this@ScreenAlwaysOnService,
                CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(mainText)
                .setSmallIcon(R.drawable.ic_bulb_on)
                .setContentIntent(pendingIntent)
                .addAction(Notification.Action.Builder(
                        Icon.createWithResource(
                                this, R.drawable.ic_bulb_off),
                        getString(R.string.screen_always_on_action_turn_off),
                        turnOffPendingIntent)
                        .build())
                .addAction(Notification.Action.Builder(
                        Icon.createWithResource(
                                this, R.drawable.ic_dim),
                        allowDimmingActionString,
                        allowDimmingPendingIntent)
                        .build())
                .addAction(Notification.Action.Builder(
                        Icon.createWithResource(
                                this, R.drawable.ic_low_battery),
                        stopWhenBatterLowActionString,
                        stopWhenBatteryLowPendingIntent)
                        .build())
                .setStyle(Notification.InboxStyle()
                        .addLine(mainText)
                        .addLine(allowDimmingText)
                        .addLine(batterySaverText)
                        .setBigContentTitle(title)
                        .setSummaryText(getString(R.string.screen_always_on)))
                .build()

        startForeground(notificationId, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        stopForeground(true)
        try {
            unregisterReceiver(batteryLevelReceiver)
        } catch (e: Exception) {
            //Do nothing
        }
        releaseWakelock()
        stopRunning()
        notifyOfChanges()
    }
    fun configurationChanges(allowDimming : Boolean = this.allowDimming,
                             stopWhenBatteryLow: Boolean = this.stopWhenBatteryLow) {
        val batteryReceiverChangePending = this.stopWhenBatteryLow != stopWhenBatteryLow
        val allowDimmingChanged = this.allowDimming != allowDimming
        val restartPending = allowDimmingChanged || batteryReceiverChangePending
        this.allowDimming = allowDimming
        this.stopWhenBatteryLow = stopWhenBatteryLow
        if (running && batteryReceiverChangePending) {
            updateBatteryReceiver()
        }
        if (restartPending) {
            notifyOfChanges()
            restartService()
        }
    }

    private fun updateBatteryReceiver() {
        if (stopWhenBatteryLow) {
            registerBatteryReceiver()
        } else {
            unregisterBatteryReceiver()
        }
    }

    private fun batteryStateLow() {
        if (running) {
            if(!paused) {
                releaseWakelock(paused)
            }
            paused = true
            makeServiceForeground()
        }
    }

    private fun batteryStateOkay() {
        if (running && paused) {
            val oldPauseState = paused
            paused = false
            restartService(oldPauseState)
        }
    }

    private fun startService() {
        val pm = getSystemService(PowerManager::class.java)
        wakeLock = if (allowDimming) {
            pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, ":screenalwayson")
        } else {
            pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, ":screenalwayson")
        }
        acquireWakelock()
        makeServiceForeground()
        notifyOfChanges()
    }

    private fun postStartService() {
        if (stopWhenBatteryLow) {
            registerBatteryReceiver()
        }
    }

    private fun registerBatteryReceiver() {
        registerReceiver(batteryLevelReceiver, IntentFilter(Intent.ACTION_BATTERY_LOW))
        registerReceiver(batteryLevelReceiver, IntentFilter(Intent.ACTION_BATTERY_OKAY))
        checkBattery()
    }

    private fun unregisterBatteryReceiver() {
        unregisterReceiver(batteryLevelReceiver)
        batteryStateOkay()
    }

    private fun checkBattery() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = registerReceiver(null, filter)
        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        (level / scale.toFloat() * 100).let {
            when {
                it <= 15 -> batteryStateLow()
                it >= 20 -> batteryStateOkay()
            }
        }
    }

    private fun restartService(paused : Boolean = this.paused) {
        if (running) {
            if (!paused) {
                releaseWakelock()
            }
            startService()
        }
    }

    private fun startRunning() {
        running = true
        TileService.requestListeningState(this,
                ComponentName(this, ScreenAlwaysOnQSTileService::class.java))
//        sendBroadcast(Intent(ScreenAlwaysOnQSTileService.ACTION_UPDATE_TILE))
    }

    private fun stopRunning() {
        running = false
        TileService.requestListeningState(this,
                ComponentName(this, ScreenAlwaysOnQSTileService::class.java))
    }

    private fun acquireWakelock() {
        wakeLock.acquire()
        val screenOnPrefs = getSharedPreferences(DisplayToolsActivity.SCREEN_ON_PREFERENCES, Context.MODE_PRIVATE)
        val editor = screenOnPrefs.edit()
        editor.putBoolean(DisplayToolsActivity.SCREEN_ALWAYS_ON, true)
        editor.apply()
    }

    private fun releaseWakelock(paused: Boolean = false) {
        try {
            wakeLock.release()
        } catch(e: Exception) {

        }
        if (!paused) {
            val screenOnPrefs = getSharedPreferences(DisplayToolsActivity.SCREEN_ON_PREFERENCES, Context.MODE_PRIVATE)
            val editor = screenOnPrefs.edit()
            editor.putBoolean(DisplayToolsActivity.SCREEN_ALWAYS_ON, false)
            editor.apply()
        }
    }

    //TODO Move this code to a generic place
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = getString(R.string.on_going_channel_name)
            val description = getString(R.string.on_going_channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private val batteryLevelReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when(p1?.action) {
                Intent.ACTION_BATTERY_LOW -> batteryStateLow()
                Intent.ACTION_BATTERY_OKAY -> batteryStateOkay()
            }
        }

    }

    inner class ConfigurationChangeReceiver: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            configurationChanges(getDimmingFromSharedPreferences(),
                    getBatterySettingFromSharedPreferences())
        }

    }
}
