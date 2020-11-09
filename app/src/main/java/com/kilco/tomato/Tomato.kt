package com.kilco.tomato

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle

class Tomato(application: Application, private var handle: SavedStateHandle) :
    AndroidViewModel(application) {
    val customTimeLength = MutableLiveData<Int>(100)
    val customTimeButton = MutableLiveData<String>("自定义")
    val maxCustomTimeLength = MutableLiveData<Int>(200)
    val lastCustomTimeLength = MutableLiveData<Int>(233)
    val keepScreenOnTeller = MutableLiveData<Boolean>(false)
    val keepScreenOn = MutableLiveData<Boolean>(false)

    init {
        if (!handle.contains("maxCustomTimeLength") || !handle.contains("lastCustomTimeLength") || !handle.contains(
                "keepScreenOnTeller"
            )
        ) load()
    }

    enum class TimerStateOptions {
        Stopped, Running
    }

    private var timerState = TimerStateOptions.Stopped

    val timerDisplay = MutableLiveData<String>("Hello Tomato!")
    private lateinit var timer: CountDownTimer
    fun timerStart(timeLength: Long) {
        if (timerState == TimerStateOptions.Running) {
            timer.cancel()
            timerState = TimerStateOptions.Stopped
        }
        if (keepScreenOnTeller.value == true) keepScreenOn.value = true
        timer = object : CountDownTimer(timeLength, 1000) {
            override fun onTick(millisInFuture: Long) {
                val century =
                    (millisInFuture / 3153600000000).toString()
                val year = ((millisInFuture % 3153600000000) / 31536000000).toString()
                val day = ((millisInFuture % 31536000000) / 86400000).toString()
                var hour = ((millisInFuture % 86400000) / 3600000).toString()
                var min = ((millisInFuture % 3600000) / 60000).toString()
                var sec = ((millisInFuture % 60000) / 1000).toString()
                if (hour.length == 1) hour = "0$hour"
                if (min.length == 1) min = "0$min"
                if (sec.length == 1) sec = "0$sec"
                when (millisInFuture) {
                    in 0.toLong()..3599999.toLong() -> timerDisplay.value = "$min:$sec"
                    in 3600000.toLong()..86399999.toLong() -> timerDisplay.value = "$hour:$min:$sec"
                    in 86400000.toLong()..31536000000 -> timerDisplay.value =
                        "$day 天\n$hour:$min:$sec"
                    in 31536000000..3153600000000 -> timerDisplay.value =
                        "$year 年 $day 天\n$hour:$min:$sec"
                    in 3153600000000..59999999940000 -> timerDisplay.value =
                        "$century 世纪 $year 年 $day 天\n$hour:$min:$sec"
                    else -> timerDisplay.value =
                        "Time Beyond the Universe."
                }
            }

            override fun onFinish() {
                timerDisplay.value = "Done."
                keepScreenOn.value = false
            }
        }
        timer.start()
        timerState = TimerStateOptions.Running
    }

    fun timerStop() {
        if (timerState == TimerStateOptions.Running) {
            timer.cancel()
            timerState = TimerStateOptions.Stopped
            timerDisplay.value = "结束"
        }
        keepScreenOn.value = false
    }


    private fun load() {
        val tomatoPreferences: SharedPreferences =
            getApplication<Application>().getSharedPreferences(
                "Settings",
                Context.MODE_PRIVATE
            )
        maxCustomTimeLength.value = tomatoPreferences.getInt("maxCustomTimeLength", 200)
        lastCustomTimeLength.value = tomatoPreferences.getInt("lastCustomTimeLength", 30)
        keepScreenOnTeller.value = tomatoPreferences.getBoolean("keepScreenOnTeller", false)
        //handle.set("maxCustomTimeLength", maxCustomTimeLength)
    }

    fun save() {
        val tomatoPreferences: SharedPreferences =
            getApplication<Application>().getSharedPreferences(
                "Settings",
                Context.MODE_PRIVATE
            )
        val editor: SharedPreferences.Editor = tomatoPreferences.edit()
        maxCustomTimeLength.value?.let { editor.putInt("maxCustomTimeLength", it) }
        lastCustomTimeLength.value?.let { editor.putInt("lastCustomTimeLength", it) }
        keepScreenOnTeller.value?.let { editor.putBoolean("keepScreenOnTeller", it) }
        editor.apply()
    }
}