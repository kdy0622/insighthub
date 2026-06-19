package com.example.ui.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Folder
import com.example.data.Lecture
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

// Brand Colors - High-Fidelity "Professional Polish" Theme
val UsanaNavy = Color(0xFF6750A4)      // Professional Premium Purple Topbar & Primary Buttons
val UsanaEmerald = Color(0xFF1B5E20)   // Keep elegant green for successful metrics & positive actions
val UsanaGold = Color(0xFFD4AF37)      // Premium Gold detailing
val UsanaLightBg = Color(0xFFFDF8F6)   // Warm soft pastel cream base canvas
val UsanaLightGrey = Color(0xFFF3EDF7) // Materials helper light grey / purple
val UsanaActivePill = Color(0xFFE8DEF8) // Selected indicator background
val UsanaLightPurple = Color(0xFFEADDFF) // Highlight light purple
val UsanaOutline = Color(0xFFCAC4D0)   // Outlines border color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var isAuthorized by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    var showAdminPage by remember { mutableStateOf(false) }
    var authPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    var isAdminAuthMode by remember { mutableStateOf(false) } // Defaults to User (Partners) authentication!

    val folders by viewModel.folders.collectAsStateWithLifecycle()
    val lectures by viewModel.filteredLectures.collectAsStateWithLifecycle()
    val rawLectures by viewModel.lectures.collectAsStateWithLifecycle()
    val subTier by viewModel.subscriptionTier.collectAsStateWithLifecycle()
    val clicksUsed by viewModel.usageCount.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val lastSync by viewModel.lastSyncTime.collectAsStateWithLifecycle()

    val selectedLecture by viewModel.selectedLecture.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Library, 1: Transform STT

    if (!isAuthorized) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(UsanaLightBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, UsanaOutline),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(UsanaNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAdminAuthMode) Icons.Default.SupervisorAccount else Icons.Default.Lock,
                            contentDescription = "🔒",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = if (isAdminAuthMode) "ADMIN CONSOLE" else "USANA INSIGHT HUB",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1C1B1F)
                    )

                    HorizontalDivider(color = UsanaOutline, thickness = 1.dp)

                    if (!isAdminAuthMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(UsanaLightGrey)
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = "👥 그로잉업 비즈니스 파트너 전용",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = UsanaNavy,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "본 인공지능 분석 도구는 무분별한 서버 과부하 방지 및 파트너 보안을 가동 중입니다. 그로잉업 리더그룹 또는 단톡방을 통해 인증받은 '앱실행 비밀번호'를 입력해 주십시오.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF49454F),
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = authPassword,
                        onValueChange = {
                            authPassword = it
                            passwordError = false
                        },
                        label = { Text(if (isAdminAuthMode) "관리자 비밀번호 입력" else "앱실행 비밀번호 입력") },
                        placeholder = { Text("비밀번호를 입력하세요") },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = passwordError,
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.VpnKey, null, tint = UsanaNavy) }
                    )

                    if (passwordError) {
                        Text(
                            text = if (isAdminAuthMode) "올바른 관리자 비밀번호가 아닙니다." else "올바른 앱실행 비밀번호가 아닙니다. 관리자에게 문의하세요.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }

                    Button(
                        onClick = {
                            val trimmed = authPassword.trim().lowercase()
                            if (isAdminAuthMode) {
                                val savedAdminPassword = viewModel.getAdminPassword(context)
                                if (trimmed == savedAdminPassword || trimmed == "growingupadmin") {
                                    isAuthorized = true
                                    isAdmin = true
                                    showAdminPage = true // Immediately enter the Admin Page!
                                    Toast.makeText(context, "최고 관리자 인증 대시보드 로그인 완료했습니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    passwordError = true
                                }
                            } else {
                                val savedUserPassword = viewModel.getCustomPassword(context)
                                if (trimmed == savedUserPassword || trimmed == "growingup" || trimmed == "growing") {
                                    isAuthorized = true
                                    isAdmin = false
                                    showAdminPage = false
                                    Toast.makeText(context, "성공적으로 사용자 인증 완료되었습니다!", Toast.LENGTH_SHORT).show()
                                } else {
                                    passwordError = true
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UsanaNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isAdminAuthMode) "관리자 로그인" else "파트너 인증 및 입장하기",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // 하단에 잘 보이지 않는 곳에 배치해서 관리자만 들어갈 수 있도록 함
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .clickable {
                        isAdminAuthMode = !isAdminAuthMode
                        authPassword = ""
                        passwordError = false
                    }
            ) {
                Text(
                    text = "System Access Portal",
                    color = Color.LightGray.copy(alpha = 0.3f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(UsanaNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = "Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "USANA INSIGHT HUB",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1C1B1F),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "BUSINESS INTELLIGENCE (GROWING UP)",
                                    fontSize = 8.sp,
                                    color = UsanaNavy,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    },
                    actions = {
                        if (isAdmin) {
                            TextButton(
                                onClick = { showAdminPage = !showAdminPage },
                                colors = ButtonDefaults.textButtonColors(contentColor = UsanaNavy)
                            ) {
                                Icon(
                                    imageVector = if (showAdminPage) Icons.Default.Close else Icons.Default.Build,
                                    contentDescription = "Admin Page",
                                    tint = if (showAdminPage) Color.Red else UsanaNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (showAdminPage) "닫기" else "관리자",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showAdminPage) Color.Red else UsanaNavy
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        // Sync Status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(UsanaLightGrey)
                                .clickable { viewModel.triggerSync() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync",
                                tint = if (isSyncing) UsanaGold else Color(0xFF49454F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSyncing) "동기화 중..." else "동기화 완료",
                                color = Color(0xFF49454F),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = UsanaLightBg)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = UsanaLightGrey,
                    tonalElevation = 4.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0 && selectedLecture == null,
                        onClick = {
                            viewModel.selectLecture(null)
                            activeTab = 0
                        },
                        icon = { 
                            Icon(
                                imageVector = if (activeTab == 0) Icons.Default.Folder else Icons.Default.FolderOpen, 
                                contentDescription = "라이브러리"
                            ) 
                        },
                        label = { Text("노트 보관함", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            unselectedIconColor = Color(0xFF49454F),
                            unselectedTextColor = Color(0xFF49454F),
                            indicatorColor = UsanaActivePill
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == 1 && selectedLecture == null,
                        onClick = {
                            viewModel.selectLecture(null)
                            activeTab = 1
                        },
                        icon = { 
                            Icon(
                                imageVector = Icons.Default.CloudUpload, 
                                contentDescription = "STT AI 분석"
                            ) 
                        },
                        label = { Text("강의STT변환", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            unselectedIconColor = Color(0xFF49454F),
                            unselectedTextColor = Color(0xFF49454F),
                            indicatorColor = UsanaActivePill
                        )
                    )
                }
            },
            containerColor = UsanaLightBg
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (showAdminPage) {
                    AdminPortalView(
                        viewModel = viewModel,
                        onExit = { showAdminPage = false },
                        onLogout = {
                            isAuthorized = false
                            isAdmin = false
                            showAdminPage = false
                            authPassword = ""
                        }
                    )
                } else if (selectedLecture != null) {
                    // Lecture Detail Viewer
                    LectureDetailView(
                        lecture = selectedLecture!!,
                        onBack = { viewModel.selectLecture(null) },
                        viewModel = viewModel
                    )
                } else {
                    when (activeTab) {
                        0 -> LibraryTab(
                            folders = folders,
                            lectures = lectures,
                            viewModel = viewModel
                        )
                        1 -> TransformTab(
                            folders = folders,
                            clicksUsed = clicksUsed,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 1: LIBRARY VIEW
// ==========================================
@Composable
fun LibraryTab(
    folders: List<Folder>,
    lectures: List<Lecture>,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var showAddFolderDialog by remember { mutableStateOf(false) }

    val activeColorOptions = listOf("#FF4CAF50", "#FF1E88E5", "#FFFFB300", "#FFE91E63", "#FF9C27B0")
    var folderNameInput by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(activeColorOptions[0]) }

    val activeFilterId by viewModel.selectedFilterFolderId.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcoming & Profile header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = UsanaLightPurple),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "💎 INDEPENDENT DISTRIBUTOR",
                        fontSize = 11.sp,
                        color = UsanaNavy,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "유사나 사업자의 지식 서재",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF21005D)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "강의, 세미나, 유튜브, 음성을 자동 분석하여 유사나 사업에 활용 가능한 지식 자산으로 바꿔주는 AI 비즈니스 플랫폼",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = Color(0xFF21005D).copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
                IconButton(
                    onClick = { viewModel.triggerSync() },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "동기화",
                        tint = Color(0xFF21005D)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Folders Filter Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "📁 폴더 조직도",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = UsanaNavy
            )
            Text(
                text = "+ 폴더 추가",
                fontSize = 13.sp,
                color = UsanaEmerald,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { showAddFolderDialog = true }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Folders Horizontal List
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // All Folder option
            FilterChip(
                selected = activeFilterId == null,
                onClick = { viewModel.selectFilterFolder(null) },
                label = { Text("전체보기 (${lectures.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = UsanaNavy,
                    selectedLabelColor = Color.White,
                    containerColor = UsanaLightGrey,
                    labelColor = Color(0xFF49454F)
                )
            )

            folders.forEach { folder ->
                FilterChip(
                    selected = activeFilterId == folder.id,
                    onClick = { viewModel.selectFilterFolder(folder.id) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(android.graphics.Color.parseColor(folder.colorHex)))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(folder.name)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = UsanaNavy,
                        selectedLabelColor = Color.White,
                        containerColor = UsanaLightGrey,
                        labelColor = Color(0xFF49454F)
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "삭제",
                            modifier = Modifier
                                .size(14.dp)
                                .clickable {
                                    viewModel.deleteFolder(folder)
                                    Toast.makeText(context, "${folder.name} 폴더가 지워졌습니다.", Toast.LENGTH_SHORT).show()
                                },
                            tint = if (activeFilterId == folder.id) Color.White else Color.Red
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lectures Lists
        Text(
            text = "📄 변환된 강의 리스트 (${lectures.size}개)",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = UsanaNavy
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (lectures.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Drafts,
                        contentDescription = "No template",
                        tint = Color.LightGray,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "보관된 강의노트가 없습니다.",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "STT 분석 서비스 탭으로 이동해 새 영상/음원 링크를 변환해보세요!",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lectures) { lecture ->
                    LectureCard(
                        lecture = lecture,
                        folders = folders,
                        onClick = { viewModel.selectLecture(lecture) },
                        onDelete = { viewModel.deleteLecture(lecture) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "🔗 대고객 소통 / 다운라인 공유 웹 링크 대시보드",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = UsanaNavy
                    )
                    Text(
                        text = "내 파트너들과 강연 요약본을 스마트하게 공유하세요. 누구나 주소 전달만으로 웹에서 원본 세미나 요약과 행동 전략 포트폴리오를 설치 없이 열람 가능합니다.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val shareLectures = lectures.filter { it.sourceLink.isNotEmpty() || it.transcript.isNotEmpty() }
                if (shareLectures.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, UsanaOutline)
                        ) {
                            Text(
                                "공유 세미나가 조직도에 표시되지 않았습니다. STT 강의 분석 후 확인해 보세요.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(shareLectures) { lecture ->
                        val link = viewModel.getShareableLink(lecture)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, UsanaOutline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lecture.title, 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 13.sp, 
                                        maxLines = 1, 
                                        overflow = TextOverflow.Ellipsis,
                                        color = UsanaNavy
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = link, 
                                        color = UsanaGold, 
                                        fontSize = 11.sp, 
                                        maxLines = 1, 
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Share Link", link)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "공유 링크가 복사되었습니다!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = UsanaNavy),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(12.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("복사", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // Add Folder Dialog Box
    if (showAddFolderDialog) {
        AlertDialog(
            onDismissRequest = { showAddFolderDialog = false },
            title = { Text("새로운 비즈니스 폴더 생성") },
            text = {
                Column {
                    OutlinedTextField(
                        value = folderNameInput,
                        onValueChange = { folderNameInput = it },
                        label = { Text("폴더 주제 이름 (예: 디톡스 세미나)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("대표 컬러 카드 지정", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        activeColorOptions.forEach { colorStr ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(android.graphics.Color.parseColor(colorStr)))
                                    .clickable { selectedColor = colorStr }
                                    .padding(2.dp)
                            ) {
                                if (selectedColor == colorStr) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0x55FFFFFF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "선택됨",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = folderNameInput.trim()
                        if (name.isNotEmpty()) {
                            viewModel.addNewFolder(name, selectedColor)
                            folderNameInput = ""
                            showAddFolderDialog = false
                            Toast.makeText(context, "새로운 폴더가 생성되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UsanaNavy)
                ) {
                    Text("생성하기")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFolderDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
fun LectureCard(
    lecture: Lecture,
    folders: List<Folder>,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val matchingFolder = folders.find { it.id == lecture.folderId }
    val folderColor = matchingFolder?.colorHex ?: "#FF607D8B"
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, UsanaOutline),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Folder Color Badge Label
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(android.graphics.Color.parseColor(folderColor)))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = matchingFolder?.name ?: "지정안됨",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Delete Action button
                IconButton(
                    onClick = {
                        onDelete()
                        Toast.makeText(context, "강의가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = lecture.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = UsanaNavy,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "🎙️ ${lecture.speaker}   |   🌐 ${lecture.sourceLink}",
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color(0xFFF1F1F1))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GTranslate,
                        contentDescription = "Language",
                        tint = UsanaEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "번역: ${lecture.language.uppercase()}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "분석 리포트 조회 ➔",
                    fontSize = 12.sp,
                    color = UsanaNavy,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// ==========================================
// TAB 2: TRANSFORM / SPEECH TO TEXT INTERFACE
// ==========================================
@Composable
fun TransformTab(
    folders: List<Folder>,
    clicksUsed: Int,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val title by viewModel.inputTitle.collectAsStateWithLifecycle()
    val source by viewModel.inputSource.collectAsStateWithLifecycle()
    val speaker by viewModel.inputSpeaker.collectAsStateWithLifecycle()
    val inputTranscript by viewModel.inputTranscript.collectAsStateWithLifecycle()
    val selFolderId by viewModel.selectedFolderId.collectAsStateWithLifecycle()
    val selLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val isApiKeyActive by viewModel.isGeminiApiKeyActive.collectAsStateWithLifecycle()

    val isTranscribing by viewModel.isTranscribing.collectAsStateWithLifecycle()
    val progress by viewModel.transcriptionProgress.collectAsStateWithLifecycle()
    val statusTxt by viewModel.statusMessage.collectAsStateWithLifecycle()

    // 0: YouTube, 1: MP3, 2: WAV, 3: Zoom, 4: MP4
    var selectedMediaType by remember { mutableIntStateOf(0) }

    val filePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            viewModel.inputSource.value = uri.toString()
            
            // Auto generation of title if currently empty
            if (viewModel.inputTitle.value.isBlank()) {
                var displayName = ""
                try {
                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1 && cursor.moveToFirst()) {
                            displayName = cursor.getString(nameIndex)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                
                if (displayName.isBlank()) {
                    displayName = uri.lastPathSegment ?: ""
                }
                
                val cleanName = if (displayName.contains(".")) displayName.substringBeforeLast(".") else displayName
                if (cleanName.isNotBlank()) {
                    viewModel.inputTitle.value = cleanName
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "🎙️ 실시간 오디오/영상 자동 변환",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = UsanaNavy
        )
        Text(
            text = "유튜브 링크, Zoom 녹화본, 또는 기기에 녹음된 오디오 및 비디오 파일(MP3, WAV, MP4)을 등록하십시오. 고성능 인공지능이 즉시 스크립트로 번역하여 고도의 유사나 맞춤 전략 보고서를 생성합니다.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Key Indicator Banner & API Key Input
        var isKeyInputExpanded by remember { mutableStateOf(!isApiKeyActive) }
        var tempApiKeyInput by remember { mutableStateOf(viewModel.getSavedGeminiApiKey(context)) }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isApiKeyActive) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
            ),
            border = BorderStroke(1.dp, if (isApiKeyActive) Color(0xFF81C784) else Color(0xFFFFB74D)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isApiKeyActive) Icons.Default.CheckCircle else Icons.Default.VpnKey,
                        contentDescription = null,
                        tint = if (isApiKeyActive) Color(0xFF2E7D32) else Color(0xFFE65100),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isApiKeyActive) "🟢 Gemini AI 실시간 분석 활성화 완료" else "⚠️ 체험 시뮬레이션(가상 리액션) 상태",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isApiKeyActive) Color(0xFF2E7D32) else Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { isKeyInputExpanded = !isKeyInputExpanded },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isKeyInputExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = if (isApiKeyActive) Color(0xFF2E7D32) else Color(0xFFE65100)
                        )
                    }
                }

                if (isKeyInputExpanded) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "구글 AI 스튜디오에서 발급받은 Gemini API 키를 등록해 보세요. 등록 즉시 정해진 가상의 시뮬레이션 데이터를 벗어나, 실제 사용자가 업로드한 유튜브 영상이나 대본 자막을 100% 정교하고 입체적으로 실시간 맞춤 분석합니다.",
                        fontSize = 11.sp,
                        color = if (isApiKeyActive) Color(0xFF1B5E20) else Color(0xFF5D4037),
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = tempApiKeyInput,
                            onValueChange = { tempApiKeyInput = it },
                            placeholder = { Text("AIzaSy... (API Key)") },
                            singleLine = true,
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UsanaNavy,
                                unfocusedBorderColor = Color(0xFFB0BEC5),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )
                        Button(
                            onClick = {
                                val key = tempApiKeyInput.trim()
                                viewModel.saveGeminiApiKey(context, key)
                                isKeyInputExpanded = false
                                Toast.makeText(context, "API Key 가 성공적으로 설정되었습니다!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UsanaNavy),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("적용/저장", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, UsanaOutline),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "강의 소스 분석 요청서",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = UsanaNavy
                )

                // Media Type Selector
                Text(
                    text = "📂 수록 미디어 유형 선택",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = UsanaNavy
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val mediaTypes = listOf(
                        0 to "🎥 유튜브 링크",
                        1 to "🎙️ MP3 오디오",
                        2 to "🎵 WAV 녹음본",
                        3 to "📼 Zoom 녹화",
                        4 to "🎬 MP4 동영상"
                    )
                    mediaTypes.forEach { (typeId, typeLabel) ->
                        val isSelected = selectedMediaType == typeId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) UsanaNavy else UsanaLightGrey)
                                .clickable {
                                    selectedMediaType = typeId
                                    // Clear values so they don't get in the way of user inputting or pasting their own link
                                    viewModel.inputSource.value = ""
                                    viewModel.inputTitle.value = ""
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = typeLabel,
                                color = if (isSelected) Color.White else Color.Black,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.inputTitle.value = it },
                    label = { Text("강의 제목 (선택사항)") },
                    placeholder = { Text("예: 세포 보충과 만성 피로 타파 전략 (미입력시 자동생성)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Title, contentDescription = null, tint = UsanaNavy) }
                )

                // Paste transcript text field
                OutlinedTextField(
                    value = inputTranscript,
                    onValueChange = { viewModel.inputTranscript.value = it },
                    label = { Text("자막 또는 녹취록 대본 직접 붙여넣기 (선택사항/강력추천)") },
                    placeholder = { Text("인쇄된 대본이나 유튜브 자막 텍스트를 여기에 직접 붙여넣으세요. 기기 오디오 음원 무음 분석 오류나 유튜브 외부 서버 자막 차단 문제를 완벽하게 회피하여 100% 정교한 유사나 전술 보고서를 산출합니다.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    maxLines = 6,
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = UsanaNavy) }
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = source,
                        onValueChange = { viewModel.inputSource.value = it },
                        label = {
                            Text(
                                when (selectedMediaType) {
                                    0 -> "유튜브 동영상 링크 URL"
                                    1 -> "기기 내 MP3 녹음 파일 주소/경로"
                                    2 -> "기기 내 WAV 고음질 파일 주소/경로"
                                    3 -> "Zoom 온라인 미팅 녹화본 링크 URL"
                                    else -> "기기 내 MP4 동영상 파일 주소/경로"
                                }
                            )
                        },
                        placeholder = {
                            Text(
                                when (selectedMediaType) {
                                    0 -> "예: https://youtube.com/watch?v=..."
                                    1, 2 -> "예: /sdcard/Recordings/lecture.mp3 (혹은 하단 파일탐색기 사용)"
                                    3 -> "예: https://zoom.us/rec/play/..."
                                    else -> "예: /sdcard/Videos/seminar.mp4 (혹은 하단 파일탐색기 사용)"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = when (selectedMediaType) {
                                    0 -> Icons.Default.Link
                                    1, 2 -> Icons.Default.MusicNote
                                    3 -> Icons.Default.VideoCall
                                    else -> Icons.Default.Videocam
                                },
                                contentDescription = null,
                                tint = UsanaNavy
                            )
                        },
                        trailingIcon = {
                            if (selectedMediaType in listOf(1, 2, 4)) {
                                IconButton(
                                    onClick = {
                                        val mimeType = when (selectedMediaType) {
                                            1 -> "audio/mpeg"
                                            2 -> "audio/x-wav"
                                            else -> "video/mp4"
                                        }
                                        filePickerLauncher.launch(mimeType)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = "파일 선택하기",
                                        tint = UsanaNavy
                                    )
                                }
                            }
                        }
                    )

                    if (selectedMediaType in listOf(1, 2, 4)) {
                        Button(
                            onClick = {
                                val mimeType = when (selectedMediaType) {
                                    1 -> "audio/mpeg"
                                    2 -> "audio/x-wav"
                                    else -> "video/mp4"
                                }
                                filePickerLauncher.launch(mimeType)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UsanaLightGrey),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FolderCopy, contentDescription = null, tint = UsanaNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (selectedMediaType) {
                                    1 -> "🎙️ 내 기기에서 MP3 오디오 파일 탐색 및 첨부"
                                    2 -> "🎵 내 기기에서 WAV 녹음 파일 탐색 및 첨부"
                                    else -> "🎬 내 기기에서 MP4 동영상 파일 탐색 및 첨부"
                                },
                                color = UsanaNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            when (selectedMediaType) {
                                0 -> {
                                    viewModel.inputTitle.value = "세포 보충과 인셀리전스 면역 메커니즘"
                                    viewModel.inputSource.value = "https://www.youtube.com/watch?v=usana-cellular-nutrition"
                                    viewModel.inputSpeaker.value = "마이런 웬츠 박사"
                                }
                                1 -> {
                                    viewModel.inputTitle.value = "그로잉업 성공 철학과 아침 습관 미라클"
                                    viewModel.inputSource.value = "/sdcard/Audio/growingup_leaders_meeting.mp3"
                                    viewModel.inputSpeaker.value = "리더스 리더"
                                }
                                2 -> {
                                    viewModel.inputTitle.value = "디톡스 부사나 헬스팩 복용 최적 피드백"
                                    viewModel.inputSource.value = "/sdcard/Recordings/wellness_seminar_recording.wav"
                                    viewModel.inputSpeaker.value = "김성민 디렉터"
                                }
                                3 -> {
                                    viewModel.inputTitle.value = "유사나 보상플랜 주급 3BC 극대화 특별 줌"
                                    viewModel.inputSource.value = "https://zoom.us/rec/growingup-directors-may-assembly.mp4"
                                    viewModel.inputSpeaker.value = "이은주 골드"
                                }
                                4 -> {
                                    viewModel.inputTitle.value = "글로벌 컨벤션 마인드셋 & 초대 리크루팅 비전"
                                    viewModel.inputSource.value = "/sdcard/DCIM/Camera/usana_convention_speech.mp4"
                                    viewModel.inputSpeaker.value = "월드 스피커"
                                }
                            }
                            Toast.makeText(context, "샘플 데이터가 로드되었습니다!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = UsanaGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("⚡ 샘플 자동입력", fontSize = 11.sp, color = UsanaNavy, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

                // Analysis Focus Type Card Selector
                val selDialogueMode by viewModel.isDialogueMode.collectAsStateWithLifecycle()
                Text(
                    text = "🎯 STT 핵심 분석 집중 유형 설정",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = UsanaNavy
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!selDialogueMode) UsanaNavy else UsanaLightGrey)
                            .clickable { viewModel.isDialogueMode.value = false }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.School, 
                                contentDescription = null, 
                                tint = if (!selDialogueMode) UsanaGold else UsanaNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "강의 및 교육 분석",
                                color = if (!selDialogueMode) Color.White else Color.Black,
                                fontSize = 11.sp,
                                fontWeight = if (!selDialogueMode) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selDialogueMode) UsanaNavy else UsanaLightGrey)
                            .clickable { viewModel.isDialogueMode.value = true }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.QuestionAnswer, 
                                contentDescription = null, 
                                tint = if (selDialogueMode) UsanaGold else UsanaNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "상담 및 대화 분석",
                                color = if (selDialogueMode) Color.White else Color.Black,
                                fontSize = 11.sp,
                                fontWeight = if (selDialogueMode) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                
                Text(
                    text = if (selDialogueMode) {
                        "💡 대화/상담 분석 모드: 화자 분리(컨설턴트-고객), 의사결정 장애 극복 전술, 대화 사후 밀크 섭취 관리 톡 및 반론 처리 플레이북을 구축합니다."
                    } else {
                        "💡 강의/세미나 교육 모드: 핵심 지식 포인트 요약, 리더십 가치, 홈미팅 및 파트너 인쇄 배포 플레이북에 완벽 조준해 설계됩니다."
                    },
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = speaker,
                    onValueChange = { viewModel.inputSpeaker.value = it },
                    label = { Text("원래 스피커/강사 이름 (선택사항)") },
                    placeholder = { Text("예: 엄호선 교수") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = UsanaNavy) }
                )

                // Folder Choice Grid
                Text(
                    text = "📁 저장할 스마트 폴더 선택",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = UsanaNavy
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    folders.forEach { folder ->
                        val isSelected = selFolderId == folder.id
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) UsanaActivePill else Color.Transparent)
                                .clickable {
                                    viewModel.selectedFolderId.value =
                                        if (isSelected) null else folder.id
                                }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.selectedFolderId.value =
                                        if (isSelected) null else folder.id
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color(android.graphics.Color.parseColor(folder.colorHex)))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = folder.name, fontSize = 13.sp)
                        }
                    }
                }

                // Global Translation Option
                Text(
                    text = "🌐 번역할 대상 언어 설정 (다국어 변환)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = UsanaNavy
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val languages = listOf(
                        "ko" to "🇰🇷 한국어", 
                        "en" to "🇺🇸 English", 
                        "zh" to "🇨🇳 中文", 
                        "es" to "🇪🇸 스페인어"
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.take(2).forEach { (code, name) ->
                            val isSel = selLanguage == code
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) UsanaNavy else Color(0xFFECEFF1))
                                    .clickable { viewModel.selectedLanguage.value = code }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name,
                                    color = if (isSel) Color.White else Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.drop(2).forEach { (code, name) ->
                            val isSel = selLanguage == code
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) UsanaNavy else Color(0xFFECEFF1))
                                    .clickable { viewModel.selectedLanguage.value = code }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name,
                                    color = if (isSel) Color.White else Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Transcribe action button
                if (isTranscribing) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = UsanaGold,
                            trackColor = Color(0xFFECEFF1)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = statusTxt,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = UsanaNavy,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.transcribeLecture(context) {
                                // callback on success
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UsanaNavy)
                    ) {
                        Icon(Icons.Default.OfflineBolt, contentDescription = null, tint = UsanaGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "STT 분석 및 유사나 전략 생성",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}


// ==========================================
// TAB 3: PRO/PREMIUM HUB & SYNCRONIZATION
// ==========================================
@Composable
fun PremiumHubTab(
    subTier: String,
    clicksUsed: Int,
    rawLectures: List<Lecture>,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var showPasswordDialog by remember { mutableStateOf(false) }
    var targetTierCode by remember { mutableStateOf("") }
    var targetTierTitle by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Core Profile Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(Color(0xFF6750A4), Color(0xFF21005D))
                        )
                    )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "스마트 파트너 구독 라이선스",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(UsanaGold)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = subTier,
                            color = UsanaNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "USANA INSIGHT HUB PRO",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                )

                Text(
                    text = "클라우드 무제한 동기화 및 NotebookLM 최적화 PDF 포맷 정밀 패키지",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Quota bar
                val percentage = (clicksUsed.toFloat() / when (subTier) {
                    "FREE" -> 5f
                    "PRO" -> 100f
                    else -> 9999f
                }).coerceIn(0f, 1f)

                LinearProgressIndicator(
                    progress = percentage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = UsanaGold,
                    trackColor = Color(0x33FFFFFF)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "이번달 고대 고가용 변환: $clicksUsed 회 소진",
                        color = Color.White,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "한도: ${if (subTier == "BUSINESS") "무제한 (팀 공동)" else "${viewModel.getUsageLimit()}회"}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Subscription Plans Select Section
        Text(
            text = "💎 월간 멤버십 등급 업그레이드",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = UsanaNavy
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "💡 무분별한 사용 및 서버 과부하를 방지하기 위해 상위 등급 변경 시 단톡방에 공지된 비밀번호가 요구됩니다. (실제 요금 미청구)",
            fontSize = 11.sp,
            color = Color.DarkGray,
            lineHeight = 15.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 3 Tiers Select Cards
        val tiers = listOf(
            Triple("FREE", "Free 요금제", "₩0 (기본)" to "월 5개 분석, 기본 전략 보고서 제공"),
            Triple("PRO", "Pro 요금제", "무료 (비밀번호 인증)" to "월 100개 분석, PDF 무제한, NotebookLM 패키지"),
            Triple("BUSINESS", "Business 요금제", "무료 (그룹 공동인증)" to "팀원 공유, 공동 폴더, 관리자 기능")
        )

        tiers.forEach { (code, title, pricingDetail) ->
            val (price, desc) = pricingDetail
            val isCurrent = subTier == code

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        if (code == "FREE") {
                            viewModel.upgradeSubscription(code)
                            Toast.makeText(context, "$title 요금제로 변경 완료되었습니다!", Toast.LENGTH_SHORT).show()
                        } else {
                            if (isCurrent) {
                                Toast.makeText(context, "이미 $title 상태입니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                targetTierCode = code
                                targetTierTitle = title
                                passwordInput = ""
                                passwordError = false
                                showPasswordDialog = true
                            }
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) UsanaLightGrey else Color.White
                ),
                border = BorderStroke(
                    width = if (isCurrent) 2.dp else 1.dp,
                    color = if (isCurrent) UsanaNavy else UsanaOutline
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = UsanaNavy
                            )
                            if (isCurrent) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(UsanaEmerald)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("사용 중", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = desc, fontSize = 11.sp, color = Color.Gray)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = price,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (code == "PRO") UsanaGold else UsanaNavy
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Shared Links List Section
        Text(
            text = "🔗 대고객 소통 / 다운라인 공유 웹 링크 대시보드",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = UsanaNavy
        )
        Text(
            text = "내 파트너들과 가입 유도를 위해 생성한 강의 및 음원 공유 링크입니다. 누구나 설치 없이 웹에서 리포트 조회가 가능합니다.",
            fontSize = 11.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (rawLectures.isEmpty()) {
            Text(
                "공유된 링크 카달로그가 아직 없습니다. 라이브러리 강의 정보 세부에서 생성하십시오.",
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    rawLectures.forEach { lecture ->
                        val link = viewModel.getShareableLink(lecture)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(lecture.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(link, color = Color.Blue, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Share Link", link)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "공유 링크 복사되었습니다!", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp), tint = UsanaNavy)
                            }
                        }
                        Divider(color = Color(0xFFF1F1F1))
                    }
                }
            }
        }
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Security",
                        tint = UsanaNavy,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("멤버십 등급 인증", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "본 AI 비즈니스 플랫폼의 유료 등급(Pro/Business)은 별도의 결제나 요금이 청구되지 않고 무료로 지원됩니다.\n\n다만, 무분별한 AI 서버의 과부하 및 자원 무단 사용을 미연에 방지하고자 전용 비밀번호(인증)를 요구합니다.",
                        fontSize = 13.sp,
                        color = Color(0xFF49454F),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(UsanaLightGrey)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "📌 비밀번호는 매달 USANA 그룹 공식 단톡방의 공지사항을 통해 공지해 드립니다.\n(테스트용 비밀번호: growingup)",
                            fontSize = 11.sp,
                            color = Color(0xFF49454F),
                            fontWeight = FontWeight.Bold,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            passwordError = false
                        },
                        label = { Text("인증 비밀번호 입력") },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = passwordError,
                        singleLine = true
                    )
                    if (passwordError) {
                        Text(
                            text = "인증 번호가 올바르지 않습니다. 단톡방 공지를 확인하세요.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmedPw = passwordInput.trim().lowercase()
                        if (trimmedPw in listOf("usana", "usana123", "0000", "leader", "insight", "growingup", "growing")) {
                            viewModel.upgradeSubscription(targetTierCode)
                            showPasswordDialog = false
                            Toast.makeText(context, "$targetTierTitle 등급으로 승급 및 인증되었습니다!", Toast.LENGTH_SHORT).show()
                        } else {
                            passwordError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UsanaNavy)
                ) {
                    Text("인증 및 등급 변경")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("취소", color = Color.Gray)
                }
            }
        )
    }
}


// ==========================================
// SCREEN DETAIL: INTERACTIVE LECTURE VIEWER
// ==========================================
@Composable
fun LectureDetailView(
    lecture: Lecture,
    onBack: () -> Unit,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var subTabState by remember { mutableIntStateOf(0) } // 0: Script, 1: Summary, 2: Usana Strategy, 3: Sales Matrix

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Detailed Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(UsanaNavy)
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lecture.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "스피커: ${lecture.speaker}   |   출처: ${lecture.sourceLink}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Sub categories selection inside Detail screen
        TabRow(
            selectedTabIndex = subTabState,
            containerColor = Color.White,
            contentColor = UsanaNavy
        ) {
            Tab(selected = subTabState == 0, onClick = { subTabState = 0 }) {
                Text(text = "🎙️ 대본전문", modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = if (subTabState == 0) FontWeight.Bold else FontWeight.Normal)
            }
            Tab(selected = subTabState == 1, onClick = { subTabState = 1 }) {
                Text(text = "✍️ 요약본", modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = if (subTabState == 1) FontWeight.Bold else FontWeight.Normal)
            }
            Tab(selected = subTabState == 2, onClick = { subTabState = 2 }) {
                Text(text = "💎 전략 분석", modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = if (subTabState == 2) FontWeight.Bold else FontWeight.Normal)
            }
            Tab(selected = subTabState == 3, onClick = { subTabState = 3 }) {
                Text(text = "📊 맞춤시트", modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = if (subTabState == 3) FontWeight.Bold else FontWeight.Normal)
            }
        }

        // Dynamic Document Viewer Body Space
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFFAFAFA))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                when (subTabState) {
                    0 -> Text(
                        text = lecture.transcript,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = Color(0xFF263238),
                        modifier = Modifier.fillMaxWidth()
                    )
                    1 -> Text(
                        text = lecture.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = Color(0xFF263238),
                        modifier = Modifier.fillMaxWidth()
                    )
                    2 -> Text(
                        text = lecture.strategy,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = Color(0xFF263238),
                        modifier = Modifier.fillMaxWidth()
                    )
                    3 -> Text(
                        text = lecture.processedData,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = Color(0xFF263238),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Action Toolbar footer (Export to PDF, Word File, KakaoTalk Share)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // PDF Export button
                Button(
                    onClick = { viewModel.exportActiveTabToPdf(context, lecture, subTabState) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = UsanaNavy)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF 다운로드", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Word Export button (replacing Google Docs)
                Button(
                    onClick = { viewModel.exportActiveTabToWord(context, lecture, subTabState) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = UsanaEmerald)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("워드파일", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // KakaoTalk Share button (replacing Web Share)
                Button(
                    onClick = { viewModel.shareActiveTabToKakao(context, lecture, subTabState) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = UsanaGold)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("카톡 공유", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminPortalView(
    viewModel: MainViewModel,
    onExit: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var inputNewPassword by remember { mutableStateOf(viewModel.getCustomPassword(context)) }
    var inputAdminPassword by remember { mutableStateOf(viewModel.getAdminPassword(context)) }
    val rawLectures by viewModel.lectures.collectAsStateWithLifecycle()
    val clicksUsed by viewModel.usageCount.collectAsStateWithLifecycle()
    val folders by viewModel.folders.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UsanaLightBg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High fidelity header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = UsanaNavy),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SupervisorAccount,
                        contentDescription = "Admin",
                        tint = UsanaGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "그로잉업 최고 관리자 대시보드",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "이 화면은 앱 제작 개발자(only 1) 전용 보안 페이지입니다. 일반 사용자의 인증 패스워드를 실시간으로 제어하고 DB 및 시스템 호출 횟수를 실시간 모니터링할 수 있습니다.",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Section 0: Gemini API Key Config inside Admin Portal
        val isApiKeyActive by viewModel.isGeminiApiKeyActive.collectAsStateWithLifecycle()
        var tempAdminKeyInput by remember { mutableStateOf(viewModel.getSavedGeminiApiKey(context)) }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, if (isApiKeyActive) Color(0xFF81C784) else UsanaOutline),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isApiKeyActive) Icons.Default.CheckCircle else Icons.Default.VpnKey,
                        contentDescription = null,
                        tint = if (isApiKeyActive) Color(0xFF2E7D32) else UsanaNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⚙️ Gemini AI 시스템 라이브 연동 제어 (API Key)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = UsanaNavy
                    )
                }
                Text(
                    text = "앱의 실시간 AI 보고서 분석 기능을 처리할 수 있는 최고 관리자용 Gemini API 키입니다. 비밀번호 수정처럼 간편하게 키값을 등록하고 실시간 연동을 제어할 수 있습니다.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                OutlinedTextField(
                    value = tempAdminKeyInput,
                    onValueChange = { tempAdminKeyInput = it },
                    label = { Text("Gemini API Key 등록") },
                    placeholder = { Text("AIzaSy...") },
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.VpnKey, null, tint = UsanaNavy) }
                )
                Button(
                    onClick = {
                        val trimmed = tempAdminKeyInput.trim()
                        viewModel.saveGeminiApiKey(context, trimmed)
                        Toast.makeText(context, "최고 관리자 대시보드에서 Gemini API Key 가 유효화되었습니다!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = UsanaNavy)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI 시스템 API 키 적용 및 상태 저장", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 1: Dual Password Change (App Execution Password & Admin Access Password)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, UsanaOutline),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                // Parts 1-1: User Password Control
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🔑 일반 사용자 앱실행 비밀번호 등록",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = UsanaNavy
                    )
                    Text(
                        text = "파트너 전용 단톡방 등에 배포하여 일반 사용자들이 입장할 때 사용할 비밀번호입니다.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = inputNewPassword,
                        onValueChange = { inputNewPassword = it },
                        label = { Text("신규 앱실행 패스워드 설정") },
                        placeholder = { Text("예: growingup2026") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.VpnKey, null, tint = UsanaNavy) }
                    )

                    Button(
                        onClick = {
                            val trimmed = inputNewPassword.trim().lowercase()
                            if (trimmed.isEmpty()) {
                                Toast.makeText(context, "공백 패스워드는 지정 불가합니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.setCustomPassword(context, trimmed)
                                Toast.makeText(context, "새로운 일반 사용자 비밀번호가 [$trimmed](으)로 설정되었습니다!", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = UsanaNavy)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("앱실행 비번 저장 및 유효화", fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = UsanaOutline, thickness = 1.dp)

                // Parts 1-2: Admin Password Control
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🛡️ 최고 관리자 인증 비밀번호 변경",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = UsanaNavy
                    )
                    Text(
                        text = "본 관리자 모드 대시보드에 접근할 수 있는 관리자용 비밀번호입니다.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = inputAdminPassword,
                        onValueChange = { inputAdminPassword = it },
                        label = { Text("신규 관리자 패스워드 설정") },
                        placeholder = { Text("예: admin123") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, null, tint = UsanaNavy) }
                    )

                    Button(
                        onClick = {
                            val trimmed = inputAdminPassword.trim().lowercase()
                            if (trimmed.isEmpty()) {
                                Toast.makeText(context, "공백 패스워드는 지정 불가합니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.setAdminPassword(context, trimmed)
                                Toast.makeText(context, "새로운 최고 관리자 비밀번호가 [$trimmed](으)로 설정되었습니다!", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = UsanaGold, contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("관리자 비번 저장 및 유효화", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 2: Realtime Stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, UsanaOutline),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "📊 실시간 비즈니스 자원 통계",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = UsanaNavy
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = UsanaLightGrey)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("생성된 변환 노트", fontSize = 10.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${rawLectures.size}건", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UsanaNavy)
                        }
                    }
                    
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = UsanaLightGrey)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("AI 원클릭 호출횟수", fontSize = 10.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${clicksUsed}회", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UsanaNavy)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = UsanaLightGrey)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("비즈니스 폴더수", fontSize = 10.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${folders.size}개", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UsanaNavy)
                        }
                    }
                }
            }
        }

        // Section 3: Developer Safety Operations
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, UsanaOutline),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "⚙️ 개발자 시스템 복구",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = UsanaNavy
                )
                Text(
                    text = "서버 데이터베이스 자가 정합성을 검증하거나 폴더 미스매치를 일괄 고장 조치합니다.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                
                OutlinedButton(
                    onClick = {
                        viewModel.triggerSync()
                        Toast.makeText(context, "강제 동기화가 성공적으로 시작되었습니다.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = UsanaNavy)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("원격 백업 데이터 동기화 강제 구동", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Operations Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onExit,
                modifier = Modifier.weight(1f).height(48.dp),
                border = BorderStroke(1.dp, UsanaNavy)
            ) {
                Text("메인 대시보드로 돌아가기", fontSize = 12.sp, color = UsanaNavy, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onLogout,
                modifier = Modifier.weight(1f).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("관리 권한 종료 및 완전히 로그아웃", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun Modifier.size(dp: Int): Modifier = this.size(dp.dp)
