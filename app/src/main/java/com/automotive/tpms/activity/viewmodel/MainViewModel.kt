package com.automotive.tpms.activity.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import kotlin.time.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@HiltViewModel
class MainViewModel @Inject constructor(
    // TODO: any repository interface dependency
) : ViewModel() {
    private val _count = mutableStateOf(0)

    @OptIn(ExperimentalTime::class)
    val timestamp: LocalTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    val counter: State<Int> = _count

    fun incrementCounter() {
        _count.value++
    }
}