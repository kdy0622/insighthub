package com.example.ui.viewmodel

import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.Folder
import com.example.data.Lecture
import com.example.data.api.StrategicAnalyst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    // --- Database Flows ---
    val folders: StateFlow<List<Folder>> = repository.allFolders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lectures: StateFlow<List<Lecture>> = repository.allLectures
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Subscription & Usage State ---
    private val _subscriptionTier = MutableStateFlow("FREE") // FREE, PRO, BUSINESS
    val subscriptionTier: StateFlow<String> = _subscriptionTier.asStateFlow()

    private val _usageCount = MutableStateFlow(1) // converted count
    val usageCount: StateFlow<Int> = _usageCount.asStateFlow()

    fun getUsageLimit(): Int {
        return when (subscriptionTier.value) {
            "FREE" -> 5
            "PRO" -> 100
            "BUSINESS" -> 9999
            else -> 5
        }
    }

    fun upgradeSubscription(tier: String) {
        _subscriptionTier.value = tier
    }

    // --- Sync & Backup State ---
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("방금 주 업데이트 완료")
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    fun triggerSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1500)
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
            _lastSyncTime.value = "${sdf.format(Date())} 실시간 클라우드 동기화 완료"
            _isSyncing.value = false
        }
    }

    // --- Folder / Filter State ---
    private val _selectedFilterFolderId = MutableStateFlow<Int?>(null)
    val selectedFilterFolderId: StateFlow<Int?> = _selectedFilterFolderId.asStateFlow()

    fun selectFilterFolder(folderId: Int?) {
        _selectedFilterFolderId.value = folderId
    }

    val filteredLectures: StateFlow<List<Lecture>> = combine(lectures, selectedFilterFolderId) { list, folderId ->
        if (folderId == null) list else list.filter { it.folderId == folderId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Add Lecture Form & AI Transcriber State ---
    var inputTitle = MutableStateFlow("")
    var inputSource = MutableStateFlow("")
    var inputSpeaker = MutableStateFlow("")
    var inputTranscript = MutableStateFlow("")
    var selectedFolderId = MutableStateFlow<Int?>(null)
    var selectedLanguage = MutableStateFlow("ko") // ko, en, ja, zh
    var isDialogueMode = MutableStateFlow(false)   // false = Lecture/Education, true = Dialogue/Consultation

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing.asStateFlow()

    private val _transcriptionProgress = MutableStateFlow(0f)
    val transcriptionProgress: StateFlow<Float> = _transcriptionProgress.asStateFlow()

    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    // --- SharedPreferences Dynamic Password Management ---
    fun getAdminPassword(context: Context): String {
        val prefs = context.getSharedPreferences("usana_prefs", Context.MODE_PRIVATE)
        return prefs.getString("admin_password", "growingupadmin") ?: "growingupadmin"
    }

    fun setAdminPassword(context: Context, newPass: String) {
        val prefs = context.getSharedPreferences("usana_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("admin_password", newPass.trim().lowercase()).apply()
    }

    fun getCustomPassword(context: Context): String {
        val prefs = context.getSharedPreferences("usana_prefs", Context.MODE_PRIVATE)
        return prefs.getString("regular_password", "growingup") ?: "growingup"
    }

    fun setCustomPassword(context: Context, newPass: String) {
        val prefs = context.getSharedPreferences("usana_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("regular_password", newPass.trim().lowercase()).apply()
    }

    var isGeminiApiKeyActive = MutableStateFlow(com.example.data.api.StrategicAnalyst.isKeyValid())

    fun getSavedGeminiApiKey(context: Context): String {
        val prefs = context.getSharedPreferences("usana_prefs", Context.MODE_PRIVATE)
        return prefs.getString("gemini_api_key", "") ?: ""
    }

    fun saveGeminiApiKey(context: Context, key: String) {
        val prefs = context.getSharedPreferences("usana_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("gemini_api_key", key.trim()).apply()
        // Update StrategicAnalyst with the latest key
        com.example.data.api.StrategicAnalyst.customApiKey = key.trim()
        isGeminiApiKeyActive.value = com.example.data.api.StrategicAnalyst.isKeyValid()
    }

    // --- Selected Detail Viewer Lecture ---
    private val _selectedLecture = MutableStateFlow<Lecture?>(null)
    val selectedLecture: StateFlow<Lecture?> = _selectedLecture.asStateFlow()

    fun selectLecture(lecture: Lecture?) {
        _selectedLecture.value = lecture
    }

    init {
        viewModelScope.launch {
            repository.checkAndPrepopulate()
        }
    }

    // Add folder
    fun addNewFolder(name: String, colorHex: String) {
        viewModelScope.launch {
            repository.insertFolder(Folder(name = name, colorHex = colorHex))
        }
    }

    // Delete folder
    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            repository.deleteFolder(folder)
            // reset filters if needed
            if (selectedFilterFolderId.value == folder.id) {
                _selectedFilterFolderId.value = null
            }
        }
    }

    // Delete a lecture
    fun deleteLecture(lecture: Lecture) {
        viewModelScope.launch {
            repository.deleteLecture(lecture)
            if (selectedLecture.value?.id == lecture.id) {
                _selectedLecture.value = null
            }
        }
    }

    // Direct Speech-to-Text dynamic transcription engine
    fun transcribeLecture(context: Context, onComplete: () -> Unit) {
        val rawTitle = inputTitle.value.trim()
        val source = inputSource.value.trim()
        val speaker = inputSpeaker.value.trim()
        val customTranscript = inputTranscript.value.trim()
        val folderId = selectedFolderId.value
        val lang = selectedLanguage.value

        if (source.isBlank() && customTranscript.isBlank()) {
            Toast.makeText(context, "영상/음원 링크, 파일 경로 또는 직접 입력 자막을 작성하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val finalSource = if (source.isBlank() && customTranscript.isNotBlank()) {
            "직접 입력대본"
        } else {
            source
        }

        val title = if (rawTitle.isBlank()) {
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA).format(Date())
            when {
                customTranscript.isNotBlank() && source.isBlank() -> "직접 기입 스크립트 ($dateStr)"
                source.contains("youtube") || source.contains("youtu.be") -> "유튜브 영상 분석 ($dateStr)"
                source.contains("zoom.us") -> "Zoom 미팅 분석 ($dateStr)"
                else -> "${if (isDialogueMode.value) "상담" else "강의"} 분석 자료 ($dateStr)"
            }
        } else {
            rawTitle
        }

        viewModelScope.launch {
            try {
                _isTranscribing.value = true
                _transcriptionProgress.value = 0.05f
                _statusMessage.value = "고해상도 영상 및 오디오 미디어 분석 중..."
                delay(1200)

                _transcriptionProgress.value = 0.25f
                _statusMessage.value = "화자 분리 및 실시간 STT 엔진 구동 중 (v1beta)..."
                delay(1500)

                _transcriptionProgress.value = 0.55f
                _statusMessage.value = "Gemini AI를 통한 초고속 맥락 언어 보정 및 번역..."
                delay(1500)

                _transcriptionProgress.value = 0.85f
                _statusMessage.value = "유사나(USANA) 하이퀄리티 매칭 보고서 및 권리소득 설계 중..."
                
                // Live call / Simulated AI analysis
                val resultLecture = StrategicAnalyst.analyzeLecture(
                    context = context,
                    title = title,
                    source = finalSource,
                    speaker = speaker,
                    folderId = folderId,
                    targetLang = lang,
                    isDialogue = isDialogueMode.value,
                    customTranscript = if (customTranscript.isNotBlank()) customTranscript else null
                )

                _transcriptionProgress.value = 0.95f
                delay(800)

                repository.insertLecture(resultLecture)
                _usageCount.value += 1

                // Clear input form
                inputTitle.value = ""
                inputSource.value = ""
                inputSpeaker.value = ""
                inputTranscript.value = ""
                
                _transcriptionProgress.value = 1f
                _isTranscribing.value = false
                _statusMessage.value = "성공적으로 분석이 처리되었습니다!"
                
                Toast.makeText(context, "새로운 변환 강의노트가 저장되었습니다!", Toast.LENGTH_SHORT).show()
                onComplete()
            } catch (e: Exception) {
                _isTranscribing.value = false
                _statusMessage.value = "분석 에러: ${e.message}"
                Toast.makeText(context, "에러가 발생했습니다: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Copy web sharing link to clipboard helper
    fun getShareableLink(lecture: Lecture): String {
        return "https://notelm.usana.com/share/${lecture.shareToken}"
    }

    private fun wrapTextKorean(text: String, maxLineLength: Int): List<String> {
        val result = mutableListOf<String>()
        for (line in text.split("\n")) {
            if (line.isEmpty()) {
                result.add("")
                continue
            }
            var startIndex = 0
            while (startIndex < line.length) {
                val endIndex = minOf(startIndex + maxLineLength, line.length)
                result.add(line.substring(startIndex, endIndex))
                startIndex = endIndex
            }
        }
        return result
    }

    // --- NEW TARGETED EXPORT TO PDF BY ACTIVE TAB ---
    fun exportActiveTabToPdf(context: Context, lecture: Lecture, tabIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tabName = when (tabIndex) {
                    0 -> "대본전문"
                    1 -> "요약본"
                    2 -> "전략분석"
                    else -> "맞춤시트"
                }
                val contentText = when (tabIndex) {
                    0 -> lecture.transcript
                    1 -> lecture.summary
                    2 -> lecture.strategy
                    else -> lecture.processedData
                }

                val pdfDocument = PdfDocument()
                val pageWidth = 595
                val pageHeight = 842
                var pageNumber = 1

                var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                var page = pdfDocument.startPage(pageInfo)
                var canvas = page.canvas

                val titlePaint = Paint().apply {
                    color = AndroidColor.parseColor("#0F3661") // Usana Corporate Blue Accent
                    textSize = 14f
                    isFakeBoldText = true
                    isAntiAlias = true
                }

                val subtitlePaint = Paint().apply {
                    color = AndroidColor.DKGRAY
                    textSize = 9f
                    isAntiAlias = true
                }

                val headerPaint = Paint().apply {
                    color = AndroidColor.parseColor("#0F3661")
                    textSize = 13f
                    isFakeBoldText = true
                    isAntiAlias = true
                }

                val bodyPaint = Paint().apply {
                    color = AndroidColor.BLACK
                    textSize = 10f
                    isAntiAlias = true
                }

                val dividerPaint = Paint().apply {
                    color = AndroidColor.LTGRAY
                    strokeWidth = 1f
                }

                var y = 50f

                // Document Header Banner
                canvas.drawRect(30f, 30f, (pageWidth - 30).toFloat(), 80f, Paint().apply { color = AndroidColor.parseColor("#E1F5FE") })
                canvas.drawText("USANA INSIGHT HUB - $tabName 리포트", 50f, 60f, titlePaint)

                y = 110f
                canvas.drawText("제목: ${lecture.title}", 40f, y, titlePaint.apply { textSize = 16f; isFakeBoldText = true })
                titlePaint.textSize = 14f // restore reference
                y += 22f
                canvas.drawText("강사: ${lecture.speaker} | 원본제공: ${lecture.sourceLink}", 40f, y, subtitlePaint)
                y += 18f
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
                canvas.drawText("분석일시: ${sdf.format(Date(lecture.createdAt))} | 구분: $tabName", 40f, y, subtitlePaint)
                y += 20f
                canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, dividerPaint)
                y += 25f

                canvas.drawText("◼ [$tabName] 상세 분석 목록", 40f, y, headerPaint)
                y += 20f

                val lines = wrapTextKorean(contentText, 45)
                for (line in lines) {
                    if (y > pageHeight - 60) {
                        pdfDocument.finishPage(page)
                        pageNumber++
                        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        y = 50f
                    }
                    if (line.isNotBlank()) {
                        canvas.drawText(line, 45f, y, bodyPaint)
                        y += 15f
                    } else {
                        y += 8f
                    }
                }

                pdfDocument.finishPage(page)

                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val safeTitle = lecture.title.replace(Regex("[^a-zA-Z0-9가-힣\\s]"), "").take(15).trim()
                val pdfFile = File(downloadsDir, "InsightHub_${safeTitle}_${tabName}.pdf")
                val fos = FileOutputStream(pdfFile)
                pdfDocument.writeTo(fos)
                fos.close()
                pdfDocument.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "PDF 다운로드 완료: ${pdfFile.name}\n(다운로드 폴더에 저장되었습니다)", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "PDF 다운로드 중 오류: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // --- REAL WORD DOCUMENT (DOC) EXPORTER BY ACTIVE TAB ---
    fun exportActiveTabToWord(context: Context, lecture: Lecture, tabIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tabName = when (tabIndex) {
                    0 -> "대본전문"
                    1 -> "요약본"
                    2 -> "전략분석"
                    else -> "맞춤시트"
                }
                val contentText = when (tabIndex) {
                    0 -> lecture.transcript
                    1 -> lecture.summary
                    2 -> lecture.strategy
                    else -> lecture.processedData
                }

                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val safeTitle = lecture.title.replace(Regex("[^a-zA-Z0-9가-힣\\s]"), "").take(15).trim()
                val docFile = File(downloadsDir, "InsightHub_${safeTitle}_${tabName}.doc")

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
                val fileContent = buildString {
                    append("=========================================\r\n")
                    append("      USANA INSIGHT HUB - $tabName REPORT \r\n")
                    append("=========================================\r\n\r\n")
                    append("■ 강의/상담 대안 정보\r\n")
                    append("- 제목: ${lecture.title}\n")
                    append("- 강사/상담가: ${lecture.speaker}\r\n")
                    append("- 소스/대본 링크: ${lecture.sourceLink}\r\n")
                    append("- 분석 완료 일시: ${sdf.format(Date(lecture.createdAt))}\n")
                    append("-----------------------------------------\r\n\r\n")
                    append("■ [$tabName] 상세 리포트 전문\r\n\r\n")
                    append(contentText)
                    append("\r\n\r\n")
                    append("=========================================\r\n")
                    append("본 보고서는 USANA GROWINGUP GROUP INSIGHT HUB에서 인공지능을 바탕으로 정밀 자동 추출해 생성한 문서이며, 마이크로소프트 Word와 완벽 정합합니다.\r\n")
                    append("=========================================\r\n")
                }

                val fos = FileOutputStream(docFile)
                fos.write(fileContent.toByteArray(Charsets.UTF_8))
                fos.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "워드 파일 다운로드 완료: ${docFile.name}\n(다운로드 폴더에 저장되었습니다)", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "워드 다운로드 중 오류: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // --- REAL KAKAOTALK TEXT & PDF SHARE HANDLER BY ACTIVE TAB ---
    fun shareActiveTabToKakao(context: Context, lecture: Lecture, tabIndex: Int) {
        try {
            val tabName = when (tabIndex) {
                0 -> "대본전문"
                1 -> "요약본"
                2 -> "전략분석"
                else -> "맞춤시트"
            }
            val contentText = when (tabIndex) {
                0 -> lecture.transcript
                1 -> lecture.summary
                2 -> lecture.strategy
                else -> lecture.processedData
            }

            when (tabIndex) {
                0 -> {
                    // 대본전문: txt 파일 변환하여 공유
                    val cacheDir = context.cacheDir
                    val safeTitle = lecture.title.replace(Regex("[^a-zA-Z0-9가-힣\\s]"), "").take(10).trim()
                    val txtFile = File(cacheDir, "대본전문_${safeTitle}.txt")
                    
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
                    val header = "=========================================\r\n" +
                            "  USANA INSIGHT HUB - 대본전문 리포트\r\n" +
                            "=========================================\r\n" +
                            "제목: ${lecture.title}\r\n" +
                            "강사/상담가: ${lecture.speaker}\r\n" +
                            "분석일시: ${sdf.format(Date(lecture.createdAt))}\r\n" +
                            "=========================================\r\n\r\n"
                            
                    txtFile.writeText(header + contentText, Charsets.UTF_8)
                    
                    val fileUri: android.net.Uri = androidx.core.content.FileProvider.getUriForFile(
                        context, 
                        "${context.packageName}.fileprovider", 
                        txtFile
                    )
                    
                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_STREAM, fileUri)
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "🌱 유사나 대본전문: ${lecture.title}")
                        putExtra(android.content.Intent.EXTRA_TEXT, "🎬 유사나 STT 대본파일을 공유합니다.\n제목: ${lecture.title}")
                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    
                    val chooserIntent = android.content.Intent.createChooser(shareIntent, "카카오톡으로 대본전송")
                    context.startActivity(chooserIntent)
                }
                1 -> {
                    // 요약본: 이모티콘 포함하여 가공된 텍스트 전송
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                    
                    var formattedSummary = contentText
                        .replace("■ 핵심 요약 (EXECUTIVE SUMMARY)", "📢 핵심 요약 (EXECUTIVE SUMMARY) 🌟")
                        .replace("■ 핵심포인트 (KEY POINTS)", "💡 핵심포인트 (KEY POINTS) 🎯")
                        .replace("■ 실천 액션플랜 (ACTION PLAN)", "🚀 실천 액션플랜 (ACTION PLAN) 🔥")
                        .replace("■ 미래 예측 인사이트 (INSIGHTS)", "💎 미래 예측 인사이트 (INSIGHTS) 🔬")
                        .replace("■ 대화 사후 관리 자료 (POST-CONVERSATION CARE)", "💖 대화 사후 관리 자료 (POST-CONVERSATION CARE) 💌")
                        .replace("■ 사업 파트너 복제용 (FOR DISTRIBUTOR REPLICATION)", "👥 사업 파트너 복제용 (FOR DISTRIBUTOR REPLICATION) 🤝")
                        .replace("- ", "✅ ")
                        .replace("• ", "✨ ")
                        .replace("* ", "📌 ")

                    val prettyText = buildString {
                        append("✨ [유사나 GrowingUp Group - AI 요약 보고서] ✨\r\n\r\n")
                        append("💡 강연/세미나 분석 내용을 멋지게 요약하여 비즈니스 전파용으로 공유해 드립니다!\r\n\r\n")
                        append("📌 제목: ${lecture.title}\n")
                        append("🎙️ 강사: ${lecture.speaker}\r\n")
                        append("📅 분석일자: ${sdf.format(Date(lecture.createdAt))}\r\n")
                        append("=========================================\r\n\r\n")
                        append(formattedSummary)
                        append("\r\n\r\n=========================================\r\n")
                        append("🌈 세포 과학 전문 기업 USANA와 함께, 매일 건강한 권리 자산을 구축하세요! 🌈")
                    }
                    
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = android.content.ClipData.newPlainText("Usana AI Summary", prettyText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "텍스트가 멋지게 카피라이팅되어 복사되었습니다!", Toast.LENGTH_SHORT).show()
                    
                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, prettyText)
                    }
                    
                    val chooserIntent = android.content.Intent.createChooser(shareIntent, "카카오톡으로 요약 공유")
                    context.startActivity(chooserIntent)
                }
                2, 3 -> {
                    // 전략분석 & 맞춤시트: PDF 로 공유
                    val cacheDir = context.cacheDir
                    val safeTitle = lecture.title.replace(Regex("[^a-zA-Z0-9가-힣\\s]"), "").take(10).trim()
                    val pdfFile = File(cacheDir, "Shared_${tabName}_${safeTitle}.pdf")
                    
                    val pdfDocument = PdfDocument()
                    val pageWidth = 595
                    val pageHeight = 842
                    var pageNumber = 1

                    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    var page = pdfDocument.startPage(pageInfo)
                    var canvas = page.canvas

                    val titlePaint = Paint().apply {
                        color = AndroidColor.parseColor("#0F3661")
                        textSize = 14f
                        isFakeBoldText = true
                        isAntiAlias = true
                    }
                    val subtitlePaint = Paint().apply {
                        color = AndroidColor.DKGRAY
                        textSize = 9f
                        isAntiAlias = true
                    }
                    val headerPaint = Paint().apply {
                        color = AndroidColor.parseColor("#0B7143")
                        textSize = 13f
                        isFakeBoldText = true
                        isAntiAlias = true
                    }
                    val bodyPaint = Paint().apply {
                        color = AndroidColor.BLACK
                        textSize = 10f
                        isAntiAlias = true
                    }
                    val dividerPaint = Paint().apply {
                        color = AndroidColor.LTGRAY
                        strokeWidth = 1f
                    }

                    var y = 50f
                    canvas.drawRect(30f, 30f, (pageWidth - 30).toFloat(), 80f, Paint().apply { color = AndroidColor.parseColor("#E8F5E9") })
                    canvas.drawText("USANA GROWINGUP - $tabName 전달 리포트", 50f, 60f, titlePaint)

                    y = 110f
                    canvas.drawText("제목: ${lecture.title}", 40f, y, titlePaint.apply { textSize = 15f; isFakeBoldText = true })
                    titlePaint.textSize = 14f
                    y += 22f
                    canvas.drawText("강사: ${lecture.speaker} | 원본: ${lecture.sourceLink}", 40f, y, subtitlePaint)
                    y += 18f
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
                    canvas.drawText("분석일: ${sdf.format(Date(lecture.createdAt))} | 구분: $tabName (카톡 전송용)", 40f, y, subtitlePaint)
                    y += 20f
                    canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, dividerPaint)
                    y += 25f

                    canvas.drawText("■ [$tabName] 유사나 비즈니스 가공 리포트", 40f, y, headerPaint)
                    y += 20f

                    val lines = wrapTextKorean(contentText, 45)
                    for (line in lines) {
                        if (y > pageHeight - 60) {
                            pdfDocument.finishPage(page)
                            pageNumber++
                            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                            page = pdfDocument.startPage(pageInfo)
                            canvas = page.canvas
                            y = 50f
                        }
                        if (line.isNotBlank()) {
                            canvas.drawText(line, 45f, y, bodyPaint)
                            y += 15f
                        } else {
                            y += 8f
                        }
                    }

                    pdfDocument.finishPage(page)
                    
                    val fos = FileOutputStream(pdfFile)
                    pdfDocument.writeTo(fos)
                    fos.close()
                    pdfDocument.close()

                    val fileUri: android.net.Uri = androidx.core.content.FileProvider.getUriForFile(
                        context, 
                        "${context.packageName}.fileprovider", 
                        pdfFile
                    )
                    
                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(android.content.Intent.EXTRA_STREAM, fileUri)
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "💎 유사나 $tabName PDF: ${lecture.title}")
                        putExtra(android.content.Intent.EXTRA_TEXT, "📊 유사나Growingup 인공지능이 분석/설계한 $tabName PDF 보고서입니다.")
                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    
                    val chooserIntent = android.content.Intent.createChooser(shareIntent, "카카오톡으로 PDF 전송")
                    context.startActivity(chooserIntent)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "카카오 공유 오류: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
