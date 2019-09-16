package com.scythe.developertools.memory

import android.app.Service
import android.content.Intent
import android.os.*
import java.util.*
import android.os.HandlerThread


/**
 * Created by Camilo on 6/16/2018.
 */
sealed class MemoryFillingService : Service() {

    companion object {
        const val MSG_STOP = 1
    }

    private lateinit var memoryInfoHelper : MemoryInfoHelper

    private lateinit var serviceLooper : Looper
    private lateinit var serviceHandler : ServiceHandler

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message?) {
            try {
                running = true
                var allocated = 0
                while (running) {
                    if (memoryInfoHelper.isMemoryAvailable()) {
                        val bytes = ByteArray(1024 * 1024 * 5)
                        Random().nextBytes(bytes)
                        allocations.add(bytes)
                        allocated += 5
                        try {
                            Thread.sleep(1000)
                        } catch (e : InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e : InterruptedException) {
                Thread.currentThread().interrupt()
            }
            stopSelf()
        }
    }

    override fun onCreate() {
        super.onCreate()
        memoryInfoHelper = MemoryInfoHelper(applicationContext)
        val thread = HandlerThread("Thread Name",
                Process.THREAD_PRIORITY_BACKGROUND)
        //Start the thread//
        thread.start()

        serviceLooper = thread.looper
        serviceHandler = ServiceHandler(serviceLooper)
    }

    private class IncomingHandler(val memoryFillingService: MemoryFillingService) : Handler() {
        override fun handleMessage(msg: Message) {
            when(msg.what)  {
                MSG_STOP -> {
                    memoryFillingService.running = false
                }
            }
        }
    }

    private var running = false
    //TODO: Fix memory leak to use weak reference
    private val messenger = Messenger(IncomingHandler(this))
    private val allocations = ArrayList<ByteArray>()


    override fun onBind(p0: Intent?): IBinder {
        return messenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        running = false
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val msg = serviceHandler.obtainMessage()
        serviceHandler.sendMessage(msg)
        return START_NOT_STICKY
    }

    class MemoryFillingService1 : MemoryFillingService()
    class MemoryFillingService2 : MemoryFillingService()
    class MemoryFillingService3 : MemoryFillingService()
    class MemoryFillingService4 : MemoryFillingService()
    class MemoryFillingService5 : MemoryFillingService()
    class MemoryFillingService6 : MemoryFillingService()
    class MemoryFillingService7 : MemoryFillingService()
    class MemoryFillingService8 : MemoryFillingService()
    class MemoryFillingService9 : MemoryFillingService()
    class MemoryFillingService10 : MemoryFillingService()
    class MemoryFillingService11 : MemoryFillingService()
    class MemoryFillingService12 : MemoryFillingService()
    class MemoryFillingService13 : MemoryFillingService()
    class MemoryFillingService14 : MemoryFillingService()
    class MemoryFillingService15 : MemoryFillingService()
    class MemoryFillingService16 : MemoryFillingService()
    class MemoryFillingService17 : MemoryFillingService()
    class MemoryFillingService18 : MemoryFillingService()
    class MemoryFillingService19 : MemoryFillingService()
    class MemoryFillingService20 : MemoryFillingService()
    class MemoryFillingService21 : MemoryFillingService()
    class MemoryFillingService22 : MemoryFillingService()
    class MemoryFillingService23 : MemoryFillingService()
    class MemoryFillingService24 : MemoryFillingService()
    class MemoryFillingService25 : MemoryFillingService()
    class MemoryFillingService26 : MemoryFillingService()
    class MemoryFillingService27 : MemoryFillingService()
    class MemoryFillingService28 : MemoryFillingService()
    class MemoryFillingService29 : MemoryFillingService()
    class MemoryFillingService30 : MemoryFillingService()
    class MemoryFillingService31 : MemoryFillingService()
    class MemoryFillingService32 : MemoryFillingService()
    class MemoryFillingService33 : MemoryFillingService()
    class MemoryFillingService34 : MemoryFillingService()
    class MemoryFillingService35 : MemoryFillingService()
    class MemoryFillingService36 : MemoryFillingService()
    class MemoryFillingService37 : MemoryFillingService()
    class MemoryFillingService38 : MemoryFillingService()
    class MemoryFillingService39 : MemoryFillingService()
    class MemoryFillingService40 : MemoryFillingService()
    class MemoryFillingService41 : MemoryFillingService()
    class MemoryFillingService42 : MemoryFillingService()
    class MemoryFillingService43 : MemoryFillingService()
    class MemoryFillingService44 : MemoryFillingService()
    class MemoryFillingService45 : MemoryFillingService()
    class MemoryFillingService46 : MemoryFillingService()
    class MemoryFillingService47 : MemoryFillingService()
    class MemoryFillingService48 : MemoryFillingService()
    class MemoryFillingService49 : MemoryFillingService()
    class MemoryFillingService50 : MemoryFillingService()
    class MemoryFillingService51 : MemoryFillingService()
    class MemoryFillingService52 : MemoryFillingService()
    class MemoryFillingService53 : MemoryFillingService()
    class MemoryFillingService54 : MemoryFillingService()
    class MemoryFillingService55 : MemoryFillingService()
    class MemoryFillingService56 : MemoryFillingService()
    class MemoryFillingService57 : MemoryFillingService()
    class MemoryFillingService58 : MemoryFillingService()
    class MemoryFillingService59 : MemoryFillingService()
    class MemoryFillingService60 : MemoryFillingService()
    class MemoryFillingService61 : MemoryFillingService()
    class MemoryFillingService62 : MemoryFillingService()
    class MemoryFillingService63 : MemoryFillingService()
    class MemoryFillingService64 : MemoryFillingService()
    class MemoryFillingService65 : MemoryFillingService()
    class MemoryFillingService66 : MemoryFillingService()
    class MemoryFillingService67 : MemoryFillingService()
    class MemoryFillingService68 : MemoryFillingService()
    class MemoryFillingService69 : MemoryFillingService()
    class MemoryFillingService70 : MemoryFillingService()
    class MemoryFillingService71 : MemoryFillingService()
    class MemoryFillingService72 : MemoryFillingService()
    class MemoryFillingService73 : MemoryFillingService()
    class MemoryFillingService74 : MemoryFillingService()
    class MemoryFillingService75 : MemoryFillingService()
    class MemoryFillingService76 : MemoryFillingService()
    class MemoryFillingService77 : MemoryFillingService()
    class MemoryFillingService78 : MemoryFillingService()
    class MemoryFillingService79 : MemoryFillingService()
    class MemoryFillingService80 : MemoryFillingService()
    class MemoryFillingService81 : MemoryFillingService()
    class MemoryFillingService82 : MemoryFillingService()
    class MemoryFillingService83 : MemoryFillingService()
    class MemoryFillingService84 : MemoryFillingService()
    class MemoryFillingService85 : MemoryFillingService()
    class MemoryFillingService86 : MemoryFillingService()
    class MemoryFillingService87 : MemoryFillingService()
    class MemoryFillingService88 : MemoryFillingService()
    class MemoryFillingService89 : MemoryFillingService()
    class MemoryFillingService90 : MemoryFillingService()
    class MemoryFillingService91 : MemoryFillingService()
    class MemoryFillingService92 : MemoryFillingService()
    class MemoryFillingService93 : MemoryFillingService()
    class MemoryFillingService94 : MemoryFillingService()
    class MemoryFillingService95 : MemoryFillingService()
    class MemoryFillingService96 : MemoryFillingService()
    class MemoryFillingService97 : MemoryFillingService()
    class MemoryFillingService98 : MemoryFillingService()
    class MemoryFillingService99 : MemoryFillingService()
}