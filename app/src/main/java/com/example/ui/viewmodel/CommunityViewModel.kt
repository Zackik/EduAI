package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.GroupMessage
import com.example.data.model.LeaderboardEntry
import com.example.data.model.StudyGroup
import com.example.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CommunityUiState(
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val studyGroups: List<StudyGroup> = emptyList(),
    val selectedGroup: StudyGroup? = null,
    val groupMessages: List<GroupMessage> = emptyList(),
    val messageInput: String = "",
    val isLoading: Boolean = false,
    val feedbackMessage: String? = null
)

class CommunityViewModel(
    private val firestoreRepo: FirestoreRepository,
    private val userId: String,
    private val userDisplayName: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    init {
        loadCommunityData()
    }

    private fun loadCommunityData() {
        viewModelScope.launch {
            firestoreRepo.observeLeaderboard().collect { entries ->
                _uiState.value = _uiState.value.copy(leaderboard = entries)
            }
        }

        viewModelScope.launch {
            firestoreRepo.observeStudyGroups().collect { groups ->
                _uiState.value = _uiState.value.copy(studyGroups = groups)
            }
        }
    }

    fun selectGroup(group: StudyGroup) {
        _uiState.value = _uiState.value.copy(selectedGroup = group, groupMessages = emptyList())
        viewModelScope.launch {
            firestoreRepo.observeGroupMessages(group.groupId).collect { messages ->
                _uiState.value = _uiState.value.copy(groupMessages = messages)
            }
        }
    }

    fun clearSelectedGroup() {
        _uiState.value = _uiState.value.copy(selectedGroup = null, groupMessages = emptyList())
    }

    fun setMessageInput(text: String) {
        _uiState.value = _uiState.value.copy(messageInput = text)
    }

    fun sendMessage() {
        val group = _uiState.value.selectedGroup ?: return
        val text = _uiState.value.messageInput.trim()
        if (text.isBlank()) return

        _uiState.value = _uiState.value.copy(messageInput = "")
        viewModelScope.launch {
            try {
                firestoreRepo.sendGroupMessage(group.groupId, userDisplayName, text)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(feedbackMessage = "Lỗi gửi tin nhắn: ${e.message}")
            }
        }
    }

    fun joinGroup(group: StudyGroup) {
        viewModelScope.launch {
            try {
                firestoreRepo.joinStudyGroup(group)
                _uiState.value = _uiState.value.copy(feedbackMessage = "Đã tham gia nhóm: ${group.name}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(feedbackMessage = "Lỗi tham gia nhóm: ${e.message}")
            }
        }
    }

    fun createGroup(name: String, subject: String, description: String) {
        viewModelScope.launch {
            try {
                firestoreRepo.createStudyGroup(name, subject, description)
                _uiState.value = _uiState.value.copy(feedbackMessage = "Đã tạo nhóm học: $name")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(feedbackMessage = "Lỗi tạo nhóm: ${e.message}")
            }
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}
