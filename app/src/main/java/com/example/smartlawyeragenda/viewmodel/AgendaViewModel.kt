package com.example.smartlawyeragenda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.data.entities.SessionStatus
import com.example.smartlawyeragenda.repository.MainRepository
import com.example.smartlawyeragenda.repository.OverallStatistics
import com.example.smartlawyeragenda.repository.DatabaseExport
import com.example.smartlawyeragenda.utils.BackupManager
import com.example.smartlawyeragenda.utils.HijriUtils
import kotlinx.coroutines.flow.*
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
    fun getTimeDisplay(): String = session.sessionTime ?: "غير محدد"
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

class AgendaViewModel(
    private val repository: MainRepository,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgendaUiState())
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    init {
        updateDateInfo()
        loadSessionsForDate(_uiState.value.selectedDate)
        loadStatistics()
        observeCases()
    }

    // --------------------------- 🗓 Date Management ---------------------------
    fun selectDate(dateMillis: Long) {
        _uiState.update { it.copy(
            selectedDate = dateMillis,
            isSearchMode = false,
            searchQuery = ""
        ) }
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

        _uiState.update { it.copy(
            gregorianDate = gregorianDate,
            hijriDate = hijriDate
        ) }
    }

    // --------------------------- 📂 Session Loading ---------------------------
    private fun loadSessionsForDate(dateMillis: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                repository.getSessionsForDate(dateMillis)
                    .combine(repository.getAllCases()) { sessions, cases ->
                        sessions.mapNotNull { session ->
                            val case = cases.find { it.caseId == session.caseId }
                            case?.let { SessionWithCase(session, it) }
                        }.sortedBy { it.session.sessionTime ?: "00:00" }
                    }
                    .collect { sessionsWithCases ->
                        _uiState.update { it.copy(
                            sessions = sessionsWithCases,
                            isLoading = false
                        ) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "حدث خطأ غير متوقع"
                ) }
            }
        }
    }

    private fun observeCases() {
        viewModelScope.launch {
            repository.getAllCases().collect { cases ->
                _uiState.update { it.copy(cases = cases) }
            }
        }
    }

    // --------------------------- 🔍 Search Functionality ---------------------------
    fun searchSessions(query: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(
                    isLoading = true,
                    searchQuery = query,
                    isSearchMode = query.isNotBlank()
                ) }

                if (query.isBlank()) {
                    loadSessionsForDate(_uiState.value.selectedDate)
                    return@launch
                }

                val searchedCases = repository.searchCases(query).first()
                val searchedSessions = repository.searchSessions(query).first()
                val allCases = repository.getAllCases().first()

                val sessionsWithCases = mutableListOf<SessionWithCase>()

                // Add sessions from case search
                searchedCases.forEach { case ->
                    repository.getSessionsByCaseId(case.caseId).first().forEach { session ->
                        sessionsWithCases.add(SessionWithCase(session, case))
                    }
                }

                // Add sessions from session search
                searchedSessions.forEach { session ->
                    val case = allCases.find { it.caseId == session.caseId }
                    case?.let {
                        if (sessionsWithCases.none { it -> it.session.sessionId == session.sessionId }) {
                            sessionsWithCases.add(SessionWithCase(session, it))
                        }
                    }
                }

                _uiState.update { it.copy(
                    sessions = sessionsWithCases.sortedByDescending { it -> it.session.sessionDate },
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في البحث"
                ) }
            }
        }
    }

    fun clearSearch() {
        _uiState.update { it.copy(
            searchQuery = "",
            isSearchMode = false
        ) }
        loadSessionsForDate(_uiState.value.selectedDate)
    }

    // --------------------------- 💾 CRUD Operations ---------------------------
    fun saveSession(
        case: CaseEntity,
        session: SessionEntity,
        createNextSession: Boolean = false,
        nextSessionDate: Long? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                repository.saveCaseWithSession(case, session, createNextSession, nextSessionDate)

                refreshCurrentView()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في حفظ الجلسة"
                ) }
            }
        }
    }

    fun updateSessionStatus(sessionId: Long, newStatus: SessionStatus, notes: String? = null) {
        viewModelScope.launch {
            try {
                val session = repository.getSessionById(sessionId)
                session?.let {
                    val updatedSession = it.copy(status = newStatus, notes = notes)
                    repository.updateSession(updatedSession)
                    refreshCurrentView()
                } ?: run {
                    _uiState.update { it.copy(error = "الجلسة غير موجودة") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: "فشل في تحديث حالة الجلسة"
                ) }
            }
        }
    }

    fun deleteSession(session: SessionEntity) {
        viewModelScope.launch {
            try {
                repository.deleteSession(session)
                refreshCurrentView()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: "فشل في حذف الجلسة"
                ) }
            }
        }
    }

    suspend fun getCaseById(caseId: Long): CaseEntity? {
        return try {
            repository.getCaseById(caseId)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message ?: "فشل في جلب بيانات القضية") }
            null
        }
    }

    suspend fun getSessionById(sessionId: Long): SessionEntity? {
        return try {
            repository.getSessionById(sessionId)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message ?: "فشل في جلب بيانات الجلسة") }
            null
        }
    }

    fun deleteCaseWithSessions(caseId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteCaseWithSessions(caseId)
                refreshCurrentView()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: "فشل في حذف القضية"
                ) }
            }
        }
    }

    fun saveCase(case: CaseEntity) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                if (!case.isValid()) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "بيانات القضية غير صالحة"
                    ) }
                    return@launch
                }

                if (repository.isCaseNumberExists(case.caseNumber, case.caseId)) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "رقم القضية موجود بالفعل"
                    ) }
                    return@launch
                }

                if (case.caseId == 0L) {
                    repository.insertCase(case)
                } else {
                    repository.updateCase(case)
                }

                refreshCurrentView()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في حفظ القضية"
                ) }
            }
        }
    }

    fun toggleCaseStatus(caseId: Long) {
        viewModelScope.launch {
            try {
                val case = repository.getCaseById(caseId)
                case?.let {
                    val updatedCase = it.copy(isActive = !it.isActive)
                    repository.updateCase(updatedCase)
                    refreshCurrentView()
                } ?: run {
                    _uiState.update { it.copy(error = "القضية غير موجودة") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = e.message ?: "فشل في تغيير حالة القضية"
                ) }
            }
        }
    }

    // --------------------------- 📊 Statistics ---------------------------
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

    // --------------------------- ☁️ Backup and Restore ---------------------------
    fun backupToDrive() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                if (!backupManager.isSignedIn()) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "يجب تسجيل الدخول أولاً لإنشاء نسخة احتياطية"
                    ) }
                    return@launch
                }

                val exportData = repository.exportData()
                val result = backupManager.backupToDrive(exportData.cases, exportData.sessions)

                result.fold(
                    onSuccess = { message ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            backupStatus = message
                        ) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = error.message ?: "فشل في إنشاء النسخة الاحتياطية"
                        ) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في إنشاء النسخة الاحتياطية"
                ) }
            }
        }
    }

    fun restoreFromDrive() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                if (!backupManager.isSignedIn()) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "يجب تسجيل الدخول أولاً لاستعادة النسخة الاحتياطية"
                    ) }
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
                            _uiState.update { it.copy(
                                isLoading = false,
                                backupStatus = "تم استعادة البيانات بنجاح - ${importResult.importedCases} قضية، ${importResult.importedSessions} جلسة"
                            ) }
                            refreshAllViews()
                        } else {
                            _uiState.update { it.copy(
                                isLoading = false,
                                error = importResult.error ?: "فشل في استيراد البيانات"
                            ) }
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = error.message ?: "فشل في استعادة البيانات"
                        ) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في استعادة البيانات"
                ) }
            }
        }
    }

    suspend fun exportLocalBackup(): DatabaseExport {
        return repository.exportData()
    }

    // --------------------------- 🧹 State Management ---------------------------
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
                _uiState.update { it.copy(backupStatus = "تم تسجيل الخروج بنجاح") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "فشل في تسجيل الخروج") }
            }
        }
    }

    // --------------------------- 🎯 Quick Actions ---------------------------
    fun getUpcomingSessions() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, isSearchMode = true) }

                val upcomingSessions = repository.getUpcomingSessions().first()
                val allCases = repository.getAllCases().first()

                val sessionsWithCases = upcomingSessions.mapNotNull { session ->
                    val case = allCases.find { it.caseId == session.caseId }
                    case?.let { SessionWithCase(session, it) }
                }

                _uiState.update { it.copy(
                    sessions = sessionsWithCases,
                    isLoading = false,
                    searchQuery = "الجلسات القادمة"
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في جلب الجلسات القادمة"
                ) }
            }
        }
    }

    fun getSessionsForWeek(weekStart: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                repository.getSessionsForWeek(weekStart)
                    .combine(repository.getAllCases()) { sessions, cases ->
                        sessions.mapNotNull { session ->
                            val case = cases.find { it.caseId == session.caseId }
                            case?.let { SessionWithCase(session, it) }
                        }.sortedBy { it.session.sessionDate }
                    }
                    .collect { sessionsWithCases ->
                        _uiState.update { it.copy(
                            sessions = sessionsWithCases,
                            isLoading = false,
                            isSearchMode = true,
                            searchQuery = "جلسات الأسبوع"
                        ) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في جلب جلسات الأسبوع"
                ) }
            }
        }
    }

    fun getSessionsForMonth(monthStart: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                repository.getSessionsForMonth(monthStart)
                    .combine(repository.getAllCases()) { sessions, cases ->
                        sessions.mapNotNull { session ->
                            val case = cases.find { it.caseId == session.caseId }
                            case?.let { SessionWithCase(session, it) }
                        }.sortedBy { it.session.sessionDate }
                    }
                    .collect { sessionsWithCases ->
                        _uiState.update { it.copy(
                            sessions = sessionsWithCases,
                            isLoading = false,
                            isSearchMode = true,
                            searchQuery = "جلسات الشهر"
                        ) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في جلب جلسات الشهر"
                ) }
            }
        }
    }

    // --------------------------- 🔧 Validation Helpers ---------------------------
    suspend fun validateSession(session: SessionEntity): String? {
        return try {
            if (!session.isValid()) {
                return "بيانات الجلسة غير صالحة"
            }

            if (repository.isSessionExists(session.caseId, session.sessionDate, session.sessionId)) {
                return "توجد جلسة أخرى لنفس القضية في هذا التاريخ"
            }

            null
        } catch (e: Exception) {
            e.message ?: "خطأ في التحقق من صحة البيانات"
        }
    }

    // --------------------------- 📊 Sample Data Management ---------------------------
    fun populateSampleData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                repository.populateSampleData()
                refreshCurrentView()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في تحميل البيانات التجريبية"
                ) }
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
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في مسح البيانات"
                ) }
            }
        }
    }

    // --------------------------- 🔄 Helper Methods ---------------------------
    private fun refreshCurrentView() {
        viewModelScope.launch {
            if (_uiState.value.isSearchMode) {
                searchSessions(_uiState.value.searchQuery)
            } else {
                loadSessionsForDate(_uiState.value.selectedDate)
            }
            loadStatistics()
        }
    }

    private fun refreshAllViews() {
        viewModelScope.launch {
            loadSessionsForDate(_uiState.value.selectedDate)
            loadStatistics()
        }
    }
}