package com.example.smartlawyeragenda.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.ui.components.SearchBar
import com.example.smartlawyeragenda.ui.components.EnhancedCaseCard
import com.example.smartlawyeragenda.ui.theme.*
import com.example.smartlawyeragenda.repository.CaseStatistics
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasesScreen(
    cases: List<CaseEntity>,
    caseStatistics: Map<Long, CaseStatistics>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onAddCaseClick: () -> Unit,
    onCaseClick: (CaseEntity) -> Unit,
    onEditCaseClick: (CaseEntity) -> Unit,
    onDeleteCaseClick: (CaseEntity) -> Unit,
    onToggleCaseStatus: (CaseEntity) -> Unit,
    onSearchQuery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showInactiveCases by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

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

    // Filter cases based on active status
    val filteredCases = if (showInactiveCases) cases else cases.filter { it.isActive }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "إدارة القضايا",
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
                },
                actions = {
                    // Toggle inactive cases visibility
                    IconButton(onClick = { showInactiveCases = !showInactiveCases }) {
                        Icon(
                            imageVector = if (showInactiveCases) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showInactiveCases) "إخفاء القضايا المغلقة" else "إظهار القضايا المغلقة",
                            tint = AppColors.OnPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCaseClick,
                containerColor = AppColors.Primary,
                contentColor = AppColors.OnPrimary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = AppElevation.Level4
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة قضية"
                )
            }
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
                    .padding(AppSpacing.Medium)
            ) {
                // Enhanced Search Bar with animation
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = onSearchQuery,
                    placeholder = "البحث في القضايا...",
                    modifier = Modifier
                        .alpha(alphaAnimation.value)
                        .offset(y = slideAnimation.value.dp)
                )

                // Enhanced Cases Statistics Summary with animation
                if (cases.isNotEmpty()) {
                    CasesStatisticsSummary(
                        totalCases = cases.size,
                        activeCases = cases.count { it.isActive },
                        inactiveCases = cases.count { !it.isActive },
                        modifier = Modifier
                            .padding(vertical = AppSpacing.Small)
                            .alpha(alphaAnimation.value)
                            .offset(y = slideAnimation.value.dp)
                    )
                }

                // Filter indicator
                if (!showInactiveCases && cases.any { !it.isActive }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "يتم عرض القضايا النشطة فقط (${cases.count { it.isActive }} من ${cases.size})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Cases list
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "جاري تحميل القضايا...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    filteredCases.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⚖️",
                                    style = MaterialTheme.typography.displayLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (cases.isEmpty()) "لا توجد قضايا" else "لا توجد قضايا نشطة",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (cases.isEmpty())
                                        "اضغط على + لإضافة قضية جديدة"
                                    else
                                        "اضغط على أيقونة العين لإظهار القضايا المغلقة",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredCases) { case ->
                                EnhancedCaseCard(
                                    case = case,
                                    statistics = caseStatistics[case.caseId],
                                    onClick = { onCaseClick(case) },
                                    onEditClick = { onEditCaseClick(case) },
                                    onDeleteClick = { onDeleteCaseClick(case) },
                                    onToggleStatus = { onToggleCaseStatus(case) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CasesStatisticsSummary(
    totalCases: Int,
    activeCases: Int,
    inactiveCases: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticItem(
                title = "إجمالي القضايا",
                value = totalCases.toString(),
                color = MaterialTheme.colorScheme.primary
            )

            StatisticItem(
                title = "القضايا النشطة",
                value = activeCases.toString(),
                color = MaterialTheme.colorScheme.tertiary
            )

            if (inactiveCases > 0) {
                StatisticItem(
                    title = "القضايا المغلقة",
                    value = inactiveCases.toString(),
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun StatisticItem(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
