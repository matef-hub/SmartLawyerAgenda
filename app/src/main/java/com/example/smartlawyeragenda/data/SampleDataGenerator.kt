package com.example.smartlawyeragenda.data

import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.data.entities.SessionStatus
import java.util.*

/**
 * Sample data generator for testing and demonstration purposes
 */
object SampleDataGenerator {
    
    fun generateSampleCases(): List<CaseEntity> {
        return listOf(
            CaseEntity(
                caseId = 1L,
                caseNumber = "123/2024",
                rollNumber = "456",
                clientName = "أحمد محمد علي",
                opponentName = "شركة التقنية المتقدمة",
                caseType = "تجاري",
                caseDescription = "قضية تجارية تتعلق بعقد توريد أجهزة حاسوب",
                isActive = true
            ),
            CaseEntity(
                caseId = 2L,
                caseNumber = "124/2024",
                rollNumber = "457",
                clientName = "فاطمة السعيد",
                opponentName = "مؤسسة البناء الحديث",
                caseType = "مدني",
                caseDescription = "قضية مدنية تتعلق بعقد بناء منزل",
                isActive = true
            ),
            CaseEntity(
                caseId = 3L,
                caseNumber = "125/2024",
                rollNumber = "458",
                clientName = "محمد عبد الرحمن",
                opponentName = "شركة النقل السريع",
                caseType = "تجاري",
                caseDescription = "قضية تجارية تتعلق بعقد نقل بضائع",
                isActive = false
            ),
            CaseEntity(
                caseId = 4L,
                caseNumber = "126/2024",
                rollNumber = "459",
                clientName = "عائشة حسن",
                opponentName = "مؤسسة التعليم الأهلي",
                caseType = "مدني",
                caseDescription = "قضية مدنية تتعلق بعقد تعليم خاص",
                isActive = true
            ),
            CaseEntity(
                caseId = 5L,
                caseNumber = "127/2024",
                rollNumber = "460",
                clientName = "خالد إبراهيم",
                opponentName = "شركة التأمين الشامل",
                caseType = "تجاري",
                caseDescription = "قضية تجارية تتعلق بعقد تأمين",
                isActive = true
            )
        )
    }
    
    fun generateSampleSessions(): List<SessionEntity> {
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis
        
        // Generate sessions for the next 30 days
        return listOf(
            // Today's sessions
            SessionEntity(
                sessionId = 1L,
                caseId = 1L,
                sessionDate = today,
                sessionTime = "09:00",
                status = SessionStatus.SCHEDULED,
                notes = "جلسة استماع أولى"
            ),
            SessionEntity(
                sessionId = 2L,
                caseId = 2L,
                sessionDate = today,
                sessionTime = "11:00",
                status = SessionStatus.SCHEDULED,
                notes = "جلسة مرافعة"
            ),
            
            // Tomorrow's sessions
            SessionEntity(
                sessionId = 3L,
                caseId = 3L,
                sessionDate = today + (24 * 60 * 60 * 1000), // Tomorrow
                sessionTime = "10:00",
                status = SessionStatus.SCHEDULED,
                notes = "جلسة استماع"
            ),
            SessionEntity(
                sessionId = 4L,
                caseId = 4L,
                sessionDate = today + (24 * 60 * 60 * 1000), // Tomorrow
                sessionTime = "14:00",
                status = SessionStatus.SCHEDULED,
                notes = "جلسة مرافعة"
            ),
            
            // Day after tomorrow
            SessionEntity(
                sessionId = 5L,
                caseId = 5L,
                sessionDate = today + (2 * 24 * 60 * 60 * 1000), // Day after tomorrow
                sessionTime = "09:30",
                status = SessionStatus.SCHEDULED,
                notes = "جلسة استماع"
            ),
            
            // Next week
            SessionEntity(
                sessionId = 6L,
                caseId = 1L,
                sessionDate = today + (7 * 24 * 60 * 60 * 1000), // Next week
                sessionTime = "10:30",
                status = SessionStatus.SCHEDULED,
                notes = "جلسة مرافعة نهائية"
            ),
            SessionEntity(
                sessionId = 7L,
                caseId = 2L,
                sessionDate = today + (7 * 24 * 60 * 60 * 1000), // Next week
                sessionTime = "15:00",
                status = SessionStatus.SCHEDULED,
                notes = "جلسة استماع"
            ),
            
            // Past sessions (completed)
            SessionEntity(
                sessionId = 8L,
                caseId = 1L,
                sessionDate = today - (7 * 24 * 60 * 60 * 1000), // Last week
                sessionTime = "10:00",
                status = SessionStatus.COMPLETED,
                notes = "تم الانتهاء من المرافعة",
                decision = "تم تأجيل الجلسة للاستماع للمزيد من الأدلة"
            ),
            SessionEntity(
                sessionId = 9L,
                caseId = 3L,
                sessionDate = today - (14 * 24 * 60 * 60 * 1000), // Two weeks ago
                sessionTime = "11:00",
                status = SessionStatus.POSTPONED,
                notes = "تم تأجيل الجلسة",
                reason = "عدم حضور الشاهد الرئيسي"
            ),
            
            // More upcoming sessions
            SessionEntity(
                sessionId = 10L,
                caseId = 4L,
                sessionDate = today + (14 * 24 * 60 * 60 * 1000), // Two weeks from now
                sessionTime = "09:00",
                status = SessionStatus.SCHEDULED,
                notes = "جلسة استماع"
            ),
            SessionEntity(
                sessionId = 11L,
                caseId = 5L,
                sessionDate = today + (21 * 24 * 60 * 60 * 1000), // Three weeks from now
                sessionTime = "13:00",
                status = SessionStatus.SCHEDULED,
                notes = "جلسة مرافعة"
            )
        )
    }
    
    fun generateSampleData(): Pair<List<CaseEntity>, List<SessionEntity>> {
        return Pair(generateSampleCases(), generateSampleSessions())
    }
}
