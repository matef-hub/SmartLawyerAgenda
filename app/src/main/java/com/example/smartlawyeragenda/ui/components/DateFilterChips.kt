package com.example.smartlawyeragenda.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

data class DateFilter(
    val label: String,
    val startDate: Long,
    val endDate: Long
)

@Composable
fun CustomDateFilterChips(
    selectedFilter: DateFilter?,
    onFilterSelected: (DateFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = System.currentTimeMillis()
    val calendar = Calendar.getInstance()
    
    val dateFilters = remember {
        listOf(
            DateFilter(
                label = "اليوم",
                startDate = getStartOfDay(today),
                endDate = getEndOfDay(today)
            ),
            DateFilter(
                label = "غداً",
                startDate = getStartOfDay(today + 24 * 60 * 60 * 1000),
                endDate = getEndOfDay(today + 24 * 60 * 60 * 1000)
            ),
            DateFilter(
                label = "هذا الأسبوع",
                startDate = getStartOfWeek(today),
                endDate = getEndOfWeek(today)
            ),
            DateFilter(
                label = "الأسبوع القادم",
                startDate = getStartOfWeek(today + 7 * 24 * 60 * 60 * 1000),
                endDate = getEndOfWeek(today + 7 * 24 * 60 * 60 * 1000)
            ),
            DateFilter(
                label = "هذا الشهر",
                startDate = getStartOfMonth(today),
                endDate = getEndOfMonth(today)
            )
        )
    }
    
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(dateFilters) { filter ->
            FilterChip(
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.label) },
                selected = selectedFilter == filter
            )
        }
    }
}

private fun getStartOfDay(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

private fun getEndOfDay(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis
}

private fun getStartOfWeek(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    return getStartOfDay(calendar.timeInMillis)
}

private fun getEndOfWeek(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
    return getEndOfDay(calendar.timeInMillis)
}

private fun getStartOfMonth(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    return getStartOfDay(calendar.timeInMillis)
}

private fun getEndOfMonth(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    return getEndOfDay(calendar.timeInMillis)
}
