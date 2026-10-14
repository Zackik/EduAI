package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppSectionHeader
import com.example.ui.components.MetricStatCard
import com.example.ui.theme.ColorCodeBg
import com.example.ui.theme.ColorStreak
import com.example.ui.theme.ColorSuccess
import com.example.ui.theme.ColorXp
import com.example.ui.viewmodel.AdminReportViewModel

@Composable
fun AdminReportScreen(
    adminViewModel: AdminReportViewModel,
    modifier: Modifier = Modifier
) {
    val state by adminViewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 84.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bảng Quản Trị & Báo Cáo Tiến Độ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hệ thống giám sát định kỳ chất lượng đào tạo và chuyên cần học viên",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    IconButton(
                        onClick = { adminViewModel.loadReportData() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Làm mới", tint = Color.White)
                    }
                }
            }
        }

        // Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Tổng học viên",
                    value = "${state.totalStudents}",
                    icon = Icons.Default.People,
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Điểm XP TB",
                    value = "${state.averageXp} XP",
                    icon = Icons.Default.TrendingUp,
                    accentColor = ColorXp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Streak TB",
                    value = "${state.averageStreak} ngày",
                    icon = Icons.Default.Timeline,
                    accentColor = ColorStreak,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Hoàn thành bài",
                    value = "88.2%",
                    icon = Icons.Default.Assessment,
                    accentColor = ColorSuccess,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Educational Track Progress Breakdown
        item {
            AppSectionHeader(
                title = "Thống kê chuyên ngành đào tạo",
                subtitle = "Chỉ số hoàn thành theo khung chương trình Đại học"
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    TrackProgressRow(
                        name = "Cấu trúc Dữ liệu & Giải thuật nâng cao (Core)",
                        progress = state.csRatio,
                        accentColor = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    TrackProgressRow(
                        name = "Lập trình Web & Di động cho người mới bắt đầu",
                        progress = state.webMobileRatio,
                        accentColor = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    TrackProgressRow(
                        name = "Hệ điều hành, Mạng & Hệ phân tán",
                        progress = 0.74f,
                        accentColor = ColorSuccess
                    )
                }
            }
        }

        // Periodic Report Viewer & Export Action
        item {
            AppSectionHeader(
                title = "Văn bản báo cáo định kỳ (Tự động sinh)",
                action = {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(state.generatedReportText))
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("export_report_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Sao chép báo cáo", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Xuất Báo Cáo")
                    }
                }
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                color = ColorCodeBg
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = state.generatedReportText,
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackProgressRow(
    name: String,
    progress: Float,
    accentColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = accentColor, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = accentColor
        )
    }
}
