package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.screens.AdminReportScreen
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.ExamScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.viewmodel.AdminReportViewModel
import com.example.ui.viewmodel.AiTutorViewModel
import com.example.ui.viewmodel.CommunityViewModel
import com.example.ui.viewmodel.ExamViewModel
import com.example.ui.viewmodel.LibraryViewModel
import com.example.ui.viewmodel.PlannerViewModel

enum class ScreenTab(
    val titleVi: String,
    val titleEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Lộ trình", "Planner", Icons.Default.Home, Icons.Outlined.Home),
    AI_TUTOR("Gia sư AI", "AI Tutor", Icons.Default.Psychology, Icons.Outlined.Psychology),
    EXAM("Thi thử", "Mock Exam", Icons.Default.Quiz, Icons.Outlined.Quiz),
    COMMUNITY("Thi đua", "Rankings", Icons.Default.EmojiEvents, Icons.Outlined.EmojiEvents),
    LIBRARY("Tài liệu", "Library", Icons.Default.MenuBook, Icons.Outlined.MenuBook),
    ADMIN("Báo cáo", "Admin", Icons.Default.Assessment, Icons.Outlined.Assessment),
    SETTINGS("Cài đặt", "Settings", Icons.Default.Settings, Icons.Outlined.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(
    userProfile: UserProfile?,
    plannerViewModel: PlannerViewModel,
    aiTutorViewModel: AiTutorViewModel,
    examViewModel: ExamViewModel,
    communityViewModel: CommunityViewModel,
    libraryViewModel: LibraryViewModel,
    adminViewModel: AdminReportViewModel,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    currentLanguage: String,
    onSelectLanguage: (String) -> Unit,
    onSignOut: () -> Unit
) {
    var currentTab by remember { mutableStateOf(ScreenTab.HOME) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "EduAI CS Prep",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (currentLanguage == "VI") currentTab.titleVi else currentTab.titleEn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Quick Language Switcher
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier
                            .clickable {
                                onSelectLanguage(if (currentLanguage == "VI") "EN" else "VI")
                            }
                            .padding(end = 4.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == "VI") "🇻🇳 VI" else "🇬🇧 EN",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }

                    // Dark Theme Quick Toggle
                    IconButton(onClick = { onToggleDarkTheme(!isDarkTheme) }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                            contentDescription = "Chuyển chế độ sáng tối",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Settings Icon
                    IconButton(
                        onClick = { currentTab = ScreenTab.SETTINGS },
                        modifier = Modifier.testTag("top_bar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Cài đặt",
                            tint = if (currentTab == ScreenTab.SETTINGS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                listOf(
                    ScreenTab.HOME,
                    ScreenTab.AI_TUTOR,
                    ScreenTab.EXAM,
                    ScreenTab.COMMUNITY,
                    ScreenTab.LIBRARY,
                    ScreenTab.ADMIN
                ).forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.titleVi
                            )
                        },
                        label = {
                            Text(
                                text = if (currentLanguage == "VI") tab.titleVi else tab.titleEn,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.HOME -> {
                    HomeScreen(
                        userProfile = userProfile,
                        plannerViewModel = plannerViewModel,
                        onNavigateToAiTutor = { currentTab = ScreenTab.AI_TUTOR },
                        onNavigateToExam = { currentTab = ScreenTab.EXAM }
                    )
                }
                ScreenTab.AI_TUTOR -> {
                    AiTutorScreen(tutorViewModel = aiTutorViewModel)
                }
                ScreenTab.EXAM -> {
                    ExamScreen(examViewModel = examViewModel)
                }
                ScreenTab.COMMUNITY -> {
                    CommunityScreen(communityViewModel = communityViewModel)
                }
                ScreenTab.LIBRARY -> {
                    LibraryScreen(libraryViewModel = libraryViewModel)
                }
                ScreenTab.ADMIN -> {
                    AdminReportScreen(adminViewModel = adminViewModel)
                }
                ScreenTab.SETTINGS -> {
                    SettingsScreen(
                        userProfile = userProfile,
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = onToggleDarkTheme,
                        currentLanguage = currentLanguage,
                        onSelectLanguage = onSelectLanguage,
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }
}
