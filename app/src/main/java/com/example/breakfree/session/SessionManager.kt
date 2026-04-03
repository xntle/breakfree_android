package com.example.breakfree.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ActiveSession(
    val durationMinutes: Int,
    val blockedApps: Set<String>,
    val startTimeMs: Long = System.currentTimeMillis()
) {
    val endTimeMs: Long get() = startTimeMs + durationMinutes * 60_000L
    val remainingMs: Long get() = maxOf(0L, endTimeMs - System.currentTimeMillis())
    val isExpired: Boolean get() = remainingMs == 0L
}

object SessionManager {
    private val _activeSession = MutableStateFlow<ActiveSession?>(null)
    val activeSession: StateFlow<ActiveSession?> = _activeSession.asStateFlow()

    fun startSession(durationMinutes: Int, blockedApps: Set<String>) {
        _activeSession.value = ActiveSession(
            durationMinutes = durationMinutes,
            blockedApps = blockedApps
        )
    }

    fun endSession() {
        _activeSession.value = null
    }

    fun isAppBlocked(packageName: String): Boolean =
        _activeSession.value?.blockedApps?.contains(packageName) == true
}
