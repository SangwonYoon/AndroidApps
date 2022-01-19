package com.example.audiorecorder

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private var startTimeStamp: Long = 0L

    private val countUpAction : Runnable = object : Runnable{
        override fun run(){
            val currentTimeStamp = SystemClock.elapsedRealtime()

            val countTimeSeconds = ((currentTimeStamp - startTimeStamp)/1000L).toInt()
            updateCountTime(countTimeSeconds)

            handler?.postDelayed(this, 1000L)
        }
    }

    fun startCountUp(){
        startTimeStamp = SystemClock.elapsedRealtime() // 부팅된 시점부터 현재까지의 시간을 millisecond로 리턴
        handler?.post(countUpAction)
    }

    fun stopCountUp(){
        handler?.removeCallbacks(countUpAction)
    }

    fun clearCountTime(){
        updateCountTime(0)
    }

    private fun updateCountTime(countTimeSeconds: Int){
        val minutes = countTimeSeconds / 60
        val seconds = countTimeSeconds % 60

        text = "%02d:%02d".format(minutes, seconds)
    }
}