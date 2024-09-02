package com.example.services.cases.background_unbound_service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BackgroundUnBoundService : Service() {

    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var sharedPreferences: SharedPreferences

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("BackgroundUnBoundService", Context.MODE_PRIVATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.serviceScope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> {
                start()
            }
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private fun start() {
        if (isServiceActive) return

        isServiceActive = true
        emitNumber()
    }

    private fun emitNumber() {
        serviceScope.launch {
            repeat(100) {
                delay(1000)
                sharedPreferences.edit().putInt(LAST_NUMBER, it).apply()
            }
            stopSelf()
        }
    }

    fun stop() {
        stopSelf()
        isServiceActive = false
        serviceScope.cancel()

        serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    companion object {
        var isServiceActive = false
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val LAST_NUMBER = "LAST_NUMBER"
    }
}