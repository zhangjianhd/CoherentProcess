package com.zj.coherent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progress: ProgressBar = findViewById(R.id.progress)
        val tcProgress: TransferringCover = findViewById(R.id.tcProgress)
        val btnStart: Button = findViewById(R.id.btnStart)
        val btnEnd: Button = findViewById(R.id.btnEnd)

        progress.max = 1000 //更加细腻

        val progressCoherentProcess =
            ProgressCoherentProcess(progress, ProgressCoherentProcess.ProgressUpdate {
                progress.progress = (it * 10).toInt()
            })

        val tcProgressCoherentProcess =
            ProgressCoherentProcess(progress, ProgressCoherentProcess.ProgressUpdate {
                tcProgress.progress = it/100
            })

        btnStart.setOnClickListener {
            progressCoherentProcess.setProgress(0)
            tcProgressCoherentProcess.setProgress(0)
        }

        btnEnd.setOnClickListener {
            progressCoherentProcess.setProgress(100)
            tcProgressCoherentProcess.setProgress(100)
        }
    }
}