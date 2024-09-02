package com.example.services.cases.background_bound_service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BackgroundBoundService : Service() {

    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val mBinder = BackgroundBoundBinder()

    private val _numberFlow = MutableStateFlow(0)
    val numberFlow: StateFlow<Int> = _numberFlow

    inner class BackgroundBoundBinder : Binder() {
        fun getService(): BackgroundBoundService = this@BackgroundBoundService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
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
                _numberFlow.value = it
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
    }
}