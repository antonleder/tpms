package com.automotive.tpms.activity.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle
import com.automotive.tpms.activity.action.ActivityAction
import com.automotive.tpms.activity.action.nextActivity
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val _activityAction = MutableStateFlow(
        ActivityAction.fromString(
            savedStateHandle.get<String>(BUNDLE_ACTIVITY_ACTION_KEY) ?: ""
        )
    )

    @OptIn(ExperimentalTime::class)
    val timestamp: LocalTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    val counter = _count.asStateFlow()
    val logs = _logsList.asStateFlow()
    val activityAction = _activityAction.asStateFlow()

    fun incrementCounter() {
        savedStateHandle[BUNDLE_COUNT_KEY] = ++_count.value
    }

    fun addNewlog(logLine: String) {
        _logsList.value.add(logLine)
        savedStateHandle[BUNDLE_LOGS_LIST_KEY] = _logsList.value
    }

    fun toNextActivityAction() {
        val next = _activityAction.value.nextActivity()
        savedStateHandle[BUNDLE_ACTIVITY_ACTION_KEY] = next.activityName
    }

    fun updateActivityAction(action: ActivityAction) {
        if (action == _activityAction.value ||
            action == ActivityAction.EmptyActivityAction /* in this clause  _activityAction.value is smth valid */) {
            return
        }
        _activityAction.value = action
        savedStateHandle[BUNDLE_ACTIVITY_ACTION_KEY] = action.activityName
    }

    companion object {
        const val BUNDLE_COUNT_KEY = "count"
        const val BUNDLE_LOGS_LIST_KEY = "logsList"
        const val BUNDLE_ACTIVITY_ACTION_KEY = "activityAction"
    }
}