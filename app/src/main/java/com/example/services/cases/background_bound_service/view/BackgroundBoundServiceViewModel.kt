package com.example.services.cases.background_bound_service.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class BackgroundBoundServiceViewModel: ViewModel() {

    fun update(number: Int) {
        state = number
    }

    var state by mutableStateOf(0)
        private set
}