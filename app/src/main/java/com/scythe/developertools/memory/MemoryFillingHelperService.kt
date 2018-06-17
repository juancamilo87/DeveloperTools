package com.scythe.developertools.memory

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger

/**
 * Created by Camilo on 6/16/2018.
 */
class MemoryFillingHelperService : Service() {

    companion object {
        const val MSG_CHECK_STATIC_VARIABLE = 1
        var test = 1
    }

    private val messenger = Messenger(HelperHandler())

    inner class HelperHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            when(msg?.what) {
                MSG_CHECK_STATIC_VARIABLE -> {
                    msg.replyTo.send(Message.obtain(null,
                            MemoryFillingActivity.SEND_STATIC_VARIABLE, test, 0))
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        return messenger.binder
    }
}