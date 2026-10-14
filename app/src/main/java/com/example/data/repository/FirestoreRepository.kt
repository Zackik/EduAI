package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.ExamResult
import com.example.data.model.GroupMessage
import com.example.data.model.LeaderboardEntry
import com.example.data.model.StudyGroup
import com.example.data.model.StudyMaterial
import com.example.data.model.StudyPlan
import com.example.data.model.StudySession
import com.example.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val context: Context) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.applicationContext.getString(R.string.firestore_database_id)
    )

    private fun requireUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // 1. User Profile
    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val docRef = db.collection("users").document(userId)
        emitAll(
            docRef.snapshots()
                .map { snapshot -> snapshot.toObject<UserProfile>() }
                .catch { error ->
                    Log.e("FirestoreRepo", "Error observing user profile: ${error.message}", error)
                    throw error
                }
        )
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val payload = mapOf(
            "userId" to uid,
            "displayName" to profile.displayName.ifBlank { "CS Student" },
            "email" to (FirebaseAuth.getInstance().currentUser?.email ?: profile.email),
            "major" to profile.major,
            "yearOfStudy" to profile.yearOfStudy,
            "totalXp" to profile.totalXp,
            "streakDays" to profile.streakDays,
            "lastActiveDate" to profile.lastActiveDate,
            "createdAt" to (profile.createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // 2. Study Plans
    fun observeStudyPlans(userId: String): Flow<List<StudyPlan>> = flow {
        val collection = db.collection("users").document(userId).collection("plans")
        emitAll(
            collection.orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot -> snapshot.toObjects<StudyPlan>() }
                .catch { error ->
                    Log.e("FirestoreRepo", "Error observing study plans: ${error.message}", error)
                    throw error
                }
        )
    }

    suspend fun addStudyPlan(plan: StudyPlan) {
        val uid = requireUserId()
        val plansRef = db.collection("users").document(uid).collection("plans")
        val docRef = if (plan.planId.isNotBlank()) plansRef.document(plan.planId) else plansRef.document()
        val newPlanId = docRef.id
        val payload = mapOf(
            "planId" to newPlanId,
            "userId" to uid,
            "title" to plan.title,
            "subject" to plan.subject,
            "targetExamDate" to plan.targetExamDate,
            "dailyMinutes" to plan.dailyMinutes,
            "progressPercent" to plan.progressPercent,
            "status" to plan.status,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    suspend fun updateStudyPlanProgress(planId: String, newProgress: Int, status: String = "active") {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("plans").document(planId)
        docRef.update(
            mapOf(
                "progressPercent" to newProgress,
                "status" to status,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    // 3. Study Sessions (Attendance & Focus)
    fun observeStudySessions(userId: String): Flow<List<StudySession>> = flow {
        val collection = db.collection("users").document(userId).collection("sessions")
        emitAll(
            collection.orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot -> snapshot.toObjects<StudySession>() }
                .catch { error ->
                    Log.e("FirestoreRepo", "Error observing sessions: ${error.message}", error)
                    throw error
                }
        )
    }

    suspend fun logStudySession(session: StudySession) {
        val uid = requireUserId()
        val sessionsRef = db.collection("users").document(uid).collection("sessions")
        val docRef = if (session.sessionId.isNotBlank()) sessionsRef.document(session.sessionId) else sessionsRef.document()
        val newSessionId = docRef.id
        val payload = mapOf(
            "sessionId" to newSessionId,
            "userId" to uid,
            "subject" to session.subject,
            "durationMinutes" to session.durationMinutes,
            "date" to session.date,
            "notes" to session.notes,
            "isCompleted" to session.isCompleted,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // 4. Exam Results
    fun observeExamResults(userId: String): Flow<List<ExamResult>> = flow {
        val collection = db.collection("users").document(userId).collection("exam_results")
        emitAll(
            collection.orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot -> snapshot.toObjects<ExamResult>() }
                .catch { error ->
                    Log.e("FirestoreRepo", "Error observing exam results: ${error.message}", error)
                    throw error
                }
        )
    }

    suspend fun recordExamResult(result: ExamResult) {
        val uid = requireUserId()
        val collection = db.collection("users").document(uid).collection("exam_results")
        val docRef = if (result.resultId.isNotBlank()) collection.document(result.resultId) else collection.document()
        val newId = docRef.id
        val payload = mapOf(
            "resultId" to newId,
            "userId" to uid,
            "examId" to result.examId,
            "examTitle" to result.examTitle,
            "subject" to result.subject,
            "score" to result.score,
            "totalQuestions" to result.totalQuestions,
            "correctAnswers" to result.correctAnswers,
            "timeSpentSeconds" to result.timeSpentSeconds,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // 5. Leaderboard
    fun observeLeaderboard(): Flow<List<LeaderboardEntry>> = flow {
        emitAll(
            db.collection("leaderboard")
                .orderBy("totalXp", Query.Direction.DESCENDING)
                .limit(50)
                .snapshots()
                .map { snapshot -> snapshot.toObjects<LeaderboardEntry>() }
                .catch { error ->
                    Log.e("FirestoreRepo", "Error observing leaderboard: ${error.message}", error)
                    throw error
                }
        )
    }

    suspend fun syncLeaderboardEntry(displayName: String, major: String, totalXp: Int, streakDays: Int) {
        val uid = requireUserId()
        val tier = when {
            totalXp >= 2000 -> "Diamond"
            totalXp >= 1200 -> "Platinum"
            totalXp >= 600 -> "Gold"
            totalXp >= 250 -> "Silver"
            else -> "Bronze"
        }
        val docRef = db.collection("leaderboard").document(uid)
        val payload = mapOf(
            "entryId" to uid,
            "userId" to uid,
            "displayName" to displayName,
            "major" to major,
            "totalXp" to totalXp,
            "rankTier" to tier,
            "streakDays" to streakDays,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // 6. Study Groups
    fun observeStudyGroups(): Flow<List<StudyGroup>> = flow {
        emitAll(
            db.collection("study_groups")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot -> snapshot.toObjects<StudyGroup>() }
                .catch { error ->
                    Log.e("FirestoreRepo", "Error observing study groups: ${error.message}", error)
                    throw error
                }
        )
    }

    suspend fun createStudyGroup(name: String, subject: String, description: String): String {
        val uid = requireUserId()
        val docRef = db.collection("study_groups").document()
        val groupId = docRef.id
        val payload = mapOf(
            "groupId" to groupId,
            "name" to name,
            "subject" to subject,
            "description" to description,
            "memberUids" to listOf(uid),
            "createdBy" to uid,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
        return groupId
    }

    suspend fun joinStudyGroup(group: StudyGroup) {
        val uid = requireUserId()
        if (uid in group.memberUids) return
        val updatedMembers = (group.memberUids + uid).distinct()
        val docRef = db.collection("study_groups").document(group.groupId)
        docRef.update(
            mapOf(
                "memberUids" to updatedMembers,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    fun observeGroupMessages(groupId: String): Flow<List<GroupMessage>> = flow {
        emitAll(
            db.collection("study_groups").document(groupId).collection("messages")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .limit(100)
                .snapshots()
                .map { snapshot -> snapshot.toObjects<GroupMessage>() }
                .catch { error ->
                    Log.e("FirestoreRepo", "Error observing messages for group $groupId: ${error.message}", error)
                    throw error
                }
        )
    }

    suspend fun sendGroupMessage(groupId: String, senderName: String, content: String) {
        val uid = requireUserId()
        val messagesRef = db.collection("study_groups").document(groupId).collection("messages")
        val docRef = messagesRef.document()
        val payload = mapOf(
            "messageId" to docRef.id,
            "groupId" to groupId,
            "senderId" to uid,
            "senderName" to senderName,
            "content" to content,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // 7. Study Materials
    fun observeStudyMaterials(): Flow<List<StudyMaterial>> = flow {
        emitAll(
            db.collection("materials")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot -> snapshot.toObjects<StudyMaterial>() }
                .catch { error ->
                    Log.e("FirestoreRepo", "Error observing materials: ${error.message}", error)
                    throw error
                }
        )
    }

    // 8. Admin Reports (Lists all users for authorized admins)
    suspend fun getAdminUserReports(): List<UserProfile> {
        val snapshot = db.collection("users").get().await()
        return snapshot.toObjects<UserProfile>()
    }
}
