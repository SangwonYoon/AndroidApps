package com.example.pomodorotimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView : TextView by lazy{
        findViewById(R.id.remainMinutesTextView)
    }

    private val remainSecondsTextView : TextView by lazy{
        findViewById(R.id.remainSecondsTextView)
    }

    private val seekBar : SeekBar by lazy{
        findViewById(R.id.seekBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
    }

    private fun bindViews(){
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    remainMinutesTextView.text = "%02d".format(p1) //한자리 숫자 앞에 0을 넣어줌
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            }
        )
    }

    private fun createCountDownTimer(initialMillis: Long) = // return을 생략하고 = 다음에 반환값을 작성할 수 있다.
        object : CountDownTimer(initialMillis, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                updateRemainingTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                TODO("Not yet implemented")
            }
        }

    private fun updateRemainingTime(remainMillis : Long){ // 남은 시간을 보여주는 UI를 업데이트 해주는 함수
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long){ // SeekBar의 슬라이드를 남은 분에 맞춰 변경해주는 함수
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }
}