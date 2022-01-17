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

    @RequiresApi(Build.VERSION_CODES.M)
    val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{ // 어떻게 그릴 것인지 정의, onDraw가 자주 호출되는 함수이므로 onDraw 밖에서 객체를 정의해야 한다.
        color = context.getColor(R.color.purple_500) // 색 정의
        strokeWidth = LINE_WIDTH // 선 굵기 정의
        strokeCap = Paint.Cap.ROUND // 선의 양 끝 속성 정의
    }

    var drawingWidth: Int = 0
    var drawingHeight: Int = 0
    var drawingAmplitudes: List<Int> = (0..10).map{ Random.nextInt(Short.MAX_VALUE.toInt())} // 소리의 진폭을 저장할 리스트

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) { // 화면 사이즈가 바뀌었을 때 또는 초기 액티비티가 호출될 때 새롭게 높이와 너비를 정의
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) { // View가 그려질 때 호출된다.
        super.onDraw(canvas)

        canvas ?: return // canvas가 null이면 return

        val centerY = drawingHeight / 2f

        var offsetX = drawingWidth.toFloat() // 선을 그릴 X축 위치

        drawingAmplitudes.forEach{ amplitude ->
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

    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat() // 정수값 -> 실수값으로 변환
    }
}