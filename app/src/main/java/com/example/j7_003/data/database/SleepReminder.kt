package com.example.j7_003.data.database

import android.app.*
import android.content.Context
import android.content.Intent
import com.example.j7_003.MainActivity
import com.example.j7_003.system_interaction.handler.StorageHandler
import com.example.j7_003.system_interaction.receiver.NotificationReceiver
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class SleepReminder {

    companion object {
        private val myCalendar = Calendar.getInstance()
        private var currentHour by Delegates.notNull<Int>()
        private var currentMinute by Delegates.notNull<Int>()

        var timings: IntArray = IntArray(4)
        private var isSet: Boolean = false

        var days: BooleanArray = BooleanArray(7)

        private const val fileName: String = "SLEEP_REMINDER"

        fun init() {
            StorageHandler.createJsonFile(
                fileName,
                "SReminder.json"
            )
            load()
        }

        fun isRemindTimeReached(): Boolean {
            getClock()

            return compareHours() || compareWithMinutes()
        }

        fun editReminder(newHour: Int, newMinute: Int) {
            timings[0] = newHour
            timings[1] = newMinute
        }

        fun editWakeUp(newHour: Int, newMinute: Int) {
            timings[2] = newHour
            timings[3] = newMinute
        }

        fun disable() {
            isSet = false
        }

        fun enable() {
            isSet = true
        }

        private fun compareHours(): Boolean = currentHour in timings[0]+1 until timings[2]
        private fun compareWithMinutes(): Boolean {
            return when (currentHour) {
                timings[0].toInt() -> currentMinute >= timings[1]
                timings[2].toInt() -> currentMinute < timings[3]
                else -> false
            }
        }

        private fun getClock() {
            currentHour = myCalendar.get(Calendar.HOUR_OF_DAY)
            currentMinute = myCalendar.get(Calendar.MINUTE)
        }

        fun save() {
            val saveableList = arrayListOf(
                timings,
                days
            )
            StorageHandler.saveAsJsonToFile(
                StorageHandler.files[fileName],
                saveableList
            )
        }

        fun load() {
            val jsonString = StorageHandler.files[fileName]?.readText()

            val loadedData = GsonBuilder().create()
                .fromJson(jsonString, object : TypeToken<ArrayList<Any>>() {}.type) as ArrayList<Any>

            val list1 = loadedData[0] as ArrayList<Int>
            val list2 = loadedData[1] as ArrayList<Boolean>

            timings = list1.toIntArray()
            days = list2.toBooleanArray()
        }
    }
}