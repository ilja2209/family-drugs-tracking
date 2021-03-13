package com.drugstracking

import android.R
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Message
import com.drugstracking.entities.Drug
import com.drugstracking.entities.Person
import java.time.LocalTime
import java.util.*
import kotlin.concurrent.schedule


class Tracker(val drugs: Map<Person, List<Drug>>, val context: Context?) {

    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val item = msg.obj as DrugMessage
            AlertDialog.Builder(context)
                .setTitle(item.personName)
                .setMessage("Пора выпить: " + item.drugDescription)
                .setPositiveButton(R.string.yes,
                    { dialog, which ->
                    })
                .setNegativeButton(R.string.no, null)
                .setIcon(R.drawable.ic_dialog_alert)
                .show()
            super.handleMessage(msg)
        }
    }

    fun start() {
        drugs.entries.forEach { item ->
            createTimer(item)
        }
    }

    private suspend fun track() {

    }

    private fun createTimer(item: Map.Entry<Person, List<Drug>>) {
        item.value.forEach { drug -> createTimerForDrug(item.key, drug) }
    }

    private fun createTimerForDrug(person: Person, drug: Drug) {
        val parsedTime = drug.time.split(":")
        val delay = LocalTime.of(parsedTime[0].toInt(), parsedTime[1].toInt()).toSecondOfDay() - LocalTime.now().toSecondOfDay()
        if (delay < 0) {
            return
        }
        val timer = Timer()
        timer.schedule(delay * 1000L) {
            mHandler.obtainMessage(1, DrugMessage(person.name, drug.toString())).sendToTarget()
        }
    }

    class DrugMessage(val personName: String, val drugDescription: String) {
    }
}