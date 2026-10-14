package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GroupMessage
import com.example.data.model.LeaderboardEntry
import com.example.data.model.StudyGroup
import com.example.ui.components.AppSectionHeader
import com.example.ui.theme.ColorStreak
import com.example.ui.theme.ColorXp
import com.example.ui.viewmodel.CommunityViewModel

@Composable
fun CommunityScreen(
    communityViewModel: CommunityViewModel,
    modifier: Modifier = Modifier
) {
    val state by communityViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        // Tab Row: Leaderboard vs Study Groups
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Bảng xếp hạng", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bảng Thi Đua (XP)")
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Group, contentDescription = "Nhóm học tập", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nhóm Thảo Luận")
                    }
                }
            )
        }

        if (selectedTab == 0) {
            // LEADERBOARD TAB
            LeaderboardTabContent(entries = state.leaderboard)
        } else {
            // STUDY GROUPS & DISCUSSIONS TAB
            if (state.selectedGroup != null) {
                GroupChatRoom(
                    group = state.selectedGroup!!,
                    messages = state.groupMessages,
                    input = state.messageInput,
                    onInputChange = { communityViewModel.setMessageInput(it) },
                    onSendMessage = { communityViewModel.sendMessage() },
                    onBack = { communityViewModel.clearSelectedGroup() }
                )
            } else {
                StudyGroupsList(
                    groups = state.studyGroups,
                    onSelectGroup = { communityViewModel.selectGroup(it) },
                    onJoinGroup = { communityViewModel.joinGroup(it) },
                    onOpenCreateDialog = { showCreateGroupDialog = true }
                )
            }
        }
    }

    if (showCreateGroupDialog) {
        CreateStudyGroupDialog(
            onDismiss = { showCreateGroupDialog = false },
            onCreate = { name, subject, desc ->
                communityViewModel.createGroup(name, subject, desc)
                showCreateGroupDialog = false
            }
        )
    }
}

@Composable
private fun LeaderboardTabContent(
    entries: List<LeaderboardEntry>
) {
    val sampleList = if (entries.isNotEmpty()) entries else listOf(
        LeaderboardEntry(displayName = "Trần Minh Quân", major = "Khoa học Máy tính", totalXp = 2450, rankTier = "Diamond", streakDays = 21),
        LeaderboardEntry(displayName = "Nguyễn Thùy Linh", major = "Kỹ thuật Phần mềm", totalXp = 2100, rankTier = "Diamond", streakDays = 18),
        LeaderboardEntry(displayName = "Lê Hải Đăng", major = "Lập trình Web/Mobile", totalXp = 1750, rankTier = "Platinum", streakDays = 14),
        LeaderboardEntry(displayName = "Phạm Thanh Tùng", major = "Khoa học Dữ liệu", totalXp = 1420, rankTier = "Platinum", streakDays = 12),
        LeaderboardEntry(displayName = "Vũ Đình Nam", major = "An toàn Thông tin", totalXp = 980, rankTier = "Gold", streakDays = 8),
        LeaderboardEntry(displayName = "Hoàng Phương Thảo", major = "Khoa học Máy tính", totalXp = 750, rankTier = "Gold", streakDays = 6)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = "Huy chương",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Đấu Trường Học Thuật Toàn Cầu",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Tích lũy XP qua giải đề thi, chuỗi chuyên cần & trao đổi gia sư",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        item {
            AppSectionHeader(title = "Bảng vàng danh dự")
        }

        itemsIndexed(sampleList) { index, entry ->
            val rank = index + 1
            val rankColor = when (rank) {
                1 -> Color(0xFFFFD700) // Gold
                2 -> Color(0xFFC0C0C0) // Silver
                3 -> Color(0xFFCD7F32) // Bronze
                else -> MaterialTheme.colorScheme.outline
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rank Badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(rankColor.copy(alpha = if (rank <= 3) 0.25f else 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#$rank",
                            fontWeight = FontWeight.Bold,
                            color = if (rank <= 3) rankColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.displayName,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = entry.major.ifBlank { "Khoa học Máy tính" },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Streak & XP
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = ColorStreak,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${entry.streakDays}d",
                                style = MaterialTheme.typography.labelSmall,
                                color = ColorStreak,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ColorXp.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${entry.totalXp} XP",
                                color = ColorXp,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyGroupsList(
    groups: List<StudyGroup>,
    onSelectGroup: (StudyGroup) -> Unit,
    onJoinGroup: (StudyGroup) -> Unit,
    onOpenCreateDialog: () -> Unit
) {
    val sampleGroups = if (groups.isNotEmpty()) groups else listOf(
        StudyGroup(
            groupId = "group_algo",
            name = "CLB Luyện Thuật Toán & LeetCode",
            subject = "Data Structures & Algorithms",
            description = "Cùng giải bài tập quy hoạch động, đồ thị và chuẩn bị phỏng vấn kỹ thuật hàng tuần.",
            memberUids = listOf("1", "2", "3", "4", "5")
        ),
        StudyGroup(
            groupId = "group_web_mobile",
            name = "Nhóm Lập Trình Web & Di Động",
            subject = "Web & Mobile Dev",
            description = "Chia sẻ kinh nghiệm làm đồ án thực tế với Kotlin Jetpack Compose, React và Node.js.",
            memberUids = listOf("1", "2", "3")
        ),
        StudyGroup(
            groupId = "group_os",
            name = "Ôn Thi Hệ Điều Hành & Kiến Trúc",
            subject = "Operating Systems",
            description = "Ôn tập đề thi lý thuyết và bài tập về quản lý tiến trình, bộ nhớ ảo, paging.",
            memberUids = listOf("1", "2")
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nhóm học tập & Thảo luận",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Kết nối bạn bè cùng chuyên ngành, chia sẻ lịch thi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = onOpenCreateDialog,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tạo nhóm")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tạo nhóm")
                }
            }
        }

        items(sampleGroups) { grp ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectGroup(grp) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = grp.subject,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${grp.memberUids.size} thành viên",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = grp.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = grp.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onSelectGroup(grp) }) {
                            Text("Vào phòng chat →")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupChatRoom(
    group: StudyGroup,
    messages: List<GroupMessage>,
    input: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onBack: () -> Unit
) {
    val sampleMessages = if (messages.isNotEmpty()) messages else listOf(
        GroupMessage(senderName = "Trần Minh", content = "Chào cả nhóm! Hôm nay ôn bài nào vậy mọi người?"),
        GroupMessage(senderName = "Hải Đăng", content = "Chúng mình đang giải bài tập Quy hoạch động Knapsack và Đồ thị nhé."),
        GroupMessage(senderName = "Thu Hà", content = "Gia sư AI vừa giải thích thuật toán Dijkstra rất dễ hiểu trong phòng AI Tutor đó!")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Room Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Trở lại")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(group.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text(group.subject, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sampleMessages) { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = msg.senderName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = msg.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Send Input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    placeholder = { Text("Nhắn tin vào nhóm học tập...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onSendMessage,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Gửi", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CreateStudyGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Computer Science Core") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo nhóm học tập mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên nhóm học tập") },
                    placeholder = { Text("Ví dụ: Nhóm Ôn Thi Giải Thuật C++") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Môn học / Chuyên đề") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Mục tiêu & Mô tả nhóm") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, subject, desc)
                    }
                }
            ) {
                Text("Tạo nhóm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
