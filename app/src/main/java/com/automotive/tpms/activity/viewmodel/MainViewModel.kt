package com.automotive.tpms.activity.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _count = savedStateHandle.getMutableStateFlow(BUNDLE_COUNT_KEY, 0)
    private val _logsList =
        savedStateHandle.getMutableStateFlow(BUNDLE_LOGS_LIST_KEY, mutableListOf<String>())

    @OptIn(ExperimentalTime::class)
    val timestamp: LocalTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    val counter: StateFlow<Int> = _count.asStateFlow()
    val logs: StateFlow<List<String>> = _logsList.asStateFlow()

    fun incrementCounter() {
        savedStateHandle[BUNDLE_COUNT_KEY] = ++_count.value
    }

    fun addNewlog(logLine: String) {
        _logsList.value.add(logLine)
        savedStateHandle[BUNDLE_LOGS_LIST_KEY] = _logsList.value
    }

    companion object {
        const val BUNDLE_COUNT_KEY = "count"
        const val BUNDLE_LOGS_LIST_KEY = "logsList"
    }
}