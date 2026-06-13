package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AppRepository(
    private val folderDao: FolderDao,
    private val lectureDao: LectureDao
) {
    val allFolders: Flow<List<Folder>> = folderDao.getAllFolders()
    val allLectures: Flow<List<Lecture>> = lectureDao.getAllLectures()

    fun getLecturesByFolder(folderId: Int): Flow<List<Lecture>> {
        return lectureDao.getLecturesByFolder(folderId)
    }

    suspend fun getLectureById(id: Int): Lecture? = withContext(Dispatchers.IO) {
        lectureDao.getLectureById(id)
    }

    suspend fun insertLecture(lecture: Lecture): Long = withContext(Dispatchers.IO) {
        lectureDao.insertLecture(lecture)
    }

    suspend fun updateLecture(lecture: Lecture) = withContext(Dispatchers.IO) {
        lectureDao.updateLecture(lecture)
    }

    suspend fun deleteLecture(lecture: Lecture) = withContext(Dispatchers.IO) {
        lectureDao.deleteLecture(lecture)
    }

    suspend fun insertFolder(folder: Folder): Long = withContext(Dispatchers.IO) {
        folderDao.insertFolder(folder)
    }

    suspend fun deleteFolder(folder: Folder) = withContext(Dispatchers.IO) {
        folderDao.deleteFolder(folder)
    }

    suspend fun checkAndPrepopulate() = withContext(Dispatchers.IO) {
        val currentFolders = folderDao.getAllFolders().first()
        if (currentFolders.isEmpty()) {
            // Seed folders optimized for USANA business
            val mindsetId = folderDao.insertFolder(Folder(name = "📁 성공철학 & 마인드셋", colorHex = "#6750A4")).toInt()
            val leadershipId = folderDao.insertFolder(Folder(name = "📁 리더십", colorHex = "#E11D48")).toInt()
            val recruitId = folderDao.insertFolder(Folder(name = "📁 초대 및 리크루팅", colorHex = "#2563EB")).toInt()
            val prodEduId = folderDao.insertFolder(Folder(name = "📁 제품 교육", colorHex = "#059669")).toInt()
            val incelligenceId = folderDao.insertFolder(Folder(name = "📁 인셀리전스 연구자료", colorHex = "#4F46E5")).toInt()
            val healthInfoId = folderDao.insertFolder(Folder(name = "📁 건강 정보", colorHex = "#0D9488")).toInt()
            val compPlanId = folderDao.insertFolder(Folder(name = "📁 보상플랜", colorHex = "#CA8A04")).toInt()
            val bizPresentationId = folderDao.insertFolder(Folder(name = "📁 사업설명회 자료", colorHex = "#EA580C")).toInt()
            val successStoryId = folderDao.insertFolder(Folder(name = "📁 성공사례 & 체험담", colorHex = "#DB2777")).toInt()
            val seminarId = folderDao.insertFolder(Folder(name = "📁 세미나 & 컨벤션", colorHex = "#7C3AED")).toInt()
            val etcId = folderDao.insertFolder(Folder(name = "📁 기타", colorHex = "#757575")).toInt()

            // Seed a high quality welcome Lecture in product education folder
            lectureDao.insertLecture(
                Lecture(
                    folderId = prodEduId,
                    title = "인간 세포 과학과 하이퀄리티 영양제의 비밀",
                    sourceLink = "https://www.youtube.com/watch?v=cellular-science",
                    speaker = "마이런 웬츠 박사 (세포면역학자)",
                    transcript = """
                        [00:00] 여러분의 건강은 우연히 결정되는 것이 아닙니다. 우리 인체의 건강은 결국 60조 개 세포 건강의 총합이기 때문입니다. 현대 의학은 증상을 완화하는 데 머무르지만, 참된 웰니스는 세포 하나하나가 세포 간 올바른 영양 신호 전달(Cell Signaling)을 원활하게 이루어지게 하는 일에서 기초합니다.
                        [01:30] 안타깝게도 우리가 들이쉬는 공기, 먹는 과일과 인스턴트 식품, 그리고 극에 달한 스트레스는 유해산소(활성산소, Free Radicals)를 방출시킵니다. 이 활성산소들이 세포를 산화시켜 노화와 인체의 피로 원인이 되며 염증을 기폭합니다. 
                        [02:40] 이것을 해결하는 비책이 항산화 작용입니다. 비타민 C, 비타민 E, 코엔자임 Q10, 그리고 포도씨 추출물 같은 파이토케미컬 항산화 영양 성분들이 세포 벽을 보호하고 손상된 유전 인자를 회복시킵니다. 복제 세포가 정상적으로 일할 수 있도록 의약품 제조 품질을 통과한 순수하고 고함량 영양제를 적시 수급하십시오.
                    """.trimIndent(),
                    summary = """
                        ■ 핵심 주제: 세포 기능 부활 및 항산화 영양 복합 배합의 중요성
                        
                        ■ 요약 포인트:
                        - 인체 전체 건강은 미세 세포들의 정상적 대사 및 항산화 수비에서 결정.
                        - 항산화제(비타민C, 포도씨 추출물 등)의 활성산소(Free Radical) 중화 가치 입증.
                        - 해결책: 인셀리전스 신호 전달 공법 기반 최적 뉴트리셔널 브랜드 패키지 섭취 권장.
                    """.trimIndent(),
                    strategy = """
                        ### [유사나(USANA) 사업 연계 전략 보고서]
                        
                        **1. 강의 요약과 유사나 사업 철학의 정합성**
                        인체의 세포 활력과 영양 신호 작용은 유사나의 핵심 특허인 "인셀리전스 테크놀로지" 기술의 완벽한 근거가 됩니다. 일반 보충제와 비교 불가한 세포 영양 의학 과학 가치를 전달함으로써, 유사나 헬스사이언스 디스트리뷰터가 전달해야할 가치를 교육형 콘텐츠로 활용 가능합니다.
                        
                        **2. 타겟 소비군 세그먼트 및 셀링 포인트**
                        *   **만성 영양 부족군**: 생활습관성 활력 고갈 지인 고객.
                        *   **노후 준비 시니어**: 무산소 운동 및 연골, 심장 세포 재생을 돕는 심혈관, 뼈 케어 마니아 유도.
                        *   **셀링 토크**: "건강을 우연에 맡기시겠습니까? 유사나의 독점 세포 통신 보완 팩인 '헬스팩'으로 세포 하나까지 눈부신 에너지를 채워보세요."
                        
                        **3. 파트너 리크루팅 연계 핵심 전략**
                        - **브랜드 안심 마케팅**: 컨슈머랩(ConsumerLab)의 엄격한 자율 검증 및 인폼드초이스 스포츠 무독성 도핑 인증을 필두로 정당한 웰니스 유전학 사업의 지적 가치를 강조해 프로 디스트리뷰터로의 유입을 성사시킵니다.
                        
                        **4. 디스트리뷰터 실행 과제 리스트**
                        - [ ] 스마트폰 주소록에서 '만성 컨디션 피로'를 앓는 3명을 메모하고 연락.
                        - [ ] 세포 면역 세미나 줌 녹화 본을 지인에게 1포 무료 샘플팩과 격려 메시지로 전파.
                    """.trimIndent(),
                    processedData = """
                        ### [유사나 맞춤 가공 데이터 및 액션 템플릿]
                        
                        #### 1. 추천 영양 패키지 매칭 가이드
                        | 타겟 건강 이슈 | 핵심 추천 제품 | 배합 효과 및 권장 섭취 이유 |
                        | :--- | :--- | :--- |
                        | 세포 영양 & 에너지 | **헬스팩 (Healthpak)** | 인셀리전스 복합체가 탑재된 14가지 최고 순도 비타민, 9가지 영양 미네랄, 강력한 허브 포그 함유 팩 |
                        | 관절 & 연골 세포 케어 | **프로코사 글루코사민** | 식물성 글루코사민과 특허 받은 메리바 커큐민 배합으로 연골 활력 및 유연 관절 회복 작용 극대화 |
                        
                        #### 2. 실전 고객 맞춤형 스크립트
                        > *"안녕하세요 [지인 이름]님! 늘 열심히 일하시느라 지쳐 보이시는 것 같아 염려가 되었어요. 방금 유명 세포 면역 과학 강의를 수리했는데 세포 영양이 세포 수준에서 활성산소를 씻어내야 아침에 눈뜰 때 확실히 상쾌하대요. 제가 먹어본 영양 제품 중 가장 과학 검증을 거친 유사나 세포 올인원 패키지 '헬스팩' 샘플 3일분 전달 드려볼게요! 느껴보시고 대화 나눠요~!"*
                    """.trimIndent(),
                    language = "ko",
                    shareToken = "welcome-share",
                    googleDocsLink = "https://docs.google.com/document/d/mock-welcome-docs-link"
                )
            )
        } else {
            val hasEtc = currentFolders.any { it.name.contains("기타") }
            if (!hasEtc) {
                folderDao.insertFolder(Folder(name = "📁 기타", colorHex = "#757575"))
            }
        }
    }
}
