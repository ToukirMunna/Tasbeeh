package com.toukir.tasbeeh.data

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.widget.Toast

class ToastReminderService : Service() {
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var intervalMinutes: Int = 15
    private var reminderText: String = "Time for Dhikr"
    private var isScreenReceiverRegistered = false
    
    // Receiver to detect screen state
    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> startToastLoop()
                Intent.ACTION_SCREEN_OFF -> stopToastLoop()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            reminderText = intent.getStringExtra("text") ?: "Time for Dhikr"
            intervalMinutes = intent.getIntExtra("interval", 15).coerceAtLeast(1)
        }
        
        if (!isScreenReceiverRegistered) {
            val filter = android.content.IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }
            registerReceiver(screenReceiver, filter)
            isScreenReceiverRegistered = true
        }
        
        if (isScreenInteractive()) {
            startToastLoop()
        } else {
            stopToastLoop()
        }

        return START_STICKY
    }

    private fun startToastLoop() {
        stopToastLoop() // clear existing
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                try {
                    Toast.makeText(applicationContext, reminderText, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                handler?.postDelayed(this, intervalMinutes * 60 * 1000L)
            }
        }
        handler?.postDelayed(runnable!!, intervalMinutes * 60 * 1000L)
    }

    private fun stopToastLoop() {
        if (handler != null && runnable != null) {
            handler?.removeCallbacks(runnable!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopToastLoop()
        if (isScreenReceiverRegistered) {
            try {
                unregisterReceiver(screenReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isScreenReceiverRegistered = false
        }
    }

    private fun isScreenInteractive(): Boolean {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isInteractive
    }
}
