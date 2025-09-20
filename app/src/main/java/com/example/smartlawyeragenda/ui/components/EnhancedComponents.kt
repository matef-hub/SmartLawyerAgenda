package com.example.smartlawyeragenda.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.smartlawyeragenda.ui.theme.*
import com.example.smartlawyeragenda.data.entities.SessionStatus
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.viewmodel.SessionWithCase
import com.example.smartlawyeragenda.repository.OverallStatistics
import com.example.smartlawyeragenda.repository.CaseStatistics
import com.example.smartlawyeragenda.ui.screens.StatisticItem
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.*

/**
 * Enhanced UI Components for Professional App Design
 */

// ==================== ENHANCED CARDS ====================

@Composable
fun EnhancedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Int = 2,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(AppCornerRadius.Large)
            ),
        shape = RoundedCornerShape(AppCornerRadius.Large),
        colors = appCardStyle(),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        onClick = onClick ?: {}
    ) {
        Column(content = content)
    }
}

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = AppElevation.Level3,
                shape = RoundedCornerShape(AppCornerRadius.Large)
            ),
        shape = RoundedCornerShape(AppCornerRadius.Large),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = onClick ?: {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(AppGradients.Primary),
                    shape = RoundedCornerShape(AppCornerRadius.Large)
                )
        ) {
            Column(content = content)
        }
    }
}

// ==================== ENHANCED BUTTONS ====================

@Composable
fun EnhancedButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Primary
) {
    val colors = when (variant) {
        ButtonVariant.Primary -> primaryButtonStyle()
        ButtonVariant.Secondary -> secondaryButtonStyle()
        ButtonVariant.Outlined -> outlinedButtonStyle()
    }
    
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(AppCornerRadius.Large)),
        enabled = enabled && !isLoading,
        colors = colors,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (variant == ButtonVariant.Outlined) 0.dp else AppElevation.Level2
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = if (variant == ButtonVariant.Outlined) AppColors.Primary else AppColors.OnPrimary,
                strokeWidth = 2.dp
            )
        } else {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(AppSpacing.Small))
            }
            Text(
                text = text,
                style = AppTypography.LabelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppColors.Primary
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .shadow(
                elevation = AppElevation.Level4,
                shape = CircleShape
            ),
        containerColor = backgroundColor,
        contentColor = AppColors.OnPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = AppElevation.Level4
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}

// ==================== ENHANCED CHIPS ====================

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier,
    count: Int? = null
) {
    val (text, color) = when (status) {
        "SCHEDULED" -> "مجدولة" to AppColors.Scheduled
        "COMPLETED" -> "مكتملة" to AppColors.Completed
        "POSTPONED" -> "مؤجلة" to AppColors.Postponed
        "CANCELLED" -> "ملغاة" to AppColors.Cancelled
        else -> status to AppColors.Neutral500
    }
    
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = if (count != null) "$text ($count)" else text,
                style = AppTypography.LabelMedium,
                fontWeight = FontWeight.Medium
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color
        ),
        modifier = modifier
    )
}

@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = AppTypography.LabelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        },
        selected = selected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AppColors.Primary.copy(alpha = 0.1f),
            selectedLabelColor = AppColors.Primary
        ),
        modifier = modifier
    )
}

// ==================== ENHANCED TEXT FIELDS ====================

@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = leadingIcon?.let { icon ->
                { Icon(imageVector = icon, contentDescription = null) }
            },
            trailingIcon = trailingIcon?.let { icon ->
                { 
                    IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                }
            },
            isError = isError,
            enabled = enabled,
            singleLine = singleLine,
            shape = RoundedCornerShape(AppCornerRadius.Medium),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                unfocusedBorderColor = AppColors.Neutral300,
                errorBorderColor = AppColors.Error
            )
        )
        
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = AppColors.Error,
                style = AppTypography.BodySmall,
                modifier = Modifier.padding(top = AppSpacing.ExtraSmall)
            )
        }
    }
}

// ==================== ENHANCED LISTS ====================

@Composable
fun ListItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.ExtraSmall),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Level1)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(AppSpacing.Medium))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTypography.TitleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = AppTypography.BodyMedium,
                        color = AppColors.OnSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = AppColors.OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ==================== ENHANCED PROGRESS INDICATORS ====================

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "جاري التحميل..."
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = AppColors.Primary,
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        Text(
            text = message,
            style = AppTypography.BodyLarge,
            color = AppColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = AppColors.Primary
) {
    LinearProgressIndicator(
    progress = { progress },
    modifier = modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(AppCornerRadius.Small)),
    color = color,
    trackColor = AppColors.Neutral200,
    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    )
}

// ==================== ENHANCED DIVIDERS ====================

@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    color: Color = AppColors.Neutral200
) {
    HorizontalDivider(
        modifier = modifier,
        color = color,
        thickness = 1.dp
    )
}

// ==================== ENHANCED BADGES ====================

@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AppColors.Primary
) {
    Box(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(AppCornerRadius.Small)
            )
            .padding(horizontal = AppSpacing.Small, vertical = AppSpacing.ExtraSmall)
    ) {
        Text(
            text = text,
            color = AppColors.OnPrimary,
            style = AppTypography.LabelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==================== ENHANCED SESSION CARDS ====================

@Composable
fun EnhancedSessionCard(
    sessionWithCase: SessionWithCase,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUpdateStatus: (SessionStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val session = sessionWithCase.session
    val case = sessionWithCase.case
    
    EnhancedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = 3
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.Medium)
        ) {
            // Header with case info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "قضية رقم: ${case.caseNumber}",
                    style = AppTypography.TitleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
                
                StatusChip(
                    status = session.status.name,
                    modifier = Modifier
                )
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.Small))
            
            // Client and opponent info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "الموكل: ${case.clientName}",
                    style = AppTypography.BodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "الخصم: ${case.opponentName}",
                    style = AppTypography.BodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.Small))
            
            // Session details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "التاريخ: ${SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(
                        Date(session.sessionDate)
                    )}",
                    style = AppTypography.BodySmall,
                    color = AppColors.OnSurfaceVariant
                )
                
                Text(
                    text = "الوقت: ${session.sessionTime}",
                    style = AppTypography.BodySmall,
                    color = AppColors.OnSurfaceVariant
                )
            }
            
            // Notes if available
            if (!session.notes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(AppSpacing.Small))
                Text(
                    text = "ملاحظات: ${session.notes}",
                    style = AppTypography.BodySmall,
                    color = AppColors.OnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "تعديل الجلسة",
                        tint = AppColors.Primary
                    )
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف الجلسة",
                        tint = AppColors.Error
                    )
                }
            }
        }
    }
}

// ==================== ENHANCED SEARCH BAR ====================

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var isSearching by remember { mutableStateOf(false) }
    
    OutlinedTextField(
        value = query,
        onValueChange = { 
            onQueryChange(it)
            if (it.isNotEmpty()) {
                onSearch(it)
                isSearching = true
            } else {
                isSearching = false
            }
        },
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "بحث"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { 
                    onQueryChange("")
                    onSearch("")
                    isSearching = false
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "مسح"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(AppCornerRadius.Large),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.Primary,
            unfocusedBorderColor = AppColors.Neutral300
        )
    )
}

// ==================== ENHANCED DATE FILTER CHIPS ====================

@Composable
fun DateFilterChips(
    selectedFilter: DateFilter?,
    onFilterSelected: (DateFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = System.currentTimeMillis()
    val calendar = Calendar.getInstance()
    
    val filters = listOf(
        DateFilter(
            label = "اليوم",
            startDate = getStartOfDay(today),
            endDate = getEndOfDay(today)
        ),
        DateFilter(
            label = "هذا الأسبوع",
            startDate = getStartOfWeek(today),
            endDate = getEndOfWeek(today)
        ),
        DateFilter(
            label = "هذا الشهر",
            startDate = getStartOfMonth(today),
            endDate = getEndOfMonth(today)
        )
    )
    
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
        contentPadding = PaddingValues(horizontal = AppSpacing.Medium)
    ) {
        items(filters) { filter ->
            FilterChip(
                text = filter.label,
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}


// ==================== ENHANCED STATISTICS CARD ====================

@Composable
fun EnhancedStatisticsCard(
    statistics: OverallStatistics,
    modifier: Modifier = Modifier
) {
    EnhancedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = 2
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.Medium)
        ) {
            Text(
                text = "الإحصائيات",
                style = AppTypography.TitleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary
            )
            
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    title = "القضايا الإجمالية",
                    value = statistics.totalCases.toString(),
                    color = AppColors.Primary
                )
                
                StatisticItem(
                    title = "الجلسات المكتملة",
                    value = statistics.totalSessions.toString(),
                    color = AppColors.Tertiary
                )
                
                StatisticItem(
                    title = "الجلسات القادمة",
                    value = statistics.upcomingSessions.toString(),
                    color = AppColors.Secondary
                )
                
                StatisticItem(
                    title = "القضايا النشطة",
                    value = statistics.activeCases.toString(),
                    color = AppColors.Primary
                )
            }
        }
    }
}


// ==================== HELPER FUNCTIONS ====================

private fun getStartOfDay(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

private fun getEndOfDay(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis
}

private fun getStartOfWeek(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    return getStartOfDay(calendar.timeInMillis)
}

private fun getEndOfWeek(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
    return getEndOfDay(calendar.timeInMillis)
}

private fun getStartOfMonth(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    return getStartOfDay(calendar.timeInMillis)
}

private fun getEndOfMonth(timeMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    return getEndOfDay(calendar.timeInMillis)
}

// ==================== ENUMS ====================

enum class ButtonVariant {
    Primary,
    Secondary,
    Outlined
}

// ==================== DATA CLASSES ====================

// ==================== ENHANCED CASE CARD ====================

@Composable
fun EnhancedCaseCard(
    case: CaseEntity,
    statistics: CaseStatistics?,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    EnhancedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = 4
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.Medium)
        ) {
            // Header with case number and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "قضية رقم: ${case.caseNumber}",
                    style = AppTypography.TitleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )

                StatusBadge(
                    isActive = case.isActive,
                    modifier = Modifier
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.Small))

            // Case type and roll number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                case.caseType?.let { type ->
                    Text(
                        text = "النوع: $type",
                        style = AppTypography.BodyMedium,
                        color = AppColors.OnSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }

                case.rollNumber?.let { rollNumber ->
                    Text(
                        text = "رقم الجدول: $rollNumber",
                        style = AppTypography.BodyMedium,
                        color = AppColors.OnSurfaceVariant,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.Small))

            // Client and opponent
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "الموكل: ${case.clientName}",
                        style = AppTypography.BodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "الخصم: ${case.opponentName}",
                        style = AppTypography.BodyMedium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Case description
            case.caseDescription?.let { description ->
                Spacer(modifier = Modifier.height(AppSpacing.Small))
                Text(
                    text = "الوصف: $description",
                    style = AppTypography.BodySmall,
                    color = AppColors.OnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Statistics row
            statistics?.let { stats ->
                Spacer(modifier = Modifier.height(AppSpacing.Small))
                CaseStatisticsRow(statistics = stats)
            }

            Spacer(modifier = Modifier.height(AppSpacing.Small))

            // Creation date
            Text(
                text = "تاريخ الإنشاء: ${dateFormatter.format(Date(case.createdAt))}",
                style = AppTypography.BodySmall,
                color = AppColors.OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(AppSpacing.Medium))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle active status
                IconButton(onClick = onToggleStatus) {
                    Icon(
                        imageVector = if (case.isActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (case.isActive) "إغلاق القضية" else "تفعيل القضية",
                        tint = if (case.isActive) AppColors.Error else AppColors.Primary
                    )
                }

                // Edit button
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "تعديل القضية",
                        tint = AppColors.Primary
                    )
                }

                // Delete button
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف القضية",
                        tint = AppColors.Error
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = if (isActive) "نشطة" else "مغلقة",
                style = AppTypography.LabelSmall
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isActive)
                AppColors.Success.copy(alpha = 0.1f)
            else
                AppColors.Error.copy(alpha = 0.1f),
            labelColor = if (isActive)
                AppColors.Success
            else
                AppColors.Error
        ),
        modifier = modifier
    )
}

@Composable
fun CaseStatisticsRow(
    statistics: CaseStatistics,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatisticChip(
            label = "جلسات",
            value = statistics.totalSessions.toString(),
            color = AppColors.Primary
        )

        if (statistics.upcomingSessionsCount > 0) {
            StatisticChip(
                label = "قادمة",
                value = statistics.upcomingSessionsCount.toString(),
                color = AppColors.Secondary
            )
        }
    }
}

@Composable
fun StatisticChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = "$label: $value",
                style = AppTypography.LabelSmall
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color
        ),
        modifier = modifier
    )
}
