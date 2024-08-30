package com.example.services

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
import com.example.services.cases.foreground_bound_service.view.ForegroundBoundServiceActivity
import com.example.services.cases.foreground_unbound_service.view.ForegroundUnBoundServiceActivity
import com.example.services.ui.theme.ServicesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServicesTheme {
                // A surface container using the 'background' color from the theme
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
                            text = stringResource(id = R.string.foreground_unbound_service),
                            onClick = {
                                startActivity(Intent(this@MainActivity, ForegroundUnBoundServiceActivity::class.java))
                            }
                        )
                        ServiceButton(
                            text = stringResource(id = R.string.foreground_bound_service),
                            onClick = {
                                startActivity(Intent(this@MainActivity, ForegroundBoundServiceActivity::class.java))
                            }
                        )
                    }
                }
            }
        }
    }
}



//foreground - bound
//foreground - unbound
//background - bound
//background - unbound



//foreground
//background
//bound
//unbound