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
import java.text.SimpleDateFormat
import java.util.*

data class SessionWithCase(
    val session: SessionEntity,
    val case: CaseEntity
) {
    // Helper methods for UI display
    fun getDisplayTitle(): String = "${case.caseNumber} - ${case.clientName}"
    fun getFormattedDate(): String = session.getFormattedDate()
    fun getStatusDisplay(): String = session.getStatusDisplay()
    fun getTimeDisplay(): String = session.sessionTime ?: "غير محدد"
}

data class AgendaUiState(
    val sessions: List<SessionWithCase> = emptyList(),
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

    private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    init {
        updateDateInfo()
        loadSessionsForDate(_uiState.value.selectedDate)
        loadStatistics()
    }

    // ---------------------------
    // 🗓 Date Management
    // ---------------------------
    fun selectDate(dateMillis: Long) {
        _uiState.value = _uiState.value.copy(
            selectedDate = dateMillis,
            isSearchMode = false,
            searchQuery = ""
        )
        updateDateInfo()
        loadSessionsForDate(dateMillis)
    }

    fun goToToday() {
        selectDate(System.currentTimeMillis())
    }

    private fun updateDateInfo() {
        val selectedDate = Date(_uiState.value.selectedDate)
        val gregorianDate = dateFormatter.format(selectedDate)
        val hijriDate = HijriUtils.getHijriDate(selectedDate)

        _uiState.value = _uiState.value.copy(
            gregorianDate = gregorianDate,
            hijriDate = hijriDate
        )
    }

    // ---------------------------
    // 📂 Session Loading
    // ---------------------------
    private fun loadSessionsForDate(dateMillis: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Use the new repository method for getting sessions for a specific date
                repository.getSessionsForDate(dateMillis)
                    .combine(repository.getAllCases()) { sessions, cases ->
                        sessions.mapNotNull { session ->
                            val case = cases.find { it.caseId == session.caseId }
                            case?.let { SessionWithCase(session, it) }
                        }.sortedBy { it.session.sessionTime ?: "00:00" }
                    }
                    .collect { sessionsWithCases ->
                        _uiState.value = _uiState.value.copy(
                            sessions = sessionsWithCases,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "حدث خطأ غير متوقع"
                )
            }
        }
    }

    // ---------------------------
    // 🔍 Search Functionality
    // ---------------------------
    fun searchSessions(query: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    searchQuery = query,
                    isSearchMode = query.isNotBlank()
                )

                if (query.isBlank()) {
                    // Return to date view
                    loadSessionsForDate(_uiState.value.selectedDate)
                    return@launch
                }

                // Search both cases and sessions
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
                    case?.let { it ->
                        val sessionWithCase = SessionWithCase(session, it)
                        if (!sessionsWithCases.any { it -> it.session.sessionId == session.sessionId }) {
                            sessionsWithCases.add(sessionWithCase)
                        }
                    }
                }

                _uiState.value = _uiState.value.copy(
                    sessions = sessionsWithCases.sortedByDescending { it.session.sessionDate },
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في البحث"
                )
            }
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            isSearchMode = false
        )
        loadSessionsForDate(_uiState.value.selectedDate)
    }

    // ---------------------------
    // 💾 CRUD Operations
    // ---------------------------
    fun saveSession(
        case: CaseEntity,
        session: SessionEntity,
        createNextSession: Boolean = false,
        nextSessionDate: Long? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Use the new repository method
                repository.saveCaseWithSession(case, session, createNextSession, nextSessionDate)

                // Refresh current view
                if (_uiState.value.isSearchMode) {
                    searchSessions(_uiState.value.searchQuery)
                } else {
                    loadSessionsForDate(_uiState.value.selectedDate)
                }

                loadStatistics()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في حفظ الجلسة"
                )
            }
        }
    }

    fun updateSessionStatus(sessionId: Long, newStatus: SessionStatus, notes: String? = null) {
        viewModelScope.launch {
            try {
                val session = repository.getSessionById(sessionId)
                if (session != null) {
                    val updatedSession = session.copy(
                        status = newStatus,
                        notes = notes
                    )
                    repository.updateSession(updatedSession)

                    // Refresh current view
                    if (_uiState.value.isSearchMode) {
                        searchSessions(_uiState.value.searchQuery)
                    } else {
                        loadSessionsForDate(_uiState.value.selectedDate)
                    }
                    loadStatistics()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "فشل في تحديث حالة الجلسة"
                )
            }
        }
    }

    fun deleteSession(session: SessionEntity) {
        viewModelScope.launch {
            try {
                repository.deleteSession(session)

                // Refresh current view
                if (_uiState.value.isSearchMode) {
                    searchSessions(_uiState.value.searchQuery)
                } else {
                    loadSessionsForDate(_uiState.value.selectedDate)
                }
                loadStatistics()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "فشل في حذف الجلسة"
                )
            }
        }
    }

    suspend fun getCaseById(caseId: Long): CaseEntity? {
        return try {
            repository.getCaseById(caseId)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = e.message ?: "فشل في جلب بيانات القضية"
            )
            null
        }
    }

    suspend fun getSessionById(sessionId: Long): SessionEntity? {
        return try {
            repository.getSessionById(sessionId)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = e.message ?: "فشل في جلب بيانات الجلسة"
            )
            null
        }
    }

    fun deleteCaseWithSessions(caseId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteCaseWithSessions(caseId)

                // Refresh current view
                if (_uiState.value.isSearchMode) {
                    searchSessions(_uiState.value.searchQuery)
                } else {
                    loadSessionsForDate(_uiState.value.selectedDate)
                }
                loadStatistics()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "فشل في حذف القضية"
                )
            }
        }
    }

    fun saveCase(case: CaseEntity) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Validate case before saving
                if (!case.isValid()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "بيانات القضية غير صالحة"
                    )
                    return@launch
                }

                // Check for duplicate case number
                if (repository.isCaseNumberExists(case.caseNumber, case.caseId)) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "رقم القضية موجود بالفعل"
                    )
                    return@launch
                }

                // Save the case
                if (case.caseId == 0L) {
                    repository.insertCase(case)
                } else {
                    repository.updateCase(case)
                }

                // Refresh current view
                if (_uiState.value.isSearchMode) {
                    searchSessions(_uiState.value.searchQuery)
                } else {
                    loadSessionsForDate(_uiState.value.selectedDate)
                }
                loadStatistics()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في حفظ القضية"
                )
            }
        }
    }

    fun updateCase(case: CaseEntity) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Validate case before updating
                if (!case.isValid()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "بيانات القضية غير صالحة"
                    )
                    return@launch
                }

                // Check for duplicate case number (excluding current case)
                if (repository.isCaseNumberExists(case.caseNumber, case.caseId)) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "رقم القضية موجود بالفعل"
                    )
                    return@launch
                }

                // Update the case
                repository.updateCase(case)

                // Refresh current view
                if (_uiState.value.isSearchMode) {
                    searchSessions(_uiState.value.searchQuery)
                } else {
                    loadSessionsForDate(_uiState.value.selectedDate)
                }
                loadStatistics()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في تحديث القضية"
                )
            }
        }
    }

    fun toggleCaseStatus(caseId: Long) {
        viewModelScope.launch {
            try {
                val case = repository.getCaseById(caseId)
                if (case != null) {
                    val updatedCase = case.copy(isActive = !case.isActive)
                    repository.updateCase(updatedCase)

                    // Refresh current view
                    if (_uiState.value.isSearchMode) {
                        searchSessions(_uiState.value.searchQuery)
                    } else {
                        loadSessionsForDate(_uiState.value.selectedDate)
                    }
                    loadStatistics()
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "القضية غير موجودة"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "فشل في تغيير حالة القضية"
                )
            }
        }
    }

    // ---------------------------
    // 📊 Statistics
    // ---------------------------
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                val stats = repository.getOverallStatistics()
                _uiState.value = _uiState.value.copy(statistics = stats)
            } catch (_: Exception) {
                // Statistics failure shouldn't break the app, just log it
                _uiState.value = _uiState.value.copy(
                    statistics = OverallStatistics(0, 0, 0, 0, 0)
                )
            }
        }
    }

    fun refreshStatistics() {
        loadStatistics()
    }

    // ---------------------------
    // ☁️ Backup and Restore
    // ---------------------------
    fun backupToDrive() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Check if user is signed in
                if (!backupManager.isSignedIn()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "يجب تسجيل الدخول أولاً لإنشاء نسخة احتياطية"
                    )
                    return@launch
                }

                // Use the new export functionality
                val exportData = repository.exportData()
                val result = backupManager.backupToDrive(exportData.cases, exportData.sessions)

                result.fold(
                    onSuccess = { message ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            backupStatus = message
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "فشل في إنشاء النسخة الاحتياطية"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في إنشاء النسخة الاحتياطية"
                )
            }
        }
    }

    fun restoreFromDrive() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Check if user is signed in
                if (!backupManager.isSignedIn()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "يجب تسجيل الدخول أولاً لاستعادة النسخة الاحتياطية"
                    )
                    return@launch
                }

                val result = backupManager.restoreFromDrive()
                result.fold(
                    onSuccess = { backupData ->
                        // Use the new import functionality
                        val exportData = DatabaseExport(
                            cases = backupData.cases,
                            sessions = backupData.sessions,
                            exportDate = System.currentTimeMillis()
                        )

                        val importResult = repository.importData(exportData)

                        if (importResult.success) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                backupStatus = "تم استعادة البيانات بنجاح - ${importResult.importedCases} قضية، ${importResult.importedSessions} جلسة"
                            )

                            // Refresh all views
                            loadSessionsForDate(_uiState.value.selectedDate)
                            loadStatistics()
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = importResult.error ?: "فشل في استيراد البيانات"
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "فشل في استعادة البيانات"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في استعادة البيانات"
                )
            }
        }
    }

    fun exportLocalBackup(): DatabaseExport? {
        return try {
            viewModelScope.launch {
                repository.exportData()
            }
            null // This should be handled differently, returning from coroutine
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = e.message ?: "فشل في تصدير البيانات"
            )
            null
        }
    }

    // ---------------------------
    // 🧹 State Management
    // ---------------------------
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearBackupStatus() {
        _uiState.value = _uiState.value.copy(backupStatus = null)
    }
    
    // Google Account methods
    fun isSignedInToGoogle(): Boolean {
        return backupManager.isSignedIn()
    }
    
    fun signOutFromGoogle() {
        viewModelScope.launch {
            try {
                backupManager.signOut()
                _uiState.value = _uiState.value.copy(
                    backupStatus = "تم تسجيل الخروج بنجاح"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "فشل في تسجيل الخروج"
                )
            }
        }
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    // ---------------------------
    // 🎯 Quick Actions
    // ---------------------------
    fun getTodaySessions() {
        selectDate(System.currentTimeMillis())
    }

    fun getUpcomingSessions() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, isSearchMode = true)

                val upcomingSessions = repository.getUpcomingSessions().first()
                val allCases = repository.getAllCases().first()

                val sessionsWithCases = upcomingSessions.mapNotNull { session ->
                    val case = allCases.find { it.caseId == session.caseId }
                    case?.let { SessionWithCase(session, it) }
                }

                _uiState.value = _uiState.value.copy(
                    sessions = sessionsWithCases,
                    isLoading = false,
                    searchQuery = "الجلسات القادمة"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في جلب الجلسات القادمة"
                )
            }
        }
    }

    fun getSessionsForWeek(weekStart: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                repository.getSessionsForWeek(weekStart)
                    .combine(repository.getAllCases()) { sessions, cases ->
                        sessions.mapNotNull { session ->
                            val case = cases.find { it.caseId == session.caseId }
                            case?.let { SessionWithCase(session, it) }
                        }.sortedBy { it.session.sessionDate }
                    }
                    .collect { sessionsWithCases ->
                        _uiState.value = _uiState.value.copy(
                            sessions = sessionsWithCases,
                            isLoading = false,
                            isSearchMode = true,
                            searchQuery = "جلسات الأسبوع"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في جلب جلسات الأسبوع"
                )
            }
        }
    }

    // ---------------------------
    // 🔧 Validation Helpers
    // ---------------------------
    suspend fun validateSession(session: SessionEntity): String? {
        return try {
            if (!session.isValid()) {
                return "بيانات الجلسة غير صالحة"
            }

            if (repository.isSessionExists(session.caseId, session.sessionDate, session.sessionId)) {
                return "توجد جلسة أخرى لنفس القضية في هذا التاريخ"
            }

            null // Valid
        } catch (e: Exception) {
            e.message ?: "خطأ في التحقق من صحة البيانات"
        }
    }

    // ---------------------------
    // 📊 Sample Data Management
    // ---------------------------
    fun populateSampleData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.populateSampleData()
                
                // Refresh current view
                if (_uiState.value.isSearchMode) {
                    searchSessions(_uiState.value.searchQuery)
                } else {
                    loadSessionsForDate(_uiState.value.selectedDate)
                }
                loadStatistics()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في تحميل البيانات التجريبية"
                )
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.clearAllData()
                
                // Refresh current view
                loadSessionsForDate(_uiState.value.selectedDate)
                loadStatistics()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "فشل في مسح البيانات"
                )
            }
        }
    }

}