package com.example.data.model

object ExamCatalog {
    val sampleExams = listOf(
        MockExam(
            examId = "exam_dsa_01",
            title = "Kỳ thi mô phỏng: Cấu trúc Dữ liệu & Giải thuật",
            subject = "Data Structures & Algorithms",
            durationMinutes = 15,
            questions = listOf(
                QuizQuestion(
                    id = "q1",
                    questionText = "Độ phức tạp thời gian trung bình (average-case) để tìm kiếm một phần tử trong Bảng băm (Hash Table) sử dụng kỹ thuật chaining là bao nhiêu?",
                    options = listOf("O(1)", "O(log N)", "O(N)", "O(N log N)"),
                    correctOptionIndex = 0,
                    explanation = "Với hàm băm đồng đều (Uniform Hashing), bảng băm đạt độ phức tạp tìm kiếm trung bình là O(1)."
                ),
                QuizQuestion(
                    id = "q2",
                    questionText = "Cho đoạn mã tìm kiếm nhị phân sau. Điều kiện dừng hoặc cập nhật nào bị thiếu để tránh vòng lặp vô hạn?",
                    codeSnippet = "fun binarySearch(arr: IntArray, target: Int): Int {\n    var left = 0\n    var right = arr.size - 1\n    while (left <= right) {\n        val mid = (left + right) / 2\n        if (arr[mid] == target) return mid\n        else if (arr[mid] < target) left = mid + 1\n        else right = mid - 1\n    }\n    return -1\n}",
                    options = listOf(
                        "Code đã chính xác và tối ưu",
                        "Phải dùng left < right thay vì left <= right",
                        "Nên dùng mid = left + (right - left) / 2 để tránh tràn số nguyên",
                        "Phải dùng arr[mid + 1] trong điều kiện else"
                    ),
                    correctOptionIndex = 2,
                    explanation = "Biểu thức (left + right) / 2 có thể gây ra Integer Overflow khi tổng vượt quá Integer.MAX_VALUE (2^31 - 1). Kỹ thuật chuẩn là: left + (right - left) / 2."
                ),
                QuizQuestion(
                    id = "q3",
                    questionText = "Cây Cân Bằng (AVL Tree) duy trì hiệu độ cao giữa 2 cây con (Balance Factor) trong khoảng nào?",
                    options = listOf("-2 đến +2", "-1, 0, hoặc +1", "Chỉ duy nhất 0", "Từ 0 đến 2"),
                    correctOptionIndex = 1,
                    explanation = "Mỗi node trong cây AVL có Balance Factor = Height(Left) - Height(Right), luôn được cân bằng trong tập {-1, 0, 1}."
                ),
                QuizQuestion(
                    id = "q4",
                    questionText = "Thuật toán sắp xếp nào sau đây có tính chất Ổn định (Stable Sorting) và độ phức tạp O(N log N) trong mọi trường hợp?",
                    options = listOf("Quick Sort", "Heap Sort", "Merge Sort", "Selection Sort"),
                    correctOptionIndex = 2,
                    explanation = "Merge Sort bảo toàn thứ tự ban đầu của các phần tử có giá trị bằng nhau (Stable) và luôn chạy trong O(N log N)."
                )
            )
        ),
        MockExam(
            examId = "exam_os_01",
            title = "Kỳ thi mô phỏng: Hệ Điều Hành & Hệ Thống Máy Tính",
            subject = "Operating Systems",
            durationMinutes = 12,
            questions = listOf(
                QuizQuestion(
                    id = "os_q1",
                    questionText = "Hiện tượng 'Thrashing' trong bộ nhớ ảo (Virtual Memory) xảy ra khi nào?",
                    options = listOf(
                        "CPU sử dụng 100% cho tính toán ma trận",
                        "Hệ thống tốn phần lớn thời gian tráo đổi trang (Page Swapping) thay vì thực thi tiến trình",
                        "Đĩa cứng bị phân mảnh nghiêm trọng",
                        "Bộ nhớ đệm L1 Cache bị đầy"
                    ),
                    correctOptionIndex = 1,
                    explanation = "Thrashing là trạng thái hệ thống liên tục xử lý Page Faults và Swap in/out giữa RAM và Disk, khiến thông lượng CPU tụt dốc."
                ),
                QuizQuestion(
                    id = "os_q2",
                    questionText = "Điều kiện nào sau đây KHÔNG PHẢI là 1 trong 4 điều kiện Coffman gây ra hiện tượng Deadlock?",
                    options = listOf(
                        "Mutual Exclusion (Độc quyền sử dụng)",
                        "Hold and Wait (Giữ và Chờ)",
                        "Preemptive Scheduling (Thu hồi cưỡng bức tài nguyên)",
                        "Circular Wait (Chờ đợi vòng tròn)"
                    ),
                    correctOptionIndex = 2,
                    explanation = "Điều kiện Coffman là 'No Preemption' (không thể tước đoạt tài nguyên). Nếu có Preemption thì deadlock sẽ không xảy ra."
                ),
                QuizQuestion(
                    id = "os_q3",
                    questionText = "Thuật toán lập lịch nào có thể dẫn đến hiện tượng 'Starvation' (đói tài nguyên) đối với các tiến trình dài?",
                    options = listOf("Round Robin (RR)", "First-Come First-Served (FCFS)", "Shortest Job First (SJF)", "Two-Phase Locking"),
                    correctOptionIndex = 2,
                    explanation = "SJF luôn ưu tiên tiến trình ngắn, do đó nếu liên tục có tiến trình ngắn đến, tiến trình dài sẽ bị đói tài nguyên vô thời hạn."
                )
            )
        ),
        MockExam(
            examId = "exam_web_mobile_01",
            title = "Kỳ thi mô phỏng: Lập trình Web & Di Động",
            subject = "Web & Mobile Development",
            durationMinutes = 15,
            questions = listOf(
                QuizQuestion(
                    id = "wm_q1",
                    questionText = "Trong Jetpack Compose của Android, hàm 'remember' có tác dụng gì?",
                    options = listOf(
                        "Lưu dữ liệu vào SharedPreferences vĩnh viễn",
                        "Giữ giá trị của biến qua các lần Recomposition trong cùng Composition lifecycle",
                        "Đồng bộ state lên Firebase Firestore tự động",
                        "Tạo luồng Background Coroutine"
                    ),
                    correctOptionIndex = 1,
                    explanation = "'remember' giúp lưu trữ đối tượng trong bộ nhớ trong suốt các lần Recomposition của composable node."
                ),
                QuizQuestion(
                    id = "wm_q2",
                    questionText = "Phương thức HTTP nào sau đây là Idempotent (thực hiện nhiều lần cho kết quả như nhau mà không gây hiệu ứng phụ bất thường)?",
                    options = listOf("POST", "PUT", "PATCH (non-idempotent)", "CONNECT"),
                    correctOptionIndex = 1,
                    explanation = "PUT, GET, DELETE là idempotent. Gửi request PUT cùng một payload nhiều lần sẽ để lại trạng thái tài nguyên giống hệt nhau trên server."
                ),
                QuizQuestion(
                    id = "wm_q3",
                    questionText = "Trong kiến trúc RESTful API và JWT (JSON Web Token), chữ ký (Signature) của token được dùng để đảm bảo điều gì?",
                    options = listOf(
                        "Mã hóa toàn bộ payload để người khác không đọc được",
                        "Xác thực tính toàn vẹn (Integrity) rằng token chưa bị chỉnh sửa giả mạo",
                        "Tự động giải nén dữ liệu dung lượng lớn",
                        "Lưu trữ token vào Session cookie"
                    ),
                    correctOptionIndex = 1,
                    explanation = "JWT Signature được ký bởi Secret key hoặc Private key của server để verify xem payload có bị tampering hay không. Payload chỉ encode base64 chứ không mã hóa bí mật."
                )
            )
        )
    )

    val defaultMaterials = listOf(
        StudyMaterial(
            materialId = "mat_dsa_cheatsheet",
            title = "Cheat Sheet: Độ phức tạp Thuật toán & CTDL Cơ bản",
            subject = "Data Structures",
            category = "CheatSheet",
            summary = "Bảng tra cứu Big-O chi tiết cho Array, LinkedList, Stack, Queue, Hash Table, Binary Search Tree.",
            content = """
            # Cheat Sheet Độ Phức Tạp Thuật Toán (Big-O)
            
            | Cấu trúc dữ liệu | Truy cập | Tìm kiếm | Chèn | Xóa |
            |---|---|---|---|---|
            | Array | O(1) | O(N) | O(N) | O(N) |
            | Stack / Queue | O(N) | O(N) | O(1) | O(1) |
            | Hash Table | N/A | O(1) | O(1) | O(1) |
            | BST (Cân bằng) | O(log N) | O(log N) | O(log N) | O(log N) |
            
            ## Kỹ thuật Tối ưu Thường gặp trong kỳ thi:
            - **Two Pointers**: Dùng cho mảng đã sắp xếp, tìm cặp số, loại bỏ trùng lặp.
            - **Sliding Window**: Tìm chuỗi con liên tiếp thỏa mãn điều kiện độ dài K hoặc tổng sum.
            - **Prefix Sum**: Truy vấn tổng đoạn [L, R] trong O(1).
            """.trimIndent()
        ),
        StudyMaterial(
            materialId = "mat_os_summary",
            title = "Tóm tắt: Kiến trúc Bộ nhớ Ảo & Phân trang trong Hệ điều hành",
            subject = "Operating Systems",
            category = "Theory",
            summary = "Khái niệm Paging, Segmentation, TLB (Translation Lookaside Buffer) và thuật toán thay thế trang LRU.",
            content = """
            # Hệ Điều Hành: Bộ Nhớ Ảo & Phân Trang
            
            1. **Virtual Address Space**: Mỗi tiến trình sở hữu không gian địa chỉ ảo độc lập từ 0 đến 2^N - 1.
            2. **Page Table**: Bảng chuyển đổi từ Virtual Page Number (VPN) sang Physical Frame Number (PFN).
            3. **TLB (Translation Lookaside Buffer)**: Bộ nhớ cache phần cứng tốc độ cao lưu các mục Page Table gần nhất.
            4. **Thuật toán thay thế trang (Page Replacement)**:
               - **FIFO**: Thay trang vào sớm nhất (có hiện tượng Belady's Anomaly).
               - **LRU (Least Recently Used)**: Thay trang lâu nhất chưa được truy cập (tối ưu thực tế).
               - **Optimal (OPT)**: Thay trang mà trong tương lai xa nhất mới cần dùng (chuẩn lý thuyết).
            """.trimIndent()
        ),
        StudyMaterial(
            materialId = "mat_web_api",
            title = "Hướng dẫn: Thiết kế RESTful API & Cơ chế Xác thực JWT",
            subject = "Web & Mobile",
            category = "Guide",
            summary = "Quy chuẩn đặt tên endpoint, HTTP Status codes, cấu trúc header Authorization: Bearer và refresh token.",
            content = """
            # Thiết kế RESTful API Chuẩn
            
            ## 1. HTTP Status Codes:
            - `200 OK`: Thành công
            - `201 Created`: Tạo tài nguyên mới thành công
            - `400 Bad Request`: Sai định dạng dữ liệu đầu vào
            - `401 Unauthorized`: Chưa xác thực token
            - `403 Forbidden`: Không có quyền truy cập
            - `404 Not Found`: Không tìm thấy tài nguyên
            - `500 Internal Server Error`: Lỗi máy chủ
            
            ## 2. Cấu trúc JWT (JSON Web Token):
            `Header.Payload.Signature`
            - Luôn gửi kèm token trong HTTP Header: `Authorization: Bearer <token>`
            """.trimIndent()
        )
    )
}
