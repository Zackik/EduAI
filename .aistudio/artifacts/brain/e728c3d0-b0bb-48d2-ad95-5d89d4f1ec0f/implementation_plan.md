# EduAI CS Prep - Hệ Thống AI Quản Lý Học Tập & Ôn Thi Đại Học CNTT

Hệ thống quản lý học tập thông minh và luyện thi chuyên ngành Khoa học Máy tính & Công nghệ Thông tin (CS/IT), tích hợp gia sư AI đa phương thức (thoại thời gian thực và văn bản), bộ mô phỏng kỳ thi thực tế, bảng thi đua học tập, đồng bộ đám mây và hỗ trợ học ngoại tuyến.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> Dựa trên phản hồi trong bước làm rõ yêu cầu, kế hoạch đã được thiết lập theo các định hướng chính sau:

- **Chuyên ngành đào tạo trọng tâm**: Khoa học Máy tính & Công nghệ Thông tin Đại học (bao gồm Cấu trúc Dữ liệu & Giải thuật, Hệ điều hành, Mạng máy tính, Cơ sở Dữ liệu, Lập trình Web & Di động).
- **Trải nghiệm Gia sư AI**: Đa phương thức toàn diện — kết hợp hội thoại giọng nói thời gian thực (Gemini Live API `gemini-3.8-live` / Native Audio), chuyển đổi âm thanh thành văn bản (`gemini-3.5-transcribe`), và trò chuyện văn bản chuyên sâu với lời giải thuật toán chi tiết.
- **Phong cách giao diện**: Học đường chuyên nghiệp (Academic Indigo & Slate) — tông màu Indigo thanh lịch (`#4338CA`, `#6366F1`), hỗ trợ cả Chế độ Sáng (Light) và Tối (Dark), giao diện chuẩn Material 3 tối ưu cho trải nghiệm tập trung.
- **Chiến lược dữ liệu và hạ tầng**: Kiến trúc Hybrid phân tầng — Firebase Firestore + Google Sign-In (Credential Manager) cho đồng bộ đa thiết bị, bảng xếp hạng và thảo luận nhóm; kết hợp Room Database cục bộ để lưu trữ tài liệu và học tập ngoại tuyến liền mạch.

---

## 1. Overview & Core Concept

### What It Does
EduAI CS Prep là trợ lý học tập cá nhân hóa toàn diện dành cho sinh viên CNTT:
1. **Lập kế hoạch & Lộ trình thông minh**: Tự động đánh giá năng lực ban đầu, xây dựng lộ trình ôn thi theo từng tín chỉ/môn học (CS/IT), phân bổ mục tiêu ôn tập hàng ngày và gửi nhắc nhở định kỳ.
2. **Gia sư ảo AI đa phương thức**: Hỗ trợ trao đổi qua giọng nói thời gian thực (Voice Conversation) hoặc chat văn bản kèm ghi âm micro (Speech-to-Text), giải thích code, gỡ lỗi (debugging) và minh họa sơ đồ thuật toán.
3. **Mô phỏng kỳ thi chuẩn thực tế**: Phòng thi trắc nghiệm & giải thuật bấm giờ chuẩn xác, chấm điểm tức thì, phân tích điểm yếu và gợi ý nội dung cần bồi dưỡng.
4. **Thi đua & Cộng đồng học tập**: Bảng xếp hạng XP theo tuần/tháng, kết nối thi đua toàn cầu, điểm danh chuỗi chuyên cần (Streaks), phòng học nhóm và chia sẻ lịch ôn thi.
5. **Thư viện tài liệu khoa học**: Kho tóm tắt lý thuyết, cheat sheet, thuật toán mẫu và bài giảng có thể tải về học offline.
6. **Báo cáo tiến độ cho Admin & Học viên**: Thống kê trực quan qua biểu đồ sinh động (giờ học, độ chính xác thi thử, tỷ lệ chuyên cần) và xuất báo cáo định kỳ.

### Target Audience & Persona
- **Sinh viên Khoa học Máy tính, Kỹ thuật Phần mềm, CNTT**: Cần công cụ quản lý lịch học nhiều môn chuyên ngành, ôn thi cuối kỳ, luyện thuật toán và chuẩn bị phỏng vấn kỹ thuật.
- **Giảng viên / Nhóm trưởng học tập**: Cần theo dõi tiến độ tổng thể của các thành viên trong nhóm hoặc lớp học.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Khởi động & Xác thực bảo mật**: Đăng nhập bằng Google qua Jetpack Credential Manager. Nhận diện hồ sơ sinh viên, chuyên ngành và mục tiêu kỳ thi.
2. **Dashboard Trung tâm (Học tập & Tiến độ)**:
   - Thẻ tiến độ hôm nay (Target Progress, Chuỗi Streak điểm danh, Nhiệm vụ ưu tiên).
   - Lối tắt tham gia luyện thi nhanh, vào phòng Gia sư AI hoặc mở tài liệu gần nhất.
   - Biểu đồ năng lực theo từng mảng kiến thức (Algorithms, OS, Database, Web/Mobile).
3. **Phòng Gia sư AI Đa phương thức**:
   - Chế độ Voice Live: Trò chuyện giọng nói tương tác trực tiếp với hiệu ứng âm thanh động.
   - Chế độ Chat & Ghi âm: Nhập câu hỏi hoặc nhấn giữ ghi âm micro để chuyển thành văn bản (`gemini-3.5-transcribe`), hiển thị code block rõ ràng kèm nút sao chép và giải thích từng bước.
4. **Phòng Thi Mô Phỏng (Mock Exam Arena)**:
   - Chọn bộ đề theo môn học và độ khó.
   - Giao diện làm bài thi toàn màn hình có đồng hồ đếm ngược, đánh dấu câu hỏi cần xem lại.
   - Màn hình kết quả thi với phân tích chi tiết từng câu và gợi ý ôn tập từ AI.
5. **Nhóm Học tập & Bảng Thi đua**:
   - Bảng xếp hạng tuần/tháng với các danh hiệu học thuật.
   - Thảo luận theo chủ đề, chia sẻ lịch thi và tài liệu học.
6. **Thư viện Tài liệu & Báo cáo Admin**:
   - Duyệt tài liệu theo cây thư mục môn học, hỗ trợ đánh dấu yêu thích và lưu offline.
   - Xem báo cáo tổng kết tháng, chỉ số chuyên cần và nút xuất báo cáo.

### Visual Identity & Theme (Academic Indigo)
- **Palette**:
  - Primary: `Indigo 600` (`#4F46E5`), Primary Container: `Indigo 100` (`#E0E7FF`)
  - Accent / Tertiary: `Cyan 500` (`#06B6D4`) và `Emerald 500` (`#10B981`) cho trạng thái hoàn thành và chuỗi streak
  - Background Light: `Slate 50` (`#F8FAFC`), Surface: `#FFFFFF`
  - Background Dark: `Slate 950` (`#0F172A`), Surface Dark: `Slate 900` (`#1E293B`)
- **Typography**: Phông chữ hiển thị hiện đại, phân cấp rõ ràng (`titleLarge`, `bodyMedium`, `labelSmall`), font monospace cho code blocks.
- **Layout Rhythm**: Sử dụng card bo tròn 16dp, độ cao bóng đổ mềm mại, viền border 1dp tinh tế (`Slate 200` / `Slate 800`).
- **Bilingual Switcher**: Chuyển đổi ngôn ngữ Tiếng Việt 🇻🇳 và English 🇬🇧 ngay tại cài đặt hoặc trên thanh tiêu đề.

---

## 3. Key Product Decisions & Trade-Offs

- **Trải nghiệm AI (REST + Multimodal Audio)**:
  - *Lựa chọn*: Tích hợp Gemini 3.5 Flash / Pro cho Q&A thuật toán và phân tích đề thi; Gemini 3.5 Transcribe cho ghi âm giọng nói; Gemini Audio API cho tương tác âm thanh trực quan.
  - *Lý do*: Đáp ứng trọn vẹn yêu cầu voice chat và audio transcription của người dùng với độ trễ thấp và độ chính xác cao.
- **Hệ thống dữ liệu Hybrid (Firebase Firestore + Room Database)**:
  - *Lựa chọn*: Firebase Firestore quản lý người dùng, đồng bộ đám mây đa thiết bị, bảng xếp hạng và nhóm học tập; Room Database đóng vai trò Local Cache để đảm bảo truy cập tài liệu và ghi nhận tiến độ học tập mượt mà ngay cả khi mất mạng (Offline-First).
  - *Lý do*: Đảm bảo tính sẵn sàng cao, bảo mật theo vai trò tài khoản người dùng, đồng thời mang lại trải nghiệm nhanh tức thì trên thiết bị Android.
- **Thông báo & Nhắc nhở**:
  - *Lựa chọn*: Tích hợp hệ thống nhắc nhở lịch học thông minh trong ứng dụng kết hợp quản lý lịch thi cử theo mốc thời gian thực tế.

---

## 4. Technical Architecture & Component Hierarchy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        EduAI CS Prep Architecture                      │
└────────────────────────────────────────────────────────────────────────┘
                                   │
       ┌───────────────────────────┴───────────────────────────┐
       ▼                                                       ▼
┌──────────────────────────────┐              ┌──────────────────────────────┐
│       UI & Presentation      │              │         Domain & State       │
│  - Theme: Academic Indigo    │              │  - AuthViewModel             │
│  - Navigation Compose        │◄────────────►│  - PlannerViewModel          │
│  - Dashboard & Analytics     │              │  - AiTutorViewModel          │
│  - Mock Exam Simulator       │              │  - ExamViewModel             │
│  - Leaderboard & Groups      │              │  - LibraryViewModel          │
│  - Study Library & Settings  │              │  - AdminReportViewModel      │
└──────────────────────────────┘              └──────────────────────────────┘
               │                                             │
               ▼                                             ▼
┌──────────────────────────────┐              ┌──────────────────────────────┐
│        AI & Voice Engine     │              │     Data & Synchronization   │
│  - Gemini REST Service       │              │  - Firebase Auth & CredMgr   │
│  - Audio Transcribe Service  │              │  - Firestore Repository      │
│  - Multimodal Audio Player   │              │  - Room Local Cache DB       │
│  - Smart Prompt Templates    │              │  - Network State Monitor     │
└──────────────────────────────┘              └──────────────────────────────┘
```

### Component Details
1. **Presentation Layer**:
   - `HomeScreen`: Tổng quan lộ trình, chuỗi chuyên cần, biểu đồ tiến độ tháng.
   - `AiTutorScreen`: Giao diện tương tác kép (Voice Live Waveform + Chat Stream).
   - `ExamScreen`: Bộ điều khiển kỳ thi mô phỏng, bảng câu hỏi, đồng hồ và chấm điểm.
   - `CommunityScreen`: Bảng xếp hạng điểm thi đua, phòng học nhóm, lịch ôn chung.
   - `LibraryScreen`: Quản lý tài liệu theo môn học, đọc offline.
   - `AdminReportScreen`: Thống kê tổng hợp học viên, báo cáo chi tiết theo chuyên ngành.
2. **Data Layer**:
   - `UserRepository`, `StudyPlanRepository`, `ExamRepository`, `LeaderboardRepository`, `LibraryRepository`.
   - `firestore.rules` & `firebase-blueprint.json` với phân quyền bảo mật chặt chẽ.
   - Room Entities: `OfflineStudySession`, `CachedMaterial`, `OfflineQuiz`.

---

## 5. Execution Steps

1. **Thiết lập Cấu hình & Dependencies**:
   - Cập nhật `metadata.json` và `strings.xml` với tên ứng dụng chính thức: **EduAI CS Prep**.
   - Bổ sung các dependencies cần thiết trong `app/build.gradle.kts` (Firebase Auth, Firestore, Credential Manager, Kotlin Serialization, Room KSP, Material Icons Extended).
2. **Bảo mật & Backend Provisioning**:
   - Cấu hình Firebase (`set_up_firebase`) và khởi tạo `firebase-blueprint.json` cùng `firestore.rules`.
   - Triển khai rules và kiểm thử phân quyền người dùng.
3. **Triển khai AI Service & Đa phương thức**:
   - Tích hợp Gemini API Client (Text, Code Explanations, Audio Transcribe với `gemini-3.5-transcribe`, Voice interaction).
4. **Phát triển Data Layer & Repositories**:
   - Xây dựng Room Database và Firestore Repositories với khả năng đồng bộ 2 chiều (Cloud + Offline Cache).
5. **Xây dựng Giao diện & Tính năng theo Chủ đề Academic Indigo**:
   - Triển khai M3 Theme (Chế độ Sáng/Tối, bảng màu Indigo, code typography).
   - Hoàn thiện toàn bộ các màn hình chức năng: Dashboard, Gia sư AI, Thi thử, Xếp hạng, Nhóm học tập, Báo cáo Admin.
6. **Kiểm thử & Biên dịch Xác minh**:
   - Biên dịch ứng dụng với `compile_applet` và đảm bảo 0 lỗi cảnh báo cú pháp.
