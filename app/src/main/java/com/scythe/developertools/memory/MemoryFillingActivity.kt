package com.scythe.developertools.memory

import android.app.ActivityManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.scythe.developertools.R
import kotlinx.android.synthetic.main.activity_memory_tools_running.*

open class MemoryFillingActivity : AppCompatActivity() {

    companion object {
        const val SEND_STATIC_VARIABLE = 2
    }

    private val serviceStarterHandler = Handler()
    private lateinit var serviceStarterRunnable : Runnable

    private lateinit var memoryInfoHelper : MemoryInfoHelper

    private val handler = Handler()
    private var update = false
    private var finished = false

    private var services = mutableMapOf<Class<out Service>, Triple<Boolean, ServiceConnection, Messenger?>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_tools_running)
        setupServicesMap()
        stop_filling_memory_button.setOnClickListener{
            stop_filling_memory_button.isEnabled = false
            finished = true
            update = false
            serviceStarterHandler.removeCallbacks(serviceStarterRunnable)
            progress_bar.visibility = View.GONE
            failed_result.visibility = View.VISIBLE
            successful_result.visibility = View.GONE
            stopMemoryFilling()
        }
        startAndBindServices()
    }

    private fun setupServicesMap() {
        if (services.isEmpty()) {
            allServiceClasses.forEach {serviceClass ->
                services[serviceClass] = Triple(false,
                        object : ServiceConnection {
                            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                                val messenger = Messenger(p1)
                                services[serviceClass] =
                                        services[serviceClass]
                                                ?.copy(first = true, third = messenger)
                                        ?:Triple(true, this, messenger)
                                if (finished) {
                                    val msg = Message.obtain(null, MemoryFillingService.MSG_STOP, 0, 0)
                                    try {
                                        messenger.send(msg)
                                    } catch (e : RemoteException) {
                                        e.printStackTrace()
                                    }
                                }
                            }

                            override fun onServiceDisconnected(p0: ComponentName?) {
                                services[serviceClass] =
                                        services[serviceClass]
                                                ?.copy(first = false, third = null)
                                        ?:Triple(false, this, null)
                            }
                        }
                        , null)
            }
        }
    }

    private fun startAndBindServices() {
        val serviceChunks = services.asIterable().chunked(10)
        var counter = 0
        var servicesCounter = 0
        concurrent_services.text = "$servicesCounter"
        serviceStarterRunnable = Runnable {
            if (!finished) {
                serviceChunks[counter].forEach {
                    startService(Intent(this, it.key))
                    bindService(Intent(this, it.key), it.value.second,
                            Context.BIND_AUTO_CREATE)
                    servicesCounter++
                    concurrent_services.text = "$servicesCounter"
                }
                counter++
                if (serviceChunks.size > counter) {
                    serviceStarterHandler.postDelayed(serviceStarterRunnable, 300)
                }
            }
        }
        serviceStarterHandler.postDelayed(serviceStarterRunnable, 500)
    }

    override fun onResume() {
        super.onResume()
        if (!finished) {
            update = true
            handler.post(UpdateRunnable())
        }
    }

    private fun stopMemoryFilling() {
        concurrent_services_label.text = getString(R.string.concurrent_services_ran)
        stop_filling_memory_button.isEnabled = false
        val msg = Message.obtain(null, MemoryFillingService.MSG_STOP, 0, 0)
        services.forEach {
            if (it.value.first) {
                try {
                    it.value.third?.send(msg)
                } catch (e : RemoteException) {
                    e.printStackTrace()
                }
            }
            if (it.value.first) {
                services[it.key] = it.value.copy(first = false, third = null)
                try {
                    unbindService(it.value.second)
                } catch (e : Exception) {
                    Log.d("CAMILO", "Error unbinding service")
                }
            }
        }
        destroyHandler.postDelayed({
            destroyRunnable
        }, 5000)
    }

    override fun onPause() {
        super.onPause()
        update = false
    }

    private val destroyHandler = Handler()
    private val destroyRunnable = Runnable { killEverything() }

    override fun onDestroy() {
        doUnbindServices()
        destroyHandler.removeCallbacks(destroyRunnable)
        killEverything()
        super.onDestroy()
    }

    private fun killEverything() {
        services.forEach {
            stopService(Intent(this, it.key))
        }
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        am.runningAppProcesses.forEach {runningService ->
            if (runningService.processName.contains("memoryFillingService")) {
                Process.killProcess(runningService.pid)
            }
        }
        am.killBackgroundProcesses("com.scythe.developertools")
    }

    private fun doUnbindServices() {
        services.forEach {
            if (it.value.first) {
                services[it.key] = it.value.copy(first = false, third = null)
                try {
                    unbindService(it.value.second)
                } catch (e : Exception) {
                    Log.d("CAMILO", "Error unbinding service")
                }
            }
        }
    }

    private fun updateMemoryInfo() {
        if (!::memoryInfoHelper.isInitialized) {
            memoryInfoHelper = MemoryInfoHelper(applicationContext)
        }
        memoryInfoHelper.update()
        free_memory.text = "${memoryInfoHelper.getAvailableMemory()} MB"
        free_memory_percentage.text = "${memoryInfoHelper.getFreeMemoryPercentage()} %"
        memory_low.visibility = if (memoryInfoHelper.isMemoryLow()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    inner class UpdateRunnable : Runnable {
        override fun run() {
            if (update) {
                updateMemoryInfo()
                checkStaticVariable()
                handler.postDelayed(this, 1000)
            } else {
                handler.removeCallbacks(this)
            }
        }
    }

    private var helperServiceBound = false
    private var helperServiceMessenger : Messenger? = null

    private val helperServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            helperServiceBound = true
            helperServiceMessenger = Messenger(p1)
            checkStaticVariable()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            helperServiceBound = false
            helperServiceMessenger = null
        }
    }

    private fun checkStaticVariable() {
        if (!helperServiceBound) {
            bindService(Intent(this, MemoryFillingHelperService::class.java),
                    helperServiceConnection, Context.BIND_AUTO_CREATE)
        } else {
            val msg = Message.obtain(null, MemoryFillingHelperService.MSG_CHECK_STATIC_VARIABLE,
                    0, 0)
            msg.replyTo = incomingMessenger
            try {
                helperServiceMessenger!!.send(msg)
            } catch (e : RemoteException) {
                e.printStackTrace()
            }
            doUnbindHelperService()
        }
    }

    private fun doUnbindHelperService() {
        helperServiceBound = false
        helperServiceMessenger = null
        unbindService(helperServiceConnection)

    }

    private val incomingMessenger = Messenger(IncomingHandler())

    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when(msg.what) {
                SEND_STATIC_VARIABLE -> {
                    if (msg.arg1 != 5) {
                        //Successfully cleared value
                        finished = true
                        update = false
                        progress_bar.visibility = View.GONE
                        failed_result.visibility = View.GONE
                        successful_result.visibility = View.VISIBLE
                        stopMemoryFilling()
                    }
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    private val allServiceClasses = listOf(
            MemoryFillingService.MemoryFillingService1::class.java,
            MemoryFillingService.MemoryFillingService2::class.java,
            MemoryFillingService.MemoryFillingService3::class.java,
            MemoryFillingService.MemoryFillingService4::class.java,
            MemoryFillingService.MemoryFillingService5::class.java,
            MemoryFillingService.MemoryFillingService6::class.java,
            MemoryFillingService.MemoryFillingService7::class.java,
            MemoryFillingService.MemoryFillingService8::class.java,
            MemoryFillingService.MemoryFillingService9::class.java,
            MemoryFillingService.MemoryFillingService10::class.java,
            MemoryFillingService.MemoryFillingService11::class.java,
            MemoryFillingService.MemoryFillingService12::class.java,
            MemoryFillingService.MemoryFillingService13::class.java,
            MemoryFillingService.MemoryFillingService14::class.java,
            MemoryFillingService.MemoryFillingService15::class.java,
            MemoryFillingService.MemoryFillingService16::class.java,
            MemoryFillingService.MemoryFillingService17::class.java,
            MemoryFillingService.MemoryFillingService18::class.java,
            MemoryFillingService.MemoryFillingService19::class.java,
            MemoryFillingService.MemoryFillingService20::class.java,
            MemoryFillingService.MemoryFillingService21::class.java,
            MemoryFillingService.MemoryFillingService22::class.java,
            MemoryFillingService.MemoryFillingService23::class.java,
            MemoryFillingService.MemoryFillingService24::class.java,
            MemoryFillingService.MemoryFillingService25::class.java,
            MemoryFillingService.MemoryFillingService26::class.java,
            MemoryFillingService.MemoryFillingService27::class.java,
            MemoryFillingService.MemoryFillingService28::class.java,
            MemoryFillingService.MemoryFillingService29::class.java,
            MemoryFillingService.MemoryFillingService30::class.java,
            MemoryFillingService.MemoryFillingService31::class.java,
            MemoryFillingService.MemoryFillingService32::class.java,
            MemoryFillingService.MemoryFillingService33::class.java,
            MemoryFillingService.MemoryFillingService34::class.java,
            MemoryFillingService.MemoryFillingService35::class.java,
            MemoryFillingService.MemoryFillingService36::class.java,
            MemoryFillingService.MemoryFillingService37::class.java,
            MemoryFillingService.MemoryFillingService38::class.java,
            MemoryFillingService.MemoryFillingService39::class.java,
            MemoryFillingService.MemoryFillingService40::class.java,
            MemoryFillingService.MemoryFillingService41::class.java,
            MemoryFillingService.MemoryFillingService42::class.java,
            MemoryFillingService.MemoryFillingService43::class.java,
            MemoryFillingService.MemoryFillingService44::class.java,
            MemoryFillingService.MemoryFillingService45::class.java,
            MemoryFillingService.MemoryFillingService46::class.java,
            MemoryFillingService.MemoryFillingService47::class.java,
            MemoryFillingService.MemoryFillingService48::class.java,
            MemoryFillingService.MemoryFillingService49::class.java,
            MemoryFillingService.MemoryFillingService50::class.java,
            MemoryFillingService.MemoryFillingService51::class.java,
            MemoryFillingService.MemoryFillingService52::class.java,
            MemoryFillingService.MemoryFillingService53::class.java,
            MemoryFillingService.MemoryFillingService54::class.java,
            MemoryFillingService.MemoryFillingService55::class.java,
            MemoryFillingService.MemoryFillingService56::class.java,
            MemoryFillingService.MemoryFillingService57::class.java,
            MemoryFillingService.MemoryFillingService58::class.java,
            MemoryFillingService.MemoryFillingService59::class.java,
            MemoryFillingService.MemoryFillingService60::class.java,
            MemoryFillingService.MemoryFillingService61::class.java,
            MemoryFillingService.MemoryFillingService62::class.java,
            MemoryFillingService.MemoryFillingService63::class.java,
            MemoryFillingService.MemoryFillingService64::class.java,
            MemoryFillingService.MemoryFillingService65::class.java,
            MemoryFillingService.MemoryFillingService66::class.java,
            MemoryFillingService.MemoryFillingService67::class.java,
            MemoryFillingService.MemoryFillingService68::class.java,
            MemoryFillingService.MemoryFillingService69::class.java,
            MemoryFillingService.MemoryFillingService70::class.java,
            MemoryFillingService.MemoryFillingService71::class.java,
            MemoryFillingService.MemoryFillingService72::class.java,
            MemoryFillingService.MemoryFillingService73::class.java,
            MemoryFillingService.MemoryFillingService74::class.java,
            MemoryFillingService.MemoryFillingService75::class.java,
            MemoryFillingService.MemoryFillingService76::class.java,
            MemoryFillingService.MemoryFillingService77::class.java,
            MemoryFillingService.MemoryFillingService78::class.java,
            MemoryFillingService.MemoryFillingService79::class.java,
            MemoryFillingService.MemoryFillingService80::class.java,
            MemoryFillingService.MemoryFillingService81::class.java,
            MemoryFillingService.MemoryFillingService82::class.java,
            MemoryFillingService.MemoryFillingService83::class.java,
            MemoryFillingService.MemoryFillingService84::class.java,
            MemoryFillingService.MemoryFillingService85::class.java,
            MemoryFillingService.MemoryFillingService86::class.java,
            MemoryFillingService.MemoryFillingService87::class.java,
            MemoryFillingService.MemoryFillingService88::class.java,
            MemoryFillingService.MemoryFillingService89::class.java,
            MemoryFillingService.MemoryFillingService90::class.java,
            MemoryFillingService.MemoryFillingService91::class.java,
            MemoryFillingService.MemoryFillingService92::class.java,
            MemoryFillingService.MemoryFillingService93::class.java,
            MemoryFillingService.MemoryFillingService94::class.java,
            MemoryFillingService.MemoryFillingService95::class.java,
            MemoryFillingService.MemoryFillingService96::class.java,
            MemoryFillingService.MemoryFillingService97::class.java,
            MemoryFillingService.MemoryFillingService98::class.java,
            MemoryFillingService.MemoryFillingService99::class.java)
}
