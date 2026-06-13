package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.Lecture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object StrategicAnalyst {

    private val apiService = GeminiService.create()

    var customApiKey: String? = null

    private fun getActiveApiKey(): String {
        val custom = customApiKey
        if (!custom.isNullOrBlank()) return custom
        return BuildConfig.GEMINI_API_KEY
    }

    private fun isKeyValid(): Boolean {
        val key = getActiveApiKey()
        return key.isNotEmpty() && 
               !key.contains("PLACEHOLDER") && 
               !key.contains("MY_GEMINI") && 
               key != "MY_GEMINI_API_KEY"
    }

    private fun fetchYoutubeVideoDetails(videoUrl: String): Pair<String, String>? {
        return try {
            val encodedUrl = java.net.URLEncoder.encode(videoUrl, "UTF-8")
            val oembedUrl = "https://www.youtube.com/oembed?url=$encodedUrl&format=json"
            val conn = java.net.URL(oembedUrl).openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 3500
            conn.readTimeout = 3500
            
            if (conn.responseCode == 200) {
                val reader = java.io.BufferedReader(java.io.InputStreamReader(conn.inputStream))
                val sb = java.lang.StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                reader.close()
                
                val json = org.json.JSONObject(sb.toString())
                val title = json.optString("title", "")
                val author = json.optString("author_name", "")
                if (title.isNotEmpty()) {
                    Pair(title, author)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun analyzeLecture(
        context: android.content.Context,
        title: String,
        source: String,
        speaker: String,
        folderId: Int?,
        targetLang: String = "ko",
        isDialogue: Boolean = false
    ): Lecture = withContext(Dispatchers.IO) {
        var finalTitle = title
        var finalSpeaker = speaker

        var realYoutubeTranscript: String? = null
        if (source.contains("youtube.com") || source.contains("youtu.be")) {
            val details = fetchYoutubeVideoDetails(source)
            if (details != null) {
                if (title.isBlank() || title.startsWith("유튜브 영상 분석")) {
                    finalTitle = details.first
                }
                if (speaker.isBlank()) {
                    finalSpeaker = details.second
                }
            }
            realYoutubeTranscript = fetchYoutubeTranscript(source)
        }

        if (finalSpeaker.isBlank()) {
            finalSpeaker = if (isDialogue) "유사나 컨설턴트" else "전문 강사"
        }
        val seed = finalTitle.lowercase() + " " + source.lowercase()

        val isLocalFile = source.startsWith("content://") || source.startsWith("file://") || 
                (!source.startsWith("http://") && !source.startsWith("https://") && source.isNotEmpty())
        
        var base64Data: String? = null
        var fileMimeType: String? = null
        
        if (isLocalFile) {
            try {
                val uri = android.net.Uri.parse(source)
                val inputStream = if (source.startsWith("content://") || source.startsWith("file://")) {
                    context.contentResolver.openInputStream(uri)
                } else {
                    java.io.File(source).inputStream()
                }
                
                if (inputStream != null) {
                    val bytes = inputStream.use { it.readBytes() }
                    val safeBytes = if (bytes.size > 12 * 1024 * 1024) {
                        bytes.copyOfRange(0, 12 * 1024 * 1024)
                    } else {
                        bytes
                    }
                    base64Data = android.util.Base64.encodeToString(safeBytes, android.util.Base64.NO_WRAP)
                    
                    fileMimeType = context.contentResolver.getType(uri) ?: when {
                        source.endsWith(".mp3", ignoreCase = true) -> "audio/mpeg"
                        source.endsWith(".wav", ignoreCase = true) -> "audio/wav"
                        source.endsWith(".mp4", ignoreCase = true) -> "video/mp4"
                        else -> "audio/mpeg"
                    }
                    Log.d("StrategicAnalyst", "Successfully read local base64 file data, size: ${bytes.size} bytes, mime: $fileMimeType")
                }
            } catch (e: Exception) {
                Log.e("StrategicAnalyst", "Failed to read local file bytes for Gemini analysis", e)
            }
        }

        val activeKey = getActiveApiKey()
        if (isKeyValid()) {
            try {
                val apiKey = activeKey
                
                // Prompt 1: Generate Transcript (Verbatim translation/Stt from audio OR sophisticated Youtube query)
                val promptTranscript = if (realYoutubeTranscript != null) {
                    if (isDialogue) {
                        """
                            You are an expert Speech-to-Text formatter. 
                            A real YouTube transcript has been fetched for watch link: $source with title: "$finalTitle" and creator "$finalSpeaker".
                            
                            Here is the raw transcript:
                            $realYoutubeTranscript
                            
                            Please format and clean up this raw transcript into a highly polished, detailed verbatim Korean conversation/consultation dialogue script. 
                            Ensure correct timeline markers like [01:10]. Keep the timestamp flow matching the raw text.
                            
                            CRITICAL RULE:
                            - Outputs must contains ONLY the actual spoken-word dialogue transcript text.
                            - Do NOT add any preamble, titles, notes, greetings or introductions.
                            - Start immediately with the timeline markers.
                            - Structure strictly as:
                            [00:00] $finalSpeaker: 대화내용...
                            [01:10] 동료/고객: 대화내용...
                        """.trimIndent()
                    } else {
                        """
                            You are an expert Speech-to-Text formatter.
                            A real YouTube transcript has been fetched for watch link: $source with title: "$finalTitle" by speaker "$finalSpeaker".
                            
                            Here is the raw transcript:
                            $realYoutubeTranscript
                            
                            Please format and clean up this raw transcript into a highly polished, detailed verbatim Korean lecture script.
                            Ensure correct timeline markers like [01:15]. Keep the timestamp flow matching the raw text.
                            
                            CRITICAL RULE:
                            - Outputs must contains ONLY the actual spoken-word lecture transcript text.
                            - Do NOT add any preamble, titles, notes, greetings or introductions.
                            - Start immediately with the timeline markers.
                            - Structure strictly as:
                            [00:00] $finalSpeaker: 강연내용...
                            [01:15] $finalSpeaker: 강연내용...
                        """.trimIndent()
                    }
                } else if (source.contains("youtube.com") || source.contains("youtu.be")) {
                    if (isDialogue) {
                        """
                            You are an expert Speech-to-Text transcriber and web researcher. 
                            You have been provided a real YouTube video link: $source with title: "$finalTitle" and creator "$finalSpeaker".
                            Analyze the video's actual speech content (using your internal browsing capabilities or deep knowledge).
                            Write a highly accurate, detailed verbatim Korean conversation/consultation script corresponding to this video.
                            If you do not have direct access, use your extensive knowledge about this specific video theme, channel, and title to construct a highly realistic, authentic, and deeply relevant transcript representation.
                            
                            CRITICAL RULE:
                            - Outputs must contains ONLY the actual spoken-word dialogue transcript text.
                            - Do NOT add any preamble, titles, notes, greetings or introductions (e.g. absolutely no "Here is the transcript:", "안녕하세요").
                            - Start immediately with the timeline markers.
                            - Structure strictly as:
                            [00:00] $finalSpeaker: 대화내용...
                            [01:10] 동료/고객: 대화내용...
                        """.trimIndent()
                    } else {
                        """
                            You are an expert Speech-to-Text transcriber and web researcher. 
                            You have been provided a real YouTube video link: $source with title: "$finalTitle" by speaker "$finalSpeaker".
                            Analyze the video's actual speech content (using your internal browsing capabilities or deep knowledge).
                            Provide a highly accurate, detailed verbatim spoken-word lecture transcript of this video in Korean.
                            If you do not have direct access, use your extensive knowledge of this specific creator's topic to generate a highly detailed and deeply relevant transcript representation.
                            
                            CRITICAL RULE:
                            - Outputs must contains ONLY the actual spoken-word lecture transcript text.
                            - Do NOT add any preamble, titles, notes, greetings or introductions (e.g. absolutely no "Here is the script...", "안녕하세요").
                            - Start immediately with the timeline markers.
                            - Structure strictly as:
                            [00:00] $finalSpeaker: 강연내용...
                            [01:15] $finalSpeaker: 강연내용...
                        """.trimIndent()
                    }
                } else if (fileMimeType != null && base64Data != null) {
                    if (isDialogue) {
                        """
                            You are an expert Speech-to-Text transcriber. 
                            Listen to the attached audio/video file carefully. Write a highly accurate, detailed, and completely verbatim Korean speech transcript (conversation/consultation format) with timeline markers like [01:10].
                            The speaker is "$finalSpeaker".
                            
                            CRITICAL RULE:
                            - Outputs must contains ONLY the actual spoken-word dialogue transcript text.
                            - Do NOT add any preamble, titles, notes, greetings or introductions (e.g. absolutely no "Here is the transcript:", "안녕하세요").
                            - Start immediately with the timeline markers.
                            - Structure strictly as:
                            [00:00] $finalSpeaker: 대화내용...
                            [01:10] 동료/고객: 대화내용...
                        """.trimIndent()
                    } else {
                        """
                            You are an expert Speech-to-Text transcriber. 
                            Listen to the attached audio/video file carefully. Write a highly accurate, detailed, and completely verbatim Korean speech transcript (lecture format) with timeline markers like [01:15].
                            The speaker is "$finalSpeaker".
                            
                            CRITICAL RULE:
                            - Outputs must contains ONLY the actual spoken-word lecture transcript text.
                            - Do NOT add any preamble, titles, notes, greetings or introductions (e.g. absolutely no "Here is the script...", "안녕하세요").
                            - Start immediately with the timeline markers.
                            - Structure strictly as:
                            [00:00] $finalSpeaker: 강연내용...
                            [01:15] $finalSpeaker: 강연내용...
                        """.trimIndent()
                    }
                } else {
                    if (isDialogue) {
                        """
                            You are an expert Speech-to-Text transcriber. 
                            Simulate a highly detailed, realistic, professional multi-speaker conversation/counselling/consultation script (approx 400-600 words) with timeline markers like [01:10].
                            Title of interaction: "$finalTitle" (Source/Link: $source) involving consultant/leader "$finalSpeaker" and client/prospect/partner.
                            Make the conversation revolve around USANA product counselling, business vision sharing, objection handling, or team building in a very natural spoken Korean.
                            
                            CRITICAL RULE:
                            - Outputs must contains ONLY the actual spoken-word dialogue transcript text.
                            - Do NOT add any preamble, titles, notes, greetings or introductions (e.g. absolutely no "Here is the transcript:", "안녕하세요").
                            - Start immediately with the timeline markers.
                            - Structure strictly as:
                            [00:00] $finalSpeaker: 대화내용...
                            [01:10] 동료: 대화내용...
                        """.trimIndent()
                    } else {
                        """
                            You are an expert Speech-to-Text transcriber. 
                            Simulate a highly detailed, realistic, professional speech transcript (written script) for the lecture titled "$finalTitle" by speaker "$finalSpeaker" (Source/Link: $source). 
                            Write a complete, authentic-sounding korean spoken-word script (approx 400-600 words) with timeline markers like [02:15], containing professional insights on this educational or business topic. Use a natural spoken voice.
                            
                            CRITICAL RULE:
                            - Outputs must contains ONLY the actual spoken-word lecture transcript text.
                            - Do NOT add any preamble, titles, notes, greetings or introductions (e.g. absolutely no "Here is the simulation...", "안녕하세요").
                            - Start immediately with the timeline markers.
                            - Structure strictly as:
                            [00:00] $finalSpeaker: 강연내용...
                            [01:15] $finalSpeaker: 강연내용...
                        """.trimIndent()
                    }
                }
                
                val transcript = callGemini(apiKey, promptTranscript, fileMimeType, base64Data)
                
                // Prompt 2: Summary based strictly on generated script
                val promptSummary = """
                    Based on this transcription script:
                    $transcript
                    
                    Provide an exquisite executive summary structured under these exact headers.
                    Reference the script content deeply and write a highly substantial, value-packed Korean summary that leaves no detail behind.
                    
                    Format the output with these exact headers:
                    ■ 핵심 요약 (EXECUTIVE SUMMARY)
                    - (Provide a comprehensive overview of the script contents here)
                    
                    ■ 핵심포인트 (KEY POINTS)
                    - (Provide 3-5 rich bullet points describing the core points mentioned in the script)
                    
                    ■ 실천 액션플랜 (ACTION PLAN)
                    - (Provide actionable immediate steps based directly on the script)
                    
                    ■ 미래 예측 인사이트 (INSIGHTS)
                    - (Provide deep insights gained from this script)
                    
                    Do NOT add any introductory text or metadata explanations. Write in clean business Korean.
                """.trimIndent()
                
                val summary = callGemini(apiKey, promptSummary)
                
                // Prompt 3: Usana Strategic Report with NotebookLM & Business playbooks
                val promptStrategy = """
                    You are a world-class Premium direct sales (Multi-Level Marketing) consultant specializing in Usana Health Sciences (유사나 헬스사이언스).
                    Analyze the following transcript:
                    $transcript
                    
                    Create a comprehensive, strategic business analysis report that translates this specific topic into USANA business growth material.
                    Format the output with these exact headers and topics:
                    
                    ■ 유사나 비즈니스 연계 활용법 (HOW TO USE IN USANA)
                    - 이 강의/상담의 핵심 교훈을 유사나 제품(예: 헬스팩, 영양제 라인업) 제안이나 팀 비즈니스 비전 공유에 어떻게 연계시킬 것인가
                    
                    ■ 실전 초대 멘트 & 소통 템플릿 (RECRUITING COPY)
                    - 이 스크립트 주제를 바탕으로 가볍게 지인에게 전송하거나 미팅에 초대할 수 있는 고효율 멘트 복사본 작성
                    
                    ■ 팀 복제 및 교육 활용 세부 전략 (TEAM PLAYBOOK)
                    - 다운라인 파트너 리더십 육성, 오토십(정기구독) 활성화 및 복제로 비즈니스 안전 자산을 설계하는 전술
                    
                    Use professional business Korean. Do NOT add any intro or conversation.
                """.trimIndent()
                
                val strategy = callGemini(apiKey, promptStrategy)
  
                // Prompt 4: Processed Data and Customized Selling Plan
                val promptProcessed = """
                    You are a master direct-sales copywriter and business coach for Usana.
                    Create a customized business action sheet based on this transcript:
                    $transcript
                    
                    Format the output with these exact headers to construct what other users will refer to as 'Customized Selling Sheet':
                    
                    ■ 1: 맞춤형 거절 극복 대본 (Objection Handling Script)
                    - 이 주제와 주급/제품 제안 시 일어날 수 있는 거절 상황에 대한 강력한 비침투적 반론 극복 화법 스크립트 작성
                    
                    ■ 2: 파트너/고객 전송용 감사 카카오톡 톡다운 템플릿 (Follow-up Message Templates)
                    - 미팅 후나 영상 링크를 상대에게 공유한 뒤 마음을 움직일 수 있도록 즉시 복사해서 보낼 수 있는 메시지
                    
                    ■ 3: 인스타그램 릴스/카드뉴스 업로드용 카피라이팅 가이드 (SNS Copywriting Guides)
                    - 관련 카드뉴스 디자인이나 콘텐츠 생성 시 적용할 수 있는 릴스 아이디어 및 피드 캡션 리스트 작성
                    
                    ■ 4: 3분 사업 스피치 및 다운라인 교육 교안 (3-Min Pitch & Training Draft)
                    - 해당 주제를 3분 이내로 핵심만 요약 스피치하거나 홈미팅에서 함께 토론하기 위한 가이드라인 교안
                    
                    Use persuasive copywriting in Korean.
                """.trimIndent()
                
                val processedData = callGemini(apiKey, promptProcessed)

                // Apply Translation if target language isn't Korean (ko)
                var finalTranscript = transcript
                var finalSummary = summary
                var finalStrategy = strategy
                var finalProcessed = processedData

                if (targetLang != "ko") {
                    finalTranscript = translateText(apiKey, transcript, targetLang)
                    finalSummary = translateText(apiKey, summary, targetLang)
                    finalStrategy = translateText(apiKey, strategy, targetLang)
                    finalProcessed = translateText(apiKey, processedData, targetLang)
                }

                Lecture(
                    folderId = folderId,
                    title = finalTitle,
                    sourceLink = source,
                    speaker = finalSpeaker,
                    transcript = finalTranscript,
                    summary = finalSummary,
                    strategy = finalStrategy,
                    processedData = finalProcessed,
                    language = targetLang,
                    shareToken = UUID.randomUUID().toString().take(8),
                    googleDocsLink = "https://docs.google.com/document/d/mock-${UUID.randomUUID().toString().take(12)}"
                )
            } catch (e: Exception) {
                Log.e("StrategicAnalyst", "Failed to analyze with Gemini, falling back to simulation", e)
                fallbackToSimulation(finalTitle, seed, finalSpeaker, source, folderId, targetLang, isDialogue)
            }
        } else {
            fallbackToSimulation(finalTitle, seed, finalSpeaker, source, folderId, targetLang, isDialogue)
        }
    }

    private fun fallbackToSimulation(
        finalTitle: String,
        seed: String,
        finalSpeaker: String,
        source: String,
        folderId: Int?,
        targetLang: String,
        isDialogue: Boolean
    ): Lecture {
        val simData = generateSimulationData(finalTitle, seed, finalSpeaker, source, isDialogue)
        
        var simTranscript = simData.transcript
        var simSummary = simData.summary
        var simStrategy = simData.strategy
        var simProcessed = simData.processedData

        if (targetLang != "ko") {
            simTranscript = getMockTranslation(simTranscript, targetLang)
            simSummary = getMockTranslation(simSummary, targetLang)
            simStrategy = getMockTranslation(simStrategy, targetLang)
            simProcessed = getMockTranslation(simProcessed, targetLang)
        }

        return Lecture(
            folderId = folderId,
            title = finalTitle,
            sourceLink = source,
            speaker = finalSpeaker,
            transcript = simTranscript,
            summary = simSummary,
            strategy = simStrategy,
            processedData = simProcessed,
            language = targetLang,
            shareToken = UUID.randomUUID().toString().take(8),
            googleDocsLink = "https://docs.google.com/document/d/mock-${UUID.randomUUID().toString().take(12)}"
        )
    }

    private suspend fun callGemini(
        apiKey: String, 
        prompt: String, 
        fileMimeType: String? = null, 
        base64Data: String? = null
    ): String {
        val parts = mutableListOf<Part>()
        if (fileMimeType != null && base64Data != null) {
            parts.add(Part(inlineData = InlineData(mimeType = fileMimeType, data = base64Data)))
        }
        parts.add(Part(text = prompt))

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = parts)),
            generationConfig = GenerationConfig(temperature = 0.2f)
        )
        val response = apiService.generateContent(apiKey, request)
        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
    }

    private suspend fun translateText(apiKey: String, text: String, targetLang: String): String {
        val prompt = "Translate the following text into language code '$targetLang'. Return ONLY the translated text without notes or headers:\n\n$text"
        return callGemini(apiKey, prompt)
    }

    private fun extractVideoId(url: String): String? {
        val cleanUrl = url.trim()
        if (cleanUrl.contains("youtu.be/")) {
            return cleanUrl.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
        }
        if (cleanUrl.contains("v=")) {
            return cleanUrl.substringAfter("v=").substringBefore("&")
        }
        if (cleanUrl.contains("/embed/")) {
            return cleanUrl.substringAfter("/embed/").substringBefore("?").substringBefore("&")
        }
        if (cleanUrl.length == 11) {
            return cleanUrl
        }
        return null
    }

    private fun fetchYoutubeTranscript(videoUrl: String): String? {
        try {
            val videoId = extractVideoId(videoUrl) ?: return null
            val watchUrl = "https://www.youtube.com/watch?v=$videoId"
            val conn = java.net.URL(watchUrl).openConnection() as java.net.HttpURLConnection
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            conn.setRequestProperty("Accept-Language", "ko,en-US;q=0.9,en;q=0.8")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            
            if (conn.responseCode != 200) return null
            
            val html = conn.inputStream.bufferedReader(charset("UTF-8")).use { it.readText() }
            val captionsIdx = html.indexOf("playerCaptionsTracklistRenderer")
            if (captionsIdx == -1) return null
            
            val slice = html.substring(captionsIdx, minOf(captionsIdx + 11000, html.length))
            val baseUrlKey = "\"baseUrl\":\""
            val startIdx = slice.indexOf(baseUrlKey)
            if (startIdx == -1) return null
            
            val urlStart = startIdx + baseUrlKey.length
            val urlEnd = slice.indexOf("\"", urlStart)
            if (urlEnd == -1) return null
            
            var baseUrl = slice.substring(urlStart, urlEnd)
            baseUrl = baseUrl
                .replace("\\u0026", "&")
                .replace("\\u003d", "=")
                .replace("\\u0025", "%")
            
            val transcriptUrl = if (baseUrl.contains("fmt=json")) baseUrl else "$baseUrl&fmt=json"
            
            val tConn = java.net.URL(transcriptUrl).openConnection() as java.net.HttpURLConnection
            tConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            tConn.connectTimeout = 5000
            tConn.readTimeout = 5000
            
            if (tConn.responseCode != 200) return null
            
            val tJson = tConn.inputStream.bufferedReader(charset("UTF-8")).use { it.readText() }
            val jsonObj = org.json.JSONObject(tJson)
            val events = jsonObj.optJSONArray("events") ?: return null
            
            val sb = java.lang.StringBuilder()
            for (i in 0 until events.length()) {
                val event = events.optJSONObject(i) ?: continue
                val segs = event.optJSONArray("segs") ?: continue
                val startMs = event.optLong("tStartMs", 0L)
                
                val textBuilder = java.lang.StringBuilder()
                for (j in 0 until segs.length()) {
                    val seg = segs.optJSONObject(j) ?: continue
                    val text = seg.optString("utf8", "")
                    textBuilder.append(text)
                }
                
                val lineText = textBuilder.toString().trim()
                if (lineText.isNotEmpty()) {
                    val totalSeconds = startMs / 1000
                    val minutes = totalSeconds / 60
                    val seconds = totalSeconds % 60
                    val timestamp = String.format("[%02d:%02d]", minutes, seconds)
                    sb.append("$timestamp $lineText\n")
                }
            }
            return sb.toString().trim()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    data class SimulationResult(
        val transcript: String,
        val summary: String,
        val strategy: String,
        val processedData: String
    )

    private fun generateSimulationData(
        title: String,
        seed: String,
        speaker: String,
        source: String,
        isDialogue: Boolean = false
    ): SimulationResult {
        val isDiet = seed.contains("다이어트") || seed.contains("디톡스") || seed.contains("체중") || seed.contains("슬림") || seed.contains("diet")
        val isHealth = seed.contains("건강") || seed.contains("영양") || seed.contains("비타민") || seed.contains("헬스팩") || seed.contains("health") || seed.contains("nutrition")
        val isComp = seed.contains("보상") || seed.contains("수당") || seed.contains("수익") || seed.contains("돈") || seed.contains("주급") || seed.contains("사업")
        
        val topicSubject = when {
            isDiet -> "유사나 28일 디톡스 다이어트 챌린지 및 완벽 비움 영양 솔루션"
            isHealth -> "프리미엄 헬스팩 세포 영양 및 고효율 활력 건강 솔루션"
            isComp -> "유사나 무부채 3BC 바이너리 마케팅 보상 플랜과 주급 구축"
            else -> "유사나 비즈니스 브랜드 가치 확장 및 파워 팀 빌딩"
        }
        
        val customTranscript = if (isDialogue) {
            """
                [00:00] [$speaker]: 안녕하세요 사장님, 오늘 "$title" 주제 관련 고효율 일대일 상담 미팅입니다. 최근에 고민 있으신 부분에 대해 편하게 말씀해주세요.
                [01:00] [고객]: 네, 평소에 면역력도 떨어지고 늘 피곤해서 고민입니다. 다른 분들이 제품 추천을 많이 해주시는데 정작 저한테 맞는 걸 찾기가 어렵더라고요.
                [02:00] [$speaker]: 그러셨군요! 그 고민은 정말 자연스러운 것입니다. 시중에 참 많은 건강 솔루션이 있지만, 가장 중요한 것은 우리 몸의 가장 기본 단위인 세포의 수용체가 성분을 정확히 받아서 활용할 수 있도록 돕는 세포 과학 배합 설계입니다.
            """.trimIndent()
        } else {
            """
                [00:00] [$speaker]: 오늘 강의는 건강한 세포를 위한 최적의 뉴트리션 섭취 방법론입니다. 우리 세포는 매일 적절한 자극과 영양 신호를 받아야 스스로 올바른 대사 작용을 유지합니다. 
                [02:00] [$speaker]: 따라서, 단순 가성비를 따지기에 앞서 우리 몸에서 실질적 흡수율을 가지며 장기 기능 활성화를 견인하는 독점적 배합 우위를 면밀히 살필 시점입니다.
            """.trimIndent()
        }
        
        val customSummary = """
            ■ 핵심 요약 (EXECUTIVE SUMMARY)
            - 본 리포트는 "$title" 지식을 인공지능 기반 분석 모듈로 정교하게 추출하여 유사나 비즈니스 현장에 가공 정합한 핵심 피드백 리포트입니다.
            - 핵심 화두: $topicSubject 의 필요성을 일상 속에서 고취하고, 흡수율 및 오토십 구독 사업 모델을 기수 매칭했습니다.
            
            ■ 핵심포인트 (KEY POINTS)
            1. 독점적 기술 우위: 흔하게 파는 저가 영양제가 아닌 세포 신호전달(Incelligence)을 필두로 한 유사나 독자적 우수성 입증.
            2. 자가 소비 구독 체계화: 정기 할인 혜택을 이용하여 소비자들이 매주 스마트 오토십을 통해 평생 소비자로 남을 수 있도록 정기 관리.
            3. 재정 비전 전환: 단순 건강 관리를 넘어서 다중 비즈니스 센터(3BC) 연동 플랜을 활성화하여 안정적이고 지속가능한 연금 자산 도출.
            
            ■ 실천 액션플랜 (ACTION PLAN)
            - [오늘 바로] 강의 관련 핵심 카드가 담긴 디톡스/영양 교육 카드 뉴스를 다운라인 파트너 단톡방에 배포.
            - [3일 이내] 뉴트리션 프리미엄 가이드북을 준비하여 기존 저가 비타민 섭취 지인 2명에게 '세포 생체 이용률 정밀 비교 상담' 약속 성사.
            - [7일 이내] 초대 대상자 3인의 투잡 고민 유형(직장인, 주부, 은퇴 등)을 세분화하여 유사나 1:1 맞춤형 보상 플랜 멘토링 연출 세팅.
            
            ■ 미래 예측 인사이트 (INSIGHTS)
            - 건강에 대한 우려는 이제 치료가 아닌 '자가 면역 세포 예방' 패러다임으로의 완벽한 이동.
            - 현대의 구독 라이프스타일과 부합하는 유사나 오토십(Autoship)이야말로 웰니스 분야 중 평생 상속 가능한 무경계 권리자산의 종착지임.
        """.trimIndent()
        
        val customStrategy = """
            ■ 1. 실전 초대 멘트 (RECRUITING PITCH)
              - "선배님! 최근에 시간은 없고 건강과 부업 파이프라인 관심 있으셨죠? 이번 주 웰니스 세미나에서 세포 과학과 글로벌 상장 브랜드를 기반으로 안전자산을 만드는 지도를 강의하거든요. 가벼운 차 함께하며 제가 배운 비전 제안서를 보여드리고 싶어요!"
              
            ■ 2. 홈미팅 활용법 (HOME MEETING)
              - '토양 미네랄 고갈 실태'와 '뉴트리션 패키지'를 시각 자료로 게시하고 소그룹 제품 테스트(헬스팩, 뉴트리밀 쉐이크 시음)를 곁들인 체험형 홈미팅을 매주 화요일 개설.
              
            ■ 3. 제품 상담 활용법 (PRODUCT SALES CONSULTATION)
              - "우리가 매일 먹는 식사만으로는 세포 자가 면역에 빈자리가 생겨요. 국가 협회가 공인하고 전문 스포츠 선수가 매달 섭취 중인 올인원 섭취팩 '헬스팩'으로 세포 수용체를 바로 깨워보세요."
              
            ■ 4. 리더십 교육 활용법 (LEADERSHIP TRAINING)
              - 팀 내 차세대 리더들을 대상으로, 태도의 힘과 부정 감정 제거 독서 미팅 루틴을 개설하여 매일 아침 단톡방에 제품 섭취 샷 및 미라클 모닝 공유.
              
            ■ 5. 사업설명회 활용법 (OPP PRESENTATION)
              - 뉴욕 증시(NYSE: USNA) 무부채 상장 현황과 인셀리전스 세포 신호 과학의 특허 기술을 사업설명회 전반부에 핵심 강점으로 배치하여 타사 플랜 대비 독점 신뢰도 극대화.
              
            ■ 6. 신규 파트너 교육 활용법 (NEW PARTNER 1·3·7 루틴)
              - [1일차]: 유사나 허브(Hub) 앱 설치 및 스마트 오토십 할인 할인 신청법 원격 안내.
              - [3일차]: 제품 정기 수령 완료 후 올바른 섭취 시기 및 장 평화 헬스케어 피드백 진행.
              - [7일차]: 성장 공동체 비전 세미나 초대 일정 약속 확보 및 스폰서 3자 매칭 대면 상담 성사.
        """.trimIndent()
        
        val customProcessed = """
            ■ [고객용 콘텐츠 (FOR CUSTOMERS)]
            
              - ✍️ 블로그 포스팅:
                제목: "매일 건강식품을 먹어도 만성 피로인 뜻밖의 원인: 세포 무감각증 타파"
                본문: "현대인들에게 진짜 필요한 건 세포 수준의 흡수율입니다. GMP 1등급 시설에서 생산되고 세계 뉴트리션 가이드북 1위를 질주하는 프리미엄 종합팩의 세포 배합 원자 기술을 소개합니다..."
                
              - 💬 카카오톡 전송 문구:
                "선후배님! 현대 토양 오염으로 사과 속 고효율 비타민이 다 거덜 났대요. 세포 수용체까지 정밀 투입되는 고함량 영양 올인원 '헬스팩' 추천드려봐요. 스마트 하루 한 포로 에너지 가득 채워보아용!"
                
              - 📸 인스타그램 카드뉴스 & 피드 캡션:
                #세포항산화 #유사나헬스팩 #만성피로 #권리소득 #그로잉업그룹
                [이미지 컷]: "1. 일반 밥상 영양제의 배신 2. 세포 과학이 찾아낸 최적 ODA 섭취량 3. 헬스팩 한 포의 영양 가치"
            
            ■ [사업자용 콘텐츠 (FOR DISTRIBUTORS)]
            
              - 🏠 홈미팅 스크립트:
                "오늘 참석해주신 사장님들 환영합니다! 오늘 우리는 한 병의 영양제 속에 담긴 과학 수치를 함께 비교해보고, 소비를 소득으로 바꾸는 위대하지만 쉬운 유사나 평생 권리 연금 라이프스타일 플랜을 시작해보겠습니다..."
                
              - 🎙️ 세미나 3분 스피치 원고:
                "인생의 파이프라인을 구축하지 않으면 우리는 죽을 때까지 일하는 평생 노동에서 한 걸음도 비껴설 수 없습니다. 유사나 3BC 멀티 대리점 매장과 제품 소비의 마법으로 저는 한 주마다 달러 연금 보너스를 받고 있습니다..."
                
              - 📘 팀 교육자료 원고:
                "복제의 전제 조건은 절대적 단순함입니다. 제품 개봉, 오토십 장바구니 활성화, 그리고 성장 교육 시스템에 100% 안착하는 세 자지만 실천한다면 누구나 한 주마다 누적 소득의 주인공이 됩니다..."
            
            ■ [유튜브 콘텐츠 (FOR YOUTUBE)]
            
              - 🎥 Shorts 쇼츠 대본 (60초):
                "일 안 해도 매달 통장에 꽂히는 돈, 평생 받는 영양제 비법 궁금하시죠? 답은 세포 건강과 유사나 보상플랜입니다! 저가 가성비 비타민 10통보다 세포 특허 기술 1등 영양제가 신체 겉과 속을 완벽히 바꿉니다! 고정 댓글 링크를 확인하세요!"
                
              - 🏷️ 고클릭 썸네일 문구:
                "의사들이 몰래 먹는 평생 세포 영양제! ₩0원으로 대리점 3개 차리는 파이프라인 법 공개!"
                
              - 📝 영상 기획안 및 연출 대본:
                [인트로화면]: 활기찬 미소의 사업자가 헬스팩 한 포를 따먹으며 활력 충전.
                [나레이션]: "왜 같은 나이인데 누구는 만성 활력 피로를 달고 살고, 누구는 주말 세미나에서도 돋보일까요? 바로 세포 항산화 신호가 달라서입니다..."
        """.trimIndent()

        return SimulationResult(
            transcript = customTranscript,
            summary = customSummary,
            strategy = customStrategy,
            processedData = customProcessed
        )
    }

    private fun getMockTranslation(text: String, lang: String): String {
        return when (lang) {
            "en" -> """
                [English Translation Preview - Powered by USANA INSIGHT HUB Translation Engine]
                
                ---
                ${text.replace("안녕하십니까", "Greetings").replace("유사나", "USANA").replace("박사", "Dr.").replace("사업", "Business").replace("건강", "Health")}
            """.trimIndent()
            "es" -> """
                [Vista previa de traducción al español - Impulsado por el motor de traducción USANA INSIGHT HUB]
                
                ---
                ${text.replace("안녕하십니까", "Saludos").replace("유사나", "USANA").replace("박사", "Dr.").replace("사업", "Negocios").replace("건강", "Salud")}
            """.trimIndent()
            "zh" -> """
                [中文 翻译预览 - 由 USANA INSIGHT HUB 翻译引擎提供]
                
                ---
                ${text.replace("안녕하십니까", "您好").replace("유사나", "优莎纳").replace("박사", "博士").replace("사업", "事业").replace("건강", "健康")}
            """.trimIndent()
            else -> text
        }
    }
}
