# Travel Together
Travel Together 프론트엔드 Github

Travel Together 서버 및 데이터베이스 Github https://github.com/lha0/lhy_nsh_back.git

---

### 혼여행러들을 위한 여행 동행 구하기 앱

> 게시판을 통해 원하는 여행지와 일정을 확인하고, 해당 게시물을 올린 사람에게 채팅을 보낼 수 있습니다
> 

> 채팅 기능을 통해 직접 대화하고 일정을 정할 수 있습니다.
> 

> 채팅을 통해 동행을 확정하면, 리뷰를 쓸 수 있는 탭이 생기고 해당 탭에서 리뷰를 작성하면 사용자에게 쌓인 리뷰가 업데이트됩니다.
> 

---

### 개발 팀원

- 이하영 - UNIST 컴퓨터공학과 20학번
- 남승훈 - UNIST 전산학부 22학번

---

## 개발 스택

- Kotlin
- Android Studio
- Figma

## 앱 제작 배경

---

- 친구들끼리 가면 더할 나위 없겠지만, 경제적으로, 시간적으로 부담이 될 수 있는 유럽 여행은 혼자 또는 둘이 가는 경우가 많다.
- 따라서 서로 사진을 찍어주거나 맛있는 식당(주로 2인 이상 가능한)에 가기 위해 동행을 구한다.
- 기존에 여행 동행을 구하는 시스템은 유명한 여행 네이버 카페인 유랑에서 진행되지만, 주로 따로 오픈 채팅방 링크를 올려야해서 불편한 문제가 있었다.
- 또한, 만약 오픈 채팅방 링크로 약속을 잡는다면 외부 공간으로 이동해야하기 때문에 범죄가 잃어나거나 난처한 상황이 생겨도 책임지기가 어렵다.
- **이에, 사용자의 편의 증가 + 안전성 확보를 위해 여행 동행을 구하는 전체 과정을 수행할 수 있는 앱을 제작하고자 하였다.**

## 앱 기능

---

### 로그인

- Google 소셜 로그인 구현
    - `Google API` 활용
- 로그인 성공 시 `JWT 토큰`을 발급하도록 하고, 앱 사용중에 사용자 정보를 불러올 때는 이 토큰을 활용해서 정보를 불러오도록 구현하였다
    - 보안성 확보

### 회원가입

- 데이터베이스에 사용자 정보가 존재하지 않을 시, 회원가입 진행


### Posts

- Database 내 저장된 게시글 정보들을 `Recyclerview`로 보여준다.
- 검색 및 필터링 기능 구현
- 게시글을 클릭 시 해당 게시글로 이동
    - 데이터베이스에서 해당 게시글에 대한 정보 (작성자, 사진, 제목, 날짜, 본문 등)을 띄어줌
- 채팅하기 버튼을 클릭하면 채팅액티비티로 전환


### Chatting

- `Stream chat sdk` 활용해 구현
    - 로그인된 유저의 JWT토큰을 통해 유저 인식
    - 게시글을 작성한 사람의 ID를 넘겨 1:1 채팅방 생성
- 게시글에서 채팅하기를 누르면, 채팅창으로 넘어오는데, 게시글을 작성한 사람과의 채팅방이 자동으로 생성되도록 하였다.
- 채팅방 헤더의 채팅 상대의 프로필을 클릭하면, `동행을 확정하시겠습니까?` 라는 AlertDialog가 띄어짐
    - 동행 확정 페이지로 이동 및 동행 확정 가능
- 동행을 확정하면, 해당 Dialog가 `리뷰를 작성하시겠습니까?`로 변경 및 리뷰 작성 가능
    - 리뷰 작성 시, 상대의 데이터베이스에 리뷰 저장 및 마이페이지 리뷰 업데이트


### Recommendation

- 카테고리를 이용하여 분류하였고, 각 질문에 답변하는 카테고리에 맞는 여행지 추천 제공
- 동행자 찾기 버튼을 클릭 시 첫번째 탭인 게시글 탭으로 연결

### My Page

- 로그인된 사용자의 정보를 볼 수 있는 탭
- 프로필 사진과 본인의 신분을 증명하는 신분증 사진을 업로드 가능
- 토큰을 이용하여 사용자의 정보를 데이터베이스에서 불러옴
