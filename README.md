## 빼곡 : 빼곡히 채우는 내손안의 책장
* 개발 기간 : `2024.11.01 ~ 2025.02.15`
* 도메인 : `독서/자기계발 앱`
* 팀 및 역할 : `DN 1인/FE 1인/BE 1인 中 BE 개발자로 참여`

</br>
</br>

## 📚 서비스 소개

> `빼곡`은 읽었던 책을 기록함으로써 3D책장을 채우고, 카테고리별로 서재에 정리함으로써 독서습관을 기르는데 도움을 주는 모바일 앱 입니다. </br>
3D 책장, 카테고리별 서재, 다회독 지원 등 `빼곡`만이 지원하는 차별화 된 기능으로 기록을 체계적으로 관리 할 수 있으며,
E북이나 도서관을 이용하는 독서가들에게 **'나만의 책장'을 채워가는 특별한 경험**을 선사합니다.

![그래픽이미지 최종](https://github.com/user-attachments/assets/ff6d473b-857a-4130-a4cf-72ebc3550e45)

- [[알파테스트 진행중] Google 플레이스토어 바로가기](https://play.google.com/store/apps/details?id=bbaegok.app)
- [심사 진행중] Apple 앱스토어
<details>
  <summary><b>테스트 계정</b></summary>
  
앱 내 구글 로그인 클릭 후 아래 테스트 ID, PW으로 테스트할 수 있습니다.
  
   - **테스트 ID** : bbaegokTest@gmail.com
   - **테스트 PW** : Qorhr12345
</details>


</br>
</br>

## 📚 주요기능 소개

**(1) 책 검색**
- 알라딘 API를 연동하여 책을 검색 할 수 있습니다.
- isbn을 구분값으로 선택했으며, 이미 등록한 책은 수정으로 넘어갑니다.

**(2) 독서내용 기록하기**
- "읽고싶은 책", "읽는중인 책", "다 읽은 책" 등 상태값을 구분하여 저장 및 활용합니다.
- 사용자가 직접 설정하는 카테고리를 통해 이후 서재별로 저장 및 조회할 수 있도록 지원합니다.
- 다회독의 경우 기존 독서기록에 날짜를 추가함으로써 회독별 독서노트 조회 등 체계적인 관리가 가능합니다.

**(3) 3D책장 조회**
- 등록한 책의 페이지수를 기준으로 두께를 책정하여 3D책장에 렌더링합니다.
- 날짜별로 구분해 읽었던 책을 조회할 수 있습니다.

**(4) 카테고리별 서재 분류**
- 직접 편집한 카테고리별로 서재를 구분할 수 있습니다.
- 이외에 별점순, 최신순, 오래된 순 등 정렬조건을 세부적으로 필터링 할 수 있습니다.

</br>
</br>

## 📚 트러블 슈팅
- [서버이전을 고려한 CI/CD 아키텍처 개선](https://steam-egg.tistory.com/21)
- [SameSite 쿠키 정책 이해 및 필터링 문제 해결](https://steam-egg.tistory.com/27)
- [웹뷰앱에서 다중 로그인 구현하기](https://steam-egg.tistory.com/29)
- [명시적 Null값으로 부분 업데이트(PATCH) 구현하기](https://steam-egg.tistory.com/30)
- [RaspberryPi를 통한 홈서버 구축](https://steam-egg.tistory.com/20)

</br>
</br>

## 📚 시스템 아키텍처
<img src="https://github.com/user-attachments/assets/c85d70e2-652b-4e62-b0ea-73cafb13505a" width="650">

사용자 확대에 따라 서버를 이전해야할 가능성이 있어 서버이전에도 유연하게 대응하는 CI/CD 아키텍처를 구축했습니다.
GithubActions의 Secrets를 통해 모든 환경변수를 관리함으로써 환경변수파일, nginx와 dockercompose의 설정파일 등 직접 수정하고 이전해야했던 파일들을 동적으로 생성해 자동으로 이전되도록 자동화 했습니다.
자세한 내용은 트러블 슈팅을 참고해주세요.

</br>
</br>

## 📚 기술스택
> **Backend** </br>
`Java 17`,  `SpringBoot 3.2.4`,  `JPA`,  `MySQL 8.0.41`, `Redis`

> **CI/CD & Infra** </br>
`GithubAtions`, `Docker`, `Docker-compose`, `NginX`, `portainer`

> **Frontend** </br>
`React`, `React Native`, `TypeScript`, `TanstackQuery`, `Tailwind`, `Three.js`
