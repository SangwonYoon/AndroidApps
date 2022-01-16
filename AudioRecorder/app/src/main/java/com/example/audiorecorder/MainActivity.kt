package com.example.audiorecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private val recordButton : RecordButton by lazy{
        findViewById(R.id.recordButton)
    }

    private val requiredPermissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    private val recordingFilePath : String by lazy{
        "${externalCacheDir?.absolutePath}/recording.3gp" // 캐시 디렉토리 하위에 녹음 파일을 저장
    }

    private var recorder : MediaRecorder? = null

    private var player : MediaPlayer? = null

    private var state = State.BEFORE_RECORDING
    set(value) {
        field = value
        recordButton.updateIconWithState(value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission()
        initViews()
        bindViews()
    }

    private fun requestAudioPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if(!audioRecordPermissionGranted){
            finish()
        }
    }

    private fun initViews(){
        recordButton.updateIconWithState(state)
    }

    private fun bindViews(){
        recordButton.setOnClickListener {
            when(state){
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }
    }

    private fun startRecording(){
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) // 마이크를 통해서 녹음
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // 인코딩 후 어떤 format으로 저장할 것인지 설정
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) // 어떤 방식으로 인코딩할 것인지 설정
            setOutputFile(recordingFilePath) // 이 앱에서는 녹음 후 한번 듣는 일회성 저장만 필요하기 때문에 외부 저장소의 캐시 메모리로 저장 경로를 설정한다.
            prepare()
        }
        recorder?.start()

        state = State.ON_RECORDING
    }

    private fun stopRecording(){
        recorder?.run{
            stop()
            release() // 메모리 해제
        }
        recorder = null

        state = State.AFTER_RECORDING
    }

    private fun startPlaying(){
        player = MediaPlayer().apply{
            setDataSource(recordingFilePath)
            prepare()
        }
        player?.start()

        state = State.ON_PLAYING
    }

    private fun stopPlaying(){
        player?.release()
        player = null

        state = State.AFTER_RECORDING
    }

    companion object{
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}