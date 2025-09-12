# SmartLawyerAgenda - Functionality Documentation

## 📋 Complete Function List and Usage

### 🗄️ **Database Layer (Room)**

#### **CaseEntity**
- `caseId`: Primary key (auto-generated)
- `caseNumber`: Case number (required)
- `rollNumber`: Roll number (optional)
- `clientName`: Client name (required)
- `opponentName`: Opponent name (required)
- `createdAt`: Creation timestamp

#### **SessionEntity**
- `sessionId`: Primary key (auto-generated)
- `caseId`: Foreign key to CaseEntity
- `sessionDate`: Session date in milliseconds
- `fromSession`: Reference to previous session (optional)
- `reason`: Session reason (optional)
- `decision`: Session decision (optional)
- `createdAt`: Creation timestamp

### 🔧 **DAO Layer**

#### **CaseDao Methods**
```kotlin
// Query methods
fun getAllCases(): Flow<List<CaseEntity>>
suspend fun getCaseById(caseId: Long): CaseEntity?
suspend fun getCaseByNumber(caseNumber: String): CaseEntity?

// Insert/Update/Delete methods
suspend fun insertCase(case: CaseEntity): Long
suspend fun updateCase(case: CaseEntity)
suspend fun deleteCase(case: CaseEntity)
suspend fun deleteCaseById(caseId: Long)
```

#### **SessionDao Methods**
```kotlin
// Query methods
fun getSessionsByDate(dateStartMillis: Long, dateEndMillis: Long): Flow<List<SessionEntity>>
suspend fun existsSession(caseId: Long, sessionDate: Long): SessionEntity?
suspend fun getSessionById(sessionId: Long): SessionEntity?
fun getSessionsByCaseId(caseId: Long): Flow<List<SessionEntity>>
suspend fun getLatestSession(caseId: Long): SessionEntity?
suspend fun getSessionsCountForCase(caseId: Long): Int
fun getUpcomingSessions(todayMillis: Long): Flow<List<SessionEntity>>
fun getAllSessions(): Flow<List<SessionEntity>>

// Insert/Update/Delete methods
suspend fun insertSession(session: SessionEntity): Long
suspend fun updateSession(session: SessionEntity)
suspend fun deleteSession(session: SessionEntity)
suspend fun deleteSessionById(sessionId: Long)
suspend fun deleteAllSessions()
```

### 🏪 **Repository Layer (MainRepository)**

#### **Case Operations**
```kotlin
fun getAllCases(): Flow<List<CaseEntity>>
suspend fun getCaseById(caseId: Long): CaseEntity?
suspend fun getCaseByNumber(caseNumber: String): CaseEntity?
suspend fun insertCase(case: CaseEntity): Long
suspend fun updateCase(case: CaseEntity)
suspend fun deleteCase(case: CaseEntity)
suspend fun deleteCaseById(caseId: Long)
```

#### **Session Operations**
```kotlin
fun getSessionsByDate(dateStartMillis: Long, dateEndMillis: Long): Flow<List<SessionEntity>>
suspend fun existsSession(caseId: Long, sessionDate: Long): SessionEntity?
suspend fun getSessionById(sessionId: Long): SessionEntity?
fun getSessionsByCaseId(caseId: Long): Flow<List<SessionEntity>>
suspend fun getLatestSession(caseId: Long): SessionEntity?
suspend fun getSessionsCountForCase(caseId: Long): Int
fun getUpcomingSessions(todayMillis: Long): Flow<List<SessionEntity>>
suspend fun insertSession(session: SessionEntity): Long
suspend fun updateSession(session: SessionEntity)
suspend fun deleteSession(session: SessionEntity)
suspend fun deleteSessionById(sessionId: Long)
fun getAllSessions(): Flow<List<SessionEntity>>
suspend fun deleteAllSessions()
```

#### **Combined Operations**
```kotlin
suspend fun saveSessionWithAutoNext(
    case: CaseEntity,
    session: SessionEntity,
    nextSessionDate: Long?
)
```

#### **Statistics and Analytics**
```kotlin
suspend fun getTodaySessionsCount(): Int
suspend fun getUpcomingSessionsCount(): Int
suspend fun getTotalCasesCount(): Int
suspend fun getTotalSessionsCount(): Int
suspend fun getCaseStatistics(caseId: Long): CaseStatistics
```

#### **Search Functionality**
```kotlin
suspend fun searchCases(query: String): List<CaseEntity>
suspend fun searchSessions(query: String): List<SessionEntity>
```

### 🎯 **ViewModel Layer (AgendaViewModel)**

#### **UI State Management**
```kotlin
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
```

#### **Core Functions**
```kotlin
fun selectDate(dateMillis: Long)
fun saveSession(case: CaseEntity, session: SessionEntity, nextSessionDate: Long?)
fun deleteSession(session: SessionEntity)
fun backupToDrive()
fun restoreFromDrive()
fun clearError()
fun clearBackupStatus()
```

#### **Search and Statistics**
```kotlin
fun searchSessions(query: String)
suspend fun getStatistics(): StatisticsData
```

### 🎨 **UI Components**

#### **Screens**
- **SplashScreen**: App launch screen with animations
- **LoginScreen**: Google Sign-In authentication
- **AgendaScreen**: Main agenda with search, filters, and statistics
- **AddEditSessionScreen**: Session creation/editing form
- **SettingsScreen**: Backup/restore and export settings
- **CasesScreen**: Case management interface

#### **Reusable Components**
- **SessionCard**: Display session information with actions
- **SearchBar**: Text search with clear functionality
- **DatePickerDialog**: Date selection with navigation
- **DateFilterChips**: Quick date filtering options
- **StatisticsCard**: Display statistics with icons
- **StatisticsRow**: Row of statistics cards

### 🛠️ **Utility Classes**

#### **HijriUtils**
```kotlin
fun getHijriDate(gregorianDate: Date): String
fun getHijriDateShort(gregorianDate: Date): String
fun getTodayHijri(): String
fun getTodayHijriShort(): String
```

#### **BackupManager**
```kotlin
suspend fun exportToJson(cases: List<CaseEntity>, sessions: List<SessionEntity>): String
suspend fun importFromJson(json: String): BackupData
suspend fun backupToDrive(cases: List<CaseEntity>, sessions: List<SessionEntity>): Result<String>
suspend fun restoreFromDrive(): Result<BackupData>
```

#### **ExportHelper**
```kotlin
fun exportToJson(cases: List<CaseEntity>, sessions: List<SessionEntity>, onSuccess: (File) -> Unit, onError: (String) -> Unit)
fun exportToCsv(cases: List<CaseEntity>, sessions: List<SessionEntity>, onSuccess: (File) -> Unit, onError: (String) -> Unit)
fun shareFile(file: File, onError: (String) -> Unit)
fun importFromJson(jsonString: String, onSuccess: (List<CaseEntity>, List<SessionEntity>) -> Unit, onError: (String) -> Unit)
```

#### **NotificationHelper**
```kotlin
fun showSessionReminder(session: SessionEntity, caseNumber: String, clientName: String)
fun showUpcomingSessionsReminder(sessionsCount: Int)
fun cancelNotification(notificationId: Int)
fun cancelAllNotifications()
```

### 📊 **Data Classes**

#### **SessionWithCase**
```kotlin
data class SessionWithCase(
    val session: SessionEntity,
    val case: CaseEntity
)
```

#### **CaseStatistics**
```kotlin
data class CaseStatistics(
    val caseId: Long,
    val totalSessions: Int,
    val latestSessionDate: Long?,
    val upcomingSessionsCount: Int
)
```

#### **StatisticsData**
```kotlin
data class StatisticsData(
    val todaySessions: Int,
    val totalCases: Int,
    val upcomingSessions: Int,
    val totalSessions: Int
)
```

#### **DateFilter**
```kotlin
data class DateFilter(
    val label: String,
    val startDate: Long,
    val endDate: Long
)
```

### 🔄 **Navigation Flow**

1. **SplashScreen** → **AgendaScreen**
2. **AgendaScreen** → **AddEditSessionScreen** (Add new session)
3. **AgendaScreen** → **AddEditSessionScreen** (Edit existing session)
4. **AgendaScreen** → **SettingsScreen**
5. **SettingsScreen** → **AgendaScreen** (Back)

### 🎯 **Key Features**

#### **Session Management**
- ✅ Create, read, update, delete sessions
- ✅ Auto-create next session functionality
- ✅ Date-based filtering and search
- ✅ Session statistics and analytics

#### **Case Management**
- ✅ Complete case information tracking
- ✅ Client and opponent details
- ✅ Case number and roll number support
- ✅ Case statistics and session history

#### **Calendar Integration**
- ✅ Dual date display (Gregorian + Hijri)
- ✅ Date picker with navigation
- ✅ Quick date filters (Today, Tomorrow, This Week, etc.)

#### **Data Management**
- ✅ Local Room database storage
- ✅ Google Drive backup/restore
- ✅ JSON/CSV export functionality
- ✅ Search across all data

#### **User Experience**
- ✅ Arabic RTL interface
- ✅ Material 3 design
- ✅ Loading states and error handling
- ✅ Confirmation dialogs
- ✅ Statistics dashboard

### 🧪 **Testing**

All functions are structured for testing with:
- Unit tests for repository methods
- Integration tests for database operations
- UI tests for screen interactions
- Search functionality tests
- Statistics calculation tests

### 📱 **Ready for Production**

The app includes:
- ✅ Complete error handling
- ✅ Loading states
- ✅ User feedback
- ✅ Data validation
- ✅ Backup and restore
- ✅ Export functionality
- ✅ Search and filtering
- ✅ Statistics and analytics
- ✅ Arabic localization
- ✅ RTL support

All functions are implemented, tested, and ready for use in production.

