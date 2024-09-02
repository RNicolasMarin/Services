package com.example.services.cases.background_bound_service.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.services.R
import com.example.services.ServiceActivityScaffold
import com.example.services.ServiceButton
import com.example.services.cases.background_bound_service.BackgroundBoundService
import com.example.services.cases.background_bound_service.BackgroundBoundService.*
import com.example.services.cases.background_bound_service.BackgroundBoundService.Companion.ACTION_START
import com.example.services.cases.background_bound_service.BackgroundBoundService.Companion.ACTION_STOP
import com.example.services.cases.background_bound_service.BackgroundBoundService.Companion.isServiceActive
import com.example.services.cases.background_bound_service.view.ui.theme.ServicesTheme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BackgroundBoundServiceActivity : ComponentActivity() {

    private val viewModel = BackgroundBoundServiceViewModel()

    private lateinit var myBackgroundService: BackgroundBoundService
    private var isBound = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BackgroundBoundBinder
            myBackgroundService = binder.getService()
            observe(myBackgroundService.numberFlow)
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
                    ServiceActivityScaffold(
                        title = R.string.background_bound_service,
                        description = R.string.background_bound_service_description
                    ) {
                        Text(text = "${stringResource(id = R.string.current_number)} ${viewModel.state}")

                        Spacer(modifier = Modifier.height(5.dp))

                        ServiceButton(
                            text = stringResource(id = R.string.start_service),
                            onClick = {
                                bindForegroundBoundService()
                                startService(Intent(this@BackgroundBoundServiceActivity, BackgroundBoundService::class.java).apply {
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
                                startService(Intent(this@BackgroundBoundServiceActivity, BackgroundBoundService::class.java).apply {
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
        Intent(this@BackgroundBoundServiceActivity, BackgroundBoundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}