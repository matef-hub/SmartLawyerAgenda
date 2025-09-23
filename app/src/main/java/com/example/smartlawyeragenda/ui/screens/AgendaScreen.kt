package com.example.smartlawyeragenda.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.DataThresholding
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.smartlawyeragenda.data.entities.SessionStatus
import com.example.smartlawyeragenda.repository.OverallStatistics
import com.example.smartlawyeragenda.viewmodel.AgendaUiState
import com.example.smartlawyeragenda.viewmodel.SessionWithCase
import com.example.smartlawyeragenda.ui.components.*
import com.example.smartlawyeragenda.ui.components.DateFilter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    uiState: AgendaUiState,
    onAddSessionClick: () -> Unit,
    onEditSessionClick: (SessionWithCase) -> Unit,
    onDeleteSessionClick: (SessionWithCase) -> Unit,
    onUpdateSessionStatus: (Long, SessionStatus) -> Unit,
    onSettingsClick: () -> Unit,
    onCasesClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSearchQuery: (String) -> Unit,
    onDateFilterSelected: (DateFilter) -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf(uiState.searchQuery) }
    var selectedDateFilter by remember { mutableStateOf<DateFilter?>(null) }
    var showStatistics by remember { mutableStateOf(false) }

    // Sync local search query with UI state
    LaunchedEffect(uiState.searchQuery) {
        if (searchQuery != uiState.searchQuery) {
            searchQuery = uiState.searchQuery
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "أجندة المحامي الذكية",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "إدارة الجلسات والقضايا",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showStatistics = true }) {
                            Icon(Icons.Default.DataThresholding, contentDescription = "إحصائيات", tint = Color.White)
                        }
                        IconButton(onClick = onCasesClick) {
                            Icon(Icons.Default.Balance, contentDescription = "قضايا", tint = Color.White)
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "إعدادات", tint = Color.White)
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddSessionClick,
                    containerColor = Color(0xFF1565C0),
                    contentColor = Color.White,
                    modifier = Modifier
                        .navigationBarsPadding()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة جلسة")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF7F9FC)),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search Bar
                item {
                    CustomSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = onSearchQuery,
                        onClear = { onSearchQuery("") },
                        placeholder = "ابحث في الجلسات والقضايا...",
                        modifier = Modifier
                            .padding(horizontal = 18.dp, vertical = 9.dp)
                            .fillMaxWidth()
                    )
                }

                // Date Filter
                item {
                    CustomDateFilterDropdown(
                        selectedFilter = selectedDateFilter,
                        onFilterSelected = { filter ->
                            selectedDateFilter = filter
                            onDateFilterSelected(filter)
                        },
                        modifier = Modifier
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    )
                }

                // Date Header
                item {
                    DateHeader(uiState)
                }

                // Statistics Dashboard
                if (uiState.statistics != null) {
                    item {
                        AnimatedVisibility(visible = true) {
                            StatsDashboard(uiState.statistics, Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }

                // Content Section (Loading, Empty, or Sessions)
                when {
                    uiState.isLoading -> {
                        item {
                            LoadingState(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                            )
                        }
                    }
                    uiState.sessions.isEmpty() -> {
                        item {
                            EmptyState(
                                isSearchMode = uiState.isSearchMode,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                            )
                        }
                    }
                    else -> {
                        items(uiState.sessions) { sessionWithCase ->
                            EnhancedSessionCard(
                                sessionWithCase = sessionWithCase,
                                onEdit = { onEditSessionClick(sessionWithCase) },
                                onDelete = { onDeleteSessionClick(sessionWithCase) },
                                onUpdateStatus = { newStatus ->
                                    onUpdateSessionStatus(sessionWithCase.session.sessionId, newStatus)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Statistics Dialog
        if (showStatistics && uiState.statistics != null) {
            StatisticsDialog(
                statistics = uiState.statistics,
                onDismiss = { showStatistics = false }
            )
        }
    }
}

@Composable
fun EmptyState(
    isSearchMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSearchMode) Icons.Default.Search else Icons.Default.CalendarToday,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = if (isSearchMode) "لا توجد نتائج مطابقة" else "لا توجد جلسات اليوم",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isSearchMode)
                "جرّب كلمات بحث مختلفة أو تحقق من الإملاء"
            else
                "اضغط على زر الإضافة لإضافة جلسة جديدة",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color(0xFF1565C0),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "جاري تحميل الجلسات...",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun DateHeader(uiState: AgendaUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF1565C0), Color(0xFF42A5F5))
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (uiState.isSearchMode) Icons.Default.Search else Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.Yellow,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isSearchMode) "نتائج البحث" else "جدول اليوم",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (!uiState.isSearchMode) {
                    Text(
                        uiState.gregorianDate,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        uiState.hijriDate,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    // Display current sessions count and summary
                    Text(
                        "عدد الجلسات: ${uiState.sessions.size}",
                        color = Color.Yellow,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    if (uiState.sessions.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))

                        // Show session status summary
                        val statusCounts = uiState.sessions.groupBy { it.session.status }
                        val summaryText = buildString {
                            statusCounts.forEach { (status, sessions) ->
                                when (status) {
                                    SessionStatus.SCHEDULED -> append("مجدولة: ${sessions.size}")
                                    SessionStatus.COMPLETED -> append("مكتملة: ${sessions.size}")
                                    SessionStatus.POSTPONED -> append("مؤجلة: ${sessions.size}")
                                    SessionStatus.CANCELLED -> append("ملغية: ${sessions.size}")
                                }
                                if (status != statusCounts.keys.last()) append(" • ")
                            }
                        }

                        Text(
                            text = summaryText,
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        "النتائج الموجودة: ${uiState.sessions.size}",
                        color = Color.Yellow,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (uiState.sessions.isEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color.White.copy(alpha = 0.3f))
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = if (uiState.isSearchMode) "لا توجد نتائج مطابقة" else "لا توجد جلسات اليوم",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (uiState.isSearchMode) "جرّب كلمات بحث مختلفة" else "اضغط على زر الإضافة لإضافة جلسة جديدة",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatsDashboard(statistics: OverallStatistics, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatisticsCard(
                title = "القضايا",
                value = statistics.totalCases.toString(),
                icon = Icons.Default.Balance,
                color = Color(0xFF42A5F5),
                modifier = Modifier.weight(1f)
            )
            StatisticsCard(
                title = "الجلسات",
                value = statistics.totalSessions.toString(),
                icon = Icons.Default.CalendarToday,
                color = Color(0xFF66BB6A),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatisticsCard(
                title = "القادمة",
                value = statistics.upcomingSessions.toString(),
                icon = Icons.Default.Schedule,
                color = Color(0xFFFFA726),
                modifier = Modifier.weight(1f)
            )
            StatisticsCard(
                title = "النشطة",
                value = statistics.activeCases.toString(),
                icon = Icons.Default.Info,
                color = Color(0xFFAB47BC),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "ar")
@Composable
fun AgendaScreenPreview() {
    MaterialTheme {
        AgendaScreen(
            uiState = AgendaUiState(
                searchQuery = "",
                isLoading = false,
                isSearchMode = false,
                gregorianDate = "20 سبتمبر 2025",
                hijriDate = "15 ربيع الأول 1447",
                statistics = OverallStatistics(
                    totalCases = 25,
                    activeCases = 15,
                    totalSessions = 40,
                    todaySessions = 5,
                    upcomingSessions = 10
                ),
                sessions = emptyList()
            ),
            onAddSessionClick = {},
            onEditSessionClick = {},
            onDeleteSessionClick = {},
            onUpdateSessionStatus = { _, _ -> },
            onSettingsClick = {},
            onCasesClick = {},
            onSearchQuery = {}
        )
    }
}