package com.example.smartlawyeragenda.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.data.entities.SessionStatus
import com.example.smartlawyeragenda.ui.components.*
import com.example.smartlawyeragenda.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSessionScreen(
    navController: NavController,
    caseId: Long,
    existingSession: SessionEntity? = null,
    onSave: (SessionEntity) -> Unit
) {
    // Formatter للتاريخ
    val dateFormatter = remember {
        SimpleDateFormat("yyyy/MM/dd", Locale.Builder().setLanguage("ar").setRegion("EG").build())
    }

    // Form state
    var sessionDate by remember {
        mutableStateOf(
            existingSession?.let { dateFormatter.format(Date(it.sessionDate)) } ?: ""
        )
    }
    var sessionTime by remember { mutableStateOf(existingSession?.sessionTime ?: "") }
    var sessionNotes by remember { mutableStateOf(existingSession?.notes ?: "") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    // Animation state
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimation = remember { Animatable(0f) }
    val slideAnimation = remember { Animatable(50f) }
    
    LaunchedEffect(key1 = true) {
        startAnimation = true
        
        launch {
            alphaAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = EaseOutCubic
                )
            )
        }
        
        launch {
            delay(200)
            slideAnimation.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = EaseOutCubic
                )
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
                        containerColor = AppColors.Primary
                    )
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
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
                        // Session Date with enhanced design
                        EnhancedTextField(
                            value = sessionDate,
                            onValueChange = { },
                            label = "تاريخ الجلسة",
                            placeholder = "اختر تاريخ الجلسة",
                            leadingIcon = Icons.Default.CalendarToday,
                            trailingIcon = Icons.Default.ArrowDropDown,
                            onTrailingIconClick = { showDatePicker = true },
                            enabled = false,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Session Time with enhanced design
                        ExposedDropdownMenuBox(
                            expanded = showTimePicker,
                            onExpandedChange = { showTimePicker = !showTimePicker }
                        ) {
                            EnhancedTextField(
                                value = sessionTime.ifBlank { "اختر وقت الجلسة" },
                                onValueChange = { },
                                label = "وقت الجلسة",
                                placeholder = "اختر وقت الجلسة",
                                leadingIcon = Icons.Default.AccessTime,
                                trailingIcon = Icons.Default.ArrowDropDown,
                                onTrailingIconClick = { showTimePicker = !showTimePicker },
                                enabled = false,
                                modifier = Modifier
                                    .menuAnchor(
                                        type = MenuAnchorType.PrimaryEditable,
                                        enabled = true
                                    )
                                    .fillMaxWidth()
                            )

                            val options = listOf("صباحاً", "مساءً")
                            ExposedDropdownMenu(
                                expanded = showTimePicker,
                                onDismissRequest = { showTimePicker = false }
                            ) {
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            sessionTime = option
                                            showTimePicker = false
                                        }
                                    )
                                }
                            }
                        }

                        // Notes with enhanced design
                        EnhancedTextField(
                            value = sessionNotes,
                            onValueChange = { sessionNotes = it },
                            label = "ملاحظات",
                            placeholder = "أدخل ملاحظات إضافية...",
                            leadingIcon = Icons.AutoMirrored.Filled.Note,
                            singleLine = false,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.Large))

                        // Enhanced Save Button
                        EnhancedButton(
                            onClick = {
                                val session = SessionEntity(
                                    sessionId = existingSession?.sessionId ?: 0L,
                                    caseId = caseId,
                                    sessionDate = dateFormatter.parse(sessionDate)?.time
                                        ?: System.currentTimeMillis(),
                                    sessionTime = sessionTime,
                                    notes = sessionNotes,
                                    status = existingSession?.status ?: SessionStatus.SCHEDULED
                                )
                                onSave(session)
                                navController.popBackStack()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            text = if (existingSession == null) "إضافة الجلسة" else "حفظ التعديلات",
                            icon = if (existingSession == null) Icons.Default.Add else Icons.Default.Save,
                            variant = ButtonVariant.Primary
                        )
                    }
                }
            }
        )

        // DatePickerDialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("موافق")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("إلغاء")
                    }
                }
            ) {
                val datePickerState = rememberDatePickerState()
                DatePicker(state = datePickerState)

                LaunchedEffect(datePickerState.selectedDateMillis) {
                    datePickerState.selectedDateMillis?.let { millis ->
                        sessionDate = dateFormatter.format(Date(millis))
                    }
                }
            }
        }
    }
}
