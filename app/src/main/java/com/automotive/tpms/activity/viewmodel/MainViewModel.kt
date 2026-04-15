package com.automotive.tpms.activity.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State

@HiltViewModel
class MainViewModel @Inject constructor(
    // TODO: any repository interface dependency
) : ViewModel() {
    private val _count = mutableStateOf(0)
    val counter: State<Int> = _count

    fun incrementCounter() {
        _count.value++
    }
}