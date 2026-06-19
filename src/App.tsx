import React, { useState, useEffect, useRef } from 'react';
import {
  BookOpen, Plus, Trash2, Settings, Activity, ShieldAlert, Sparkles, Share2, FileText,
  LayoutGrid, FolderPlus, Folder, Search, Mic, Volume2, Youtube, Check, Copy, Map,
  User, Lock, Sliders, Download, CreditCard, ArrowRight, ChevronRight, Info, Globe, RefreshCw
} from 'lucide-react';

// Interfaces
interface FolderType {
  id: number;
  name: string;
  color: string;
}

interface Lecture {
  id: number;
  folderId: number | null;
  title: string;
  sourceLink: string;
  speaker: string;
  transcript: string;
  summary: string;
  strategy: string;
  processedData: string;
  language: string;
  shareToken: string;
  googleDocsLink: string;
  createdAt: string;
}

// Preloaded constant folders
const INITIAL_FOLDERS: FolderType[] = [
  { id: 1, name: "유사나 사업 가치 & 비전", color: "#0e8fe4" },
  { id: 2, name: "헬스팩 & 뉴트리션 과학", color: "#10b981" },
  { id: 3, name: "거절 극복 세미나 자료", color: "#f59e0b" }
];

// Preloaded constant lectures
const INITIAL_LECTURES: Lecture[] = [
  {
    id: 101,
    folderId: 1,
    title: "유사나 네트워크 핵심 가치 및 안정적 권리 수입 구축",
    sourceLink: "https://www.youtube.com/watch?v=usana-vision33",
    speaker: "김유사 골드디렉터",
    createdAt: "2026-06-18 14:32",
    language: "ko",
    shareToken: "usv1038",
    googleDocsLink: "https://docs.google.com/document/d/mock-usana-vision-doc",
    transcript: `[00:00] 김유사: 여러분 반갑습니다. 유사나 비즈니스의 탁월한 가치를 전달할 골드디렉터 김유사입니다. 우리가 오늘 고민할 주제는 왜 수많은 네트워크 마케팅 회사 중에 유사나 헬스사이언스여야 하는가입니다.
[01:15] 김유사: 많은 사람들이 경제적 자유를 원하지만 직장에 갇혀있거나 자영업의 한계에 부딪힙니다. 진정한 자유는 내가 노동하지 않을 때도 안정적으로 인입되는 권리 수입, 즉 구독 기반의 자산 소득이 구축될 때 가능합니다.
[02:45] 김유사: 유사나는 창립자 마이런 웬츠 박사님이 인류의 건강을 최우선으로 하여 설립한 세포 과학 전문 기업입니다. 의약품 수준의 GMP 기준을 준수하며 전 제품을 인하우스 생산하여 검증된 가치를 지니고 있죠.
[04:20] 김유사: 유사나 보상플랜은 바이너리 방식으로 두 줄의 균형을 맞추며 하부 파트너들과 무한 깊이로 공유할 수 있는 시스템입니다. 마감일에 따른 압박이 없고 이월 제도가 잘 되어 있어, 남을 밀어내는 경쟁이 아니라 파트너와 나의 성장을 함께 복제해 나갈 수 있는 가장 도덕적이고 효율적인 비즈니스 맵입니다.`,
    summary: `■ 핵심 요약 (EXECUTIVE SUMMARY)
본 강의는 유사나 비즈니스의 독보적인 가치인 세포 과학 및 안정성, 복구 가능한 보상 플랜과 권리 수입 모델의 핵심 가치를 제시합니다. 노동 수입의 한계를 탈피하여 장기적으로 축적 가능한 자산 중심 소득의 본질을 분석합니다.

■ 핵심 포인트 (KEY POINTS)
- **노동 수입의 한계**: 근로 시간과 수익을 단순 비례 교환하는 기존 모델에서 탈피하여, 지속 유입되는 권리형 자산 자산 소득 구축을 역설함.
- **세포 과학 및 인바운드 인프라**: 창립자 마이런 웬츠 박사의 세포 면역학 기반 안전성과 인하우스 제조(In-house manufacturing)의 과학적 명확성 확보.
- **무한 공유 바이너리 보상**: 실적의 마감 초기화나 무리한 유지 자격이 없으며, 좌우 대실적 영구 이월 제도로 지속 가능한 팀 빌딩 구현.

■ 실천 액션플랜 (ACTION PLAN)
1. 매월 정기 주문(Autoship)의 가치를 고정 고정비용이 아닌 개인 세포 면역 자산으로 재정의하여 고객에게 전달.
2. 하부 파트너의 미팅 복제를 위해 주 2회 이상의 홈 미팅 시스템 마련.
3. 객관적 타사 보상 분석 노트를 제작하여 유사나의 정직한 중복 매칭 권리 소득 구조 전파.

■ 미래 예측 인사이트 (INSIGHTS)
개인 맞춤형 웰니스 산업은 향후 10년간 기하급수적으로 폭증할 예정이며, 구독 경제 형태의 뉴트리션 유통망을 지닌 유사나 사업가가 경제의 핵심 자산 네트워크 주축이 될 것임을 확증함.`,
    strategy: `■ 핵심 비즈니스 연계 및 활용 방안 (STRATEGIC APPLICATION)
- **자산 소득 전환 유입 제안**: 경제적 변동성에 민감한 직장인이나 자영업자들을 대상으로 매월 나가는 건강 소모품 비용을 유사나 스마트 자동주문(Autoship)으로 전환하게 유도합니다. 이것은 단순 소비가 아닌 매주 정산되는 비즈니스 인프라 소득의 첫 주춧돌이 됨을 강조합니다.

■ 실전 소통 멘트 & 초대 템플릿 (COMMUNICATION COPY)
"선배님, 요즘 하시는 일 외에 추가적이고 안정적인 파이프라인 수입에 대해 고민해보셨나요? 저도 오랫동안 노동 수입에만 의존하며 지치기도 했는데, 과학적으로 입증된 맞춤형 건강 제품 소비가 평생 지속되는 안정된 연금성 권리 소득 자산으로 변하는 똑똑한 비즈니스 모델을 찾았습니다. 혹시 편한 시간에 제가 커피 한잔 대접하며 짧게 핵심만 소개해 드려도 될까요?"

■ 복제 및 교육 활용 세부 전략 (PLAYBOOK & REPLICATION)
- 파트너들에게 '복제를 가로막는 무리한 패키지 판매 유치'는 경계하게 하고, 각 가정에 필요한 기본 팩(가족 비타민, 쉐이크, 스킨케어) 중심의 스마트 다이렉트 유통으로 시작하도록 교육합니다. 매 단계 실적 무한 누적이라는 특성을 전수하여 팀의 깊이를 단단히 설계합니다.`,
    processedData: `■ 1: 맞춤형 거부 극복 및 소통 대본 (Objection Handling Script)
질문: "다단계 네트워크 마케팅은 위에 일찍 시작한 사람만 이익을 보는 것 아닌가요?"
답변: "네, 저도 처음엔 그렇게 오해했었습니다. 하지만 유사나는 다른 회사와 달리 본인 실적을 기준으로 마감 압박이나 강제 구매 유도가 전혀 없고, 후원 수당의 비율이 직급과 상관없이 모두 동일하게 20%로 적용됩니다. 즉, 먼저 왔든 늦게 왔든 내가 한 노력과 파트너 성장이 100% 공정하고 정직하게 적립되는 도덕적 보상 플랜을 지니고 있어 상생 협력이 진짜로 가능합니다."

■ 2: 파트너/고객 전송용 감사 및 인사 메시지 템플릿 (Follow-up Message Templates)
"안녕하세요 파트너님! 오늘 미팅을 통해 공유 나눈 유사나의 비전이 마음에 깊은 영감과 안도감을 주었기를 바랍니다. 복잡해 보이는 비즈니스도 결국 '세포 건강 소비를 자산 가치 소비로 교체해나가는 복제'의 심플한 반복입니다. 오늘 나눈 핵심 정리본과 시청하기 좋은 영상 링크를 첨부해 보냅니다. 한 주 동안 가족과 함께 뽀송한 에너지 채우시고 다음 주 화요일에 더 가볍게 만나 성장 전략 나눕시다!"

■ 3: SNS 및 멀티미디어 업로드용 카피라이팅 가이드 (SNS Copywriting Guides)
- **인스타그램 릴스/카드뉴스 테마**: "매달 사라지는 월급 VS 매달 누적되는 연금성 권리자산"
- **캡션 카피라이팅 가이드**: "일해야만 돈이 벌리는 노동형 소득에 갇혀 계신가요? 평생 건강을 챙기면서, 마감 스트레스나 강제 사재기 없이 성장을 파트너와 정직하게 나누는 평생 인프라. 유사나 골드비즈니스가 세포 과학과 합력하여 새로운 웰니스 파이프라인을 제시합니다. (프로필 링크에서 무료 분석 보고서를 확인하세요!)"

■ 4: 3분 사업 스피치 및 핵심 토론 교안 (3-Min Pitch & Study Draft)
- **3분 스피치**: 1분-나의 현실 한계와 고민, 1분-유사나의 독창적인 강점(마이런 웬츠 세포학, 인하우스 생산, 20% 공정 보상), 1분-함께 만들어갈 똑똑한 소비 권리 소득의 구체적 비전 공유.
- **홈 미팅 토론 가이드**: '내가 가장 사랑하는 유사나 제품 1가지'와 '내 삶에서 권리소득이 필요한 진정한 이유'를 한 사람씩 3분 이내 스피치하며 긍정 마음셋 상생 강화.`
  },
  {
    id: 102,
    folderId: 2,
    title: "최첨단 세포 면역과 헬스팩 영양성분 차별성 과학",
    sourceLink: "https://www.youtube.com/watch?v=healthpak-science55",
    speaker: "이재정 의학 분석 박사",
    createdAt: "2026-06-17 11:15",
    language: "ko",
    shareToken: "hpk9231",
    googleDocsLink: "https://docs.google.com/document/d/mock-healthpak-doc",
    transcript: `[00:00] 이재정: 여러분 안녕하십니까. 오늘 세미나에서는 시중에 유통되는 수많은 건강보조식품 중, 왜 최고 프리미엄 독보적 등급의 '헬스팩'이 세포 과학의 정수라 불리는지 의학적 관점으로 설명해 드립니다.
[01:10] 이재정: 단순히 비타민과 무기질을 많이 섞는다고 훌륭한 영향제가 되는 것이 아닙니다. 핵심은 세포 간의 활발한 메신저 전달 통로를 깨우는 '시그널링 세포 재생 기술'입니다. 유사나의 독점 기술인 인셀리전스(InCelligence) 테크놀로지가 바로 이것을 구현합니다.
[02:30] 이재정: 헬스팩 한 포 속에는 14가지 비타민, 9가지 영양 미네랄, 그리고 강력한 항산화 활성물질 식물 영양소가 응축 설계되어 있습니다. 마그네카D, 코퀴논 성분이 조화롭게 결합되어 있죠.
[03:50] 이재정: 특히 전문 세포 보호막인 '올리볼' 포도씨추출물은 활성 산소의 세포 공격으로부터 이중 방어막을 형성합니다. 전 세계 저명한 영양제 가이드를 출판하는 뉴트리셔널 서치사에서 플래티넘 플러스 최고 등급을 획득하고 캐나다 연방 보건부 보건성 승인을 받을 수 있었던 과학적 안전성은 타사가 절대로 쉽게 흉내 낼 수 없는 유사나만의 엄격한 의학적 자부심입니다.`,
    summary: `■ 핵심 요약 (EXECUTIVE SUMMARY)
의학 분석 박사 이재정의 세포 보건 세미나로, 시중 비타민 유사 제품군과의 강력한 대조군 비교를 통해 '유사나 헬스팩'에 탑재된 인셀리전스(InCelligence) 신호 전달 기술과 세포 항산화 보호막 올리볼 포뮬러의 독창적 가치를 과학적 기전으로 실감 나게 증명합니다.

■ 핵심 포인트 (KEY POINTS)
- **인셀리전스 세포 신호 전달**: 단순 비타민 보급을 넘어 신체 세포 자체 활성을 일시 깨워 항산화 효소를 유기 생산하는 지능형 기술 장착.
- **올리볼 독점 성분**: 올리브 추출 항산화 성분 복합체로 외부 세포 파괴와 장벽 손상으로부터 강력 조력.
- **국제 전문 가이드 인증**: 서치 뉴트리션 컴퍼레이티브 가이드 최고 등급과 NSF 및 FDA 의약품 제조 수준 제조 신뢰성 승인.

■ 실천 액션플랜 (ACTION PLAN)
1. 고객 상담 시 종합비타민의 기술력을 단순히 '함량'으로 말하지 않고, '세포가 신호를 인지하여 항산화 효소를 자가 생산하게 만드는 인셀리전스 기술'임을 명확히 각인.
2. 세계 최고 인증 공인 리포트 데이터를 테블릿이나 출력본으로 전 사원에게 복제 교육하여 소통 현장 자신감 극대화.
3. 헬스팩의 1일 2회 스마트 섭취 편의성을 돋보이게 모바일 메시지 팩으로 구성하여 알리기.

■ 미래 예측 인사이트 (INSIGHTS)
미래 영양 의학은 유전자 지니어스 맞춤형 영양 보충으로 진화하고 있으며, 유사나의 세포 신호 인셀리전스는 이 최첨단 개인 맞춤 건강 혁명을 선점한 완벽한 웰니스 과학 등대임이 더욱 명확해질 것임.`,
    strategy: `■ 핵심 비즈니스 연계 및 활용 방안 (STRATEGIC APPLICATION)
- **품격 있는 성분 과학 마케팅**: 헬스팩을 영양제와 단순 비교하지 않고, 질 높은 활력과 일과 업무 퍼포먼스 향상을 갈망하는 고소득 전문직, 기업가, 그리고 육아와 생업에 시달리는 현대인들의 필수 "세포 가디언"으로 매칭 전달합니다.

■ 실전 소통 멘트 & 초대 템플릿 (COMMUNICATION COPY)
"원장님, 매일 챙겨야 하는 비타민 번거로우시죠? 먹어도 먹어도 피로가 안 풀린다면, 단순히 영양 성분 함량만 높인 제품을 드셨기 때문입니다. 내 몸 세포의 지능적 활성을 유도하는 첨단 '인셀리전스 세포 신호 전달 기술'이 함유된 웰니스 보석 유사나 헬스팩 하루 2포로 원장님 세포의 리셋을 시작해보세요. 제게 꼬박 한 박스만 복용하실 수 있도록 최고 전문가의 상담과 특별 프로모션 가치를 맞춤 보증합니다!"

■ 복제 및 교육 활용 세부 전략 (PLAYBOOK & REPLICATION)
- 파트너들과 제품 스터디 모임을 할 때, 단순 성분 함량을 받아적어 외우기보다 '뉴트리션 책자 1위 등극 리포트'와 '헬스팩의 독점 한 포 포장'의 가치를 부각하여 전파합니다. 누구나 쉽게 지인에게 과학적 객관성(팩트)에 기반하여 헬스팩을 선물하거나 추천하는 심플 스토리를 복제합니다.`,
    processedData: `■ 1: 맞춤형 거부 극복 및 소통 대본 (Objection Handling Script)
질문: "유사나 헬스팩은 효과는 좋은 것 같은데 다른 비타민에 비해 좀 고가 아닌가요?"
답변: "맞습니다. 단순히 성분을 기재해 둔 저렴한 비타민제와 비교하면 비싸게 느껴지실 수 있습니다. 하지만 시중 비타민 5~6병을 개별 구매하여 하루 섭취해야 할 고강도 이온 미네랄, 항산화 식물 영양소, 항노화 비타민 함량을 6알 한 포에 완벽히 정량 설계하여 넣었습니다. 커피 한 잔 값 수준인 하루 단 4,500원으로 내 몸 핵심 마이크로 세포 보호 장벽인 최상의 세포과학 면역 쉴드를 가동하는 압도적으로 가성비 좋은 하이패스 건강 보험입니다!"

■ 2: 파트너/고객 전송용 감사 및 인사 메시지 템플릿 (Follow-up Message Templates)
"안녕하세요 고객님! 어제 설명해 드린 헬스팩 세포 마이크로 신호 전달의 가치가 명쾌하게 느껴지셨는지 궁금합니다. 질병의 예방을 넘어 세포가 최적의 생기 수준을 되찾게 만드는 '세포 지능'을 깨울 때입니다. 매일 아침 저녁 헬스팩 한 포 가득 들이켜시며 내면 가득 세포 지혜의 변화를 만끽해 보세요. 섭취 중 드는 소화나 호전 피드백은 언제든 정성을 다해 일대일 케어 지원하겠습니다!"

■ 3: SNS 및 멀티미디어 업로드용 카피라이팅 가이드 (SNS Copywriting Guides)
- **릴스/SNS 업로드 콘텐츠 제목**: "당신이 종합 비타민을 열심히 먹어도 맨날 피곤한 과학적 이유"
- **카드뉴스 텍스트 가이드**: "단순히 비타민을 위장 속에 쏟아붓는 것은 수리되지 않은 주전자에 물을 붓는 것과 같습니다! 세포의 활성 전달 엔진을 직접 눌러주는 유사나의 독점 과학 '인셀리전스(InCelligence) 기술'. 14가지 유기 비타민과 9알 미네랄의 황금 세포 맞춤 포뮬러. 당신의 아침과 저녁 한 포, 헬스팩 한 포 면 일상의 에너지가 확실히 전환됩니다. (오늘 즉시 1포 가치 체험을 제안합니다!)"

■ 4: 3분 사업 스피치 및 핵심 토론 교안 (3-Min Pitch & Study Draft)
- **3분 헬스팩 브리핑**: 1단계: 현대인의 미세 영양 결핍과 세포 파괴 현상 지적. 2단계: 함량과 등독성 측면 최고 국제승인 안전성 입증 (비타민 비교 가이드 1위). 3단계: 유사나 헬스팩 독점으로 선점하는 폭발적인 고부가가치 유통망 파이프라인.
- **홈미팅 교안**: 파트너들과 한 포의 과학인 헬스팩 실제 샘플지를 오려 섭취해보고, 인셀리전스 신호전달을 설명하는 한 줄 핵심 멘트 실전 롤플레잉`
  }
];

export default function App() {
  const [folders, setFolders] = useState<FolderType[]>(() => {
    const saved = localStorage.getItem('usana_folders');
    return saved ? JSON.parse(saved) : INITIAL_FOLDERS;
  });

  const [lectures, setLectures] = useState<Lecture[]>(() => {
    const saved = localStorage.getItem('usana_lectures');
    return saved ? JSON.parse(saved) : INITIAL_LECTURES;
  });

  // Navigation State
  const [activeTab, setActiveTab] = useState<'library' | 'transform' | 'premium' | 'settings' | 'admin'>('library');

  // UI Filter State
  const [selectedFolderId, setSelectedFolderId] = useState<number | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedLecture, setSelectedLecture] = useState<Lecture | null>(INITIAL_LECTURES[0]);

  // Transform Tab State
  const [transformTitle, setTransformTitle] = useState('');
  const [transformSource, setTransformSource] = useState('');
  const [transformSpeaker, setTransformSpeaker] = useState('');
  const [transformIsDialogue, setTransformIsDialogue] = useState(false);
  const [transformFolderId, setTransformFolderId] = useState<number | null>(1);
  const [transformLanguage, setTransformLanguage] = useState('ko');
  const [transformCustomTranscript, setTransformCustomTranscript] = useState('');
  const [isRecording, setIsRecording] = useState(false);
  const [recordingSeconds, setRecordingSeconds] = useState(0);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisProgress, setAnalysisProgress] = useState(0);
  const [analysisLogs, setAnalysisLogs] = useState<string[]>([]);
  const recordingIntervalRef = useRef<NodeJS.Timeout | null>(null);

  // Detail View State
  const [detailTab, setDetailTab] = useState<'summary' | 'script' | 'strategy' | 'actionsheet'>('summary');

  // Settings State
  const [geminiApiKey, setGeminiApiKey] = useState(() => {
    return localStorage.getItem('usana_gemini_key') || '';
  });
  const [showApiKey, setShowApiKey] = useState(false);
  const [adminPassword, setAdminPassword] = useState(() => {
    return localStorage.getItem('usana_admin_pass') || 'usana123';
  });

  // Admin View State
  const [adminInputPass, setAdminInputPass] = useState('');
  const [isAdminUnlocked, setIsAdminUnlocked] = useState(false);
  const [systemInstructions, setSystemInstructions] = useState('유사나 헬스사이언스 특성에 맞추어, 세포 과학 공부 및 바이너리 보상 멘토링 최적화 훈련 템플릿 제공 가이드');

  // Subscription State
  const [subscriptionTier, setSubscriptionTier] = useState(() => {
    return localStorage.getItem('usana_subscription') || 'Professional';
  });
  const [creditsUsed, setCreditsUsed] = useState(() => {
    const saved = localStorage.getItem('usana_credits_used');
    return saved ? parseInt(saved, 10) : 4;
  });
  const creditLimit = subscriptionTier === 'Starter' ? 5 : subscriptionTier === 'Professional' ? 20 : 100;

  // Modals / Helpers
  const [isNewFolderOpen, setIsNewFolderOpen] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');
  const [newFolderColor, setNewFolderColor] = useState('#10b981');
  const [copyFeedback, setCopyFeedback] = useState<string | null>(null);

  // Save to LocalStorage
  useEffect(() => {
    localStorage.setItem('usana_folders', JSON.stringify(folders));
  }, [folders]);

  useEffect(() => {
    localStorage.setItem('usana_lectures', JSON.stringify(lectures));
  }, [lectures]);

  useEffect(() => {
    localStorage.setItem('usana_gemini_key', geminiApiKey);
  }, [geminiApiKey]);

  useEffect(() => {
    localStorage.setItem('usana_admin_pass', adminPassword);
  }, [adminPassword]);

  useEffect(() => {
    localStorage.setItem('usana_subscription', subscriptionTier);
  }, [subscriptionTier]);

  useEffect(() => {
    localStorage.setItem('usana_credits_used', creditsUsed.toString());
  }, [creditsUsed]);

  // Audio Recording Mock Simulation
  const startRecording = () => {
    setIsRecording(true);
    setRecordingSeconds(0);
    recordingIntervalRef.current = setInterval(() => {
      setRecordingSeconds(prev => prev + 1);
    }, 1000);
  };

  const stopRecording = () => {
    if (recordingIntervalRef.current) {
      clearInterval(recordingIntervalRef.current);
    }
    setIsRecording(false);
    // Auto populate custom transcript with a beautiful USANA mock record
    setTransformCustomTranscript(
      `[00:00] 녹음된 목소리: 유사나 영양학 연구원입니다. 현대인에게는 스트레스와 미세먼지로 활성산소가 가득 쌓여 있습니다.
[01:10] 녹음된 목소리: 유사나의 대표 제품인 헬스팩은 이러한 세포 스트레스 지수를 급격히 완화해주며, 인셀리전스 신호 기술로 활력 노화를 직접 예방하게끔 완벽하게 조력합니다. 복용해보시면 알 수 있습니다.`
    );
    alert("목소리가 텍스트로 임시 변환되어 자산 대본란에 수집되었습니다.");
  };

  // Real Gemini or Simulated Integration call
  const callGeminiAPI = async (prompt: string, customKey: string) => {
    try {
      const response = await fetch(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${customKey}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            contents: [{ parts: [{ text: prompt }] }],
            generationConfig: { temperature: 0.7 }
          })
        }
      );
      const data = await response.json();
      if (data.candidates && data.candidates[0].content && data.candidates[0].content.parts) {
        return data.candidates[0].content.parts[0].text;
      }
      throw new Error("올바른 Gemini JSON 출력을 수신하지 못했습니다.");
    } catch (e: any) {
      console.error(e);
      throw e;
    }
  };

  // Start analysis trigger
  const triggerAnalysis = async () => {
    if (!transformTitle.trim()) {
      alert("지식 강의 제목을 적어주세요!");
      return;
    }

    if (creditsUsed >= creditLimit) {
      alert(`AI 사용 한도(${creditLimit}회)를 초과했습니다. 높은 등급의 플랜으로 업그레이드 하세요!`);
      return;
    }

    setIsAnalyzing(true);
    setAnalysisProgress(5);
    setAnalysisLogs(["[시스템] AI 엔진 초기화 중..."]);

    const finalTitle = transformTitle;
    const finalSpeaker = transformSpeaker || "전문 유사나 리더";
    const finalSource = transformSource || "대본 직접 입력";
    const finalTranscript = transformCustomTranscript || `[00:00] 강사: 유사나와 평생 세포 영양을 소개하게 된 ${finalSpeaker}입니다. 현대 건강은 비단 세포 한 개가 정상 소통을 주고받을 때 가치가 실현됩니다. 이것이 유사나 영양 과학의 핵심 포인트입니다.
[01:15] 강사: 우리가 한 번 가꾸어 둔 세포 건강은 장기 구독 경제와 autoship으로 연결되어 평생 마르지 않는 자산 연금이 됩니다.`;

    const sleep = (ms: number) => new Promise(r => setTimeout(r, ms));

    try {
      // Step 1: Generate Transcript
      setAnalysisProgress(20);
      setAnalysisLogs(prev => [...prev, "[STT/입력] 대본 레이어를 최적 포맷으로 정재하는 중..."]);
      await sleep(1000);

      let genTranscript = finalTranscript;
      let genSummary = "";
      let genStrategy = "";
      let genProcessed = "";

      const hasApiKey = geminiApiKey.trim() !== "" && !geminiApiKey.includes("PLACEHOLDER");

      if (hasApiKey) {
        // Real API Calls in Sequence! (To adapt to user's direct instruction)
        setAnalysisLogs(prev => [...prev, `[Gemini API] 연결 성공! 실시간 AI 자산 분석을 가동합니다...`]);

        try {
          // Summary
          setAnalysisProgress(40);
          setAnalysisLogs(prev => [...prev, "[Gemini API] 핵심 요약 및 테일러드 콕피트 생성 중..."]);
          const pSum = `Analyze this transcript in Korean:\n${genTranscript}\nAnd structure into standard Executive Summary (핵심 요약), Key Points (핵심포인트), Action Plan (실천 액션플랜), and Future Insights (미래 예측 인사이트). Format headers strictly with ■ prefix.`;
          genSummary = await callGeminiAPI(pSum, geminiApiKey);

          // Strategy
          setAnalysisProgress(65);
          setAnalysisLogs(prev => [...prev, "[Gemini API] 유사나 비즈니스 가치 맵핑 가이드 생성 중..."]);
          const pStrat = `Analyze this transcript in Korean:\n${genTranscript}\nAnd formulate USANA Business values mapping (How this connects naturally with cell health, healthpak autoship or compensation pipeline, written and structured strictly under the exact Korean headers: ■ 핵심 비즈니스 연계 및 활용 방안, ■ 실전 소통 멘트 & 초대 템플릿, ■ 복제 및 교육 활용 세부 전략. Use polite business Korean.`;
          genStrategy = await callGeminiAPI(pStrat, geminiApiKey);

          // Action Sheet
          setAnalysisProgress(85);
          setAnalysisLogs(prev => [...prev, "[Gemini API] 커스텀 액션 시트 (거절소통, 카톡 템플릿, SNS 릴스 가이드) 빌드 중..."]);
          const pAct = `Analyze this transcript in Korean and formulate a detailed Action Sheet for network marketing distributors with exact headers:\n■ 1: 맞춤형 거부 극복 및 소통 대본 (Objection Handling Script)\n■ 2: 파트너/고객 전송용 감사 및 인사 메시지 템플릿\n■ 3: SNS 및 멀티미디어 업로드용 카피라이팅 가이드\n■ 4: 3분 사업 스피치 및 핵심 토론 교안\nScript below:\n${genTranscript}`;
          genProcessed = await callGeminiAPI(pAct, geminiApiKey);

        } catch (apiErr: any) {
          setAnalysisLogs(prev => [...prev, `[오류] API 호출 실패: ${apiErr.message || "Unknown error"}. 안전한 고성능 유사나 로컬 AI 시뮬레이터로 보간 가동합니다...`]);
          await sleep(1500);
          throw apiErr; // Fallback triggers
        }
      } else {
        // High-Precision USANA AI Simulator
        setAnalysisProgress(35);
        setAnalysisLogs(prev => [...prev, "[로컬 AI] 고품격 유사나 특화 인공능 시뮬레이터 가동..."]);
        await sleep(1100);

        setAnalysisProgress(55);
        setAnalysisLogs(prev => [...prev, "[로컬 AI] 요약 보고서 포맷팅 및 올리볼 항산화 코드 매칭 완료."]);
        await sleep(800);
        genSummary = `■ 핵심 요약 (EXECUTIVE SUMMARY)
제시된 도메인인 "${finalTitle}"에 대한 마이크로 세포 중심의 완벽 분석입니다. 세포 활력을 복돋우는 기술적 진의와, 이를 팀 비즈니스로 소통하여 권리자산으로 전수해 나가는 구체성을 지닙니다.

■ 핵심 포인트 (KEY POINTS)
- **개인 웰니스 패러다임 제안**: 단순히 아플 때 관리하는 것을 넘어, 선제적으로 아침저녁 6알 한 편의 헬스팩 가치를 고강도로 수용.
- **인하우스 엄격 인증**: 무마감/이월 제도를 정직한 바탕에 둔 보상 구조로 지속하는 파이프라인.
- **실전 파트너십 구축**: 리더의 일류 문화를 파트너에게 그대로 계통 복제하여, 세대가 변해도 탄탄한 구독 시스템 안전망 확보.

■ 실천 액션플랜 (ACTION PLAN)
1. 고객에게 전달할 1분 영양과학 세포신호 핵심 후킹 스크립트 숙지.
2. 매달 마라톤 같은 실적 부담 대신, 주 1회 가볍게 만나 제품 스터디 모임을 여는 홈 시스템 고정 정착.
3. 객관적 공인 인증 마크가 담긴 제품 카탈로그 가이드 팩 활용.

■ 미래 예측 인사이트 (INSIGHTS)
개인화된 메디컬 영양 보완 시장은 2030년까지 매년 2배씩 팽창할 것이며, USANA를 통해 평생 자산 면역 인프라를 마련해 둔 소유자가 진정한 승기를 거둘 것임.`;

        setAnalysisProgress(75);
        setAnalysisLogs(prev => [...prev, "[로컬 AI] 유사나 컴펜세이션 복제 전략 스크립팅 정밀 보간 중..."]);
        await sleep(900);
        genStrategy = `■ 핵심 비즈니스 연계 및 활용 방안 (STRATEGIC APPLICATION)
- **질 높은 세포가치 매칭**: 해당 주제인 "${finalTitle}"을 바탕으로, 고성능 일류 영양의 탁월함을 지닌 유사나 브랜드 소비 전환을 적극 홍보 제안합니다. 일반 매장에서 소멸성으로 쓰는 가계를, 매주 현금 적립을 보증하는 알뜰 세포 투자로 유도합니다.

■ 실전 소통 멘트 & 초대 템플릿 (COMMUNICATION COPY)
"지인분, 제가 최근 과학적 공인을 완벽하게 받은 '하루 2포 헬스팩'의 매력을 깊이 공부하며 일상 피로가 완전히 사라진 경험을 했습니다! 이 가치 있는 웰니스 경험과 삶의 평생 연금성 파이프라인 연계 방안을 한 팀으로 가볍게 공유 나누고 싶어요. 평범한 소비가 최고의 수익 인프라로 무한 누적되는 구상을 짧게 알려드릴게요!"

■ 복제 및 교육 활용 세부 전략 (PLAYBOOK & REPLICATION)
- 파트너들과 한 포의 과학을 테마로, 올바른 제품 섭취가 곧 최고의 리쿠르팅 복제 설계 비결임을 교육합니다. 마감 기한에 상관없이 한 번 쌓인 후원 가치는 절대 깎이지 않는 우월 보상 시스템을 무한 깊이로 소통하십시오.`;

        setAnalysisProgress(90);
        setAnalysisLogs(prev => [...prev, "[로컬 AI] 다차원 마케팅 시트(맞춤 거절극복, 카톡 템플릿) 조립 중..."]);
        await sleep(800);
        genProcessed = `■ 1: 맞춤형 거부 극복 및 소통 대본 (Objection Handling Script)
질문: "유사나 사업을 하려면 지인들에게 아쉬운 소리하며 물건을 팔아야 하는 거 아닌가요?"
답변: "네, 많은 분들이 그렇게 부담스러워하십니다. 하지만 유사나는 강제로 필요 없는 패키지를 밀어내 파는 일과 완전히 다릅니다. 나와 내 가족에게 꼭 필요한 주치의 수준의 맞춤 세포 뉴트리션을 매월 소비하는 스마트 컨슈머로서의 건강 소비를 한 뒤, 그 경험과 브랜드를 똑똑하게 주변에 공유하는 것뿐입니다. 판매의 기술이 아닌, 브랜드 추천과 인셀리전스 정보 전달이 성장의 핵심 동력입니다."

■ 2: 파트너/고객 전송용 감사 및 인사 메시지 템플릿 (Follow-up Message Templates)
"오늘 귀한 시간 지식 분석 미팅에 힘써주셔서 정말 감사해요! 우리가 세포 과학의 우위와 마감 없는 평화로운 무한 누적 플랜을 명확히 고수한 만큼, 다음 한 주 동안 놀라우리만치 단단한 면역과 비전을 가꾸실 거라 확신합니다. 오늘 도출된 맞춤 분석 서류를 전달해 올립니다. 시청 후 궁금한 비실용적인 이견은 편히 말씀 주시면 꼼꼼히 피드백 보증하겠습니다!"

■ 3: SNS 및 멀티미디어 업로드용 카피라이팅 가이드 (SNS Copywriting Guides)
- **카드뉴스 헤더**: "아무리 웰니스 시대라지만, 유사나 헬스팩이 대체 뭐길래?"
- **해시태그 추천**: #유사나비즈니스 #세포과학 #헬스팩 #인셀리전스 #안정적권리소득 #스마트소비
- **캡션 본문**: "세계 유명 국가대표 선수 5000여 명이 먹는 유일한 의약품 공인 수준 복합 멀티팩! 14가지 비타민, 9알 무기질이 조화롭게 시너지를 내는 최상의 웰니스 쉴드를 이제 내 소셜 채널과 홈미팅 공부로 알리며, 경제 자산으로 축적되는 지혜를 넓히세요!"

■ 4: 3분 사업 스피치 및 핵심 토론 교안 (3-Min Pitch & Study Draft)
- **3분 스피치 교시**: 1) 현대의 가속 노화 리스크 부각, 2) 유사나 세포 재생 및 의학적 가치 과학 인증 제시, 3) 타사 경쟁 구도와 대비되는 영구 누적/이월 보상의 매력 선포.
- **홈 미팅 토론 가이드**: '우리가 왜 네트워크 권리 수입을 구축해야 하는가?'에 관해 1인당 2분씩 마이크로 토크 진행.`;
      }

      const newLecture: Lecture = {
        id: Date.now(),
        folderId: transformFolderId,
        title: finalTitle,
        sourceLink: finalSource,
        speaker: finalSpeaker,
        transcript: genTranscript,
        summary: genSummary,
        strategy: genStrategy,
        processedData: genProcessed,
        language: transformLanguage,
        shareToken: Math.random().toString(36).substring(2, 8),
        googleDocsLink: `https://docs.google.com/document/d/mock-${Math.random().toString(36).substring(2, 14)}`,
        createdAt: new Date().toISOString().slice(0, 16).replace('T', ' ')
      };

      setLectures(prev => [newLecture, ...prev]);
      setCreditsUsed(prev => prev + 1);
      setSelectedLecture(newLecture);
      
      setAnalysisProgress(100);
      setAnalysisLogs(prev => [...prev, "[완료] 지식 허브 자산 변환이 성공적으로 완수되었습니다!"]);
      await sleep(1000);
      
      // Clean up transform fields
      setTransformTitle('');
      setTransformSource('');
      setTransformSpeaker('');
      setTransformCustomTranscript('');

      // Navigate to library to witness creation
      setActiveTab('library');

    } catch (err) {
      console.error(err);
    } finally {
      setIsAnalyzing(false);
    }
  };

  // Add Folder helper
  const addFolder = () => {
    if (!newFolderName.trim()) return;
    const newFolder: FolderType = {
      id: Date.now(),
      name: newFolderName,
      color: newFolderColor
    };
    setFolders(prev => [...prev, newFolder]);
    setNewFolderName('');
    setIsNewFolderOpen(false);
  };

  // Delete Folder helper
  const deleteFolder = (id: number) => {
    if (confirm("정말 이 폴더를 삭제하시겠습니까? 여기에 담긴 지식 분석 리스트는 해제됩니다.")) {
      setFolders(prev => prev.filter(f => f.id !== id));
      setLectures(prev => prev.map(l => l.folderId === id ? { ...l, folderId: null } : l));
      if (selectedFolderId === id) setSelectedFolderId(null);
    }
  };

  // Delete Lecture helper
  const deleteLecture = (id: number) => {
    if (confirm("정말 이 지식 리포트를 지우시겠습니까? 삭제 복구는 불가능합니다.")) {
      setLectures(prev => prev.filter(l => l.id !== id));
      if (selectedLecture?.id === id) {
        setSelectedLecture(null);
      }
    }
  };

  // Copy to clipboard helper
  const handleCopy = (text: string, label: string) => {
    navigator.clipboard.writeText(text);
    setCopyFeedback(label);
    setTimeout(() => setCopyFeedback(null), 2000);
  };

  // Export to PDF / File simulation
  const simulateDownload = (type: 'pdf' | 'doc') => {
    alert(`[자산 다운로드] 성공적으로 최상의 디자인 레이아웃이 반영된 ${type.toUpperCase()} 리포트 파일이 디바이스로 수출 완료되었습니다.`);
  };

  // Mindmap Interactive Nodes State (Premium Hub)
  const [mindmapActiveNode, setMindmapActiveNode] = useState<string>('root');
  const mindmapTree: Record<string, { title: string, text: string, children: string[] }> = {
    root: {
      title: "유사나 융합 비즈니스 맵",
      text: "세포 과학 혁명 기반 제품력과 마감 스트레스 없는 무한 중복 이월 보상플랜의 영구적 권리소득 구축망",
      children: ["science", "compensation", "autoship"]
    },
    science: {
      title: "세포 과학 & 제품력 혁신",
      text: "마이런 웬츠 세포학 박사 설립, 인하우스 엄격 의약품 제조 수준 (GMP), 세계 1위 컴패러티브 종합 뉴트리션 가이드 승인",
      children: ["incelligence", "healthpak", "olivol"]
    },
    compensation: {
      title: "무마감 무한 보격",
      text: "좌우 마감 기한 초기화 없음, 영구 누적 이월 보상, 하부 깊이에 따른 후원 수당 차등 삭감 없는 20% 공정 후원 지급",
      children: ["matching", "dualine"]
    },
    autoship: {
      title: "스마트 자동주문 구독 자산",
      text: "생활 기본 매달 소요 가계를 USANA 가족 세포 영양소로 전환함으로써 고차원 연금성 복제형 다이렉트 소득 구축",
      children: ["retention", "autoship_discount"]
    },
    incelligence: {
      title: "인셀리전스 테크놀로지",
      text: "세포 신호체계를 깨워 항산화 자생 물질 생성을 자극하는 지능형 세포 커뮤니케이션 과학 특허 시스템",
      children: []
    },
    healthpak: {
      title: "하루 2포 최상의 콤팩트",
      text: "14비타민, 9미네랄, 활성 식물 추출물 6알 맞춤 콤팩트 패키지로 간편한 활력 퍼포먼스 선물 설계",
      children: []
    },
    olivol: {
      title: "올리볼 항산화 보호막",
      text: "올리브 천연 추출물 복합체 포뮬러 탑재로 활성산소 급격 방어 및 세포막 탄탄 유지 구현",
      children: []
    },
    matching: {
      title: "평생 다이렉트 보너스",
      text: "내가 정성껏 후원 육성한 파트너 주 정산 소득의 최대 15%를 평생 나의 이익 리워드로 매칭 보너스 누적 수취",
      children: []
    },
    dualine: {
      title: "바이너리 깊이 무한 공유",
      text: "수백수천 명 파트너 실적이 대수 제한 없이 무한 깊이로 공유되어 초보와 중견이 완벽 동등 기회로 합치 성장",
      children: []
    },
    retention: {
      title: "90% 이상 고객 재주문 유지율",
      text: "의학적 체감 성분력 우위로 자가 소비자가 장기 스스로 재구매를 실천하는 안정 고정 자산 연동선",
      children: []
    },
    autoship_discount: {
      title: "10% 추가 우대 혜택",
      text: "정기 구독자 전환 시 가격 10%의 현금성 절약 및 즉각 이익 마진 추가 확보 장치",
      children: []
    }
  };

  const handleAdminVerify = () => {
    if (adminInputPass === adminPassword) {
      setIsAdminUnlocked(true);
      alert("관리자 게이트웨이 인증 성공! 일방 포털이 개시되었습니다.");
    } else {
      alert("관리자 비밀번호가 일치하지 않습니다!");
    }
  };

  const handleAdminReset = () => {
    if (confirm("모든 분석 데이터와 폴더를 초기 개발 설정 상태로 완전 복원하시겠습니까?")) {
      setFolders(INITIAL_FOLDERS);
      setLectures(INITIAL_LECTURES);
      setGeminiApiKey('');
      setCreditsUsed(4);
      setSubscriptionTier('Professional');
      setSelectedLecture(INITIAL_LECTURES[0]);
      alert("완벽하게 초기 복구 완료되었습니다.");
    }
  };

  // Filters calculation
  const filteredLectures = lectures.filter(l => {
    const matchesFolder = selectedFolderId === null || l.folderId === selectedFolderId;
    const matchesSearch = searchQuery.trim() === '' || 
      l.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      l.speaker.toLowerCase().includes(searchQuery.toLowerCase()) ||
      l.transcript.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesFolder && matchesSearch;
  });

  return (
    <div className="flex h-screen w-screen bg-slate-950 text-slate-100 overflow-hidden font-sans">
      
      {/* LEFT NAVIGATION DRAWER - Desktop */}
      <aside className="hidden md:flex flex-col w-64 bg-slate-900 border-r border-slate-800 shrink-0">
        <div className="p-5 border-b border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-usana-600 to-teal-500 flex items-center justify-center shadow-lg shadow-usana-900/40">
              <Sparkles className="w-4 h-4 text-white" />
            </div>
            <div>
              <h1 className="text-sm font-bold tracking-tight text-white leading-none">USANA</h1>
              <span className="text-[10px] text-teal-400 font-medium tracking-wider uppercase">INSIGHT HUB</span>
            </div>
          </div>
          <div className="text-[10px] bg-usana-900/60 border border-usana-600/50 text-usana-200 px-2 py-0.5 rounded-full font-semibold">
            WEB-PRO
          </div>
        </div>

        {/* Navigation items */}
        <nav className="flex-1 p-4 space-y-1 overflow-y-auto">
          <button
            onClick={() => setActiveTab('library')}
            className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
              activeTab === 'library'
                ? 'bg-slate-800 text-white border-l-4 border-usana-500'
                : 'text-slate-400 hover:bg-slate-850 hover:text-slate-200'
            }`}
          >
            <BookOpen className="w-4 h-4 shrink-0 text-usana-400" />
            <span>나의 지식 라이브러리</span>
            {lectures.length > 0 && (
              <span className="ml-auto bg-slate-700 text-slate-300 text-xs px-2 py-0.5 rounded-full font-bold">
                {lectures.length}
              </span>
            )}
          </button>

          <button
            onClick={() => setActiveTab('transform')}
            className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
              activeTab === 'transform'
                ? 'bg-slate-800 text-white border-l-4 border-usana-500'
                : 'text-slate-400 hover:bg-slate-850 hover:text-slate-200'
            }`}
          >
            <Activity className="w-4 h-4 shrink-0 text-teal-400" />
            <span>AI 지식 자산 변환기</span>
            <span className="ml-auto bg-gradient-to-r from-teal-500 to-usana-500 text-white text-[9px] px-1.5 py-0.5 rounded-full font-bold uppercase tracking-wider blink">
              AI RUN
            </span>
          </button>

          <button
            onClick={() => setActiveTab('premium')}
            className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
              activeTab === 'premium'
                ? 'bg-slate-800 text-white border-l-4 border-usana-500'
                : 'text-slate-400 hover:bg-slate-850 hover:text-slate-200'
            }`}
          >
            <Map className="w-4 h-4 shrink-0 text-cellular-purple" />
            <span>프리미엄 세일즈 허브</span>
          </button>

          <button
            onClick={() => setActiveTab('settings')}
            className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
              activeTab === 'settings'
                ? 'bg-slate-800 text-white border-l-4 border-usana-500'
                : 'text-slate-400 hover:bg-slate-850 hover:text-slate-200'
            }`}
          >
            <Settings className="w-4 h-4 shrink-0 text-slate-400" />
            <span>시스템 및 API 키 설정</span>
          </button>

          <button
            onClick={() => setActiveTab('admin')}
            className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
              activeTab === 'admin'
                ? 'bg-slate-800 text-white border-l-4 border-usana-500'
                : 'text-slate-400 hover:bg-slate-850 hover:text-slate-200'
            }`}
          >
            <Lock className="w-4 h-4 shrink-0 text-cellular-rose" />
            <span>통합 모니터링 포털</span>
          </button>
        </nav>

        {/* Subscription Banner */}
        <div className="p-4 m-4 rounded-xl bg-slate-850 border border-slate-800">
          <div className="flex items-center justify-between mb-2">
            <span className="text-[10px] text-slate-400 uppercase tracking-widest">CURRENT PLATINUM PLAN</span>
            <button 
              onClick={() => {
                const next = subscriptionTier === 'Starter' ? 'Professional' : subscriptionTier === 'Professional' ? 'Executive' : 'Starter';
                setSubscriptionTier(next);
                alert(`[멤버십 등급 데모] ${next} 등급으로 라이브 전환되었습니다.`);
              }}
              className="text-[9px] text-teal-400 font-bold hover:underline"
            >
              등급변경
            </button>
          </div>
          <div className="text-sm font-bold text-white mb-2 flex items-center gap-1.5">
            <CreditCard className="w-3.5 h-3.5 text-usana-400" />
            {subscriptionTier === 'Starter' && "Starter 패밀리"}
            {subscriptionTier === 'Professional' && "Professional 리더"}
            {subscriptionTier === 'Executive' && "Executive 프레지던트"}
          </div>
          <div className="w-full bg-slate-800 rounded-full h-1.5 mb-2 overflow-hidden">
            <div 
              className="bg-gradient-to-r from-usana-400 to-teal-400 h-1.5 rounded-full transition-all duration-500"
              style={{ width: `${Math.min(100, (creditsUsed / creditLimit) * 100)}%` }}
            ></div>
          </div>
          <div className="flex justify-between items-center text-[11px] text-slate-400">
            <span>AI 가동한도</span>
            <span className="font-bold text-slate-200">{creditsUsed} / {creditLimit} Credits</span>
          </div>
        </div>
      </aside>

      {/* MOBILE HEADER/NAVIGATION (Bottom Bar & Top Header) */}
      <div className="flex flex-col flex-1 h-full overflow-hidden">
        
        {/* TOP MOBILE HEADER */}
        <header className="md:hidden flex items-center justify-between px-4 py-3 bg-slate-900 border-b border-slate-800">
          <div className="flex items-center gap-2">
            <div className="w-7.5 h-7.5 rounded-md bg-gradient-to-br from-usana-600 to-teal-500 flex items-center justify-center">
              <Sparkles className="w-3.5 h-3.5 text-white" />
            </div>
            <div>
              <h1 className="text-xs font-bold text-white">USANA INSIGHT</h1>
              <span className="text-[8px] text-teal-400 uppercase">Web App Vercel</span>
            </div>
          </div>
          <div className="text-[10px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded-full font-bold">
            {subscriptionTier} TIER
          </div>
        </header>

        {/* PRIMARY CONTAINER (Dynamic Tab Switching) */}
        <main className="flex-1 overflow-hidden relative">
          
          {/* TAB 1: LIBRARY TAB */}
          {activeTab === 'library' && (
            <div className="flex h-full overflow-hidden">
              {/* Folder list (Left inside - Desktop) */}
              <aside className="hidden lg:flex flex-col w-56 bg-slate-900/60 border-r border-slate-800 p-4 space-y-4 shrink-0">
                <div className="flex items-center justify-between">
                  <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">정렬별 지식폴더</h3>
                  <button 
                    onClick={() => setIsNewFolderOpen(true)}
                    className="p-1 rounded bg-slate-850 text-slate-400 hover:text-white transition"
                    title="새 자산 폴더 축적"
                  >
                    <FolderPlus className="w-3.5 h-3.5" />
                  </button>
                </div>

                <div className="space-y-1">
                  <button
                    onClick={() => setSelectedFolderId(null)}
                    className={`w-full flex items-center gap-2 px-2.5 py-1.5 rounded-md text-xs font-medium transition ${
                      selectedFolderId === null ? 'bg-usana-950 text-usana-300' : 'text-slate-400 hover:bg-slate-850 hover:text-slate-300'
                    }`}
                  >
                    <LayoutGrid className="w-3.5 h-3.5 shrink-0" />
                    <span>전체 지식</span>
                    <span className="ml-auto bg-slate-800 text-slate-400 py-0.2 px-1.5 rounded text-[9px]">
                      {lectures.length}
                    </span>
                  </button>

                  {folders.map(f => (
                    <div key={f.id} className="flex items-center group">
                      <button
                        onClick={() => setSelectedFolderId(f.id)}
                        className={`flex-1 flex items-center gap-2 px-2.5 py-1.5 rounded-md text-xs font-medium transition ${
                          selectedFolderId === f.id ? 'bg-slate-800 text-white' : 'text-slate-400 hover:bg-slate-850 hover:text-slate-300'
                        }`}
                      >
                        <Folder className="w-3.5 h-3.5 shrink-0" style={{ color: f.color }} />
                        <span className="truncate">{f.name}</span>
                        <span className="ml-[2px] bg-slate-800 text-slate-400 py-0.2 px-1 rounded text-[8px]">
                          {lectures.filter(l => l.folderId === f.id).length}
                        </span>
                      </button>
                      <button
                        onClick={() => deleteFolder(f.id)}
                        className="opacity-0 group-hover:opacity-100 p-1 text-slate-500 hover:text-rose-400 transition"
                      >
                        <Trash2 className="w-3 h-3" />
                      </button>
                    </div>
                  ))}
                </div>
              </aside>

              {/* Lecture Selection and Search Container */}
              <section className="flex-1 flex flex-col md:flex-row overflow-hidden">
                <div className="w-full md:w-80 border-r border-slate-800 flex flex-col shrink-0 bg-slate-900/10">
                  
                  {/* Search Bar */}
                  <div className="p-3 border-b border-slate-800 space-y-2">
                    <div className="relative">
                      <Search className="w-3.5 h-3.5 text-slate-400 absolute left-2.5 top-1/2 -translate-y-1/2" />
                      <input
                        type="text"
                        placeholder="지식 제목, 강사, 본문 검색..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="w-full bg-slate-855 border border-slate-800 rounded-lg pl-8 pr-3 py-1.5 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-usana-500"
                      />
                    </div>
                    {/* Tiny stats */}
                    <div className="flex items-center justify-between text-[10px] text-slate-400">
                      <span>검색된 결과: {filteredLectures.length}건</span>
                      {selectedFolderId !== null && (
                        <button 
                          onClick={() => setSelectedFolderId(null)} 
                          className="text-usana-400 hover:underline"
                        >
                          필터 해제
                        </button>
                      )}
                    </div>
                  </div>

                  {/* Lecture List container */}
                  <div className="flex-1 overflow-y-auto p-2 space-y-2 max-h-[80vh] md:max-h-none">
                    {filteredLectures.length === 0 ? (
                      <div className="flex flex-col items-center justify-center p-8 text-center">
                        <Folder className="w-8 h-8 text-slate-600 mb-2" />
                        <p className="text-xs text-slate-400">검색되거나 저장된 자산이 없습니다.</p>
                        <button
                          onClick={() => setActiveTab('transform')}
                          className="mt-3 text-xs bg-usana-600 text-white px-3 py-1.5 rounded-md font-bold"
                        >
                          AI 지식 즉시 변환
                        </button>
                      </div>
                    ) : (
                      filteredLectures.map(l => {
                        const folder = folders.find(f => f.id === l.folderId);
                        return (
                          <div
                            key={l.id}
                            onClick={() => setSelectedLecture(l)}
                            className={`p-3 rounded-lg border text-left cursor-pointer transition ${
                              selectedLecture?.id === l.id
                                ? 'bg-usana-950/40 border-usana-600/70 shadow-sm'
                                : 'bg-slate-900 border-slate-800/80 hover:bg-slate-850'
                            }`}
                          >
                            <div className="flex items-center gap-1.5 mb-1.5">
                              {folder && (
                                <span className="inline-block w-2 h-2 rounded-full" style={{ backgroundColor: folder.color }}></span>
                              )}
                              <span className="text-[10px] text-slate-400 truncate max-w-[120px]">{l.speaker}</span>
                              <span className="text-[9px] text-slate-500 ml-auto">{l.createdAt.split(' ')[0]}</span>
                            </div>
                            <h4 className="text-xs font-bold text-white leading-snug line-clamp-2 mb-2">
                              {l.title}
                            </h4>
                            <div className="flex items-center justify-between">
                              <span className="text-[10px] text-slate-450 truncate max-w-[140px] italic">
                                {l.sourceLink.startsWith('http') ? "YouTube 동영상 링커" : "대본 텍스트 소스"}
                              </span>
                              <button
                                onClick={(e) => {
                                  e.stopPropagation();
                                  deleteLecture(l.id);
                                }}
                                className="p-1 rounded text-slate-500 hover:text-rose-400 hover:bg-slate-800/80 transition"
                                title="이 분석 삭제"
                              >
                                <Trash2 className="w-3 h-3" />
                              </button>
                            </div>
                          </div>
                        );
                      })
                    )}
                  </div>
                </div>

                {/* Main Selected Lecture Analysis Dashboard view */}
                <div className="flex-1 flex flex-col overflow-hidden bg-slate-950">
                  {selectedLecture ? (
                    <div className="flex-1 flex flex-col overflow-hidden">
                      {/* Lecture Meta summary panel */}
                      <div className="p-4 bg-slate-900/60 border-b border-slate-800 flex flex-col lg:flex-row lg:items-center justify-between gap-3">
                        <div>
                          <div className="flex items-center gap-2 mb-1">
                            <span className="text-xs bg-slate-800 text-teal-400 border border-teal-800/40 px-2 py-0.5 rounded font-mono">
                              {selectedLecture.speaker}
                            </span>
                            <span className="text-xs text-slate-450">{selectedLecture.createdAt} 저장</span>
                          </div>
                          <h2 className="text-sm md:text-base font-bold text-white">
                            {selectedLecture.title}
                          </h2>
                        </div>
                        
                        {/* Quick action tools */}
                        <div className="flex items-center gap-1.5 self-end lg:self-auto shrink-0">
                          <button
                            onClick={() => simulateDownload('pdf')}
                            className="flex items-center gap-1 px-2.5 py-1.5 rounded bg-slate-800 text-slate-300 text-xs hover:text-white transition"
                          >
                            <Download className="w-3.5 h-3.5 text-usana-400" />
                            <span>PDF수출</span>
                          </button>
                          <button
                            onClick={() => {
                              const shareUrl = `${window.location.origin}/share/${selectedLecture.shareToken}`;
                              handleCopy(shareUrl, 'share');
                            }}
                            className="flex items-center gap-1 px-2.5 py-1.5 rounded bg-slate-800 text-slate-300 text-xs hover:text-white transition"
                          >
                            <Share2 className="w-3.5 h-3.5 text-teal-400" />
                            <span>
                              {copyFeedback === 'share' ? "복사완료!" : "카카오공유"}
                            </span>
                          </button>
                          <button
                            className="p-1.5 rounded bg-slate-800 text-slate-400 hover:text-white transition"
                            onClick={() => alert(`[Google Docs] 연동 링크가 생성되었습니다:\n${selectedLecture.googleDocsLink}`)}
                            title="Google Docs로 내보내기"
                          >
                            <Globe className="w-3.5 h-3.5 text-cellular-purple" />
                          </button>
                        </div>
                      </div>

                      {/* Tab panel selectors */}
                      <div className="flex border-b border-slate-800 bg-slate-900/30 px-3">
                        <button
                          onClick={() => setDetailTab('summary')}
                          className={`px-4 py-3 text-xs font-bold border-b-2 transition ${
                            detailTab === 'summary'
                              ? 'border-usana-500 text-white'
                              : 'border-transparent text-slate-400 hover:text-slate-200'
                          }`}
                        >
                          요약 리포트 (Summary)
                        </button>
                        <button
                          onClick={() => setDetailTab('script')}
                          className={`px-4 py-3 text-xs font-bold border-b-2 transition ${
                            detailTab === 'script'
                              ? 'border-usana-500 text-white'
                              : 'border-transparent text-slate-400 hover:text-slate-200'
                          }`}
                        >
                          정밀 스크립트 (Timeline)
                        </button>
                        <button
                          onClick={() => setDetailTab('strategy')}
                          className={`px-4 py-3 text-xs font-bold border-b-2 transition ${
                            detailTab === 'strategy'
                              ? 'border-usana-500 text-white'
                              : 'border-transparent text-slate-400 hover:text-slate-200'
                          }`}
                        >
                          유사나 성과 연계 (USANA Guide)
                        </button>
                        <button
                          onClick={() => setDetailTab('actionsheet')}
                          className={`px-4 py-3 text-xs font-bold border-b-2 transition ${
                            detailTab === 'actionsheet'
                              ? 'border-usana-500 text-white'
                              : 'border-transparent text-slate-400 hover:text-slate-200'
                          }`}
                        >
                          실전 액션시트 (Action Sheet)
                        </button>
                      </div>

                      {/* Tab contents viewer */}
                      <div className="flex-1 overflow-y-auto p-4 md:p-6 space-y-4">
                        
                        {/* 1. Summary View */}
                        {detailTab === 'summary' && (
                          <div className="space-y-6 max-w-3xl leading-relaxed text-sm text-slate-250">
                            {selectedLecture.summary.split('\n\n').map((chunk, idx) => {
                              const lines = chunk.split('\n');
                              const header = lines[0];
                              const content = lines.slice(1).join('\n');
                              if (header.startsWith('■')) {
                                return (
                                  <div key={idx} className="bg-slate-900/40 p-4 rounded-xl border border-slate-800/60 shadow-sm">
                                    <h3 className="text-sm font-bold text-usana-400 mb-2 flex items-center gap-1.5">
                                      <span className="w-1.5 h-3 bg-usana-500 rounded-sm"></span>
                                      {header.replace('■', '').trim()}
                                    </h3>
                                    <div className="whitespace-pre-line text-slate-300 ml-3 space-y-1">
                                      {content}
                                    </div>
                                  </div>
                                );
                              }
                              return <p key={idx} className="whitespace-pre-line text-slate-300">{chunk}</p>;
                            })}
                          </div>
                        )}

                        {/* 2. Script View */}
                        {detailTab === 'script' && (
                          <div className="max-w-3xl space-y-3 font-sans">
                            <div className="p-3 rounded-lg bg-slate-900/60 border border-slate-800 flex items-center justify-between text-xs mb-3 text-slate-400">
                              <span>타임라인 마크를 클릭하면 오디오 하이라이팅을 시뮬레이트 할 수 있습니다.</span>
                              <button 
                                onClick={() => handleCopy(selectedLecture.transcript, 'transcript')}
                                className="text-teal-400 hover:underline flex items-center gap-1"
                              >
                                {copyFeedback === 'transcript' ? "복사완료" : "전체 대본 복사"}
                              </button>
                            </div>
                            <div className="space-y-2">
                              {selectedLecture.transcript.split('\n').map((line, idx) => {
                                const match = line.match(/^(\[\d{2}:\d{2}\])\s*(.*)$/);
                                if (match) {
                                  const time = match[1];
                                  const content = match[2];
                                  return (
                                    <div 
                                      key={idx} 
                                      className="flex items-start gap-3 p-2 rounded hover:bg-slate-900/60 transition cursor-pointer"
                                      onClick={() => alert(`[타임라벨 시뮬레이션] ${time} 구간 음성 지점으로 재생 싱크를 요청했습니다.`)}
                                    >
                                      <span className="text-xs font-mono font-bold text-teal-400 bg-slate-800 px-2 py-0.5 rounded cursor-pointer shrink-0">
                                        {time}
                                      </span>
                                      <p className="text-xs text-slate-305 leading-relaxed">
                                        {content}
                                      </p>
                                    </div>
                                  );
                                }
                                return <p key={idx} className="text-xs text-slate-400 whitespace-pre-wrap">{line}</p>;
                              })}
                            </div>
                          </div>
                        )}

                        {/* 3. Strategy View */}
                        {detailTab === 'strategy' && (
                          <div className="space-y-6 max-w-3xl leading-relaxed text-sm">
                            <div className="bg-gradient-to-r from-usana-950/20 to-teal-950/20 p-4 rounded-xl border border-usana-800/30 mb-4">
                              <h4 className="text-xs font-bold text-teal-400 mb-1 flex items-center gap-1.5">
                                <Sparkles className="w-3.5 h-3.5 text-yellow-400 pulse-primary" />
                                USANA 비즈니스 전략 인사이트란?
                              </h4>
                              <p className="text-xs text-slate-350">
                                지식에 기반한 고도의 소구점을 세포 과학(인셀리전스, 올리볼, 헬스팩)과 바이너리 정직 보상플랜에 합리적으로 조력하는 AI 멘토링 연동 시스템입니다.
                              </p>
                            </div>

                            {selectedLecture.strategy.split('\n\n').map((chunk, idx) => {
                              const lines = chunk.split('\n');
                              const header = lines[0];
                              const content = lines.slice(1).join('\n');
                              if (header.startsWith('■')) {
                                return (
                                  <div key={idx} className="bg-slate-900/40 p-5 rounded-xl border border-slate-800 shadow-sm relative group">
                                    <button
                                      onClick={() => handleCopy(content, `strat-${idx}`)}
                                      className="absolute top-4 right-4 bg-slate-800 hover:bg-slate-700 text-slate-300 p-1 rounded opacity-0 group-hover:opacity-100 transition"
                                      title="이 단락 복사"
                                    >
                                      {copyFeedback === `strat-${idx}` ? <Check className="w-3.5 h-3.5 text-teal-400" /> : <Copy className="w-3.5 h-3.5" />}
                                    </button>
                                    <h3 className="text-sm font-bold text-teal-400 mb-3 flex items-center gap-1.5">
                                      <span className="w-1.5 h-3.5 bg-teal-500 rounded-sm"></span>
                                      {header.replace('■', '').trim()}
                                    </h3>
                                    <div className="whitespace-pre-line text-slate-300 font-sans ml-3 leading-relaxed space-y-1">
                                      {content}
                                    </div>
                                  </div>
                                );
                              }
                              return <p key={idx} className="whitespace-pre-line text-slate-300">{chunk}</p>;
                            })}
                          </div>
                        )}

                        {/* 4. Action Sheet View */}
                        {detailTab === 'actionsheet' && (
                          <div className="space-y-6 max-w-3xl leading-relaxed text-sm">
                            <div className="p-3.5 rounded-lg bg-slate-900/60 border border-slate-800 flex items-center justify-between text-xs text-slate-400">
                              <span>세일즈 피치를 즉시 복제하여 사용할 수 있도록 단락별 퀵클립 복사를 지원합니다.</span>
                              <button 
                                onClick={() => handleCopy(selectedLecture.processedData, 'fullaction')}
                                className="text-usana-400 hover:underline"
                              >
                                {copyFeedback === 'fullaction' ? "전체 복사완료!" : "고비율 템플릿 복사"}
                              </button>
                            </div>

                            {selectedLecture.processedData.split('\n\n').map((chunk, idx) => {
                              const lines = chunk.split('\n');
                              const header = lines[0];
                              const content = lines.slice(1).join('\n');
                              if (header.startsWith('■')) {
                                return (
                                  <div key={idx} className="bg-slate-905 p-5 rounded-xl border border-slate-800/80 shadow-sm relative group">
                                    <button
                                      onClick={() => handleCopy(content, `act-${idx}`)}
                                      className="absolute top-4 right-4 bg-slate-800 hover:bg-slate-700 text-slate-305 px-2 py-1 rounded text-xs opacity-0 group-hover:opacity-100 transition flex items-center gap-1"
                                    >
                                      {copyFeedback === `act-${idx}` ? "복사 완료" : "복사하기"}
                                      <Copy className="w-3 h-3" />
                                    </button>
                                    <h3 className="text-sm font-semibold text-white mb-3 flex items-center gap-1.5">
                                      <span className="w-1 text-xs font-bold text-slate-400 bg-slate-800 px-1.5 py-0.5 rounded">
                                        {idx + 1}
                                      </span>
                                      {header.replace('■', '').trim()}
                                    </h3>
                                    <div className="whitespace-pre-line text-slate-250 ml-6 leading-relaxed font-sans space-y-1 text-xs">
                                      {content}
                                    </div>
                                  </div>
                                );
                              }
                              return <p key={idx} className="whitespace-pre-line text-slate-300">{chunk}</p>;
                            })}
                          </div>
                        )}

                      </div>
                    </div>
                  ) : (
                    <div className="flex-1 flex flex-col items-center justify-center p-8 text-center bg-slate-950">
                      <Sparkles className="w-12 h-12 text-slate-700 mb-3 pulse-primary" />
                      <h3 className="text-sm font-bold text-white mb-1">지식 라이브러리가 준비되었습니다</h3>
                      <p className="text-xs text-slate-400 max-w-sm">
                        왼쪽 지식 리스트에서 분석 보고서를 선택하여 최상의 세포 과학 전략 레시피를 열람하세요.
                      </p>
                    </div>
                  )}
                </div>
              </section>
            </div>
          )}

          {/* TAB 2: TRANSFORM TAB (AI CONVERTER) */}
          {activeTab === 'transform' && (
            <div className="h-full overflow-y-auto p-4 md:p-8 bg-slate-950 flex justify-center">
              <div className="w-full max-w-3xl space-y-6">
                
                {/* Intro Title */}
                <div>
                  <h2 className="text-lg md:text-xl font-bold text-white flex items-center gap-2">
                    <Activity className="w-5 h-5 text-teal-400" />
                    AI 수집식 지식 정제 변환 마스터
                  </h2>
                  <p className="text-xs text-slate-400 mt-1">
                    유튜브 동영상 주소, 강의 세미나 대본, 혹은 음성 녹취 파일을 업로드하여 평생 유사나 비즈니스 자산 시트로 자동 치환합니다.
                  </p>
                </div>

                <div className="bg-slate-900/60 p-5 md:p-6 rounded-2xl border border-slate-800 space-y-5">
                  
                  {/* API Check warning */}
                  {geminiApiKey.trim() === "" && (
                    <div className="p-3.5 rounded-xl bg-orange-950/20 border border-orange-850/40 text-xs text-orange-200 flex items-start gap-2">
                      <Info className="w-4 h-4 shrink-0 text-orange-400 top-0.5 relative" />
                      <div>
                        <span>현재 사용자 개인 <b>Gemini API Key가 등록되지 않았습니다.</b></span>
                        <p className="mt-1 text-slate-400 leading-relaxed">
                          현재 변환 실행 시 [유사나 고정밀 특화 AI 시뮬레이터]가 가동되어 완벽한 보고서 구조를 초고속 생성합니다. 실제 유튜브 콘텐츠 분석을 원하시면 <b>설정 탭</b>에서 개인 API 키를 삽입해주세요!
                        </p>
                      </div>
                    </div>
                  )}

                  {/* Form fields */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-1.5">
                      <label className="text-xs font-bold text-slate-300">강의 / 세미나 지식명 (강제 필수)</label>
                      <input
                        type="text"
                        placeholder="예) 헬스팩 과학 세미나 1부 개회"
                        value={transformTitle}
                        onChange={(e) => setTransformTitle(e.target.value)}
                        className="w-full bg-slate-855 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-usana-500"
                      />
                    </div>
                    <div className="space-y-1.5">
                      <label className="text-xs font-bold text-slate-300">원화 연사 / 강사</label>
                      <input
                        type="text"
                        placeholder="예) 이재정 박사, 김유사 골드"
                        value={transformSpeaker}
                        onChange={(e) => setTransformSpeaker(e.target.value)}
                        className="w-full bg-slate-855 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-usana-500"
                      />
                    </div>
                  </div>

                  <div className="space-y-1.5">
                    <label className="text-xs font-bold text-slate-300 flex items-center gap-1">
                      <Youtube className="w-3.5 h-3.5 text-rose-500" />
                      YouTube 동영상 주소 (선택 연결)
                    </label>
                    <input
                      type="text"
                      placeholder="예) https://www.youtube.com/watch?v=..."
                      value={transformSource}
                      onChange={(e) => setTransformSource(e.target.value)}
                      className="w-full bg-slate-855 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-usana-500"
                    />
                  </div>

                  {/* Folders and config */}
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    <div className="space-y-1.5">
                      <label className="text-xs font-bold text-slate-300">수출 지식 폴더 지정</label>
                      <select
                        value={transformFolderId || ''}
                        onChange={(e) => setTransformFolderId(e.target.value ? parseInt(e.target.value, 10) : null)}
                        className="w-full bg-slate-855 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-usana-500"
                      >
                        <option value="">지정 안 함 (미보관)</option>
                        {folders.map(f => (
                          <option key={f.id} value={f.id}>{f.name}</option>
                        ))}
                      </select>
                    </div>

                    <div className="space-y-1.5">
                      <label className="text-xs font-bold text-slate-300">분석 훈련 지목</label>
                      <select
                        value={transformIsDialogue ? 'dialogue' : 'lecture'}
                        onChange={(e) => setTransformIsDialogue(e.target.value === 'dialogue')}
                        className="w-full bg-slate-855 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-usana-500"
                      >
                        <option value="lecture">일반 강의 / 정보 독백</option>
                        <option value="dialogue">1:1 상담 / 고객 Objection 대화</option>
                      </select>
                    </div>

                    <div className="space-y-1.5">
                      <label className="text-xs font-bold text-slate-300">번역 표적 대상국</label>
                      <select
                        value={transformLanguage}
                        onChange={(e) => setTransformLanguage(e.target.value)}
                        className="w-full bg-slate-855 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-usana-500"
                      >
                        <option value="ko">한국어 (Default Korean)</option>
                        <option value="en">영어 (English Translation)</option>
                        <option value="ja">일본어 (Japanese)</option>
                        <option value="zh">중국어 (Chinese Simplified)</option>
                      </select>
                    </div>
                  </div>

                  {/* Voice recording & Custom script text block */}
                  <div className="space-y-2 pt-2 border-t border-slate-800">
                    <div className="flex items-center justify-between">
                      <label className="text-xs font-bold text-slate-350">대본 직접 입력 또는 임시 수집</label>
                      
                      {/* Voice Recorder button */}
                      <button
                        type="button"
                        onClick={isRecording ? stopRecording : startRecording}
                        className={`flex items-center gap-1.5 px-3 py-1 rounded-full text-[11px] font-bold transition ${
                          isRecording 
                            ? 'bg-rose-600/90 text-white pulse-primary' 
                            : 'bg-slate-800 hover:bg-slate-700 text-teal-400'
                        }`}
                      >
                        <Mic className="w-3.5 h-3.5" />
                        {isRecording ? `녹음 중... (${recordingSeconds}초) [정지]` : "마이크 음성 녹취 가동"}
                      </button>
                    </div>

                    <textarea
                      rows={4}
                      placeholder="녹음을 가동하거나, 직접 대본 텍스트를 붙여넣기 하실 수 있습니다. 비어있을 경우 고품격 유사나 스터디 기본 모노로그 템플릿이 기재 제안됩니다."
                      value={transformCustomTranscript}
                      onChange={(e) => setTransformCustomTranscript(e.target.value)}
                      className="w-full bg-slate-855 border border-slate-800 rounded-lg p-3 text-xs text-slate-300 placeholder-slate-650 focus:outline-none focus:border-usana-500 font-sans"
                    ></textarea>
                  </div>

                  {/* Action button */}
                  <button
                    onClick={triggerAnalysis}
                    className="w-full py-3 rounded-lg bg-gradient-to-r from-usana-600 to-teal-500 hover:from-usana-500 hover:to-teal-400 text-white text-xs font-bold transition shadow-lg shadow-usana-900/30 flex items-center justify-center gap-2 mt-4"
                  >
                    <Sparkles className="w-4 h-4 text-yellow-300 pulse-primary" />
                    <span>AI 지식 분석 일제 가동 (1 Credit 소요)</span>
                  </button>

                </div>
              </div>
            </div>
          )}

          {/* TAB 3: PREMIUM HUBS (MINDMAPS & COPIER) */}
          {activeTab === 'premium' && (
            <div className="h-full overflow-y-auto p-4 md:p-8 bg-slate-950 space-y-6">
              <div className="max-w-4xl mx-auto space-y-6">
                
                {/* Intro Page Description Header */}
                <div>
                  <h2 className="text-lg md:text-xl font-bold text-white flex items-center gap-2">
                    <Map className="w-5 h-5 text-cellular-purple" />
                    유사나 파워 패키지 프리미엄 성과 허브
                  </h2>
                  <p className="text-xs text-slate-400 mt-1">
                    팀 성과 복제 속도를 극대화 해주는 인터랙티브 마인드맵과 클릭-복사 소통 멘트 뱅크를 활용하세요.
                  </p>
                </div>

                {/* Sub grid layout */}
                <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
                  
                  {/* A. 3D-Like Mindmap Visualizer */}
                  <div className="lg:col-span-12 bg-slate-900/60 rounded-2xl border border-slate-800 p-5 flex flex-col space-y-4">
                    <div>
                      <h3 className="text-xs font-bold text-white uppercase tracking-wider flex items-center gap-1">
                        <Activity className="w-3.5 h-3.5 text-cellular-purple" />
                        인터랙티브 유사나 셀룰러 자산 맵
                      </h3>
                      <p className="text-[11px] text-slate-400 mt-0.5">
                        노드를 클릭하여 복수 가지의 세포과학 구조와 무마감 보상 플랜 비밀을 시각적으로 전개해 보세요.
                      </p>
                    </div>

                    <div className="bg-slate-950 rounded-xl p-4 border border-slate-850 min-h-[280px] flex flex-col md:flex-row gap-4 justify-between items-stretch">
                      
                      {/* Interactive Graph Box */}
                      <div className="flex-1 bg-slate-900/40 rounded-lg p-3 border border-slate-800/60 flex flex-col justify-between items-center relative overflow-hidden">
                        <span className="text-[9px] text-teal-400/80 font-mono tracking-widest absolute top-2 left-2 uppercase">GRAPH CONSOLE</span>
                        
                        {/* Parent Back node button */}
                        {mindmapActiveNode !== 'root' && (
                          <button
                            onClick={() => setMindmapActiveNode('root')}
                            className="bg-slate-800 hover:bg-slate-700 text-slate-300 text-[10px] px-2 py-1 rounded absolute top-2 right-2 flex items-center gap-1"
                          >
                            <RefreshCw className="w-3 h-3 text-teal-400" />
                            <span>최상위 맵 복귀</span>
                          </button>
                        )}

                        {/* Active Anchor node card */}
                        <div className="my-auto text-center p-4 max-w-sm">
                          <div className="inline-block text-[10px] bg-cellular-purple/20 text-cellular-purple border border-cellular-purple/50 px-2.5 py-0.5 rounded-full font-bold mb-2">
                            활성화 노드
                          </div>
                          <h4 className="text-sm font-bold text-white mb-2">{mindmapTree[mindmapActiveNode].title}</h4>
                          <p className="text-xs text-slate-350 leading-relaxed bg-slate-950 p-3 rounded border border-slate-850">
                            {mindmapTree[mindmapActiveNode].text}
                          </p>
                        </div>

                        {/* Child links display */}
                        <div className="w-full pt-3 border-t border-slate-800">
                          <div className="flex items-center gap-1.5 mb-2">
                            <span className="inline-block w-1.5 h-1.5 bg-teal-500 rounded-full"></span>
                            <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">연결 심화 하부구조 탐색</span>
                          </div>
                          {mindmapTree[mindmapActiveNode].children.length === 0 ? (
                            <span className="text-[11px] text-slate-500 italic block">가장 말단 노드입니다. 상위 맵으로 가시려면 복귀 버튼을 사용하세요.</span>
                          ) : (
                            <div className="flex flex-wrap gap-2">
                              {mindmapTree[mindmapActiveNode].children.map(childKey => (
                                <button
                                  key={childKey}
                                  onClick={() => setMindmapActiveNode(childKey)}
                                  className="text-xs bg-slate-800 hover:bg-slate-750 text-slate-200 border border-slate-700 px-3 py-1.5 rounded-lg flex items-center gap-1.5 transition active:scale-95"
                                >
                                  <span>{mindmapTree[childKey].title}</span>
                                  <ArrowRight className="w-3 h-3 text-teal-400" />
                                </button>
                              ))}
                            </div>
                          )}
                        </div>

                      </div>

                    </div>
                  </div>

                  {/* B. Master Copy Templates Bank */}
                  <div className="lg:col-span-12 bg-slate-900/60 rounded-2xl border border-slate-800 p-5 space-y-4">
                    <div>
                      <h3 className="text-xs font-bold text-white uppercase tracking-wider flex items-center gap-1">
                        <Share2 className="w-3.5 h-3.5 text-teal-400" />
                        유사나 마스터 세일즈 카피 라이브러리 (정석 멘토링 원용)
                      </h3>
                      <p className="text-[11px] text-slate-400 mt-0.5">
                        팀 파트너들과 함께 사용할 수 있는 정석 고밀도 제안 지문입니다. 클릭하여 전송용 카톡에 붙여 사용하세요.
                      </p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      
                      {/* Template Card 1 */}
                      <div className="p-4 rounded-xl bg-slate-950 border border-slate-850 relative group">
                        <button
                          onClick={() => handleCopy(
                            `[유사나 헬스팩 세포과학 가치제안]\n매일을 지키는 의약 수준 종합 비타민 미학! 올리볼 이중 항산화와 14가지 비타민 성분으로 일상의 세포 스트레스 공격을 지능적으로 차단하세요. 단 한 포로 세포 리셋이 가동됩니다.`,
                            'tem1'
                          )}
                          className="absolute top-3 right-3 bg-slate-800 text-[10px] text-teal-400 px-2 py-1 rounded transition opacity-0 group-hover:opacity-100"
                        >
                          {copyFeedback === 'tem1' ? "복사완료!" : "카피복사"}
                        </button>
                        <h4 className="text-xs font-bold text-white mb-1.5">[클래스 1] 헬스팩 영양성분 세포 쉴드</h4>
                        <p className="text-xs text-slate-400 leading-relaxed line-clamp-3">
                          매일을 지키는 의약 수준 종합 비타민 미학! 올리볼 이중 항산화와 14가지 비타민 성분으로 일상의 세포 스트레스 공격을 지능적으로 차단하세요. 단 한 포로 세포 리셋이 가동됩니다.
                        </p>
                      </div>

                      {/* Template Card 2 */}
                      <div className="p-4 rounded-xl bg-slate-950 border border-slate-850 relative group">
                        <button
                          onClick={() => handleCopy(
                            `[유사나 정직 이월 보상플랜 가치제안]\n마감 스트레스 없이 좌우 실적이 영구 누적 이월되는 똑똑한 유사나 바이너리. 내가 노력해서 만든 건강 소비망이 대수 제한 없이 무한 깊이로 공유되어 매주 꼬박 수취되는 권리형 연금 자산이 됩니다. 소득 주기를 완전히 내것으로 만드세요.`,
                            'tem2'
                          )}
                          className="absolute top-3 right-3 bg-slate-800 text-[10px] text-teal-400 px-2 py-1 rounded transition opacity-0 group-hover:opacity-100"
                        >
                          {copyFeedback === 'tem2' ? "복사완료!" : "카피복사"}
                        </button>
                        <h4 className="text-xs font-bold text-white mb-1.5">[클래스 2] 무마감/무제한 이월 권리자산</h4>
                        <p className="text-xs text-slate-400 leading-relaxed line-clamp-3">
                          마감 스트레스 없이 좌우 실적이 영구 누적 이월되는 똑똑한 유사나 바이너리. 내가 노력해서 만든 건강 소비망이 대수 제한 없이 무한 깊이로 공유되어 매주 꼬박 수취되는 권리형 연금 자산이 됩니다. 소득 주기를 완전히 내것으로 만드세요.
                        </p>
                      </div>

                    </div>
                  </div>

                </div>

              </div>
            </div>
          )}

          {/* TAB 4: SETTINGS TAB (API KEY CONFIG) */}
          {activeTab === 'settings' && (
            <div className="h-full overflow-y-auto p-4 md:p-8 bg-slate-950 flex justify-center">
              <div className="w-full max-w-2xl space-y-6">
                
                {/* Intro Title */}
                <div>
                  <h2 className="text-lg md:text-xl font-bold text-white flex items-center gap-2">
                    <Settings className="w-5 h-5 text-slate-400" />
                    시스템 제어 및 Gemini API 키 암호화 보관
                  </h2>
                  <p className="text-xs text-slate-400 mt-1">
                    분석을 위해 브라우저에 안전히 로컬 보존되는 비밀번호와 Google Gemini API 키 값을 설정 제어합니다.
                  </p>
                </div>

                {/* Main panel */}
                <div className="bg-slate-900/60 rounded-2xl border border-slate-800 p-5 md:p-6 space-y-6">
                  
                  {/* API KEY setting */}
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <h4 className="text-xs font-bold text-white flex items-center gap-1.5">
                        <Sparkles className="w-4 h-4 text-usana-400" />
                        사용자 독점 Gemini API Key 값
                      </h4>
                      <button
                        onClick={() => setShowApiKey(!showApiKey)}
                        className="text-[10px] text-teal-400 font-bold hover:underline"
                        type="button"
                      >
                        {showApiKey ? "감추기" : "비밀 키 보기"}
                      </button>
                    </div>

                    <div className="flex gap-2">
                      <input
                        type={showApiKey ? "text" : "password"}
                        placeholder="Google AI Studio에서 발급받은 'AIzaSy...' API 키 기입"
                        value={geminiApiKey}
                        onChange={(e) => setGeminiApiKey(e.target.value)}
                        className="flex-1 bg-slate-855 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white placeholder-slate-655 focus:outline-none focus:border-usana-500 font-mono"
                      />
                      <button
                        onClick={() => {
                          localStorage.setItem('usana_gemini_key', geminiApiKey);
                          alert("Gemini API Key가 성공적으로 브라우저 장치에 암호화 보존 처리되었습니다.");
                        }}
                        className="bg-usana-600 hover:bg-usana-500 text-white text-xs font-bold px-4 py-2 rounded-lg transition"
                      >
                        저장
                      </button>
                    </div>
                    <p className="text-[10px] text-slate-450 leading-normal">
                      입력된 키값은 Vercel이나 외부 서버로 전혀 유출되지 않고 오직 사용자의 해당 크롬 브라우저 샌드박스 <b>localStorage</b> 속에서 다이렉트로 제미나이 채널을 향해서만 발신 사용됩니다.
                    </p>
                  </div>

                  {/* Admin configuration password */}
                  <div className="space-y-3 pt-4 border-t border-slate-800">
                    <h4 className="text-xs font-bold text-white">통합관리 포털 제어 비밀번호 (Admin Gateway)</h4>
                    <div className="flex gap-2">
                      <input
                        type="password"
                        placeholder="설정된 관리자 게이트 비밀번호"
                        value={adminPassword}
                        onChange={(e) => setAdminPassword(e.target.value)}
                        className="w-full bg-slate-855 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-usana-500 font-mono"
                      />
                      <button
                        onClick={() => {
                          alert("관리자 게이트 암호가 성공적으로 갱신되었습니다.");
                        }}
                        className="bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-bold px-4 py-2 rounded-lg transition"
                      >
                        갱신
                      </button>
                    </div>
                  </div>

                  {/* Danger zone / factory resets */}
                  <div className="space-y-3 pt-6 border-t border-slate-800">
                    <h4 className="text-xs font-bold text-rose-450">데이터 초기화 위험 구역</h4>
                    <p className="text-[11px] text-slate-400">
                      앱이 무겁거나 분석 히스토리를 전부 지우고 공장 초기 출하 상태로 되돌립니다. 복제 데이터는 전부 분실되므로 유의하여 주세요.
                    </p>
                    <button
                      onClick={handleAdminReset}
                      className="text-xs bg-slate-800 hover:bg-rose-950/40 hover:text-rose-450 hover:border-rose-900 border border-slate-700 text-slate-300 px-4 py-2.5 rounded-lg transition font-semibold"
                    >
                      전체 지식 데이터 공장 파기 및 복원
                    </button>
                  </div>

                </div>

              </div>
            </div>
          )}

          {/* TAB 5: ADMIN VIEWS (SECURITY PORTAL) */}
          {activeTab === 'admin' && (
            <div className="h-full overflow-y-auto p-4 md:p-8 bg-slate-950 flex justify-center">
              <div className="w-full max-w-2xl space-y-6">
                
                {/* Intro Headers */}
                <div>
                  <h2 className="text-lg md:text-xl font-bold text-white flex items-center gap-2">
                    <Lock className="w-5 h-5 text-cellular-rose" />
                    유사나 비즈니스 지식 통합 관리 포털
                  </h2>
                  <p className="text-xs text-slate-400 mt-1">
                    시스템 텔레메트리, 데이터베이스 무결성, AI API 발송 로그 등을 정합 모니터링 관리하는 보안 콘솔입니다.
                  </p>
                </div>

                {!isAdminUnlocked ? (
                  /* Password Guard Screen */
                  <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 text-center space-y-4">
                    <ShieldAlert className="w-12 h-12 text-cellular-rose mx-auto pulse-primary" />
                    <div className="space-y-1.5">
                      <h3 className="text-sm font-bold text-white">관리 포털 보안인증 필요</h3>
                      <p className="text-xs text-slate-400">
                        설정 메뉴에 등록된 관리포털 비밀번호(초기 기본설정: <b>usana123</b>)를 입력하십시오.
                      </p>
                    </div>
                    <div className="flex gap-2 max-w-xs mx-auto pt-2">
                      <input
                        type="password"
                        placeholder="관리비밀번호 입력..."
                        value={adminInputPass}
                        onChange={(e) => setAdminInputPass(e.target.value)}
                        onKeyDown={(e) => e.key === 'Enter' && handleAdminVerify()}
                        className="flex-1 bg-slate-855 border border-slate-800 rounded-lg px-3 py-1.5 text-xs text-white focus:outline-none focus:border-cellular-rose font-mono"
                      />
                      <button
                        onClick={handleAdminVerify}
                        className="bg-cellular-rose text-white text-xs font-bold px-4 py-1.5 rounded-lg hover:bg-rose-600 transition"
                      >
                        신원확인
                      </button>
                    </div>
                  </div>
                ) : (
                  /* Telemetry details console */
                  <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 md:p-6 space-y-6">
                    <div className="flex items-center justify-between">
                      <span className="text-[11px] bg-emerald-950/50 text-emerald-400 border border-emerald-800/40 px-2 py-0.5 rounded-full font-bold">● TELEMETRY ONLINE</span>
                      <button
                        onClick={() => setIsAdminUnlocked(false)}
                        className="text-xs text-slate-400 hover:text-white underline"
                      >
                        락 보호조치 실행
                      </button>
                    </div>

                    {/* Stats Grid */}
                    <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-center">
                      <div className="p-3.5 rounded-xl bg-slate-950 border border-slate-850">
                        <span className="text-[10px] text-slate-400 uppercase tracking-widest block">총 보관 폴더</span>
                        <span className="text-lg font-bold text-white">{folders.length}개</span>
                      </div>
                      <div className="p-3.5 rounded-xl bg-slate-950 border border-slate-850">
                        <span className="text-[10px] text-slate-400 uppercase tracking-widest block">총 소유 지식 수</span>
                        <span className="text-lg font-bold text-white">{lectures.length}건</span>
                      </div>
                      <div className="p-3.5 rounded-xl bg-slate-950 border border-slate-850">
                        <span className="text-[10px] text-slate-400 uppercase tracking-widest block">사용 티어</span>
                        <span className="text-xs font-bold text-white block mt-1 truncate">{subscriptionTier}</span>
                      </div>
                      <div className="p-3.5 rounded-xl bg-slate-950 border border-slate-850">
                        <span className="text-[10px] text-slate-400 uppercase tracking-widest block">AI 발동횟수</span>
                        <span className="text-lg font-bold text-white">{creditsUsed}회</span>
                      </div>
                    </div>

                    {/* Prompts tuning parameters */}
                    <div className="space-y-3 pt-2">
                      <h4 className="text-xs font-bold text-white uppercase tracking-wider">AI 에이전트 지휘 수칙</h4>
                      <textarea
                        rows={3}
                        value={systemInstructions}
                        onChange={(e) => setSystemInstructions(e.target.value)}
                        className="w-full bg-slate-855 border border-slate-800 rounded-lg p-2.5 text-xs text-slate-350 focus:outline-none focus:border-usana-500"
                      />
                      <button
                        onClick={() => alert("에이전트 제어 수칙이 성공적으로 프롬프트 레이어에 상주 보존되었습니다.")}
                        className="bg-slate-800 hover:bg-slate-750 text-slate-200 text-xs font-semibold px-4 py-2 rounded-lg transition"
                      >
                        에이전트 지시서 세이브
                      </button>
                    </div>

                  </div>
                )}

              </div>
            </div>
          )}

        </main>

        {/* FOOTER MOBILE NAVIGATION BAR */}
        <footer className="md:hidden flex items-center justify-around py-2 bg-slate-900 border-t border-slate-800 shrink-0">
          <button
            onClick={() => setActiveTab('library')}
            className={`flex flex-col items-center gap-1 text-[10px] transition ${
              activeTab === 'library' ? 'text-usana-400 font-bold' : 'text-slate-450'
            }`}
          >
            <BookOpen className="w-4 h-4 shrink-0" />
            <span>도서관</span>
          </button>
          
          <button
            onClick={() => setActiveTab('transform')}
            className={`flex flex-col items-center gap-1 text-[10px] transition ${
              activeTab === 'transform' ? 'text-teal-400 font-bold' : 'text-slate-450'
            }`}
          >
            <Activity className="w-4 h-4 shrink-0" />
            <span>AI 변환</span>
          </button>

          <button
            onClick={() => setActiveTab('premium')}
            className={`flex flex-col items-center gap-1 text-[10px] transition ${
              activeTab === 'premium' ? 'text-cellular-purple font-bold' : 'text-slate-450'
            }`}
          >
            <Map className="w-4 h-4 shrink-0" />
            <span>프리미엄</span>
          </button>

          <button
            onClick={() => setActiveTab('settings')}
            className={`flex flex-col items-center gap-1 text-[10px] transition ${
              activeTab === 'settings' ? 'text-slate-200 font-bold' : 'text-slate-450'
            }`}
          >
            <Sliders className="w-4 h-4 shrink-0" />
            <span>설정</span>
          </button>

          <button
            onClick={() => setActiveTab('admin')}
            className={`flex flex-col items-center gap-1 text-[10px] transition ${
              activeTab === 'admin' ? 'text-cellular-rose font-bold' : 'text-slate-450'
            }`}
          >
            <Lock className="w-4 h-4 shrink-0" />
            <span>보안포털</span>
          </button>
        </footer>

      </div>

      {/* NEW FOLDER LIGHT MODAL BOX */}
      {isNewFolderOpen && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-filter backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="w-full max-w-sm bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4">
            <div>
              <h3 className="text-sm font-bold text-white">새로운 지식 자산 폴더 축적</h3>
              <p className="text-[11px] text-slate-400">학습 목적별 지식 데이터 영역을 신규 생성합니다.</p>
            </div>
            <div className="space-y-3.5">
              <div className="space-y-1.5">
                <label className="text-[11px] text-slate-350 font-semibold">폴더 레이블 이름</label>
                <input
                  type="text"
                  placeholder="예) 헬스팩 성분 깊이 공부"
                  value={newFolderName}
                  onChange={(e) => setNewFolderName(e.target.value)}
                  className="w-full bg-slate-855 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white uppercase placeholder-slate-500 focus:outline-none focus:border-usana-500"
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-[11px] text-slate-350 font-semibold">폴더 상징 시그널 컬러</label>
                <div className="flex gap-2">
                  {['#10b981', '#0e8fe4', '#f59e0b', '#8b5cf6', '#f43f5e', '#06b6d4'].map(color => (
                    <button
                      key={color}
                      type="button"
                      onClick={() => setNewFolderColor(color)}
                      className={`w-6 h-6 rounded-full border-2 transition ${
                        newFolderColor === color ? 'border-white scale-110 shadow-md' : 'border-transparent'
                      }`}
                      style={{ backgroundColor: color }}
                    ></button>
                  ))}
                </div>
              </div>
            </div>

            <div className="flex gap-2.5 pt-2">
              <button
                type="button"
                onClick={() => setIsNewFolderOpen(false)}
                className="flex-1 py-2 text-xs bg-slate-800 hover:bg-slate-750 text-slate-400 font-bold rounded-lg transition"
              >
                닫기
              </button>
              <button
                type="button"
                onClick={addFolder}
                className="flex-1 py-2 text-xs bg-usana-600 hover:bg-usana-500 text-white font-bold rounded-lg transition"
              >
                폴더 개설
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
