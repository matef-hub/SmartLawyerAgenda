package com.example.smartlawyeragenda.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.smartlawyeragenda.data.entities.CaseEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCaseScreen(
    navController: NavHostController,
    onSave: (CaseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var caseNumber by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var opponentName by remember { mutableStateOf("") }
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
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
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
                                    caseType = caseType.trim().takeIf { it.isNotBlank() },
                                    caseDescription = caseDescription.trim().takeIf { it.isNotBlank() },
                                    isActive = true,
                                    createdAt = System.currentTimeMillis()
                                )
                                onSave(newCase)
                            } else {
                                errorMessage = "يرجى ملء الحقول المطلوبة"
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "حفظ",tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF1565C0), Color(0xFF42A5F5))
                    )
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
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Case Info Section
            SectionHeader("📌 بيانات الدعوى")
            CaseInputCard(
                label = "رقم الدعوى *",
                value = caseNumber,
                onValueChange = { caseNumber = it },
                isError = caseNumber.isBlank()
            )
            CaseInputCard(
                label = "رقم الرول",
                value = rollNumber,
                onValueChange = { rollNumber = it }
            )
            CaseTypeDropdown(
                selectedType = caseType,
                onTypeSelected = { caseType = it }
            )

            // Parties Section
            SectionHeader("👥 أطراف القضية")
            CaseInputCard(
                label = "اسم الموكل *",
                value = clientName,
                onValueChange = { clientName = it },
                isError = clientName.isBlank()
            )
            CaseInputCard(
                label = "اسم الخصم",
                value = opponentName,
                onValueChange = { opponentName = it }
            )

            // Description Section
            SectionHeader("📝 تفاصيل إضافية")
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
                            caseType = caseType.trim().takeIf { it.isNotBlank() },
                            caseDescription = caseDescription.trim().takeIf { it.isNotBlank() }
                        )
                        onSave(newCase)
                    } else {
                        errorMessage = "يرجى ملء الحقول المطلوبة"
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF1565C0))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("حفظ القضية", color = Color.White)
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
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        shape = RoundedCornerShape(20.dp),
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        isError = isError,
        minLines = minLines,
        maxLines = maxLines
    )
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1565C0)
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
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
        OutlinedTextField(
            value = selectedType,
            onValueChange = {},
            readOnly = true,
            shape = RoundedCornerShape(20.dp),
            label = { Text("نوع الدعوى") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
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
