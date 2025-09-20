package com.example.smartlawyeragenda.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

data class DateFilter(
    val label: String,
    val startDate: Long,
    val endDate: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateFilterDropdown(
    selectedFilter: DateFilter?,
    onFilterSelected: (DateFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedLabel by remember { mutableStateOf(selectedFilter?.label ?: "اختر الفترة") }

    val today = System.currentTimeMillis()

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

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.width(160.dp)
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            shape = RoundedCornerShape(24.dp),
            readOnly = true,
            label = { Text("الفترة الزمنية") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            dateFilters.forEach { filter ->
                DropdownMenuItem(
                    text = { Text(filter.label) },
                    onClick = {
                        selectedLabel = filter.label
                        onFilterSelected(filter)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ---------- Helpers لتحديد بداية ونهاية اليوم/الأسبوع/الشهر ----------
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
