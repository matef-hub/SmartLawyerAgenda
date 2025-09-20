package com.example.smartlawyeragenda.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smartlawyeragenda.data.entities.SessionStatus
import com.example.smartlawyeragenda.repository.OverallStatistics
import com.example.smartlawyeragenda.viewmodel.AgendaUiState
import com.example.smartlawyeragenda.viewmodel.SessionWithCase
import com.example.smartlawyeragenda.ui.components.*

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
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
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
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة جلسة")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F9FC))
        ) {
            // Search bar
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                CustomSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = onSearchQuery,
                    placeholder = "ابحث في الجلسات والقضايا...",
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Date filter chips
            CustomDateFilterChips(
                selectedFilter = selectedDateFilter,
                onFilterSelected = { filter ->
                    selectedDateFilter = filter
                    onDateSelected(filter.startDate)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Statistics dashboard grid
            uiState.statistics?.let { stats ->
                StatsDashboard(stats, Modifier.padding(horizontal = 16.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date header
            DateHeaderCard(uiState)

            Spacer(modifier = Modifier.height(12.dp))

            // Sessions list
            when {
                uiState.isLoading -> LoadingState()
                uiState.sessions.isEmpty() -> EmptyState(uiState.isSearchMode)
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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

    // Statistics dialog
    if (showStatistics && uiState.statistics != null) {
        StatisticsDialog(
            statistics = uiState.statistics,
            onDismiss = { showStatistics = false }
        )
    }
}

@Composable
fun StatsDashboard(statistics: OverallStatistics, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("📂 القضايا", statistics.totalCases.toString(), Color(0xFF42A5F5), Modifier.weight(1f))
            StatCard("📌 الجلسات", statistics.totalSessions.toString(), Color(0xFF66BB6A), Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("⏳ القادمة", statistics.upcomingSessions.toString(), Color(0xFFFFA726), Modifier.weight(1f))
            StatCard("✅ المكتملة", statistics.activeCases.toString(), Color(0xFFAB47BC), Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
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
fun DateHeaderCard(uiState: AgendaUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (uiState.isSearchMode) "نتائج البحث" else "اليوم",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (!uiState.isSearchMode) {
                Spacer(Modifier.height(4.dp))
                Text(uiState.gregorianDate, color = Color.White)
                Text(uiState.hijriDate, color = Color.White.copy(alpha = 0.9f))
            }
            Spacer(Modifier.height(6.dp))
            Text("عدد الجلسات: ${uiState.sessions.size}", color = Color.White)
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

@Composable
fun EmptyState(isSearchMode: Boolean) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = if (isSearchMode) "🔍" else "📅", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                if (isSearchMode) "لا توجد نتائج للبحث" else "لا توجد جلسات اليوم",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (isSearchMode) "جرب كلمات بحث مختلفة" else "اضغط على + لإضافة جلسة جديدة",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}
