package com.example.services.cases.background_unbound_service.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.services.R
import com.example.services.ServiceActivityScaffold
import com.example.services.ServiceButton
import com.example.services.cases.background_unbound_service.BackgroundUnBoundService
import com.example.services.cases.background_unbound_service.BackgroundUnBoundService.Companion.ACTION_START
import com.example.services.cases.background_unbound_service.BackgroundUnBoundService.Companion.ACTION_STOP
import com.example.services.cases.background_unbound_service.BackgroundUnBoundService.Companion.LAST_NUMBER
import com.example.services.cases.background_unbound_service.view.ui.theme.ServicesTheme

class BackgroundUnBoundServiceActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("BackgroundUnBoundService", Context.MODE_PRIVATE)

        setContent {
            ServicesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ServiceActivityScaffold(
                        title = R.string.background_unbound_service,
                        description = R.string.background_unbound_service_description
                    ) {
                        var lastNumber by remember { mutableStateOf("Aun no empieza") }
                        val current = stringResource(id = R.string.current_number)

                        Text(text = lastNumber)

                        Spacer(modifier = Modifier.height(5.dp))

                        ServiceButton(
                            text = stringResource(id = R.string.update_number),
                            onClick = {
                                val number = sharedPreferences.getInt(LAST_NUMBER, -1)
                                if (number != -1) {
                                    lastNumber =  "$current $number"
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        ServiceButton(
                            text = stringResource(id = R.string.start_service),
                            onClick = {
                                startService(Intent(this@BackgroundUnBoundServiceActivity, BackgroundUnBoundService::class.java).apply {
                                    action = ACTION_START
                                })
                            }
                        )
                        ServiceButton(
                            text = stringResource(id = R.string.stop_service),
                            onClick = {
                                startService(Intent(this@BackgroundUnBoundServiceActivity, BackgroundUnBoundService::class.java).apply {
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

