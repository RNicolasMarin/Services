package com.example.services.cases.foreground_bound_service.view

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import com.example.services.R
import com.example.services.ServiceButton
import com.example.services.cases.foreground_bound_service.ForegroundBoundService
import com.example.services.cases.foreground_bound_service.ForegroundBoundService.Companion.ACTION_START
import com.example.services.cases.foreground_bound_service.ForegroundBoundService.Companion.ACTION_STOP
import com.example.services.cases.foreground_bound_service.ForegroundBoundService.Companion.isServiceActive
import com.example.services.cases.foreground_bound_service.view.ui.theme.ServicesTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ForegroundBoundServiceActivity : ComponentActivity() {

    private val viewModel = ForegroundBoundViewModel()

    private lateinit var myForegroundService: ForegroundBoundService
    private var isBound = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ForegroundBoundService.ForegroundBoundBinder
            myForegroundService = binder.getService()
            observe(myForegroundService.numberFlow)
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    fun observe(numberFlow: StateFlow<Int>) {
        lifecycleScope.launch {
            numberFlow.collectLatest {
                viewModel.update(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isServiceActive) {
            bindForegroundBoundService()
        }

        setContent {
            ServicesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    // Start and bind the service
                    LaunchedEffect(Unit) {

                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Current number: ${viewModel.state}")

                        ServiceButton(
                            text = stringResource(id = R.string.start_service),
                            onClick = {
                                bindForegroundBoundService()
                                startService(Intent(this@ForegroundBoundServiceActivity, ForegroundBoundService::class.java).apply {
                                    action = ACTION_START
                                })
                            }
                        )
                        ServiceButton(
                            text = stringResource(id = R.string.stop_service),
                            onClick = {
                                if (!isBound) return@ServiceButton

                                unbindService(connection)
                                isBound = false
                                startService(Intent(this@ForegroundBoundServiceActivity, ForegroundBoundService::class.java).apply {
                                    action = ACTION_STOP
                                })
                            }
                        )
                    }
                }
            }
        }
    }

    private fun bindForegroundBoundService() {
        isBound = true
        Intent(this@ForegroundBoundServiceActivity, ForegroundBoundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}