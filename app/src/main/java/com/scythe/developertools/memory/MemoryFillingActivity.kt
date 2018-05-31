package com.scythe.developertools.memory

import android.app.Activity
import android.app.TaskStackBuilder
import android.content.Intent
import android.support.v7.app.AppCompatActivity

open class MemoryFillingActivity : AppCompatActivity() {

    companion object {
        const val finishedFillingMemory = "finished_filling_memory"
    }

    private val pathToClass = "com.scythe.developertools.memory.MemoryFillingActivity$"
    private val baseClassName = "MemoryFillingActivity"
    private val activityNumberConstant = "activity_number"

    override fun onResume() {
        super.onResume()
        startNextActivity()
    }

    open fun startNextActivity() {
        val activityNumber = intent.getIntExtra(activityNumberConstant, 0) + 1
        val intent = Intent(this, getNextActivity(activityNumber))
        intent.putExtra(activityNumberConstant, activityNumber)
        startActivity(intent)
    }

    open fun getNextActivity(activityNumber: Int) : Class<out AppCompatActivity> =
            Class.forName("$pathToClass$baseClassName$activityNumber") as Class<out AppCompatActivity>

    class MemoryFillingActivity99 : MemoryFillingActivity() {

        override fun startNextActivity() {
            val stackBuilder = TaskStackBuilder.create(this)
            val intent = Intent(this, MemoryToolsActivity::class.java)
            intent.putExtra(finishedFillingMemory, true)
            stackBuilder.addNextIntentWithParentStack(intent)
            stackBuilder.startActivities()
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in)
        }
    }

    class MemoryFillingActivity1 : MemoryFillingActivity()
    class MemoryFillingActivity2 : MemoryFillingActivity()
    class MemoryFillingActivity3 : MemoryFillingActivity()
    class MemoryFillingActivity4 : MemoryFillingActivity()
    class MemoryFillingActivity5 : MemoryFillingActivity()
    class MemoryFillingActivity6 : MemoryFillingActivity()
    class MemoryFillingActivity7 : MemoryFillingActivity()
    class MemoryFillingActivity8 : MemoryFillingActivity()
    class MemoryFillingActivity9 : MemoryFillingActivity()
    class MemoryFillingActivity10 : MemoryFillingActivity()
    class MemoryFillingActivity11 : MemoryFillingActivity()
    class MemoryFillingActivity12 : MemoryFillingActivity()
    class MemoryFillingActivity13 : MemoryFillingActivity()
    class MemoryFillingActivity14 : MemoryFillingActivity()
    class MemoryFillingActivity15 : MemoryFillingActivity()
    class MemoryFillingActivity16 : MemoryFillingActivity()
    class MemoryFillingActivity17 : MemoryFillingActivity()
    class MemoryFillingActivity18 : MemoryFillingActivity()
    class MemoryFillingActivity19 : MemoryFillingActivity()
    class MemoryFillingActivity20 : MemoryFillingActivity()
    class MemoryFillingActivity21 : MemoryFillingActivity()
    class MemoryFillingActivity22 : MemoryFillingActivity()
    class MemoryFillingActivity23 : MemoryFillingActivity()
    class MemoryFillingActivity24 : MemoryFillingActivity()
    class MemoryFillingActivity25 : MemoryFillingActivity()
    class MemoryFillingActivity26 : MemoryFillingActivity()
    class MemoryFillingActivity27 : MemoryFillingActivity()
    class MemoryFillingActivity28 : MemoryFillingActivity()
    class MemoryFillingActivity29 : MemoryFillingActivity()
    class MemoryFillingActivity30 : MemoryFillingActivity()
    class MemoryFillingActivity31 : MemoryFillingActivity()
    class MemoryFillingActivity32 : MemoryFillingActivity()
    class MemoryFillingActivity33 : MemoryFillingActivity()
    class MemoryFillingActivity34 : MemoryFillingActivity()
    class MemoryFillingActivity35 : MemoryFillingActivity()
    class MemoryFillingActivity36 : MemoryFillingActivity()
    class MemoryFillingActivity37 : MemoryFillingActivity()
    class MemoryFillingActivity38 : MemoryFillingActivity()
    class MemoryFillingActivity39 : MemoryFillingActivity()
    class MemoryFillingActivity40 : MemoryFillingActivity()
    class MemoryFillingActivity41 : MemoryFillingActivity()
    class MemoryFillingActivity42 : MemoryFillingActivity()
    class MemoryFillingActivity43 : MemoryFillingActivity()
    class MemoryFillingActivity44 : MemoryFillingActivity()
    class MemoryFillingActivity45 : MemoryFillingActivity()
    class MemoryFillingActivity46 : MemoryFillingActivity()
    class MemoryFillingActivity47 : MemoryFillingActivity()
    class MemoryFillingActivity48 : MemoryFillingActivity()
    class MemoryFillingActivity49 : MemoryFillingActivity()
    class MemoryFillingActivity50 : MemoryFillingActivity()
    class MemoryFillingActivity51 : MemoryFillingActivity()
    class MemoryFillingActivity52 : MemoryFillingActivity()
    class MemoryFillingActivity53 : MemoryFillingActivity()
    class MemoryFillingActivity54 : MemoryFillingActivity()
    class MemoryFillingActivity55 : MemoryFillingActivity()
    class MemoryFillingActivity56 : MemoryFillingActivity()
    class MemoryFillingActivity57 : MemoryFillingActivity()
    class MemoryFillingActivity58 : MemoryFillingActivity()
    class MemoryFillingActivity59 : MemoryFillingActivity()
    class MemoryFillingActivity60 : MemoryFillingActivity()
    class MemoryFillingActivity61 : MemoryFillingActivity()
    class MemoryFillingActivity62 : MemoryFillingActivity()
    class MemoryFillingActivity63 : MemoryFillingActivity()
    class MemoryFillingActivity64 : MemoryFillingActivity()
    class MemoryFillingActivity65 : MemoryFillingActivity()
    class MemoryFillingActivity66 : MemoryFillingActivity()
    class MemoryFillingActivity67 : MemoryFillingActivity()
    class MemoryFillingActivity68 : MemoryFillingActivity()
    class MemoryFillingActivity69 : MemoryFillingActivity()
    class MemoryFillingActivity70 : MemoryFillingActivity()
    class MemoryFillingActivity71 : MemoryFillingActivity()
    class MemoryFillingActivity72 : MemoryFillingActivity()
    class MemoryFillingActivity73 : MemoryFillingActivity()
    class MemoryFillingActivity74 : MemoryFillingActivity()
    class MemoryFillingActivity75 : MemoryFillingActivity()
    class MemoryFillingActivity76 : MemoryFillingActivity()
    class MemoryFillingActivity77 : MemoryFillingActivity()
    class MemoryFillingActivity78 : MemoryFillingActivity()
    class MemoryFillingActivity79 : MemoryFillingActivity()
    class MemoryFillingActivity80 : MemoryFillingActivity()
    class MemoryFillingActivity81 : MemoryFillingActivity()
    class MemoryFillingActivity82 : MemoryFillingActivity()
    class MemoryFillingActivity83 : MemoryFillingActivity()
    class MemoryFillingActivity84 : MemoryFillingActivity()
    class MemoryFillingActivity85 : MemoryFillingActivity()
    class MemoryFillingActivity86 : MemoryFillingActivity()
    class MemoryFillingActivity87 : MemoryFillingActivity()
    class MemoryFillingActivity88 : MemoryFillingActivity()
    class MemoryFillingActivity89 : MemoryFillingActivity()
    class MemoryFillingActivity90 : MemoryFillingActivity()
    class MemoryFillingActivity91 : MemoryFillingActivity()
    class MemoryFillingActivity92 : MemoryFillingActivity()
    class MemoryFillingActivity93 : MemoryFillingActivity()
    class MemoryFillingActivity94 : MemoryFillingActivity()
    class MemoryFillingActivity95 : MemoryFillingActivity()
    class MemoryFillingActivity96 : MemoryFillingActivity()
    class MemoryFillingActivity97 : MemoryFillingActivity()
    class MemoryFillingActivity98 : MemoryFillingActivity()
}
