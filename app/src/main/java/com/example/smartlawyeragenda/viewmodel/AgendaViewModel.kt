package com.example.smartlawyeragenda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.repository.MainRepository
import com.example.smartlawyeragenda.utils.BackupManager
import com.example.smartlawyeragenda.utils.HijriUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class SessionWithCase(
    val session: SessionEntity,
    val case: CaseEntity
)

data class AgendaUiState(
    val sessions: List<SessionWithCase> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDate: Long = System.currentTimeMillis(),
    val gregorianDate: String = "",
    val hijriDate: String = "",
    val isLoggedIn: Boolean = false,
    val backupStatus: String? = null
)

class AgendaViewModel(
    val repository: MainRepository,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgendaUiState())
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    init {
        updateDateInfo()
        loadSessionsForDate(_uiState.value.selectedDate)
    }

    // ---------------------------
    // 🗓 التحكم في التاريخ
    // ---------------------------
    fun selectDate(dateMillis: Long) {
        _uiState.value = _uiState.value.copy(selectedDate = dateMillis)
        updateDateInfo()
        loadSessionsForDate(dateMillis)
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
    // 📂 تحميل الجلسات
    // ---------------------------
    private fun loadSessionsForDate(dateMillis: Long) {
        val startOfDay = getStartOfDay(dateMillis)
        val endOfDay = getEndOfDay(dateMillis)

        repository.getSessionsByDate(startOfDay, endOfDay)
            .combine(repository.getAllCases()) { sessions, cases ->
                sessions.mapNotNull { session ->
                    val case = cases.find { it.caseId == session.caseId }
                    case?.let { SessionWithCase(session, it) }
                }
            }
            .onStart {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }
            .onEach { sessionsWithCases ->
                _uiState.value = _uiState.value.copy(
                    sessions = sessionsWithCases,
                    isLoading = false
                )
            }
            .catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "حدث خطأ غير متوقع"
                )
            }
            .launchIn(viewModelScope)
    }

    // ---------------------------
    // 💾 عمليات الحفظ والحذف
    // ---------------------------
    fun saveSession(
        case: CaseEntity,
        session: SessionEntity,
        nextSessionDate: Long?
    ) {
        viewModelScope.launch {
            try {
                repository.saveSessionWithAutoNext(case, session, nextSessionDate)
                loadSessionsForDate(_uiState.value.selectedDate)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "فشل في حفظ الجلسة"
                )
            }
        }
    }

    fun deleteSession(session: SessionEntity) {
        viewModelScope.launch {
            try {
                repository.deleteSession(session)
                loadSessionsForDate(_uiState.value.selectedDate)
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
    
    fun deleteCaseWithSessions(caseId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteCaseWithSessions(caseId)
                loadSessionsForDate(_uiState.value.selectedDate)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "فشل في حذف القضية"
                )
            }
        }
    }

    // ---------------------------
    // ☁️ النسخ الاحتياطي والاسترجاع
    // ---------------------------
    fun backupToDrive() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val cases = repository.getAllCases().first()
                val sessions = repository.getAllSessions().first()

                val result = backupManager.backupToDrive(cases, sessions)
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

                val result = backupManager.restoreFromDrive()
                result.fold(
                    onSuccess = { backupData ->
                        // حذف البيانات القديمة
                        repository.deleteAllSessions()
                        repository.deleteAllCases()

                        // إدخال البيانات الجديدة
                        backupData.cases.forEach { repository.insertCase(it) }
                        backupData.sessions.forEach { repository.insertSession(it) }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            backupStatus = "تم استعادة البيانات بنجاح"
                        )

                        loadSessionsForDate(_uiState.value.selectedDate)
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

    // ---------------------------
    // 🧹 تنظيف الحالة
    // ---------------------------
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearBackupStatus() {
        _uiState.value = _uiState.value.copy(backupStatus = null)
    }
    
    // -------------------------
    // Search functionality
    // -------------------------
    fun searchSessions(query: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val searchResults = repository.searchSessions(query)
                val cases = repository.getAllCases().first()
                
                val sessionsWithCases = searchResults.mapNotNull { session ->
                    val case = cases.find { it.caseId == session.caseId }
                    case?.let { SessionWithCase(session, it) }
                }
                
                _uiState.value = _uiState.value.copy(
                    sessions = sessionsWithCases,
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
    
    // -------------------------
    // Statistics functionality
    // -------------------------
    suspend fun getStatistics(): StatisticsData {
        return try {
            val todaySessions = repository.getTodaySessionsCount()
            val totalCases = repository.getTotalCasesCount()
            val upcomingSessions = repository.getUpcomingSessionsCount()
            val totalSessions = repository.getTotalSessionsCount()
            
            StatisticsData(
                todaySessions = todaySessions,
                totalCases = totalCases,
                upcomingSessions = upcomingSessions,
                totalSessions = totalSessions
            )
        } catch (_: Exception) {
            StatisticsData(0, 0, 0, 0)
        }
    }

    // ---------------------------
    // 🕒 حساب اليوم
    // ---------------------------
    private fun getStartOfDay(dateMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDay(dateMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}

// Data class for statistics
data class StatisticsData(
    val todaySessions: Int,
    val totalCases: Int,
    val upcomingSessions: Int,
    val totalSessions: Int
)
