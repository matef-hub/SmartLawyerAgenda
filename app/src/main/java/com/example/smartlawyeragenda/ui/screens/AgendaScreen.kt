package com.example.smartlawyeragenda.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
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
    onDateSelected: (Long) -> Unit,
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
                            Icon(Icons.Default.Info, contentDescription = "إحصائيات", tint = Color.White)
                        }
                        IconButton(onClick = onCasesClick) {
                            Icon(Icons.Default.Folder, contentDescription = "قضايا", tint = Color.White)
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
                item {
                    DateHeader(uiState)
                }

                if (uiState.statistics != null) {
                    item {
                        AnimatedVisibility(visible = true) {
                            StatsDashboard(uiState.statistics, Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }

                when {
                    uiState.isLoading -> {
                        item { LoadingState() }
                    }
                    uiState.sessions.isEmpty() -> {
                        item { EmptyState(uiState.isSearchMode) }
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
fun SessionsSection(
    uiState: AgendaUiState,
    onEditSession: (SessionWithCase) -> Unit,
    onDeleteSession: (SessionWithCase) -> Unit,
    onUpdateSessionStatus: (Long, SessionStatus) -> Unit
) {
    Crossfade(targetState = uiState.isLoading to uiState.sessions) { (loading, sessions) ->
        when {
            loading -> LoadingState()
            sessions.isEmpty() -> EmptyState(uiState.isSearchMode)
            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sessions) { sessionWithCase ->
                    EnhancedSessionCard(
                        sessionWithCase = sessionWithCase,
                        onEdit = { onEditSession(sessionWithCase) },
                        onDelete = { onDeleteSession(sessionWithCase) },
                        onUpdateStatus = { newStatus ->
                            onUpdateSessionStatus(sessionWithCase.session.sessionId, newStatus)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    isSearchMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSearchMode) Icons.Default.Search else Icons.Default.CalendarToday,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier
                .size(50.dp)
                .padding(bottom = 8.dp)
        )
        val message = if (isSearchMode) "لا توجد نتائج مطابقة" else "لا توجد جلسات اليوم"
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
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
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color.Yellow,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isSearchMode) "نتائج البحث" else "📅 جدول اليوم",
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
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.White.copy(alpha = 0.3f))
                Spacer(Modifier.height(10.dp))

                if (uiState.sessions.isEmpty()) {
                    Text(
                        text = if (uiState.isSearchMode) "🔍" else "📅",
                        style = MaterialTheme.typography.displayMedium,
                        color = if (uiState.isSearchMode) Color.Yellow else Color.Cyan
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        if (uiState.isSearchMode) "لا توجد نتائج مطابقة" else "لا توجد جلسات اليوم",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (uiState.isSearchMode) "🔎 جرّب كلمات بحث مختلفة" else "➕ اضغط على زر الإضافة لإضافة جلسة جديدة",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                } else {
                    Text(
                        "📌 عدد الجلسات: ${uiState.sessions.size}",
                        color = Color.Yellow,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
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
            StatisticsCard("📂 القضايا", statistics.totalCases.toString(), Color(0xFF42A5F5), Modifier.weight(1f))
            StatisticsCard("📌 الجلسات", statistics.totalSessions.toString(), Color(0xFF66BB6A), Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatisticsCard("⏳ القادمة", statistics.upcomingSessions.toString(), Color(0xFFFFA726), Modifier.weight(1f))
            StatisticsCard("✅ النشطة", statistics.activeCases.toString(), Color(0xFFAB47BC), Modifier.weight(1f))
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
            onDateSelected = {},
            onSearchQuery = {}
        )
    }
}
