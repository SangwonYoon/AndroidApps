package com.example.pomodorotimer

import android.media.SoundPool
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

    private val soundPool = SoundPool.Builder().build() // soundPool 객체 생성

    private var currentCountDownTimer: CountDownTimer? = null

    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause() // 모든 활성화된 sound stream을 정지시킨다. 앱을 백그라운드로 내려도 소리가 자동으로 정지되지 않기 때문에 따로 처리해줘야 함.
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release() // 앱이 완전히 종료되면 메모리에서 sound 파일들을 해제시킨다.
    }

    private fun bindViews(){
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(fromUser) {
                        updateRemainTime(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    currentCountDownTimer?.cancel()
                    currentCountDownTimer = null
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    p0 ?: return // ?:는 엘비스 연산자라고 불리며, 왼쪽 객체나 non-null이면 그 값이 리턴되고, null이면 오른쪽 값을 리턴한다.

                    startCountDown()
                }
            }
        )
    }

    private fun createCountDownTimer(initialMillis: Long) = // return을 생략하고 = 다음에 반환값을 작성할 수 있다.
        object : CountDownTimer(initialMillis, 1000L){
            override fun onTick(millisUntilFinished: Long) { // 매 틱마다 실행되는 함수
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }

    private fun startCountDown(){
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()

        tickingSoundId?.let{ soundId ->
            soundPool.play(soundId, 1F, 1F, 1, -1, 1F) // tickingSoundId가 null이 아닌 경우에만 이 코드를 실행
        }
    }

    private fun completeCountDown(){
        updateRemainTime(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let{ soundId ->
            soundPool.play(soundId, 1F, 1F, 1, 0, 1F)
        }
    }

    private fun updateRemainTime(remainMillis : Long){ // 남은 시간을 보여주는 UI를 업데이트 해주는 함수
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long){ // SeekBar의 슬라이드를 남은 분에 맞춰 변경해주는 함수
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }

    private fun initSounds(){
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1) // 매개변수 값 중 priority는 우선 순위를 의미, return 값은 해당 소리 파일의 아이디값
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }
}