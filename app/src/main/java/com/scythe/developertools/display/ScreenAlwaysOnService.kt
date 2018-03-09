package com.scythe.developertools.display

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.Binder
import android.os.IBinder
import android.os.Build
import android.os.PowerManager
import com.scythe.developertools.LocalBinder
import com.scythe.developertools.R
import android.os.BatteryManager


class ScreenAlwaysOnService : Service() {

    companion object{
        var running : Boolean = false
        const val ALLOW_DIMMING_TOGGLE : String = "ALLOW_DIMMING_TOGGLE"
        const val STOP_WHEN_BATTERY_LOW_TOGGLE : String = "STOP_WHEN_BATTERY_LOW_TOGGLE"
        const val TURN_OFF_ACTION : String = "TURN_OFF"
    }

    private val notificationId : Int = 204

    private val CHANNEL_ID : String = "alwaysOn.Notifications"

    private lateinit var wakeLock : PowerManager.WakeLock

    private var listeners : ArrayList<ScreenAlwaysOnServiceListener> = ArrayList()

    private var allowDimming: Boolean = false
    private var stopWhenBatteryLow: Boolean = false

    private var paused : Boolean = false


    override fun onCreate() {
        super.onCreate()
        val screenOnPrefs = getSharedPreferences(DisplayToolsActivity.SCREEN_ON_PREFERENCES,
                Context.MODE_PRIVATE)
        allowDimming = screenOnPrefs.getBoolean(DisplayToolsActivity.ALLOW_DIMMING, false)
        stopWhenBatteryLow = screenOnPrefs.getBoolean(DisplayToolsActivity.STOP_WHEN_BATTERY_LOW,
                false)
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
                running = false
                if(!paused) {
                    wakeLock.release()
                }
                paused = false
                notifyListeners()
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
                configurationChanges(intent?.getBooleanExtra(ALLOW_DIMMING_TOGGLE, false) ?: false,
                        intent?.getBooleanExtra(STOP_WHEN_BATTERY_LOW_TOGGLE, false) ?: false)
                running = true
                paused = false
                createNotificationChannels()
                startService()
                postStartService()
            }
        }
        return START_STICKY
    }

    private fun notifyListeners() {
        saveSettingsToSharedPreferences()
        for(listener in ArrayList(listeners)) {
            listener.screenAlwaysOnStateChanged(running, allowDimming, stopWhenBatteryLow, paused)
        }
    }

    private fun saveSettingsToSharedPreferences() {
        val screenOnPrefs = getSharedPreferences(DisplayToolsActivity.SCREEN_ON_PREFERENCES, Context.MODE_PRIVATE)
        val editor = screenOnPrefs.edit()
        editor.putBoolean(DisplayToolsActivity.ALLOW_DIMMING, allowDimming)
        editor.putBoolean(DisplayToolsActivity.STOP_WHEN_BATTERY_LOW, stopWhenBatteryLow)
        editor.apply()
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
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(pendingIntent)
                //TODO("Add proper icons")
                .addAction(Notification.Action.Builder(
                        Icon.createWithResource(
                                this, android.R.drawable.ic_lock_silent_mode_off),
                        getString(R.string.screen_always_on_action_turn_off),
                        turnOffPendingIntent)
                        .build())
                .addAction(Notification.Action.Builder(
                        Icon.createWithResource(
                                this, android.R.drawable.ic_lock_silent_mode_off),
                        allowDimmingActionString,
                        allowDimmingPendingIntent)
                        .build())
                .addAction(Notification.Action.Builder(
                        Icon.createWithResource(
                                this, android.R.drawable.ic_lock_silent_mode_off),
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
        stopForeground(true)
        try {
            unregisterReceiver(batteryLevelReceiver)
        } catch (e: Exception) {
            // TODO("This should be removed")
        }
        running = false
    }

    fun registerListener(listener : ScreenAlwaysOnServiceListener) {
        listeners.add(listener)
        listener.screenAlwaysOnStateChanged(running, allowDimming, stopWhenBatteryLow, paused)
    }

    fun unregisterListener(listener : ScreenAlwaysOnServiceListener) {
        listeners.remove(listener)
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
            notifyListeners()
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
                wakeLock.release()
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
            pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Screen always on")
        } else {
            pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Screen always on")
        }
        wakeLock.acquire()
        makeServiceForeground()
        notifyListeners()
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
                wakeLock.release()
            }
            startService()
        }
    }


    interface ScreenAlwaysOnServiceListener {
        fun screenAlwaysOnStateChanged(state : Boolean, allowDimming: Boolean,
                                       stopWhenBatteryLow: Boolean, paused: Boolean)
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
}