package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.LocalSessionEntity
import com.example.data.model.StudyPlan
import com.example.data.model.StudySession
import com.example.data.repository.FirestoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class PlannerUiState(
    val plans: List<StudyPlan> = emptyList(),
    val sessions: List<StudySession> = emptyList(),
    val totalMinutesToday: Int = 0,
    val targetMinutesToday: Int = 90,
    val isTimerRunning: Boolean = false,
    val timerSecondsRemaining: Int = 25 * 60, // 25 min pomodoro
    val selectedSubject: String = "Data Structures & Algorithms",
    val isLoading: Boolean = false,
    val feedbackMessage: String? = null
)

class PlannerViewModel(
    private val firestoreRepo: FirestoreRepository,
    private val localDb: AppDatabase,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            firestoreRepo.observeStudyPlans(userId).collect { plansList ->
                _uiState.value = _uiState.value.copy(plans = plansList)
            }
        }

        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            firestoreRepo.observeStudySessions(userId).collect { sessionsList ->
                val todayMinutes = sessionsList
                    .filter { it.date == todayStr }
                    .sumOf { it.durationMinutes }
                _uiState.value = _uiState.value.copy(
                    sessions = sessionsList,
                    totalMinutesToday = todayMinutes
                )
            }
        }
    }

    fun startTimer() {
        if (_uiState.value.isTimerRunning) return
        _uiState.value = _uiState.value.copy(isTimerRunning = true)
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSecondsRemaining > 0 && _uiState.value.isTimerRunning) {
                delay(1000L)
                _uiState.value = _uiState.value.copy(
                    timerSecondsRemaining = _uiState.value.timerSecondsRemaining - 1
                )
            }
            if (_uiState.value.timerSecondsRemaining <= 0) {
                completeFocusSession(25)
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerRunning = false)
    }

    fun resetTimer() {
        pauseTimer()
        _uiState.value = _uiState.value.copy(timerSecondsRemaining = 25 * 60)
    }

    fun setSubject(subject: String) {
        _uiState.value = _uiState.value.copy(selectedSubject = subject)
    }

    fun completeFocusSession(durationMinutes: Int) {
        pauseTimer()
        _uiState.value = _uiState.value.copy(timerSecondsRemaining = 25 * 60)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val sessionId = UUID.randomUUID().toString()

        val newSession = StudySession(
            sessionId = sessionId,
            userId = userId,
            subject = _uiState.value.selectedSubject,
            durationMinutes = durationMinutes,
            date = todayStr,
            notes = "Hoàn thành phiên tập trung Pomodoro cho chuyên đề ${_uiState.value.selectedSubject}",
            isCompleted = true
        )

        viewModelScope.launch {
            // Save to local Room cache
            localDb.sessionDao().insert(
                LocalSessionEntity(
                    id = sessionId,
                    subject = newSession.subject,
                    durationMinutes = newSession.durationMinutes,
                    date = todayStr,
                    notes = newSession.notes,
                    isSynced = true
                )
            )

            // Sync to Firestore
            try {
                firestoreRepo.logStudySession(newSession)
                _uiState.value = _uiState.value.copy(
                    feedbackMessage = "Chúc mừng! Đã ghi nhận +$durationMinutes phút học tập."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    feedbackMessage = "Đã lưu phiên học offline vào thiết bị."
                )
            }
        }
    }

    fun createStudyPlan(title: String, subject: String, targetExamDate: String, dailyMinutes: Int) {
        viewModelScope.launch {
            val newPlan = StudyPlan(
                title = title,
                subject = subject,
                targetExamDate = targetExamDate,
                dailyMinutes = dailyMinutes,
                progressPercent = 0,
                status = "active"
            )
            try {
                firestoreRepo.addStudyPlan(newPlan)
                _uiState.value = _uiState.value.copy(feedbackMessage = "Đã tạo lộ trình ôn thi: $title")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(feedbackMessage = "Không thể tạo kế hoạch: ${e.message}")
            }
        }
    }

    fun updatePlanProgress(planId: String, currentPercent: Int) {
        val newPercent = (currentPercent + 10).coerceAtMost(100)
        val status = if (newPercent >= 100) "completed" else "active"
        viewModelScope.launch {
            try {
                firestoreRepo.updateStudyPlanProgress(planId, newPercent, status)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(feedbackMessage = "Cập nhật tiến độ lỗi: ${e.message}")
            }
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}
