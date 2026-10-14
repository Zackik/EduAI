package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.repository.AiTutorRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class TutorMode {
    CHAT,
    VOICE_LIVE
}

data class AiTutorUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "welcome",
            text = "Xin chào! Mình là Gia sư AI chuyên ngành Khoa học Máy tính & CNTT. Bạn đang gặp khó khăn ở thuật toán, hệ điều hành hay cần phân tích đề thi nào?",
            isUser = false
        )
    ),
    val mode: TutorMode = TutorMode.CHAT,
    val isThinking: Boolean = false,
    val isRecordingAudio: Boolean = false,
    val isVoiceSpeaking: Boolean = false,
    val liveVoiceStatusText: String = "Nhấn micro để trò chuyện trực tiếp qua giọng nói",
    val currentSubject: String = "Algorithms & Data Structures",
    val error: String? = null
)

class AiTutorViewModel(
    private val aiRepo: AiTutorRepository = AiTutorRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiTutorUiState())
    val uiState: StateFlow<AiTutorUiState> = _uiState.asStateFlow()

    fun switchMode(mode: TutorMode) {
        _uiState.value = _uiState.value.copy(mode = mode)
    }

    fun setSubject(subject: String) {
        _uiState.value = _uiState.value.copy(currentSubject = subject)
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = userText.trim(),
            isUser = true
        )
        val updatedMessages = _uiState.value.messages + userMsg
        _uiState.value = _uiState.value.copy(
            messages = updatedMessages,
            isThinking = true,
            error = null
        )

        viewModelScope.launch {
            val result = aiRepo.askTutor(
                userPrompt = userText,
                history = updatedMessages,
                subject = _uiState.value.currentSubject
            )
            val replyText = result.getOrElse { "Gia sư AI đang bận. Vui lòng thử lại sau." }
            val tutorMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = replyText,
                isUser = false
            )
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + tutorMsg,
                isThinking = false
            )
        }
    }

    fun onMicrophoneAudioRecorded(base64Audio: String) {
        _uiState.value = _uiState.value.copy(isThinking = true, error = null)
        viewModelScope.launch {
            val transcribeResult = aiRepo.transcribeAudio(base64Audio)
            val text = transcribeResult.getOrElse { "Không thể nhận diện âm thanh." }
            sendMessage(text)
        }
    }

    fun triggerVoiceLiveInteraction(topic: String) {
        _uiState.value = _uiState.value.copy(
            isVoiceSpeaking = true,
            liveVoiceStatusText = "Gia sư AI đang phản hồi qua giọng nói..."
        )
        viewModelScope.launch {
            val voiceResult = aiRepo.generateVoiceExplanation(topic)
            val speechText = voiceResult.getOrElse { "Đang kết nối lại đường truyền âm thanh." }
            _uiState.value = _uiState.value.copy(
                liveVoiceStatusText = speechText,
                isVoiceSpeaking = true
            )
            // Keep speaking animation for 4 seconds then return to idle
            delay(4000L)
            _uiState.value = _uiState.value.copy(
                isVoiceSpeaking = false,
                liveVoiceStatusText = "Gia sư AI đã sẵn sàng nghe bạn nói tiếp!"
            )
        }
    }

    fun startListeningVoice() {
        _uiState.value = _uiState.value.copy(
            isRecordingAudio = true,
            liveVoiceStatusText = "Đang lắng nghe câu hỏi từ bạn..."
        )
    }

    fun stopListeningVoiceAndAsk(simulatedPrompt: String = "Giải thích thuật toán Dijkstra và ứng dụng trong định tuyến mạng") {
        _uiState.value = _uiState.value.copy(
            isRecordingAudio = false,
            liveVoiceStatusText = "Đang phân tích âm thanh bằng Gemini..."
        )
        triggerVoiceLiveInteraction(simulatedPrompt)
    }
}
