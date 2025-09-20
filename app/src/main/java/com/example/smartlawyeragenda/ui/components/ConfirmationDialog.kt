package com.example.smartlawyeragenda.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Confirmation dialog for destructive actions
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "تأكيد",
    dismissText: String = "إلغاء",
    isDestructive: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = if (isDestructive) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    } else {
                        ButtonDefaults.buttonColors()
                    }
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            }
        )
    }
}

/**
 * Confirmation dialog for deleting sessions
 */
@Composable
fun DeleteSessionDialog(
    sessionTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ConfirmationDialog(
        title = "حذف الجلسة",
        message = "هل أنت متأكد من حذف الجلسة \"$sessionTitle\"؟\n\nلا يمكن التراجع عن هذا الإجراء.",
        confirmText = "حذف",
        dismissText = "إلغاء",
        isDestructive = true,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isVisible = isVisible
    )
}

/**
 * Confirmation dialog for deleting cases
 */
@Composable
fun DeleteCaseDialog(
    caseTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ConfirmationDialog(
        title = "حذف القضية",
        message = "هل أنت متأكد من حذف القضية \"$caseTitle\"؟\n\nسيتم حذف جميع الجلسات المرتبطة بهذه القضية.\n\nلا يمكن التراجع عن هذا الإجراء.",
        confirmText = "حذف",
        dismissText = "إلغاء",
        isDestructive = true,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isVisible = isVisible
    )
}

/**
 * Confirmation dialog for case status toggle
 */
@Composable
fun ToggleCaseStatusDialog(
    caseTitle: String,
    isCurrentlyActive: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    val action = if (isCurrentlyActive) "إغلاق" else "تفعيل"
    val message = if (isCurrentlyActive) {
        "هل أنت متأكد من إغلاق القضية \"$caseTitle\"؟\n\nلن تظهر القضية في القائمة النشطة."
    } else {
        "هل أنت متأكد من تفعيل القضية \"$caseTitle\"؟\n\nستظهر القضية في القائمة النشطة."
    }
    
    ConfirmationDialog(
        title = "$action القضية",
        message = message,
        confirmText = action,
        dismissText = "إلغاء",
        isDestructive = false,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isVisible = isVisible
    )
}

/**
 * Confirmation dialog for backup operations
 */
@Composable
fun BackupConfirmationDialog(
    operation: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ConfirmationDialog(
        title = "تأكيد $operation",
        message = "هل أنت متأكد من $operation؟\n\nقد تستغرق العملية بعض الوقت.",
        confirmText = "متابعة",
        dismissText = "إلغاء",
        isDestructive = false,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isVisible = isVisible
    )
}

/**
 * Confirmation dialog for export operations
 */
@Composable
fun ExportConfirmationDialog(
    format: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ConfirmationDialog(
        title = "تصدير البيانات",
        message = "هل تريد تصدير جميع البيانات إلى ملف $format؟\n\nسيتم حفظ الملف في مجلد التطبيق.",
        confirmText = "تصدير",
        dismissText = "إلغاء",
        isDestructive = false,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isVisible = isVisible
    )
}
