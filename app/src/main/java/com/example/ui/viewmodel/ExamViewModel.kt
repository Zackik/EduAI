package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ExamCatalog
import com.example.data.model.ExamResult
import com.example.data.model.MockExam
import com.example.data.repository.FirestoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class ExamScreenState {
    object Catalog : ExamScreenState()
    data class InProgress(
        val exam: MockExam,
        val currentQuestionIndex: Int,
        val selectedAnswers: Map<Int, Int>, // questionIndex -> optionIndex
        val secondsRemaining: Int
    ) : ExamScreenState()
    data class Completed(
        val exam: MockExam,
        val score: Int,
        val correctCount: Int,
        val totalQuestions: Int,
        val timeSpentSeconds: Int,
        val selectedAnswers: Map<Int, Int>
    ) : ExamScreenState()
}

class ExamViewModel(
    private val firestoreRepo: FirestoreRepository,
    private val userId: String
) : ViewModel() {

    private val _screenState = MutableStateFlow<ExamScreenState>(ExamScreenState.Catalog)
    val screenState: StateFlow<ExamScreenState> = _screenState.asStateFlow()

    private val _pastResults = MutableStateFlow<List<ExamResult>>(emptyList())
    val pastResults: StateFlow<List<ExamResult>> = _pastResults.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadPastResults()
    }

    private fun loadPastResults() {
        viewModelScope.launch {
            firestoreRepo.observeExamResults(userId).collect { results ->
                _pastResults.value = results
            }
        }
    }

    fun startExam(exam: MockExam) {
        timerJob?.cancel()
        val totalSeconds = exam.durationMinutes * 60
        _screenState.value = ExamScreenState.InProgress(
            exam = exam,
            currentQuestionIndex = 0,
            selectedAnswers = emptyMap(),
            secondsRemaining = totalSeconds
        )

        timerJob = viewModelScope.launch {
            var remaining = totalSeconds
            while (remaining > 0) {
                delay(1000L)
                remaining--
                val currentState = _screenState.value
                if (currentState is ExamScreenState.InProgress) {
                    _screenState.value = currentState.copy(secondsRemaining = remaining)
                } else {
                    break
                }
            }
            if (_screenState.value is ExamScreenState.InProgress) {
                submitExam()
            }
        }
    }

    fun selectAnswer(optionIndex: Int) {
        val current = _screenState.value
        if (current is ExamScreenState.InProgress) {
            val updated = current.selectedAnswers.toMutableMap()
            updated[current.currentQuestionIndex] = optionIndex
            _screenState.value = current.copy(selectedAnswers = updated)
        }
    }

    fun navigateQuestion(targetIndex: Int) {
        val current = _screenState.value
        if (current is ExamScreenState.InProgress) {
            if (targetIndex in current.exam.questions.indices) {
                _screenState.value = current.copy(currentQuestionIndex = targetIndex)
            }
        }
    }

    fun submitExam() {
        val current = _screenState.value
        if (current !is ExamScreenState.InProgress) return
        timerJob?.cancel()

        val exam = current.exam
        val answers = current.selectedAnswers
        var correctCount = 0

        exam.questions.forEachIndexed { index, question ->
            val chosen = answers[index]
            if (chosen != null && chosen == question.correctOptionIndex) {
                correctCount++
            }
        }

        val total = exam.questions.size
        val scorePercent = if (total > 0) (correctCount * 100) / total else 0
        val timeSpent = (exam.durationMinutes * 60) - current.secondsRemaining

        val result = ExamResult(
            resultId = UUID.randomUUID().toString(),
            userId = userId,
            examId = exam.examId,
            examTitle = exam.title,
            subject = exam.subject,
            score = scorePercent,
            totalQuestions = total,
            correctAnswers = correctCount,
            timeSpentSeconds = timeSpent
        )

        viewModelScope.launch {
            try {
                firestoreRepo.recordExamResult(result)
            } catch (e: Exception) {
                // Ignore or handle offline
            }
        }

        _screenState.value = ExamScreenState.Completed(
            exam = exam,
            score = scorePercent,
            correctCount = correctCount,
            totalQuestions = total,
            timeSpentSeconds = timeSpent,
            selectedAnswers = answers
        )
    }

    fun exitToCatalog() {
        timerJob?.cancel()
        _screenState.value = ExamScreenState.Catalog
    }
}
