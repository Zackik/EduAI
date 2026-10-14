package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val major: String = "Computer Science",
    val yearOfStudy: Int = 3,
    val totalXp: Int = 0,
    val streakDays: Int = 1,
    val lastActiveDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class StudyPlan(
    @DocumentId val id: String = "",
    val planId: String = "",
    val userId: String = "",
    val title: String = "",
    val subject: String = "",
    val targetExamDate: String = "",
    val dailyMinutes: Int = 60,
    val progressPercent: Int = 0,
    val status: String = "active", // active, completed, archived
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class StudySession(
    @DocumentId val id: String = "",
    val sessionId: String = "",
    val userId: String = "",
    val subject: String = "",
    val durationMinutes: Int = 25,
    val date: String = "",
    val notes: String = "",
    val isCompleted: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ExamResult(
    @DocumentId val id: String = "",
    val resultId: String = "",
    val userId: String = "",
    val examId: String = "",
    val examTitle: String = "",
    val subject: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val timeSpentSeconds: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class LeaderboardEntry(
    @DocumentId val id: String = "",
    val entryId: String = "",
    val userId: String = "",
    val displayName: String = "",
    val major: String = "",
    val totalXp: Int = 0,
    val rankTier: String = "Bronze",
    val streakDays: Int = 0,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class StudyGroup(
    @DocumentId val id: String = "",
    val groupId: String = "",
    val name: String = "",
    val subject: String = "",
    val description: String = "",
    val memberUids: List<String> = emptyList(),
    val createdBy: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class GroupMessage(
    @DocumentId val id: String = "",
    val messageId: String = "",
    val groupId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class StudyMaterial(
    @DocumentId val id: String = "",
    val materialId: String = "",
    val title: String = "",
    val subject: String = "",
    val category: String = "",
    val summary: String = "",
    val content: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

// In-app Exam Data Structure
data class QuizQuestion(
    val id: String,
    val questionText: String,
    val codeSnippet: String? = null,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

data class MockExam(
    val examId: String,
    val title: String,
    val subject: String,
    val durationMinutes: Int,
    val questions: List<QuizQuestion>
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val codeSnippet: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
