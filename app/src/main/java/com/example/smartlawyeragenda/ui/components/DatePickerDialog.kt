package com.example.smartlawyeragenda.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smartlawyeragenda.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerDialog(
    initialDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDateMillis) }
    val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("اختيار التاريخ")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "التاريخ المحدد: ${dateFormatter.format(Date(selectedDate))}",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                // Simple date picker using buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { 
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = selectedDate
                            calendar.add(Calendar.DAY_OF_MONTH, -1)
                            selectedDate = calendar.timeInMillis
                        }
                    ) {
                        Text("◀")
                    }
                    
                    Text(
                        text = dateFormatter.format(Date(selectedDate)),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Button(
                        onClick = { 
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = selectedDate
                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                            selectedDate = calendar.timeInMillis
                        }
                    ) {
                        Text("▶")
                    }
                }
                
                Button(
                    onClick = { selectedDate = System.currentTimeMillis() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("اليوم")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDateSelected(selectedDate) }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
