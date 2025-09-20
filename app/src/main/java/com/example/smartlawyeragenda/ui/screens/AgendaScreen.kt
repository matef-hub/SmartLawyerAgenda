package com.example.smartlawyeragenda.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
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
    onDateSelected: (Long) -> Unit,
    onSearchQuery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf(uiState.searchQuery) }
    var selectedDateFilter by remember { mutableStateOf<DateFilter?>(null) }
    var showStatistics by remember { mutableStateOf(false) }

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
            bottomBar = {
                Button(
                    onClick = onAddSessionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة جلسة")
                    Spacer(Modifier.width(8.dp))
                    Text("إضافة جلسة")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF7F9FC)),
                contentPadding = PaddingValues(15.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                // 1️⃣ DateHeader
                item { DateHeader(uiState) }

                // 2️⃣ Stats
                uiState.statistics?.let { stats ->
                    item {
                        AnimatedVisibility(visible = true) {
                            StatsDashboard(stats, Modifier.fillMaxWidth())
                        }
                    }
                }

                // 3️⃣ Sessions Section
                when {
                    uiState.isLoading -> {
                        item { LoadingState() }
                    }
                    uiState.sessions.isEmpty() -> {
                        item { EmptyState() }
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

                // 4️⃣ Date Filter
                item {
                    CustomDateFilterDropdown(
                        selectedFilter = selectedDateFilter,
                        onFilterSelected = { filter ->
                            selectedDateFilter = filter
                            onDateSelected(filter.startDate)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 5️⃣ Search Bar
                item {
                    CustomSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = onSearchQuery,
                        placeholder = "ابحث في الجلسات والقضايا...",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 📊 Statistics Dialog
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
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

@Composable
fun StatisticsCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = color))
        }
    }
}

@Composable
fun DateHeader(uiState: AgendaUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
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
                        text = if (uiState.isSearchMode) "نتائج البحث" else " جدول اليوم",
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
fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("جاري التحميل...", color = Color.Gray)
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
