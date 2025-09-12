package com.example.smartlawyeragenda.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.smartlawyeragenda.R
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.ui.components.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSessionScreen(
    session: SessionEntity? = null,
    case: CaseEntity? = null,
    onSave: (CaseEntity, SessionEntity, Long?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditing = session != null && case != null
    
    // Form state
    var caseNumber by remember { mutableStateOf(case?.caseNumber ?: "") }
    var rollNumber by remember { mutableStateOf(case?.rollNumber ?: "") }
    var clientName by remember { mutableStateOf(case?.clientName ?: "") }
    var opponentName by remember { mutableStateOf(case?.opponentName ?: "") }
    var sessionDate by remember { mutableLongStateOf(session?.sessionDate ?: System.currentTimeMillis()) }
    var fromSession by remember { mutableStateOf(session?.fromSession ?: "") }
    var reason by remember { mutableStateOf(session?.reason ?: "") }
    var decision by remember { mutableStateOf(session?.decision ?: "") }
    var nextSessionDate by remember { mutableStateOf<Long?>(null) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showNextDatePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "تعديل الجلسة" else stringResource(R.string.add_session),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Case Information Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "معلومات القضية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = caseNumber,
                        onValueChange = { caseNumber = it },
                        label = { Text(stringResource(R.string.case_number)) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = caseNumber.isBlank()
                    )
                    
                    OutlinedTextField(
                        value = rollNumber,
                        onValueChange = { rollNumber = it },
                        label = { Text(stringResource(R.string.roll_number)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text(stringResource(R.string.client_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = clientName.isBlank()
                    )
                    
                    OutlinedTextField(
                        value = opponentName,
                        onValueChange = { opponentName = it },
                        label = { Text(stringResource(R.string.opponent_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = opponentName.isBlank()
                    )
                }
            }
            
            // Session Information Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "معلومات الجلسة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Session Date
                    OutlinedTextField(
                        value = dateFormatter.format(Date(sessionDate)),
                        onValueChange = { },
                        label = { Text(stringResource(R.string.session_date)) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            TextButton(onClick = { showDatePicker = true }) {
                                Text("اختيار")
                            }
                        }
                    )
                    
                    OutlinedTextField(
                        value = fromSession,
                        onValueChange = { fromSession = it },
                        label = { Text(stringResource(R.string.from_session)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text(stringResource(R.string.reason)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                    
                    OutlinedTextField(
                        value = decision,
                        onValueChange = { decision = it },
                        label = { Text(stringResource(R.string.decision)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                    
                    // Next Session Date
                    OutlinedTextField(
                        value = nextSessionDate?.let { dateFormatter.format(Date(it)) } ?: "",
                        onValueChange = { },
                        label = { Text(stringResource(R.string.next_session_date)) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Row {
                                if (nextSessionDate != null) {
                                    TextButton(onClick = { nextSessionDate = null }) {
                                        Text("إزالة")
                                    }
                                }
                                TextButton(onClick = { showNextDatePicker = true }) {
                                    Text("اختيار")
                                }
                            }
                        }
                    )
                }
            }
            
            // Save Button
            Button(
                onClick = {
                    if (caseNumber.isNotBlank() && clientName.isNotBlank() && opponentName.isNotBlank()) {
                        val caseEntity = CaseEntity(
                            caseId = case?.caseId ?: 0L,
                            caseNumber = caseNumber,
                            rollNumber = rollNumber.ifBlank { null },
                            clientName = clientName,
                            opponentName = opponentName
                        )
                        
                        val sessionEntity = SessionEntity(
                            sessionId = session?.sessionId ?: 0L,
                            caseId = case?.caseId ?: 0L,
                            sessionDate = sessionDate,
                            fromSession = fromSession.ifBlank { null },
                            reason = reason.ifBlank { null },
                            decision = decision.ifBlank { null }
                        )
                        
                        onSave(caseEntity, sessionEntity, nextSessionDate)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = caseNumber.isNotBlank() && clientName.isNotBlank() && opponentName.isNotBlank()
            ) {
                Text(
                    text = if (isEditing) stringResource(R.string.update_session) else stringResource(R.string.save_session),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
    
    // Date picker dialogs
    if (showDatePicker) {
        DatePickerDialog(
            initialDateMillis = sessionDate,
            onDateSelected = { dateMillis ->
                sessionDate = dateMillis
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
    
    if (showNextDatePicker) {
        DatePickerDialog(
            initialDateMillis = nextSessionDate ?: System.currentTimeMillis(),
            onDateSelected = { dateMillis ->
                nextSessionDate = dateMillis
                showNextDatePicker = false
            },
            onDismiss = { showNextDatePicker = false }
        )
    }
}

