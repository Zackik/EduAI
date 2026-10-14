package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.repository.AiTutorRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.FirestoreRepository
import com.example.ui.components.MainAppContainer
import com.example.ui.screens.AuthScreen
import com.example.ui.theme.EduAiTheme
import com.example.ui.viewmodel.AdminReportViewModel
import com.example.ui.viewmodel.AiTutorViewModel
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.CommunityViewModel
import com.example.ui.viewmodel.ExamViewModel
import com.example.ui.viewmodel.LibraryViewModel
import com.example.ui.viewmodel.PlannerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepo = AuthRepository(this)
        val firestoreRepo = FirestoreRepository(this)
        val localDb = AppDatabase.getInstance(this)
        val aiRepo = AiTutorRepository()

        val authViewModel = AuthViewModel(authRepo, firestoreRepo)

        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }
            var currentLanguage by remember { mutableStateOf("VI") }

            EduAiTheme(darkTheme = isDarkTheme) {
                val authState by authViewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    authViewModel.attemptAutoSignIn()
                }

                when (val current = authState) {
                    is AuthUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    is AuthUiState.Unauthenticated -> {
                        AuthScreen(
                            isLoading = false,
                            onSignInClicked = { activity ->
                                authViewModel.signInWithGoogle(activity)
                            }
                        )
                    }
                    is AuthUiState.Error -> {
                        AuthScreen(
                            isLoading = false,
                            errorMessage = current.message,
                            onSignInClicked = { activity ->
                                authViewModel.signInWithGoogle(activity)
                            }
                        )
                    }
                    is AuthUiState.Authenticated -> {
                        // Auth-gated instantiation: ViewModels are created ONLY when user is signed in
                        val userId = current.user.uid
                        val displayName = current.user.displayName ?: current.profile?.displayName ?: "Sinh viên CNTT"

                        val plannerViewModel = remember(userId) {
                            PlannerViewModel(firestoreRepo, localDb, userId)
                        }
                        val aiTutorViewModel = remember {
                            AiTutorViewModel(aiRepo)
                        }
                        val examViewModel = remember(userId) {
                            ExamViewModel(firestoreRepo, userId)
                        }
                        val communityViewModel = remember(userId) {
                            CommunityViewModel(firestoreRepo, userId, displayName)
                        }
                        val libraryViewModel = remember {
                            LibraryViewModel(firestoreRepo, localDb)
                        }
                        val adminViewModel = remember {
                            AdminReportViewModel(firestoreRepo)
                        }

                        MainAppContainer(
                            userProfile = current.profile,
                            plannerViewModel = plannerViewModel,
                            aiTutorViewModel = aiTutorViewModel,
                            examViewModel = examViewModel,
                            communityViewModel = communityViewModel,
                            libraryViewModel = libraryViewModel,
                            adminViewModel = adminViewModel,
                            isDarkTheme = isDarkTheme,
                            onToggleDarkTheme = { isDarkTheme = it },
                            currentLanguage = currentLanguage,
                            onSelectLanguage = { currentLanguage = it },
                            onSignOut = { authViewModel.signOut() }
                        )
                    }
                }
            }
        }
    }
}
