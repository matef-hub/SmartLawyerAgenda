package com.example.smartlawyeragenda.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Loading state component for various operations
 */
@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String = "جاري التحميل..."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Loading state for session operations
 */
@Composable
fun SessionLoadingState(
    modifier: Modifier = Modifier,
    operation: String = "جاري معالجة الجلسة..."
) {
    LoadingState(
        message = operation,
        modifier = modifier
    )
}

/**
 * Loading state for case operations
 */
@Composable
fun CaseLoadingState(
    modifier: Modifier = Modifier,
    operation: String = "جاري معالجة القضية..."
) {
    LoadingState(
        message = operation,
        modifier = modifier
    )
}

/**
 * Loading state for backup operations
 */
@Composable
fun BackupLoadingState(
    modifier: Modifier = Modifier,
            operation: String = "جاري إنشاء النسخة الاحتياطية..."
) {
    LoadingState(
        message = operation,
        modifier = modifier
    )
}

/**
 * Loading state for export operations
 */
@Composable
fun ExportLoadingState(
    modifier: Modifier = Modifier,
    format: String = "JSON"
) {
    LoadingState(
        message = "جاري تصدير البيانات إلى $format...",
        modifier = modifier
    )
}

/**
 * Loading state for search operations
 */
@Composable
fun SearchLoadingState(
    modifier: Modifier = Modifier
) {
    LoadingState(
        message = "جاري البحث...",
        modifier = modifier
    )
}

/**
 * Loading state for authentication
 */
@Composable
fun AuthLoadingState(
    modifier: Modifier = Modifier,
    operation: String = "جاري تسجيل الدخول..."
) {
    LoadingState(
        message = operation,
        modifier = modifier
    )
}

/**
 * Loading state with progress indicator
 */
@Composable
fun ProgressLoadingState(
    modifier: Modifier = Modifier,
            message: String = "جاري التحميل...",
    progress: Float? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (progress != null) {
                CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(48.dp),
                color = ProgressIndicatorDefaults.circularColor,
                strokeWidth = 4.dp,
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )
            }
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Loading state for list operations
 */
@Composable
fun ListLoadingState(
    modifier: Modifier = Modifier,
            itemType: String = "العناصر"
) {
    LoadingState(
        message = "جاري تحميل $itemType...",
        modifier = modifier
    )
}

/**
 * Loading state for data operations
 */
@Composable
fun DataLoadingState(
    modifier: Modifier = Modifier,
            operation: String = "جاري معالجة البيانات...",
) {
    LoadingState(
        message = operation,
        modifier = modifier
    )
}

/**
 * Loading state for network operations
 */
@Composable
fun NetworkLoadingState(
    modifier: Modifier = Modifier,
            operation: String = "جاري الاتصال بالخادم..."
) {
    LoadingState(
        message = operation,
        modifier = modifier
    )
}

/**
 * Loading state for file operations
 */
@Composable
fun FileLoadingState(
    modifier: Modifier = Modifier,
            operation: String = "جاري معالجة الملف..."
) {
    LoadingState(
        message = operation,
        modifier = modifier
    )
}
