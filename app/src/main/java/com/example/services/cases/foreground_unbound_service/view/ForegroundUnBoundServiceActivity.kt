package com.example.services.cases.foreground_unbound_service.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.services.R
import com.example.services.ServiceButton
import com.example.services.cases.foreground_unbound_service.ForegroundUnBoundService
import com.example.services.cases.foreground_unbound_service.ForegroundUnBoundService.Companion.ACTION_START
import com.example.services.cases.foreground_unbound_service.ForegroundUnBoundService.Companion.ACTION_STOP
import com.example.services.cases.foreground_unbound_service.view.theme.ServicesTheme

class ForegroundUnBoundServiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServicesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ServiceButton(
                            text = stringResource(id = R.string.start_service),
                            onClick = {
                                startService(Intent(this@ForegroundUnBoundServiceActivity, ForegroundUnBoundService::class.java).apply {
                                    action = ACTION_START
                                })
                            }
                        )
                        ServiceButton(
                            text = stringResource(id = R.string.stop_service),
                            onClick = {
                                startService(Intent(this@ForegroundUnBoundServiceActivity, ForegroundUnBoundService::class.java).apply {
                                    action = ACTION_STOP
                                })
                            }
                        )
                    }
                }
            }
        }
    }
}

