package com.example.smartlawyeragenda.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.ui.components.RoleDropdown
import com.example.smartlawyeragenda.ui.theme.TypographyUtils
import com.example.smartlawyeragenda.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCaseScreen(
    navController: androidx.navigation.NavHostController,
    existingCase: CaseEntity,
    onSave: (CaseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var caseNumber by remember { mutableStateOf(existingCase.caseNumber) }
    var rollNumber by remember { mutableStateOf(existingCase.rollNumber ?: "") }
    var clientName by remember { mutableStateOf(existingCase.clientName) }
    var opponentName by remember { mutableStateOf(existingCase.opponentName ?: "") }
    var clientRole by remember { mutableStateOf(existingCase.clientRole ?: "") }
    var opponentRole by remember { mutableStateOf(existingCase.opponentRole ?: "") }
    var caseType by remember { mutableStateOf(existingCase.caseType ?: "") }
    var caseDescription by remember { mutableStateOf(existingCase.caseDescription ?: "") }
    var isActive by remember { mutableStateOf(existingCase.isActive) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "تعديل القضية",
                    style = TypographyUtils.bold(MaterialTheme.typography.titleLarge)
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        if (validateInput(caseNumber, clientName)) {
                            isLoading = true
                            val updatedCase = existingCase.copy(
                                caseNumber = caseNumber.trim(),
                                rollNumber = rollNumber.trim().takeIf { it.isNotBlank() },
                                clientName = clientName.trim(),
                                opponentName = opponentName.trim().takeIf { it.isNotBlank() },
                                clientRole = clientRole.trim().takeIf { it.isNotBlank() },
                                opponentRole = opponentRole.trim().takeIf { it.isNotBlank() },
                                caseType = caseType.trim().takeIf { it.isNotBlank() },
                                caseDescription = caseDescription.trim().takeIf { it.isNotBlank() },
                                isActive = isActive,
                                createdAt = existingCase.createdAt
                            )
                            onSave(updatedCase)
                        } else {
                            errorMessage = context.getString(R.string.please_fill_required_fields)
                        }
                    },
                    enabled = !isLoading
                ) {
                    Icon(Icons.Default.Save, contentDescription = "حفظ")
                }
            }
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Message
            errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Case Number (Required)
            OutlinedTextField(
                value = caseNumber,
                onValueChange = { caseNumber = it },
                label = { Text("رقم القضية *") },
                modifier = Modifier.fillMaxWidth(),
                isError = caseNumber.isBlank()
            )

            // Roll Number (Optional)
            OutlinedTextField(
                value = rollNumber,
                onValueChange = { rollNumber = it },
                label = { Text("رقم الرول") },
                modifier = Modifier.fillMaxWidth()
            )

            // Client Name (Required)
            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text("اسم الموكل *") },
                modifier = Modifier.fillMaxWidth(),
                isError = clientName.isBlank()
            )

            // Client Role
            RoleDropdown(
                selectedRole = clientRole,
                onRoleSelected = { clientRole = it },
                label = "صفة الموكل"
            )

            // Opponent Name (Optional)
            OutlinedTextField(
                value = opponentName,
                onValueChange = { opponentName = it },
                label = { Text("اسم الخصم") },
                modifier = Modifier.fillMaxWidth()
            )

            // Opponent Role
            RoleDropdown(
                selectedRole = opponentRole,
                onRoleSelected = { opponentRole = it },
                label = "صفة الخصم"
            )

            // Case Type (Optional)
            OutlinedTextField(
                value = caseType,
                onValueChange = { caseType = it },
                label = { Text("نوع القضية") },
                modifier = Modifier.fillMaxWidth()
            )

            // Case Description (Optional)
            OutlinedTextField(
                value = caseDescription,
                onValueChange = { caseDescription = it },
                label = { Text("وصف القضية") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Active Status Switch
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حالة القضية",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                }
                Text(
                    text = if (isActive) "نشطة" else "غير نشطة",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Save Button
            Button(
                onClick = {
                    if (validateInput(caseNumber, clientName)) {
                        isLoading = true
                        val updatedCase = existingCase.copy(
                            caseNumber = caseNumber.trim(),
                            rollNumber = rollNumber.trim().takeIf { it.isNotBlank() },
                            clientName = clientName.trim(),
                            opponentName = opponentName.trim().takeIf { it.isNotBlank() },
                            clientRole = clientRole.trim().takeIf { it.isNotBlank() },
                            opponentRole = opponentRole.trim().takeIf { it.isNotBlank() },
                            caseType = caseType.trim().takeIf { it.isNotBlank() },
                            caseDescription = caseDescription.trim().takeIf { it.isNotBlank() },
                            isActive = isActive
                        )
                        onSave(updatedCase)
                    } else {
                        errorMessage = context.getString(R.string.please_fill_required_fields)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(context.getString(R.string.save_changes))
            }
        }
    }
}

private fun validateInput(caseNumber: String, clientName: String): Boolean {
    return caseNumber.trim().isNotBlank() && clientName.trim().isNotBlank()
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun EditCaseScreenPreview() {
    MaterialTheme {
        EditCaseScreen(
            navController = androidx.navigation.compose.rememberNavController(),
            existingCase = CaseEntity(
                caseId = 1L,
                caseNumber = "123/2024",
                clientName = "أحمد محمد",
                rollNumber = "456",
                opponentName = "شركة ABC",
                clientRole = "مدعي",
                opponentRole = "مدعى عليه",
                caseType = "تجاري",
                caseDescription = "قضية تجارية تتعلق بعقد"
            ),
            onSave = {}
        )
    }
}
