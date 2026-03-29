package com.mercangel.LifeTimer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mercangel.LifeTimer.ui.theme.LifeTimerTheme
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeTimerTheme {
                LifeTimerApp()
            }
        }
    }
}

// ─── Root composable ──────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeTimerApp(vm: MainViewModel = viewModel()) {
    val timeState by vm.timeState.collectAsStateWithLifecycle()
    val selectedDateTime by vm.selectedDateTime.collectAsStateWithLifecycle()
    val timeRows = buildTimeRows(timeState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    Text(
                        text = if (timeState.seconds % 2 == 0L) "Tick" else "Tock",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                DateTimePickerCard(
                    selectedDateTime = selectedDateTime,
                    onDateTimeSelected = vm::updateSelectedDateTime
                )
            }
            item {
                DirectionBadge(isPast = timeState.isPast)
            }
            items(timeRows) { row ->
                TimeUnitCard(row = row)
            }
        }
    }
}

// ─── Date/time picker card ────────────────────────────────────────────────────

@Composable
fun DateTimePickerCard(
    selectedDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val context = LocalContext.current
    val formatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy  h:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.selected_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = selectedDateTime.format(formatter),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Button(
                onClick = {
                    // Chain DatePickerDialog → TimePickerDialog
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    onDateTimeSelected(
                                        LocalDateTime.of(year, month + 1, day, hour, minute)
                                    )
                                },
                                selectedDateTime.hour,
                                selectedDateTime.minute,
                                false // 12-hour clock with AM/PM
                            ).show()
                        },
                        selectedDateTime.year,
                        selectedDateTime.monthValue - 1,
                        selectedDateTime.dayOfMonth
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.pick_date_time))
            }
        }
    }
}

// ─── SINCE / UNTIL direction badge ───────────────────────────────────────────

@Composable
fun DirectionBadge(isPast: Boolean) {
    val label = if (isPast) stringResource(R.string.since_label)
                else stringResource(R.string.until_label)
    val containerColor = if (isPast) MaterialTheme.colorScheme.tertiaryContainer
                         else MaterialTheme.colorScheme.errorContainer
    val contentColor = if (isPast) MaterialTheme.colorScheme.onTertiaryContainer
                       else MaterialTheme.colorScheme.onErrorContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⏱  $label",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                letterSpacing = 2.sp
            )
        }
    }
}

// ─── Single time-unit row ─────────────────────────────────────────────────────

data class TimeRow(val label: String, val value: Long)

@Composable
fun TimeUnitCard(row: TimeRow) {
    val numberFormatter = remember { NumberFormat.getNumberInstance(Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = row.label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = numberFormatter.format(row.value),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End
            )
        }
    }
}

// ─── Map TimeState → ordered display rows ────────────────────────────────────

@Composable
fun buildTimeRows(state: TimeState): List<TimeRow> = listOf(
    TimeRow(stringResource(R.string.unit_milliseconds), state.millis),
    TimeRow(stringResource(R.string.unit_seconds),      state.seconds),
    TimeRow(stringResource(R.string.unit_minutes),      state.minutes),
    TimeRow(stringResource(R.string.unit_hours),        state.hours),
    TimeRow(stringResource(R.string.unit_days),         state.days),
    TimeRow(stringResource(R.string.unit_weeks),        state.weeks),
    TimeRow(stringResource(R.string.unit_months),       state.months),
    TimeRow(stringResource(R.string.unit_years),        state.years)
)
