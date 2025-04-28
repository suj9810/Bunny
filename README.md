# 🐰🥕 Bunny Delivery - Outsourcing Project

본 프로젝트는 JPQL, JPA, JWT, Redis 등을 활용한 딜리버리 아웃소싱 팀 프로젝트입니다.  
로그인/회원관리, 가게관리, 메뉴관리, 주문관리, 리뷰관리, 검색, 즐겨찾기와 같은 딜리버리 기능을 구현했습니다.

---

## 🤖 설정 
### 🛠 작업 환경

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- MySQL (환경변수 설정)
- Redis (Docker 사용)
- JWT
- Spring Security
- JUnit5, Mockito

### 🤝 협업 Tool

- GitHub
- Notion

### ⚙️ 환경 변수
- **DB**: MySQL 접속 정보 환경변수 관리
- **JWT**: Secret Key를 환경변수로 관리하여 보안 강화
- **Redis**: Docker 컨테이너 사용

---

## 💡 프로젝트 특징

- 소프트 삭제(Soft Delete) 기법 적용
- JWT + Spring Security 기반 인증/인가 처리
- 단위 테스트 (JUnit5, Mockito) 작성 및 예외 상황 커버
- RefreshToken을 Redis에 저장하여 세션 관리 구현

---

## 🔗 링크(깃허브, 명세서)

- [팀 프로젝트 GitHub Repository](https://github.com/suj9810/Bunny.git)
- [API 명세서 (Notion 링크)](https://www.notion.so/teamsparta/19-API-1dd2dc3ef514809798b6dca67b18e0e9)


### API Endpoints

1. 회원/로그인 API

| HTTP | URI | 설명 |
|:---|:---|:---|
| POST | /auths/signup | 회원가입 |
| POST | /auths/login | 로그인 |
| POST | /auths/logout | 로그아웃 |
| DELETE | /auths/me | 회원 탈퇴 |
| GET | /users/{id} | 회원 조회 |
| PUT | /users/me | 회원 정보 수정 |
| PATCH | /users/{id}/update-password | 비밀번호 수정 |

2. 가게 API
| HTTP | URI | 설명 |
|:---|:---|:---|
| POST | /srotes | 가게 등록 |
| PUT | /srotes/{srotesId} | 가게 수정 |
| GET | /srotes?categories=categories | 전체가게 조회 |
| GET | /srotes/{srotesId} | 단일 가게 조회 |
| PATCH | /srotes/{srotesId} | 가게 폐업 |

3. 즐겨찾기 API
| POST | /favorites/add?storeId={store_id} | 즐겨찾기 추가 |
| DELETE | /favorites/add?storeId={store_id} | 즐겨찾기 삭제 |

4. 메뉴 API
| HTTP | URI | 설명 |
|:---|:---|:---|
| POST | /menus | 메뉴 등록 |
| PUT | /menus/{menuId}  | 메뉴 수정 |
| DELETE | /menus/{menuId} | 메뉴 삭제 |

5. 주문 API
| HTTP | URI | 설명 |
|:---|:---|:---|
| POST | /orders | 주문 생성 |
| GET | /orders | 주문 조회 |
| DELETE | /orders/{orderId} | 주문 취소 |
| PATCH | /orders/{orderId} | 주문 상태 변경 |

6. 장바구니 API
| HTTP | URI | 설명 |
|:---|:---|:---|
| POST | /carts | 장바구니 추가 |
| GET |  | 장바구니 조회 |
| DELETE | /carts/clear | 장바구니 삭제 |
| DELETE | /carts/clear | 장바구니 특정 메뉴 삭제 |

7. 리뷰 API
| HTTP | URI | 설명 |
|:---|:---|:---|
| POST | /reviews | 리뷰생성 |
| POST | /owner-comments | 사장님 리뷰 생성 |
| GET | /reviews?storeId={storeId}&minRating={minRating}&maxRating={maxRating}&page={page}&size={size} | 리뷰 조회 |
| DELETE | /reviews | 리뷰 삭제 |
| DELETE | /owner-comments | 사장님 리뷰 삭제 |

8. 검색 API
| HTTP | URI | 설명 |
|:---|:---|:---|
| POST | /searches | 검색/기록저장 |
| POST | /searches/user | 검색/기록저장(회원) |
| GET | /searches/trending | 인기 검색기록 조회 |
| GET | /searches/histories | 내 검색기록 조회 |
| DELETE | /searches/user | 내 검색기록 삭제 |



### API Status Codes


1. 회원/로그인

| API | 성공 | 실패 (예시) |
|:---|:---|:---|
| 회원가입 | 201 CREATED | 400 BAD_REQUEST(형식 오류) ,409 CONFLICT(이메일 중복) |
| 로그인 | 200 OK | 401 UNAUTHORIZED(비밀번호 불일치), 404 NOT_FOUND(유저 없음) |
| 로그아웃 | 200 OK | 401 UNAUTHORIZED(로그인 필요) |
| 회원탈퇴 | 200 OK | 400 UNAUTHORIZED(비밀번호 불일치) |
| 회원 조회 | 200 OK | 404 NOT_FOUND(유저 없음) |
| 회원 정보 수정 | 200 OK | - |
| 비밀번호 수정 | 200 OK | 400 BAD_REQUEST(비밀번호 불일치), 401 UNAUTHORIZED(동일 비밀번호) |


2. 가게 
| API | 성공 | 실패 (예시) |
|:---|:---|:---|
| 가게 등록 | 201 CREATED | 403 FORBIDDEN(권한 없음) |
| 가게 수정 | 200 OK | 404 NOT_FOUND(가게 없음) |
| 전체 가게 조회 | 200 OK | 404 NOT_FOUND(가게 없음) |
| 단일 가게 조회 | 200 OK | 404 NOT_FOUND(가게 없음) |
| 전체 가게 조회(유저) | 200 OK | 404 NOT_FOUND(가게 없음) |
| 단일 가게 조회(유저) | 200 OK | 404 NOT_FOUND(가게 없음) |
| 가게 폐업 | 200 OK | 403 FORBIDDEN(권한 없음) |

3. 즐겨찾기
| 즐겨찾기 추가 | 200 OK | - |
| 즐겨찾기 삭제 | 200 OK | - |

4. 메뉴 API
| HTTP | URI | 설명 |
|:---|:---|:---|
| 메뉴 등록 | 201 CREATED | 400 BAD_REQUEST(요청 오류), 401 UNAUTHORIZED(사용자 인증 x), 403 FORBIDDEN(사장님 권한 x), 404 NOT_FOUN(가게, 메뉴 x) |
| 메뉴 수정 | 200 OK | 400 BAD_REQUEST(요청 오류), 401 UNAUTHORIZED(사용자 인증 x), 403 FORBIDDEN(사장님 권한 x), 404 NOT_FOUN(가게, 메뉴 x) |
| 메뉴 삭제 | 204 OK | 400 BAD_REQUEST(요청 오류), 401 UNAUTHORIZED(사용자 인증 x), 403 FORBIDDEN(사장님 권한 x), 404 NOT_FOUN(가게, 메뉴 x) |

5. 주문 API
| HTTP | URI | 설명 |
|:---|:---|:---|
| 주문 생성 | 201 CREATED | 401 UNAUTHORIZED(최소금액, 가게영업시간 외), 404 NOT_FOUND(없는 메뉴) |
| 주문 조회 | 200 OK | - |
| 주문 취소 | 200 OK | 401 UNAUTHORIZED(주문상태 변경으로 취소 불가) |
| 주문 상태 변경 | 200 OK | 403 FORBIDDEN(사장님만 변경) |

6. 장바구니 API
| HTTP | URI | 설명 |
|:---|:---|:---|
| 장바구니 추가 | 201 CREATED | - |
| 장바구니 조회 | 200 OK | - |
| 장바구니 삭제 | 200 OK | - |
| 장바구니 특정 메뉴 삭제 | 200 OK | - |

7. 리뷰 API
| HTTP | URI | 설명 |
|:---|:---|:---|
| 리뷰생성 | 201 CREATED | 400 BAD_REQUEST(완료 상태 x), 403 FORBIDDEN(내 주문 x) 409 CONFLICT(리뷰중복) |
| 사장님 리뷰 생성 | 201 CREATED | 400 BAD_REQUEST(요청 오류), 403 FORBIDDEN(가게 사장 x) | 
| 리뷰 조회 | 200 OK | 400 BAD_REQUEST(요청 오류) |
| 리뷰 삭제 | 200 OK | 400 BAD_REQUEST(요청 오류) |
| 사장님 리뷰 삭제 | 200 OK | 400 BAD_REQUEST(요청 오류) |

8. 검색
| API | 성공 | 실패 (예시) |
|:---|:---|:---|
| 검색/기록저장 | 200 OK | 204 NO_CONTENT | 
| 검색/기록저장(회원) | 200 OK | 204 NO_CONTENT |
| 인기 검색기록 조회 | 200 OK | 204 NO_CONTENT |
| 내 검색기록 조회 | 200 OK | 204 NO_CONTENT |
| 유저 검색기록 삭제 | 200 OK | 204 NO_CONTENT |

---

## 🧪 테스트 코드

- JUnit5, Mockito 기반 단위 테스트 작성
- 테스트 경로: `src/java/test`
- 커버 기능:
  - 로그인/회원 : 회원가입/탈퇴, 로그인, 로그아웃, 비밀번호/회원정보 수정
  - 가게 : 가게 등록, 수정, 조회(전체, 단일), 폐업, 즐겨찾기
  - 메뉴 : 메뉴 생성, 수정, 삭제
  - 주문 : 주문 생성, 조회, 취소, 상태 변경
  - 장바구니 : 장바구니 추가, 조회, 삭제, 특정메뉴 삭제
  - 리뷰 : 리뷰 생성(사용자, 사장님), 조회, 리뷰 삭제(사용자, 사장님)
  - 검색 : 검색/기록저장(회원, 비회원), 인기 검색어 기록 조회, 내 검색기록 조회/삭제

---

## 📂 폴더 구조

```
+---main
|   +---generated
|   +---java
|   |   \---sparta
|   |       \---bunny
|   |           +---common
|   |           |   +---aop
|   |           |   +---audit
|   |           |   +---config
|   |           |   +---exception
|   |           |   |   +---dto
|   |           |   |   \---handler
|   |           |   +---response
|   |           |   +---S3
|   |           |   +---service
|   |           |   \---util
|   |           \---domain
|   |               +---auth
|   |               |   +---controller
|   |               |   +---dto
|   |               |   +---jwt
|   |               |   +---repository
|   |               |   \---service
|   |               +---cart
|   |               |   +---controller
|   |               |   +---dto
|   |               |   +---entity
|   |               |   \---service
|   |               +---menu
|   |               |   +---code
|   |               |   +---controller
|   |               |   +---dto
|   |               |   |   +---request
|   |               |   |   \---response
|   |               |   +---entity
|   |               |   +---enums
|   |               |   +---exception
|   |               |   +---repository
|   |               |   \---service
|   |               +---order
|   |               |   +---controller
|   |               |   +---dto
|   |               |   +---entity
|   |               |   +---enums
|   |               |   +---repository
|   |               |   \---service
|   |               +---review
|   |               |   +---code
|   |               |   +---controller
|   |               |   +---dto
|   |               |   |   +---request
|   |               |   |   \---response
|   |               |   +---entity
|   |               |   +---exception
|   |               |   +---repository
|   |               |   \---service
|   |               +---search
|   |               |   +---code
|   |               |   +---controller
|   |               |   +---dto
|   |               |   |   +---request
|   |               |   |   \---response
|   |               |   +---entity
|   |               |   +---exception
|   |               |   +---repository
|   |               |   \---service
|   |               +---stores
|   |               |   +---code
|   |               |   +---controller
|   |               |   +---dto
|   |               |   |   +---request
|   |               |   |   \---response
|   |               |   +---entity
|   |               |   +---exception
|   |               |   +---repository
|   |               |   \---service
|   |               \---user
|   |                   +---code
|   |                   +---controller
|   |                   +---dto
|   |                   |   +---request
|   |                   |   \---response
|   |                   +---entity
|   |                   +---exception
|   |                   +---repository
|   |                   \---service
|   \---resources
|       +---static
|       \---templates
\---test
    \---java
        \---sparta
            \---bunny
                \---domain
                    +---auth
                    |   \---service
                    +---cart
                    |   \---service
                    |       \---review
                    |           \---service
                    +---menu
                    |   \---service
                    +---review
                    |   \---service
                    \---user
                        \---service
```

---

## 👥 작성자

- Developed by **19조 : 임욱호, 신은주, 김하늘, 박형우, 손혜빈, 백종현**

---
