package com.example.services.cases.foreground_bound_service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import com.example.services.R
import com.example.services.cases.foreground_bound_service.view.ForegroundBoundServiceActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ForegroundBoundService : Service() {

    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val mBinder = ForegroundBoundBinder()

    private val _numberFlow = MutableStateFlow(0)
    val numberFlow: StateFlow<Int> = _numberFlow

    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }

    private val baseNotification by lazy {
        val activityIntent = Intent(applicationContext, ForegroundBoundServiceActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(activityIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setContentTitle(getString(R.string.foreground_bound_service))
    }

    inner class ForegroundBoundBinder : Binder() {
        fun getService(): ForegroundBoundService = this@ForegroundBoundService
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
        createNotificationChannel()

        startForeground(NOTIFICATION_ID, createNotification(0))
        emitNumber()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.foreground_bound_service),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(number: Int): Notification {
        return baseNotification
            .setContentText("El servicio lleva corriendo $number")
            .build()
    }

    private fun emitNumber() {
        serviceScope.launch {
            repeat(100) {
                delay(1000)
                _numberFlow.value = it
                updateNotification(it)
            }
            stopSelf()
        }
    }

    private fun updateNotification(number: Int) {
        val notification = createNotification(number)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun stop() {
        stopSelf()
        isServiceActive = false
        serviceScope.cancel()

        serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }
    companion object {
        var isServiceActive = false
        private const val CHANNEL_ID = "ForegroundBoundService"
        private const val NOTIFICATION_ID = 2
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}