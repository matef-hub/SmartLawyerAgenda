package com.example.smartlawyeragenda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.data.entities.SessionStatus
import com.example.smartlawyeragenda.repository.DatabaseExport
import com.example.smartlawyeragenda.repository.MainRepository
import com.example.smartlawyeragenda.repository.OverallStatistics
import com.example.smartlawyeragenda.utils.BackupManager
import com.example.smartlawyeragenda.utils.HijriUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class SessionWithCase(
    val session: SessionEntity,
    val case: CaseEntity
) {
    fun getDisplayTitle(): String = "${case.caseNumber} - ${case.clientName}"
    fun getFormattedDate(): String = session.getFormattedDate()
    fun getStatusDisplay(): String = session.getStatusDisplay()
    fun getTimeDisplay(): String = session.sessionTime ?: "Unavailable"
}

data class AgendaUiState(
    val sessions: List<SessionWithCase> = emptyList(),
    val cases: List<CaseEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDate: Long = System.currentTimeMillis(),
    val gregorianDate: String = "",
    val hijriDate: String = "",
    val isLoggedIn: Boolean = false,
    val backupStatus: String? = null,
    val searchQuery: String = "",
    val isSearchMode: Boolean = false,
    val statistics: OverallStatistics? = null
)

private enum class SessionViewMode {
    DATE,
    SEARCH,
    UPCOMING,
    WEEK,
    MONTH
}

class AgendaViewModel(
    private val repository: MainRepository,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgendaUiState())
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    private var sessionsCollectorJob: Job? = null
    private var currentViewMode: SessionViewMode = SessionViewMode.DATE
    private var currentSearchQuery: String = ""
    private var currentWeekStart: Long = System.currentTimeMillis()
    private var currentMonthStart: Long = System.currentTimeMillis()

    init {
        _uiState.update { it.copy(isLoggedIn = backupManager.isSignedIn()) }
        updateDateInfo()
        observeCases()
        loadSessionsForDate(_uiState.value.selectedDate)
        loadStatistics()
    }

    // --------------------------- Date Management ---------------------------
    fun selectDate(dateMillis: Long) {
        currentViewMode = SessionViewMode.DATE
        currentSearchQuery = ""

        _uiState.update {
            it.copy(
                selectedDate = dateMillis,
                isSearchMode = false,
                searchQuery = ""
            )
        }
        updateDateInfo()
        loadSessionsForDate(dateMillis)
    }

    fun goToToday() {
        selectDate(System.currentTimeMillis())
    }

    private fun updateDateInfo() {
        val selectedDate = Instant.ofEpochMilli(_uiState.value.selectedDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val gregorianDate = selectedDate.format(dateFormatter)
        val hijriDate = HijriUtils.getHijriDate(java.util.Date(_uiState.value.selectedDate))

        _uiState.update {
            it.copy(
                gregorianDate = gregorianDate,
                hijriDate = hijriDate
            )
        }
    }

    // --------------------------- Session Loading ---------------------------
    private fun cancelSessionsCollector() {
        sessionsCollectorJob?.cancel()
        sessionsCollectorJob = null
    }

    private fun collectSessions(
        sessionsSource: kotlinx.coroutines.flow.Flow<List<SessionEntity>>,
        isSearchMode: Boolean,
        searchQuery: String,
        comparator: Comparator<SessionWithCase>
    ) {
        cancelSessionsCollector()

        sessionsCollectorJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    isSearchMode = isSearchMode,
                    searchQuery = searchQuery
                )
            }

            sessionsSource
                .combine(repository.getAllCases()) { sessions, cases ->
                    sessions
                        .mapNotNull { session ->
                            val case = cases.find { it.caseId == session.caseId }
                            case?.let { SessionWithCase(session, it) }
                        }
                        .sortedWith(comparator)
                }
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Unexpected error while loading sessions"
                        )
                    }
                }
                .collectLatest { sessionsWithCases ->
                    _uiState.update {
                        it.copy(
                            sessions = sessionsWithCases,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun loadSessionsForDate(dateMillis: Long) {
        currentViewMode = SessionViewMode.DATE

        collectSessions(
            sessionsSource = repository.getSessionsForDate(dateMillis),
            isSearchMode = false,
            searchQuery = "",
            comparator = compareBy { it.session.sessionTime ?: "00:00" }
        )
    }

    private fun observeCases() {
        viewModelScope.launch {
            repository.getAllCases().collect { cases ->
                _uiState.update { it.copy(cases = cases) }
            }
        }
    }

    // --------------------------- Search Functionality ---------------------------
    fun searchSessions(query: String) {
        val trimmedQuery = query.trim()
        currentSearchQuery = trimmedQuery

        if (trimmedQuery.isBlank()) {
            clearSearch()
            return
        }

        currentViewMode = SessionViewMode.SEARCH
        cancelSessionsCollector()

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        error = null,
                        isSearchMode = true,
                        searchQuery = trimmedQuery
                    )
                }

                val allCases = repository.getAllCases().first()
                val allSessions = repository.getAllSessions().first()

                val searchedCases = repository.searchCases(trimmedQuery).first()
                val searchedSessions = repository.searchSessions(trimmedQuery).first()

                val searchedCaseIds = searchedCases.map { it.caseId }.toSet()
                val searchedSessionIds = searchedSessions.map { it.sessionId }.toSet()
                val casesById = allCases.associateBy { it.caseId }

                val results = allSessions
                    .filter { session ->
                        session.caseId in searchedCaseIds || session.sessionId in searchedSessionIds
                    }
                    .mapNotNull { session ->
                        casesById[session.caseId]?.let { case -> SessionWithCase(session, case) }
                    }
                    .sortedByDescending { it.session.sessionDate }

                _uiState.update {
                    it.copy(
                        sessions = results,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Search failed"
                    )
                }
            }
        }
    }

    fun clearSearch() {
        currentSearchQuery = ""
        currentViewMode = SessionViewMode.DATE

        _uiState.update {
            it.copy(
                searchQuery = "",
                isSearchMode = false
            )
        }

        loadSessionsForDate(_uiState.value.selectedDate)
    }

    // --------------------------- CRUD Operations ---------------------------
    fun saveSession(
        case: CaseEntity,
        session: SessionEntity,
        createNextSession: Boolean = false,
        nextSessionDate: Long? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                repository.saveCaseWithSession(
                    case = case,
                    session = session,
                    createNextSession = createNextSession,
                    nextSessionDate = nextSessionDate
                )

                refreshCurrentView()
                _uiState.update { it.copy(isLoading = false) }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to save session"
                    )
                }
            }
        }
    }

    fun updateSessionStatus(sessionId: Long, newStatus: SessionStatus, notes: String? = null) {
        viewModelScope.launch {
            try {
                val session = repository.getSessionById(sessionId)
                if (session == null) {
                    _uiState.update { it.copy(error = "Session not found") }
                    return@launch
                }

                val updatedSession = session.copy(status = newStatus, notes = notes)
                repository.updateSession(updatedSession)
                refreshCurrentView()
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(error = exception.message ?: "Failed to update session status")
                }
            }
        }
    }

    fun deleteSession(session: SessionEntity) {
        viewModelScope.launch {
            try {
                repository.deleteSession(session)
                refreshCurrentView()
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(error = exception.message ?: "Failed to delete session")
                }
            }
        }
    }

    suspend fun getCaseById(caseId: Long): CaseEntity? {
        return try {
            repository.getCaseById(caseId)
        } catch (exception: Exception) {
            _uiState.update { it.copy(error = exception.message ?: "Failed to load case") }
            null
        }
    }

    suspend fun getSessionById(sessionId: Long): SessionEntity? {
        return try {
            repository.getSessionById(sessionId)
        } catch (exception: Exception) {
            _uiState.update { it.copy(error = exception.message ?: "Failed to load session") }
            null
        }
    }

    fun deleteCaseWithSessions(caseId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteCaseWithSessions(caseId)
                refreshCurrentView()
            } catch (exception: Exception) {
                _uiState.update { it.copy(error = exception.message ?: "Failed to delete case") }
            }
        }
    }

    fun saveCase(case: CaseEntity) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                if (!case.isValid()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Case data is invalid"
                        )
                    }
                    return@launch
                }

                if (repository.isCaseNumberExists(case.caseNumber, case.caseId)) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Case number already exists"
                        )
                    }
                    return@launch
                }

                if (case.caseId == 0L) {
                    repository.insertCase(case)
                } else {
                    repository.updateCase(case)
                }

                refreshCurrentView()
                _uiState.update { it.copy(isLoading = false) }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to save case"
                    )
                }
            }
        }
    }

    fun toggleCaseStatus(caseId: Long) {
        viewModelScope.launch {
            try {
                val case = repository.getCaseById(caseId)
                if (case == null) {
                    _uiState.update { it.copy(error = "Case not found") }
                    return@launch
                }

                repository.updateCase(case.copy(isActive = !case.isActive))
                refreshCurrentView()
            } catch (exception: Exception) {
                _uiState.update { it.copy(error = exception.message ?: "Failed to change case status") }
            }
        }
    }

    // --------------------------- Statistics ---------------------------
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                val stats = repository.getOverallStatistics()
                _uiState.update { it.copy(statistics = stats) }
            } catch (_: Exception) {
                _uiState.update { it.copy(statistics = OverallStatistics(0, 0, 0, 0, 0)) }
            }
        }
    }

    fun refreshStatistics() {
        loadStatistics()
    }

    // --------------------------- Backup and Restore ---------------------------
    fun initializeGoogleDriveAccount(accountName: String): Boolean {
        val initialized = backupManager.initializeWithAccount(accountName)

        _uiState.update {
            it.copy(
                isLoggedIn = initialized,
                error = if (initialized) null else "Failed to initialize Google account"
            )
        }

        return initialized
    }

    fun backupToDrive() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, isLoggedIn = backupManager.isSignedIn()) }

                if (!backupManager.isSignedIn()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Please sign in before creating a backup"
                        )
                    }
                    return@launch
                }

                val exportData = repository.exportData()
                val result = backupManager.backupToDrive(exportData.cases, exportData.sessions)

                result.fold(
                    onSuccess = { message ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isLoggedIn = true,
                                backupStatus = message
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Backup failed"
                            )
                        }
                    }
                )
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Backup failed"
                    )
                }
            }
        }
    }

    fun restoreFromDrive() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, isLoggedIn = backupManager.isSignedIn()) }

                if (!backupManager.isSignedIn()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Please sign in before restoring backup"
                        )
                    }
                    return@launch
                }

                val result = backupManager.restoreFromDrive()
                result.fold(
                    onSuccess = { backupData ->
                        val exportData = DatabaseExport(
                            cases = backupData.cases,
                            sessions = backupData.sessions,
                            exportDate = System.currentTimeMillis()
                        )

                        val importResult = repository.importData(exportData)

                        if (importResult.success) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoggedIn = true,
                                    backupStatus = "Restore complete: ${importResult.importedCases} cases, ${importResult.importedSessions} sessions"
                                )
                            }
                            refreshAllViews()
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = importResult.error ?: "Restore failed"
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Restore failed"
                            )
                        }
                    }
                )
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Restore failed"
                    )
                }
            }
        }
    }

    suspend fun exportLocalBackup(): DatabaseExport {
        return repository.exportData()
    }

    // --------------------------- State Management ---------------------------
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearBackupStatus() {
        _uiState.update { it.copy(backupStatus = null) }
    }

    fun isSignedInToGoogle(): Boolean = backupManager.isSignedIn()

    fun signOutFromGoogle() {
        viewModelScope.launch {
            try {
                backupManager.signOut()
                _uiState.update {
                    it.copy(
                        isLoggedIn = false,
                        backupStatus = "Signed out successfully"
                    )
                }
            } catch (exception: Exception) {
                _uiState.update { it.copy(error = exception.message ?: "Sign-out failed") }
            }
        }
    }

    // --------------------------- Quick Actions ---------------------------
    fun getUpcomingSessions() {
        currentViewMode = SessionViewMode.UPCOMING

        collectSessions(
            sessionsSource = repository.getUpcomingSessions(),
            isSearchMode = true,
            searchQuery = "Upcoming sessions",
            comparator = compareBy { it.session.sessionDate }
        )
    }

    fun getSessionsForWeek(weekStart: Long) {
        currentViewMode = SessionViewMode.WEEK
        currentWeekStart = weekStart

        collectSessions(
            sessionsSource = repository.getSessionsForWeek(weekStart),
            isSearchMode = true,
            searchQuery = "Week sessions",
            comparator = compareBy { it.session.sessionDate }
        )
    }

    fun getSessionsForMonth(monthStart: Long) {
        currentViewMode = SessionViewMode.MONTH
        currentMonthStart = monthStart

        collectSessions(
            sessionsSource = repository.getSessionsForMonth(monthStart),
            isSearchMode = true,
            searchQuery = "Month sessions",
            comparator = compareBy { it.session.sessionDate }
        )
    }

    // --------------------------- Validation Helpers ---------------------------
    suspend fun validateSession(session: SessionEntity): String? {
        return try {
            if (!session.isValid()) {
                return "Session data is invalid"
            }

            if (repository.isSessionExists(session.caseId, session.sessionDate, session.sessionId)) {
                return "A session already exists for this case and date"
            }

            null
        } catch (exception: Exception) {
            exception.message ?: "Validation failed"
        }
    }

    // --------------------------- Sample Data Management ---------------------------
    fun populateSampleData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                repository.populateSampleData()
                refreshCurrentView()
                _uiState.update { it.copy(isLoading = false) }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to populate sample data"
                    )
                }
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                repository.clearAllData()
                refreshAllViews()
                _uiState.update { it.copy(isLoading = false) }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to clear data"
                    )
                }
            }
        }
    }

    // --------------------------- Helper Methods ---------------------------
    private fun refreshCurrentView() {
        when (currentViewMode) {
            SessionViewMode.DATE -> loadSessionsForDate(_uiState.value.selectedDate)
            SessionViewMode.SEARCH -> searchSessions(currentSearchQuery)
            SessionViewMode.UPCOMING -> getUpcomingSessions()
            SessionViewMode.WEEK -> getSessionsForWeek(currentWeekStart)
            SessionViewMode.MONTH -> getSessionsForMonth(currentMonthStart)
        }

        loadStatistics()
    }

    private fun refreshAllViews() {
        refreshCurrentView()
    }

    override fun onCleared() {
        cancelSessionsCollector()
        super.onCleared()
    }
}
