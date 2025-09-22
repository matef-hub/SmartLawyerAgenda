package com.example.smartlawyeragenda.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartlawyeragenda.repository.OverallStatistics
import java.text.SimpleDateFormat
import java.util.*

// ---------- Statistics Card (صغيرة للـ Dashboard) ----------
@Composable
fun StatisticsCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(color.red, color.green, color.blue, 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

// ---------- Statistics Row (صف من الكروت) ----------
@Composable
fun StatisticsRow(
    todaySessions: Int,
    totalCases: Int,
    upcomingSessions: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatisticsCard(
            title = "جلسات اليوم",
            value = todaySessions.toString(),
            icon = Icons.Default.CalendarToday,
            color = Color(0xFF42A5F5),
            modifier = Modifier.weight(1f)
        )

        StatisticsCard(
            title = "إجمالي القضايا",
            value = totalCases.toString(),
            icon = Icons.Default.Folder,
            color = Color(0xFF66BB6A),
            modifier = Modifier.weight(1f)
        )

        StatisticsCard(
            title = "الجلسات القادمة",
            value = upcomingSessions.toString(),
            icon = Icons.Default.Schedule,
            color = Color(0xFFFFA726),
            modifier = Modifier.weight(1f)
        )
    }
}

// ---------- Statistics Dialog (نافذة منبثقة تفصيلية) ----------
@Composable
fun StatisticsDialog(
    statistics: OverallStatistics,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "📊 الإحصائيات التفصيلية",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DetailedStatItem("إجمالي القضايا", statistics.totalCases.toString(), Color(0xFF42A5F5))
                DetailedStatItem("القضايا النشطة", statistics.activeCases.toString(), Color(0xFF66BB6A))
                DetailedStatItem("إجمالي الجلسات", statistics.totalSessions.toString(), Color(0xFFAB47BC))
                DetailedStatItem("جلسات اليوم", statistics.todaySessions.toString(), Color(0xFFFFA726))
                DetailedStatItem("الجلسات القادمة", statistics.upcomingSessions.toString(), Color(0xFFEF5350))

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )

                Text(
                    text = "آخر تحديث: ${
                        SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date())
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = Color(0xFF1565C0))
            }
        }
    )
}

@Composable
private fun DetailedStatItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
    }
}