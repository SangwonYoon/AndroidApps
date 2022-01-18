package com.example.audiorecorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

class SoundVisualizerView(context : Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var onRequestCurrentAmplitude: (() -> Int)? = null

    @RequiresApi(Build.VERSION_CODES.M)
    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{ // 어떻게 그릴 것인지 정의, onDraw가 자주 호출되는 함수이므로 onDraw 밖에서 객체를 정의해야 한다.
        color = context.getColor(R.color.purple_500) // 색 정의
        strokeWidth = LINE_WIDTH // 선 굵기 정의
        strokeCap = Paint.Cap.ROUND // 선의 양 끝 속성 정의
    }

    private var drawingWidth: Int = 0
    private var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList() // 소리의 진폭을 저장할 리스트
    private var isReplaying: Boolean = false
    private var replayingPosition: Int = 0

    private val visualizeRepeatAction : Runnable = object : Runnable{ // Runnable은 Thread를 간소화한 형태, Runnable은 인터페이스이다. run() 함수를 호출해 실행할 수 있다.
        override fun run() {
            if(!isReplaying) {
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                drawingAmplitudes =
                    listOf(currentAmplitude) + drawingAmplitudes // 새로 들어온 값이 리스트의 0번 인덱스에 위치하도록
            } else{
                replayingPosition++
            }
            invalidate() // 현재 뷰가 invalid 함을 알려 새로운 상태를 반영한 뷰를 그리게 함.

            handler?.postDelayed(this, ACTION_INTERVAL) // 자기 자신(Runnable을 상속한 익명 객체)을 20 millisecond 뒤에 호출한다.
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) { // 화면 사이즈가 바뀌었을 때 또는 초기 액티비티가 호출될 때 새롭게 높이와 너비를 정의
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDraw(canvas: Canvas?) { // View가 그려질 때 호출된다.
        super.onDraw(canvas)

        canvas ?: return // canvas가 null이면 return

        val centerY = drawingHeight / 2f

        var offsetX = drawingWidth.toFloat() // 선을 그릴 X축 위치

        drawingAmplitudes
            .let{ amplitudes ->
                if(isReplaying){ // 녹음 후 재생중일때는 리스트의 뒤에서부터 지정된 개수만큼만 반환한다.
                    amplitudes.takeLast(replayingPosition)
                } else{ // 녹음중일때는 리스트를 전부 반환한다.
                    amplitudes
                }
            }
            .forEach{ amplitude ->
            val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F

            offsetX -= LINE_SPACE
            if(offsetX < 0) return@forEach // forEach에서는 break와 continue를 쓰지 못한다. return@forEach는 continue과 같은 기능을 한다.
            canvas.drawLine(
                offsetX, // 시작점의 X 좌표
                centerY - lineLength / 2F, // 시작점의 Y 좌표
                offsetX, // 끝점의 X 좌표
                centerY + lineLength / 2F, // 끝점의 Y 좌표
                amplitudePaint // 어떻게 그릴 것인지
            )
        }
    }

    fun startVisualizing(isReplaying: Boolean){
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)
    }

    fun stopVisualizing(){
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat() // 정수값 -> 실수값으로 변환
        private const val ACTION_INTERVAL = 20L
    }
}