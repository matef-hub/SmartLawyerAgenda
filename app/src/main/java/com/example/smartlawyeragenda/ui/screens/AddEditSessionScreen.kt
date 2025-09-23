package com.example.smartlawyeragenda.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.data.entities.SessionStatus
import com.example.smartlawyeragenda.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSessionScreen(
    navController: NavController,
    cases: List<CaseEntity>,
    existingSession: SessionEntity? = null,
    onSave: (SessionEntity) -> Unit
) {
    // Formatter للتاريخ
    val dateFormatter = SimpleDateFormat(
        "yyyy/MM/dd",
        Locale.Builder().setLanguage("ar").setRegion("EG").build()
    )


    // --- Form state ---
    var selectedCase by remember {
        mutableStateOf(
            existingSession?.let { session ->
            cases.find { it.caseId == session.caseId }
        })
    }
    var sessionDate by remember {
        mutableStateOf(existingSession?.let { dateFormatter.format(Date(it.sessionDate)) } ?: "")
    }
    var sessionTime by remember { mutableStateOf(existingSession?.sessionTime ?: "") }
    var sessionReason by remember { mutableStateOf(existingSession?.reason ?: "") }
    var sessionDecision by remember { mutableStateOf(existingSession?.decision ?: "") }
    var sessionNotes by remember { mutableStateOf(existingSession?.notes ?: "") }

    // Pickers
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expandedCaseMenu by remember { mutableStateOf(false) }

    // Error
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // --- Animations ---
    val alphaAnimation = remember { Animatable(0f) }
    val slideAnimation = remember { Animatable(50f) }

    LaunchedEffect(Unit) {
        launch {
            alphaAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(800, easing = EaseOutCubic)
            )
        }
        launch {
            delay(200)
            slideAnimation.animateTo(
                targetValue = 0f,
                animationSpec = tween(600, easing = EaseOutCubic)
            )
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (existingSession == null) "إضافة جلسة" else "تعديل جلسة",
                            style = AppTypography.HeadlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppColors.Primary,
                        titleContentColor = AppColors.OnPrimary
                    )
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    AppColors.Background,
                                    AppColors.SurfaceVariant.copy(alpha = 0.3f)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(AppSpacing.Medium)
                            .alpha(alphaAnimation.value)
                            .offset(y = slideAnimation.value.dp),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium)
                    ) {
                        // --- Error message ---
                        errorMessage?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = AppTypography.BodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // --- Select Case ---
                        ExposedDropdownMenuBox(
                            expanded = expandedCaseMenu,
                            onExpandedChange = { expandedCaseMenu = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCase?.let { "${it.caseNumber} - ${it.clientName}" }
                                    ?: "اختر القضية",
                                onValueChange = {},
                                label = { Text("القضية المرتبطة") },
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCaseMenu)
                                },
                                colors = OutlinedTextFieldDefaults.colors(),
                                modifier = Modifier
                                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCaseMenu,
                                onDismissRequest = { expandedCaseMenu = false }
                            ) {
                                if (cases.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("لا توجد قضايا متاحة") },
                                        onClick = { },
                                        enabled = false
                                    )
                                } else {
                                    cases.forEach { case ->
                                        DropdownMenuItem(
                                            text = { Text("${case.caseNumber} - ${case.clientName}") },
                                            onClick = {
                                                selectedCase = case
                                                expandedCaseMenu = false
                                                errorMessage = null
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // --- Session Date ---
                        OutlinedTextField(
                            value = sessionDate.ifBlank { "" },
                            onValueChange = {},
                            label = { Text("تاريخ الجلسة") },
                            placeholder = { Text("اختر تاريخ الجلسة") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "اختر تاريخ"
                                    )
                                }
                            },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // --- Session Time ---
                        ExposedDropdownMenuBox(
                            expanded = showTimePicker,
                            onExpandedChange = { showTimePicker = it }
                        ) {
                            OutlinedTextField(
                                value = sessionTime.ifBlank { "" },
                                onValueChange = {},
                                label = { Text("وقت الجلسة") },
                                placeholder = { Text("اختر وقت الجلسة") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTimePicker)
                                },
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                                    .fillMaxWidth()
                            )

                            val timeOptions = listOf("صباحًا", "مساءً")
                            ExposedDropdownMenu(
                                expanded = showTimePicker,
                                onDismissRequest = { showTimePicker = false }
                            ) {
                                timeOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            sessionTime = option
                                            showTimePicker = false
                                            errorMessage = null
                                        }
                                    )
                                }
                            }
                        }
// --- Reason ---
                        OutlinedTextField(
                            value = sessionReason,
                            onValueChange = { sessionReason = it },
                            label = { Text("سبب التأجيل") },
                            placeholder = { Text("أدخل سبب التأجيل إن وجد") },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

// --- Decision ---
                        OutlinedTextField(
                            value = sessionDecision,
                            onValueChange = { sessionDecision = it },
                            label = { Text("القرار") },
                            placeholder = { Text("أدخل قرار المحكمة") },
                            leadingIcon = { Icon(Icons.Default.Gavel, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // --- Notes ---
                        OutlinedTextField(
                            value = sessionNotes,
                            onValueChange = { sessionNotes = it },
                            label = { Text("ملاحظات") },
                            placeholder = { Text("أدخل ملاحظات إضافية...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Note,
                                    contentDescription = null
                                )
                            },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(AppSpacing.Large))

                        // --- Save Button ---
                        Button(
                            onClick = {
                                // دمج رسائل الخطأ في خط واحد
                                val errors = mutableListOf<String>()
                                if (selectedCase == null) errors.add("يجب اختيار قضية")
                                if (sessionDate.isBlank()) errors.add("يجب اختيار تاريخ الجلسة")
                                if (sessionTime.isBlank()) errors.add("يجب اختيار وقت الجلسة")
                                if (errors.isNotEmpty()) {
                                    errorMessage = errors.joinToString("، ")
                                    return@Button
                                }

                                val parsedDateMillis = runCatching { dateFormatter.parse(sessionDate)?.time }
                                    .getOrNull() ?: System.currentTimeMillis()

                                val session = SessionEntity(
                                    sessionId = existingSession?.sessionId ?: 0L,
                                    caseId = selectedCase!!.caseId,
                                    sessionDate = parsedDateMillis,
                                    sessionTime = sessionTime,
                                    notes = sessionNotes,
                                    status = existingSession?.status ?: SessionStatus.SCHEDULED,
                                    fromSession = existingSession?.fromSession ?: "",
                                    reason = sessionReason,      
                                    decision = sessionDecision
                                )
                                onSave(session)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Primary
                            )
                        ) {
                            Icon(
                                imageVector = if (existingSession == null) Icons.Default.Add else Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = if (existingSession == null) "إضافة الجلسة" else "حفظ التعديلات"
                            )
                        }
                    }
                }
            }
        )

        // --- Date Picker Dialog ---
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = if (sessionDate.isNotBlank()) {
                    runCatching { dateFormatter.parse(sessionDate)?.time }.getOrNull()
                } else {
                    System.currentTimeMillis()
                }
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                sessionDate = dateFormatter.format(Date(millis))
                                errorMessage = null
                            }
                            showDatePicker = false
                        }
                    ) { Text("موافق") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("إلغاء") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun AddEditSessionScreenPreview() {
    val sampleCases = listOf(
        CaseEntity(1, "123", "10", "أحمد", "محمد", System.currentTimeMillis(), "وصف تجريبي"),
        CaseEntity(2, "124", "11", "سارة", "شركة X", System.currentTimeMillis(), "وصف آخر")
    )
    MaterialTheme {
        AddEditSessionScreen(
            navController = rememberNavController(),
            cases = sampleCases,
            onSave = {}
        )
    }
}
