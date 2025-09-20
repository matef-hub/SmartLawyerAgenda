package com.example.smartlawyeragenda.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Error dialog component for displaying errors to users
 */
@Composable
fun ErrorDialog(
    title: String = "خطأ",
    message: String,
    onDismiss: () -> Unit,
    isVisible: Boolean,
    showRetry: Boolean = false,
    onRetry: (() -> Unit)? = null
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                if (showRetry && onRetry != null) {
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("إعادة المحاولة")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("إغلاق")
                }
            }
        )
    }
}

/**
 * Warning dialog component
 */
@Composable
fun WarningDialog(
    title: String = "تحذير",
    message: String,
    onDismiss: () -> Unit,
    isVisible: Boolean,
    onConfirm: (() -> Unit)? = null,
    confirmText: String = "متابعة"
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                if (onConfirm != null) {
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(confirmText)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("إلغاء")
                }
            }
        )
    }
}

/**
 * Network error dialog
 */
@Composable
fun NetworkErrorDialog(
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ErrorDialog(
        title = "خطأ في الاتصال",
        message = "فشل في الاتصال بالخادم. يرجى التحقق من اتصال الإنترنت والمحاولة مرة أخرى.",
        onDismiss = onDismiss,
        isVisible = isVisible,
        showRetry = true,
        onRetry = onRetry
    )
}

/**
 * Authentication error dialog
 */
@Composable
fun AuthErrorDialog(
    message: String = "فشل في تسجيل الدخول. يرجى المحاولة مرة أخرى.",
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ErrorDialog(
        title = "خطأ في تسجيل الدخول",
        message = message,
        onDismiss = onDismiss,
        isVisible = isVisible,
        showRetry = true,
        onRetry = onRetry
    )
}

/**
 * Data loading error dialog
 */
@Composable
fun DataLoadingErrorDialog(
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ErrorDialog(
        title = "خطأ في تحميل البيانات",
        message = "فشل في تحميل البيانات. يرجى المحاولة مرة أخرى.",
        onDismiss = onDismiss,
        isVisible = isVisible,
        showRetry = true,
        onRetry = onRetry
    )
}

/**
 * Save error dialog
 */
@Composable
fun SaveErrorDialog(
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ErrorDialog(
        title = "خطأ في الحفظ",
        message = "فشل في حفظ البيانات. يرجى المحاولة مرة أخرى.",
        onDismiss = onDismiss,
        isVisible = isVisible,
        showRetry = true,
        onRetry = onRetry
    )
}

/**
 * Delete error dialog
 */
@Composable
fun DeleteErrorDialog(
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ErrorDialog(
        title = "خطأ في الحذف",
        message = "فشل في حذف العنصر. يرجى المحاولة مرة أخرى.",
        onDismiss = onDismiss,
        isVisible = isVisible,
        showRetry = true,
        onRetry = onRetry
    )
}

/**
 * Backup error dialog
 */
@Composable
fun BackupErrorDialog(
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ErrorDialog(
        title = "خطأ في النسخ الاحتياطي",
        message = "فشل في إنشاء النسخة الاحتياطية. يرجى المحاولة مرة أخرى.",
        onDismiss = onDismiss,
        isVisible = isVisible,
        showRetry = true,
        onRetry = onRetry
    )
}

/**
 * Export error dialog
 */
@Composable
fun ExportErrorDialog(
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ErrorDialog(
        title = "خطأ في التصدير",
        message = "فشل في تصدير البيانات. يرجى المحاولة مرة أخرى.",
        onDismiss = onDismiss,
        isVisible = isVisible,
        showRetry = true,
        onRetry = onRetry
    )
}

/**
 * Generic error dialog with custom message
 */
@Composable
fun GenericErrorDialog(
    message: String,
    onRetry: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    isVisible: Boolean
) {
    ErrorDialog(
        message = message,
        onDismiss = onDismiss,
        isVisible = isVisible,
        showRetry = onRetry != null,
        onRetry = onRetry
    )
}
