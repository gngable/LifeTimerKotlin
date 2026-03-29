package com.mercangel.LifeTimer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Immutable snapshot of all time-unit representations of a single duration.
 *
 * All fields are *total* elapsed or remaining time in that unit — not a component
 * (e.g. [seconds] is total seconds, not seconds-within-a-minute).
 *
 * [isPast] is true when [selectedDateTime] is in the past (counting "since").
 */
data class TimeState(
    val millis: Long = 0L,
    val seconds: Long = 0L,
    val minutes: Long = 0L,
    val hours: Long = 0L,
    val days: Long = 0L,
    val weeks: Long = 0L,
    val months: Long = 0L,
    val years: Long = 0L,
    val isPast: Boolean = true
)

class MainViewModel : ViewModel() {

    private val _selectedDateTime = MutableStateFlow(LocalDateTime.now())
    val selectedDateTime: StateFlow<LocalDateTime> = _selectedDateTime.asStateFlow()

    private val _timeState = MutableStateFlow(TimeState())
    val timeState: StateFlow<TimeState> = _timeState.asStateFlow()

    init {
        startTicker()
    }

    fun updateSelectedDateTime(dateTime: LocalDateTime) {
        _selectedDateTime.value = dateTime
    }

    private fun startTicker() {
        viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()
                val selected = _selectedDateTime.value

                // now >= selected means the selected moment is in the past
                val past = !now.isBefore(selected)
                val (earlier, later) = if (past) selected to now else now to selected

                _timeState.value = TimeState(
                    millis  = ChronoUnit.MILLIS.between(earlier, later),
                    seconds = ChronoUnit.SECONDS.between(earlier, later),
                    minutes = ChronoUnit.MINUTES.between(earlier, later),
                    hours   = ChronoUnit.HOURS.between(earlier, later),
                    days    = ChronoUnit.DAYS.between(earlier, later),
                    weeks   = ChronoUnit.WEEKS.between(earlier, later),
                    months  = ChronoUnit.MONTHS.between(earlier, later),
                    years   = ChronoUnit.YEARS.between(earlier, later),
                    isPast  = past
                )

                delay(10L)
            }
        }
    }
}
