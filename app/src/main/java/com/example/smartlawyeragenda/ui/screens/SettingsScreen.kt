package com.example.smartlawyeragenda.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.smartlawyeragenda.R
import com.example.smartlawyeragenda.ui.components.*
import com.example.smartlawyeragenda.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isLoggedIn: Boolean,
    isLoading: Boolean,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onExportJsonClick: () -> Unit,
    onExportCsvClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = AppTypography.HeadlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Primary
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = AppColors.OnPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
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
                    .verticalScroll(rememberScrollState())
                    .padding(AppSpacing.Medium)
                    .alpha(alphaAnimation.value)
                    .offset(y = slideAnimation.value.dp),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium)
            ) {
                // Enhanced Login Status Card
                EnhancedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 3
                ) {
                    Column(
                        modifier = Modifier.padding(AppSpacing.Medium)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = AppColors.Primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.Small))
                            Text(
                                text = "حالة تسجيل الدخول",
                                style = AppTypography.TitleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Primary
                            )
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.Medium))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isLoggedIn) "مسجل الدخول" else "غير مسجل الدخول",
                                style = AppTypography.BodyLarge,
                                color = if (isLoggedIn) AppColors.Success else AppColors.Error
                            )

                            Spacer(modifier = Modifier.width(AppSpacing.Small))

                            if (isLoggedIn) {
                                Badge(
                                    text = "✓",
                                    color = AppColors.Success
                                )
                            } else {
                                Badge(
                                    text = "✗",
                                    color = AppColors.Error
                                )
                            }
                        }

                        if (!isLoggedIn) {
                            Spacer(modifier = Modifier.height(AppSpacing.Small))
                            Text(
                                text = "يجب تسجيل الدخول لاستخدام النسخ الاحتياطية",
                                style = AppTypography.BodySmall,
                                color = AppColors.OnSurfaceVariant
                            )
                        }
                    }
                }

                // Enhanced Backup & Restore Section
                EnhancedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 3
                ) {
                    Column(
                        modifier = Modifier.padding(AppSpacing.Medium),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = AppColors.Primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.Small))
                            Text(
                                text = "النسخ الاحتياطية",
                                style = AppTypography.TitleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Primary
                            )
                        }

                        Text(
                            text = "يمكنك إنشاء نسخة احتياطية من جميع بياناتك على Google Drive واستعادتها لاحقاً",
                            style = AppTypography.BodyMedium,
                            color = AppColors.OnSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.Small))

                        // Enhanced Backup Button
                        EnhancedButton(
                            onClick = onBackupClick,
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.backup_to_drive),
                            icon = Icons.Default.CloudUpload,
                            enabled = isLoggedIn && !isLoading,
                            isLoading = isLoading,
                            variant = ButtonVariant.Primary
                        )

                        // Enhanced Restore Button
                        EnhancedButton(
                            onClick = onRestoreClick,
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.restore_from_drive),
                            icon = Icons.Default.CloudDownload,
                            enabled = isLoggedIn && !isLoading,
                            variant = ButtonVariant.Outlined
                        )
                    }
                }

                // Export Section
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "تصدير البيانات",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "يمكنك تصدير جميع بياناتك إلى ملفات JSON أو CSV للمشاركة أو النسخ الاحتياطية المحلية",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Export JSON Button
                        OutlinedButton(
                            onClick = onExportJsonClick,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تصدير JSON")
                        }

                        // Export CSV Button
                        OutlinedButton(
                            onClick = onExportCsvClick,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تصدير CSV")
                        }
                    }
                }

                // App Information
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "معلومات التطبيق",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "أجندة المحامي الذكية",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "الإصدار 1.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "تطبيق لإدارة أجندة المحاكم اليومية للمحامين",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Instructions
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "تعليمات الاستخدام",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• اضغط على + لإضافة جلسة جديدة\n" +
                                    "• يمكنك تعديل أو حذف الجلسات الموجودة\n" +
                                    "• استخدم النسخ الاحتياطية لحماية بياناتك\n" +
                                    "• التطبيق يدعم التاريخ الهجري والميلادي",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
