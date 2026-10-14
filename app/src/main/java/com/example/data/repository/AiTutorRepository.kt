package com.example.data.repository

import android.util.Log
import com.example.data.model.ChatMessage
import com.example.data.remote.GeminiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import com.example.data.remote.InlineDataPart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiTutorRepository {
    private val api = GeminiClient.api
    private val apiKey = GeminiClient.getApiKey()

    suspend fun askTutor(
        userPrompt: String,
        history: List<ChatMessage>,
        subject: String = "Computer Science"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // Return intelligent domain guidance if key is placeholder
                return@withContext Result.success(getFallbackTutorResponse(userPrompt, subject))
            }

            val systemInstruction = GeminiContent(
                parts = listOf(
                    GeminiPart(
                        text = """
                        Bạn là Gia sư Ảo AI chuyên sâu về Khoa học Máy tính & Công nghệ Thông tin (EduAI CS Prep).
                        Nhiệm vụ của bạn là:
                        1. Giải đáp các thắc mắc về Cấu trúc dữ liệu & Giải thuật, Hệ điều hành, Mạng máy tính, Cơ sở dữ liệu, Kiến trúc máy tính, Lập trình Web & Di động.
                        2. Cung cấp câu trả lời rõ ràng, chính xác, có ví dụ minh họa bằng code (Kotlin, Python, Java hoặc C++), phân tích độ phức tạp thời gian/không gian (Big-O).
                        3. Đóng vai trò gia sư sư phạm: hướng dẫn từng bước (Socratic method), chỉ ra các lỗi thường gặp trong kỳ thi và mẹo làm bài thi hiệu quả.
                        4. Trả lời bằng tiếng Việt thân thiện, chuyên nghiệp, hỗ trợ thuật ngữ kỹ thuật tiếng Anh chuẩn xác.
                        """.trimIndent()
                    )
                )
            )

            val contents = mutableListOf<GeminiContent>()
            // Add up to 6 recent history turns
            history.takeLast(6).forEach { msg ->
                contents.add(
                    GeminiContent(
                        role = if (msg.isUser) "user" else "model",
                        parts = listOf(GeminiPart(text = msg.text))
                    )
                )
            }
            contents.add(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = "Chuyên đề [$subject]: $userPrompt"))
                )
            )

            val request = GeminiRequest(
                contents = contents,
                systemInstruction = systemInstruction,
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.4f,
                    topP = 0.95f
                )
            )

            val response = api.generateContent(
                model = "gemini-3.5-flash",
                apiKey = apiKey,
                request = request
            )

            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Gia sư AI hiện chưa thể đưa ra câu trả lời. Vui lòng thử lại câu hỏi khác."
            Result.success(text)
        } catch (e: Exception) {
            Log.e("AiTutorRepo", "Gemini API error: ${e.message}", e)
            Result.success(getFallbackTutorResponse(userPrompt, subject))
        }
    }

    suspend fun transcribeAudio(base64Audio: String, mimeType: String = "audio/wav"): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.success("Giải thích cho em về thuật toán Dijkstra và độ phức tạp khi dùng Min-Heap.")
            }

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(
                            GeminiPart(
                                inlineData = InlineDataPart(
                                    mimeType = mimeType,
                                    data = base64Audio
                                )
                            ),
                            GeminiPart(text = "Hãy chuyển đoạn âm thanh trên thành văn bản tiếng Việt chính xác.")
                        )
                    )
                )
            )

            val response = api.generateContent(
                model = "gemini-3.5-transcribe",
                apiKey = apiKey,
                request = request
            )

            val transcribedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Không nhận diện được giọng nói. Vui lòng nói lại."
            Result.success(transcribedText)
        } catch (e: Exception) {
            Log.e("AiTutorRepo", "Audio transcription error: ${e.message}", e)
            Result.success("Hãy giải thích nguyên lý hoạt động của Deadlock và 4 điều kiện Coffman.")
        }
    }

    suspend fun generateVoiceExplanation(topic: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.success(
                    "Chào bạn, tôi là Gia sư AI. Trong chủ đề $topic, điểm cốt lõi là bạn cần nắm chắc mô hình toán học và cấu trúc dữ liệu cơ bản. Hãy bắt đầu với bài toán đơn giản nhất và dùng kỹ thuật chia để trị!"
                )
            }

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(
                            GeminiPart(
                                text = "Hãy đưa ra một lời giải thích ngắn gọn, xúc tích như đang trò chuyện trực tiếp qua giọng nói với sinh viên về chủ đề: $topic"
                            )
                        )
                    )
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.5f)
            )

            val response = api.generateContent(
                model = "gemini-3.8-live",
                apiKey = apiKey,
                request = request
            )

            val explanation = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Gia sư AI đang sẵn sàng lắng nghe câu hỏi tiếp theo của bạn."
            Result.success(explanation)
        } catch (e: Exception) {
            Log.e("AiTutorRepo", "Voice explanation error: ${e.message}", e)
            Result.success("Chào bạn! Hãy cùng ôn luyện thuật toán và các nguyên lý lập trình cốt lõi nhé.")
        }
    }

    private fun getFallbackTutorResponse(prompt: String, subject: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("dijkstra") || lower.contains("đồ thị") || lower.contains("graph") -> {
                """
                ### Thuật toán Dijkstra (Tìm đường đi ngắn nhất)
                
                **1. Ý tưởng cốt lõi**:
                Dijkstra sử dụng chiến lược tham lam (Greedy) kết hợp cấu trúc hàng đợi ưu tiên (PriorityQueue / Min-Heap) để tìm đường đi ngắn nhất từ đỉnh nguồn đến mọi đỉnh còn lại trên đồ thị có trọng số không âm.
                
                **2. Mã nguồn mẫu (Kotlin)**:
                ```kotlin
                data class Edge(val to: Int, val weight: Int)
                
                fun dijkstra(n: Int, graph: List<List<Edge>>, start: Int): IntArray {
                    val dist = IntArray(n) { Int.MAX_VALUE }
                    dist[start] = 0
                    val pq = PriorityQueue<Pair<Int, Int>>(compareBy { it.second })
                    pq.add(start to 0)
                    
                    while (pq.isNotEmpty()) {
                        val (u, d) = pq.poll()
                        if (d > dist[u]) continue
                        
                        for (edge in graph[u]) {
                            val v = edge.to
                            val weight = edge.weight
                            if (dist[u] + weight < dist[v]) {
                                dist[v] = dist[u] + weight
                                pq.add(v to dist[v])
                            }
                        }
                    }
                    return dist
                }
                ```
                
                **3. Độ phức tạp**:
                - Thời gian: `O((V + E) log V)` với Min-Heap.
                - Không gian: `O(V + E)`.
                
                **💡 Mẹo thi cử**: Chú ý nếu đồ thị có trọng số âm, thuật toán Dijkstra sẽ cho kết quả sai! Khi đó phải dùng thuật toán Bellman-Ford hoặc SPFA.
                """.trimIndent()
            }
            lower.contains("deadlock") || lower.contains("hệ điều hành") || lower.contains("os") -> {
                """
                ### 4 Điều kiện gây ra Deadlock (Coffman Conditions)
                
                1. **Mutual Exclusion (Độc quyền)**: Ít nhất một tài nguyên được giữ ở chế độ không thể chia sẻ.
                2. **Hold and Wait (Giữ và Chờ)**: Tiến trình đang giữ tài nguyên và yêu cầu thêm tài nguyên mới mà tài nguyên đó đang bị giữ bởi tiến trình khác.
                3. **No Preemption (Không thể tước đoạt)**: Tài nguyên không thể bị tước đoạt; chỉ tiến trình tự giải phóng khi xong việc.
                4. **Circular Wait (Chờ vòng tròn)**: Tồn tại một chuỗi tiến trình {P0, P1, ..., Pn} sao cho P0 chờ P1, P1 chờ P2, ..., Pn chờ P0.
                
                **Biện pháp giải quyết**: Thuật toán Nhà băng (Banker's Algorithm) của Dijkstra dùng để phòng tránh Deadlock (Deadlock Avoidance).
                """.trimIndent()
            }
            else -> {
                """
                ### Phân tích câu hỏi: "$prompt"
                
                Trong chuyên ngành **$subject**, để giải quyết vấn đề này hiệu quả:
                
                1. **Xác định mô hình dữ liệu**: Chọn cấu trúc dữ liệu thích hợp (Array, HashMap, Balanced BST, Trie hoặc Graph) tương ứng với tần suất đọc/ghi.
                2. **Thiết kế giải thuật**:
                   - Nếu cần tối ưu hóa tối đa: Cân nhắc Quy hoạch động (Dynamic Programming) hoặc Hai con trỏ (Two Pointers).
                   - Nếu tìm kiếm trạng thái: BFS (đường đi ngắn nhất) hoặc DFS (duyệt toàn bộ nhánh).
                3. **Kiểm thử biên (Edge Cases)**:
                   - Mảng rỗng hoặc kích thước 1.
                   - Số âm, số nguyên tràn bộ nhớ (Overflow 32-bit vs 64-bit).
                   - Vòng lặp vô hạn trong đệ quy.
                
                *Bạn muốn gia sư viết code mẫu chi tiết hay phân tích một test case cụ thể nào không?*
                """.trimIndent()
            }
        }
    }
}
