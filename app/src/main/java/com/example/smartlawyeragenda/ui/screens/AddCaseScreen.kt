package com.example.smartlawyeragenda.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.ui.components.EnhancedTextField
import com.example.smartlawyeragenda.ui.components.AppExposedDropdownMenu
import com.example.smartlawyeragenda.ui.components.RoleDropdown
import com.example.smartlawyeragenda.ui.theme.AppColors
import com.example.smartlawyeragenda.R
import com.example.smartlawyeragenda.ui.theme.TypographyUtils
import androidx.compose.ui.text.input.KeyboardType



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCaseScreen(
    navController: NavHostController,
    onSave: (CaseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var caseNumber by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var opponentName by remember { mutableStateOf("") }
    var clientRole by remember { mutableStateOf("") }
    var opponentRole by remember { mutableStateOf("") }
    var caseType by remember { mutableStateOf("") }
    var caseDescription by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "إضافة دعوى جديدة",
                        style = TypographyUtils.bold(MaterialTheme.typography.headlineSmall),
                        color = Color.White
                    )
               },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع",tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (validateInput(caseNumber, clientName)) {
                                isLoading = true
                                val newCase = CaseEntity(
                                    caseId = 0L,
                                    caseNumber = caseNumber.trim(),
                                    rollNumber = rollNumber.trim().takeIf { it.isNotBlank() },
                                    clientName = clientName.trim(),
                                    opponentName = opponentName.trim().takeIf { it.isNotBlank() },
                                    clientRole = clientRole.trim().takeIf { it.isNotBlank() },
                                    opponentRole = opponentRole.trim().takeIf { it.isNotBlank() },
                                    caseType = caseType.trim().takeIf { it.isNotBlank() },
                                    caseDescription = caseDescription.trim().takeIf { it.isNotBlank() },
                                    isActive = true,
                                    createdAt = System.currentTimeMillis()
                                )
                                onSave(newCase)
                            } else {
                                errorMessage = context.getString(R.string.please_fill_required_fields)
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "حفظ",tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Primary,
                    titleContentColor = AppColors.OnPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FC)) // خلفية فاتحة
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Error Message
            errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        color = Color(0xFFB71C1C),
                        modifier = Modifier.padding(16.dp),
                        style = TypographyUtils.bold(MaterialTheme.typography.bodyMedium)
                    )
                }
            }

// Case Info Section
            SectionHeader("بيانات الدعوى", Icons.Default.Description)
            CaseInputCard(
                label = "رقم الدعوى *",
                value = caseNumber,
                onValueChange = { input ->
                    val cleaned = input.trim()

                    // لو المستخدم كتب "لسنة" بنفسه، احترم إدخاله
                    if (cleaned.contains("لسنة")) {
                        caseNumber = cleaned
                        return@CaseInputCard
                    }

                    // تنظيف وتوحيد الأرقام (عربي -> إنجليزي)
                    val normalized = cleaned
                        .replace('٠', '0').replace('١', '1').replace('٢', '2')
                        .replace('٣', '3').replace('٤', '4').replace('٥', '5')
                        .replace('٦', '6').replace('٧', '7').replace('٨', '8')
                        .replace('٩', '9')

                    // فصل الأرقام والنصوص
                    val parts = normalized.split(Regex("\\s+"))
                        .filter { it.isNotBlank() }

                    caseNumber = when {
                        // حالة: رقم واحد طويل (مثل "152021" أو "6012024")
                        parts.size == 1 && parts[0].all { it.isDigit() } && parts[0].length >= 5 -> {
                            val full = parts[0]
                            // افترض آخر 4 أرقام هي السنة
                            val num = full.dropLast(4)
                            val year = full.takeLast(4)

                            // تحقق من معقولية السنة
                            val yearInt = year.toIntOrNull() ?: 0
                            if (yearInt in 1900..2100) {
                                "$num لسنة $year"
                            } else {
                                cleaned // لو السنة غير منطقية، سيب الإدخال كما هو
                            }
                        }

                        // حالة: رقمان منفصلان (مثل "15 2021")
                        parts.size == 2 &&
                                parts[0].all { it.isDigit() } &&
                                parts[1].all { it.isDigit() } -> {
                            "${parts[0]} لسنة ${parts[1]}"
                        }

                        // حالة: رقم + حروف (مثل "60 ق" أو "123 جنح")
                        parts.size == 2 &&
                                parts[0].all { it.isDigit() } &&
                                parts[1].any { it.isLetter() } -> {
                            "${parts[0]} ${parts[1]}"
                        }

                        // حالة: 3 أجزاء (مثل "15 لسنة 2021" - تنظيف)
                        parts.size == 3 &&
                                parts[1] == "لسنة" &&
                                parts[0].all { it.isDigit() } &&
                                parts[2].all { it.isDigit() } -> {
                            "${parts[0]} لسنة ${parts[2]}"
                        }

                        // أي حالة أخرى: احتفظ بالإدخال كما هو
                        else -> cleaned
                    }
                },
                isError = caseNumber.isBlank()
            )

            CaseInputCard(
                label = "رقم الرول",
                value = rollNumber,
                onValueChange = { rollNumber = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            CaseTypeDropdown(
                selectedType = caseType,
                onTypeSelected = { caseType = it }
            )

// Parties Section
            SectionHeader("أطراف القضية", Icons.Default.Group)
            CaseInputCard(
                label = "اسم الموكل *",
                value = clientName,
                onValueChange = { clientName = it },
                isError = clientName.isBlank()
            )
            RoleDropdown(
                selectedRole = clientRole,
                onRoleSelected = { clientRole = it },
                label = "صفة الموكل"
            )
            CaseInputCard(
                label = "اسم الخصم",
                value = opponentName,
                onValueChange = { opponentName = it }
            )
            RoleDropdown(
                selectedRole = opponentRole,
                onRoleSelected = { opponentRole = it },
                label = "صفة الخصم"
            )

// Description Section
            SectionHeader("تفاصيل إضافية", Icons.Default.NoteAlt)
            CaseInputCard(
                label = "وصف القضية",
                value = caseDescription,
                onValueChange = { caseDescription = it },
                minLines = 3,
                maxLines = 5
            )


            // Save Button
            ElevatedButton(
                onClick = {
                    if (validateInput(caseNumber, clientName)) {
                        isLoading = true
                        val newCase = CaseEntity(
                            caseNumber = caseNumber.trim(),
                            rollNumber = rollNumber.trim().takeIf { it.isNotBlank() },
                            clientName = clientName.trim(),
                            opponentName = opponentName.trim().takeIf { it.isNotBlank() },
                            clientRole = clientRole.trim().takeIf { it.isNotBlank() },
                            opponentRole = opponentRole.trim().takeIf { it.isNotBlank() },
                            caseType = caseType.trim().takeIf { it.isNotBlank() },
                            caseDescription = caseDescription.trim().takeIf { it.isNotBlank() }
                        )
                        onSave(newCase)
                    } else {
                        errorMessage = context.getString(R.string.please_fill_required_fields)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                )            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(context.getString(R.string.save_case), color = Color.White)
            }
        }
    }
}

@Composable
private fun CaseInputCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default // ✅ إضافة اختيار نوع الكيبورد
) {
    EnhancedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        isError = isError,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions
    )
}


@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF1565C0),
            modifier = Modifier.size(20.dp).padding(end = 8.dp)
        )
        Text(
            text = title,
            style = TypographyUtils.withAmiriFont(
                MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseTypeDropdown(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // قائمة الأنواع (ممكن تزودها/تعدلها زي ما تحب)
    val caseTypes = listOf(
        "مدني كلى",
        "مدني جزئي",
        "جنح",
        "أحوال شخصية",
        "إداري",
        "تجاري",
        "عمال",
        "عمالية",
        "اقتصادية",
        "جنايات",
        "أخرى"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        EnhancedTextField(
            value = selectedType,
            onValueChange = {},
            readOnly = true,
            label = { Text("نوع الدعوى") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true) // ✅ Updated API
                .fillMaxWidth()
        )

        AppExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            caseTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun validateInput(caseNumber: String, clientName: String): Boolean {
    return caseNumber.trim().isNotBlank() && clientName.trim().isNotBlank()
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun AddCaseScreenPreview() {
    MaterialTheme {
        AddCaseScreen(
            navController = rememberNavController(),
            onSave = {}
        )
    }
}
