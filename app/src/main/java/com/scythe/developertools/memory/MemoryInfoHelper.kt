package com.scythe.developertools.memory

import android.app.ActivityManager
import android.content.Context
import kotlin.math.roundToInt

/**
 * Created by Camilo on 6/16/2018.
 */
class MemoryInfoHelper(val context : Context) {

    private fun Float.round(decimals : Int) : Float {
        return (this * 10 * decimals).roundToInt().toFloat() / (10 * decimals)
    }

    private fun Long.toMB(type : String = "B"): Float {
        return when(type) {
            "B" -> this.toFloat() / (1024 * 1024)
            "KB" -> this.toFloat() / (1024)
            else -> this.toFloat()
        }.round(2)
    }

    private val Long.MB : Float
        get() = this.toMB()

    private val activityManager : ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val memoryInfo = ActivityManager.MemoryInfo()
    init {
        update()
    }

    public fun update() {
        activityManager.getMemoryInfo(memoryInfo)
    }

    fun getAvailableMemory() : Float {
        return memoryInfo.availMem.MB
    }

    fun isMemoryLow() : Boolean {
        return memoryInfo.lowMemory
    }

    fun getMemoryThreshold() : Float {
        return memoryInfo.threshold.MB
    }

    fun getRamSize() : Float {
        return memoryInfo.totalMem.MB
    }

    fun getFreeMemoryPercentage() : Float {
        return ((getAvailableMemory() / getRamSize()) * 100).round(2)
    }

    fun isMemoryAvailable() : Boolean {
        val freeMemoryPercent = 100 - (Runtime.getRuntime().totalMemory() / Runtime.getRuntime().maxMemory().toFloat()) * 100
        return freeMemoryPercent > 10
    }

}

