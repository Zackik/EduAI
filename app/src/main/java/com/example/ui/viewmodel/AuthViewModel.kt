package com.example.ui.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserProfile
import com.example.data.repository.AuthRepository
import com.example.data.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class AuthUiState {
    object Loading : AuthUiState()
    object Unauthenticated : AuthUiState()
    data class Authenticated(val user: FirebaseUser, val profile: UserProfile?) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authRepo: AuthRepository,
    private val firestoreRepo: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepo.observeAuthState().collect { firebaseUser ->
                if (firebaseUser != null) {
                    syncProfileAndCheckIn(firebaseUser)
                } else {
                    _uiState.value = AuthUiState.Unauthenticated
                }
            }
        }
    }

    fun attemptAutoSignIn() {
        authRepo.attemptAutoSignIn(
            scope = viewModelScope,
            onSuccess = { user -> syncProfileAndCheckIn(user) },
            onUnauthenticated = { _uiState.value = AuthUiState.Unauthenticated }
        )
    }

    fun signInWithGoogle(activity: Activity) {
        _uiState.value = AuthUiState.Loading
        authRepo.signInWithGoogle(
            activity = activity,
            scope = viewModelScope,
            onSuccess = { user -> syncProfileAndCheckIn(user) },
            onError = { errMsg -> _uiState.value = AuthUiState.Error(errMsg) },
            onCancelled = { _uiState.value = AuthUiState.Unauthenticated }
        )
    }

    private fun syncProfileAndCheckIn(user: FirebaseUser) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            try {
                firestoreRepo.observeUserProfile(user.uid).collect { existingProfile ->
                    val profileToSave = if (existingProfile != null) {
                        val newStreak = if (existingProfile.lastActiveDate == todayStr) {
                            existingProfile.streakDays
                        } else {
                            existingProfile.streakDays + 1
                        }
                        existingProfile.copy(
                            displayName = user.displayName ?: existingProfile.displayName,
                            email = user.email ?: existingProfile.email,
                            streakDays = newStreak,
                            lastActiveDate = todayStr
                        )
                    } else {
                        UserProfile(
                            userId = user.uid,
                            displayName = user.displayName ?: "Sinh viên CNTT",
                            email = user.email ?: "",
                            major = "Khoa học Máy tính & CNTT",
                            yearOfStudy = 3,
                            totalXp = 50,
                            streakDays = 1,
                            lastActiveDate = todayStr
                        )
                    }
                    firestoreRepo.saveUserProfile(profileToSave)
                    firestoreRepo.syncLeaderboardEntry(
                        displayName = profileToSave.displayName,
                        major = profileToSave.major,
                        totalXp = profileToSave.totalXp,
                        streakDays = profileToSave.streakDays
                    )
                    _uiState.value = AuthUiState.Authenticated(user, profileToSave)
                }
            } catch (e: Exception) {
                // If offline or Firestore is temporarily unreachable, still authenticate locally
                val localProfile = UserProfile(
                    userId = user.uid,
                    displayName = user.displayName ?: "Sinh viên CNTT",
                    email = user.email ?: "",
                    major = "Khoa học Máy tính & CNTT"
                )
                _uiState.value = AuthUiState.Authenticated(user, localProfile)
            }
        }
    }

    fun signOut() {
        authRepo.signOut(viewModelScope) {
            _uiState.value = AuthUiState.Unauthenticated
        }
    }
}
