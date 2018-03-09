package com.scythe.developertools.display

import android.app.*
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Binder
import android.os.IBinder
import android.os.Build
import android.os.PowerManager
import com.scythe.developertools.LocalBinder
import com.scythe.developertools.R


class ScreenAlwaysOnService : Service() {

    companion object{
        var running : Boolean = false
    }

    private val notificationId : Int = 204
    private val ACTION_EXTRA : String = "ACTION_EXTRA"
    private val TURN_OFF_ACTION : String = "TURN_OFF"

    private val CHANNEL_ID : String = "alwaysOn.Notifications"

    private lateinit var wakeLock : PowerManager.WakeLock

    private var listeners : ArrayList<ScreenAlwaysOnServiceListener> = ArrayList()

    override fun onBind(p0: Intent?): IBinder? {
        return object : Binder(), LocalBinder<ScreenAlwaysOnService> {
            override fun getService() : ScreenAlwaysOnService {
                return this@ScreenAlwaysOnService
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.getStringExtra(ACTION_EXTRA)) {
            TURN_OFF_ACTION -> {
                running = false
                wakeLock.release()
                notifyListeners()
                stopForeground(true)
                stopSelf()
            }
            else -> {
                running = true
                val pm = getSystemService(PowerManager::class.java)
                wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Screen always on")
                wakeLock.acquire()
                createNotificationChannels()
                makeServiceForeground()
                notifyListeners()
            }
        }
        return START_STICKY
    }

    private fun notifyListeners() {
        for(listener in ArrayList(listeners)) {
            listener.screenAlwaysOnStateChanged(running)
        }
    }

    private fun makeServiceForeground() {
        val intent = Intent(this, DisplayToolsActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(intent)
        val pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val turnOffIntent = Intent(this, ScreenAlwaysOnService::class.java)
        turnOffIntent.putExtra(ACTION_EXTRA, TURN_OFF_ACTION)
        val turnOffPendingIntent = PendingIntent.getService(this, 0,
                turnOffIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = Notification.Builder(this@ScreenAlwaysOnService,
                CHANNEL_ID)
                .setContentTitle(getString(R.string.display_tools_notification_title))
                .setContentText(getString(R.string.screen_always_on_notification_text))
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(pendingIntent)
                .addAction(Notification.Action.Builder(
                        Icon.createWithResource(
                                this, android.R.drawable.ic_lock_silent_mode_off),
                        getString(R.string.screen_always_on_off),
                        turnOffPendingIntent)
                        .build())
                .build()
        startForeground(notificationId, notification)
    }


    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        running = false
    }

    fun registerListener(listener : ScreenAlwaysOnServiceListener) {
        listeners.add(listener)
        listener.screenAlwaysOnStateChanged(running)
    }

    fun unregisterListener(listener : ScreenAlwaysOnServiceListener) {
        listeners.remove(listener)
    }


    interface ScreenAlwaysOnServiceListener {
        fun screenAlwaysOnStateChanged(state : Boolean)
    }

    //TODO Move this code to a generic place
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = getString(R.string.on_going_channel_name)
            val description = getString(R.string.on_going_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
