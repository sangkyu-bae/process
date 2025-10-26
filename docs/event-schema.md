# Progress Tracking Event Schema
전사 서비스 공통 사용자 여정(Progress) 추적 이벤트 스키마

## 1. 목적
본 스키마는 **여러 서비스(대출/회원가입/마이데이터/결제 등)** 에서 발생하는
사용자 **진행 단계, 소요시간, 실패/이탈 원인**을
**표준화된 이벤트 형태로 기록**하기 위한 공통 모델이다.

이 스키마는 아래 목표를 가진다.

- 서비스별로 상이한 플로우를 **의미 단위로 추상화**
- Kafka → Streams → ElasticSearch → Dashboard 를 **공통 처리 가능**하게 함
- 퍼널 분석 / 이탈율 분석 / 병목 지점 파악을 **단일한 방식으로 수행**
- 전사 Observability / Journey Intelligence 체계의 기반이 됨

---

## 2. 이벤트 구조 (JSON Format)

```json
{
  "traceId": "string",
  "service": "string",
  "step": "string",
  "status": "SUCCESS | FAIL | CANCEL | DROPPED",
  "reason": "string or null",
  "meta": {
    "channel": "WEB | MOBILE_APP | MOBILE_WEB",
    "clientVersion": "string",
    "screenId": "string",
    "device": "string"
  },
  "timestamp": 1730560123456
}
```

## 필드 설명

| 필드        | 타입     | 필수 | 설명                                                         |
|-------------|----------|:---:|--------------------------------------------------------------|
| traceId     | string   | ✅  | 사용자 여정의 고유 ID (세션/트랜잭션과 독립적인 흐름 단위)       |
| service     | string   | ✅  | 이벤트 발생 서비스명 (예: LOAN / SIGNUP / MYDATA / PAYMENT) |
| step        | string   | ✅  | 사용자 여정의 의미 단위 단계명                                |
| status      | enum     | ✅  | SUCCESS / FAIL / CANCEL / DROPPED                            |
| reason      | string   | 선택 | 실패/취소 사유 코드 (예: QUALITY_LOW / USER_ABORT 등)        |
| meta        | object   | 선택 | 화면/디바이스/AB테스트 등 분석 문맥 정보                      |
| timestamp   | number   | ✅  | 이벤트 발생 시각 (Epoch milliseconds)                       |

## 3. 공통 Step Vocabulary (도메인 독립 의미 계층)
| Step                | 설명                       | 예시 도메인 적용                  |
| ------------------- | ------------------------ | -------------------------- |
| `IDENTITY_VERIFIED` | 사용자가 본인 여부를 확인한 시점       | CI 인증, OAuth, 로그인 검증       |
| `DATA_COLLECTED`    | 내부/외부 데이터가 수집된 시점        | OCR 완료, CB 조회 완료, 금융데이터 수집 |
| `FORM_SUBMITTED`    | 사용자가 주요 입력/서류 제출을 완료한 시점 | 신청서 제출, 회원가입 정보 작성 완료      |
| `REVIEWED`          | 시스템/심사/검증 로직이 완료된 시점     | 여신심사 승인, AML Risk 검토 승인    |
| `COMPLETED`         | 최종 절차(승인/실행/가입/결제 등) 완료  | 대출 실행 완료 / 가입 완료 / 결제 승인   |
| `FAILED`            | 내부/외부 장애로 단계 실패          | 서버 예외, 외부기관 timeout        |
| `CANCEL`            | 사용자가 명시적으로 중단            | 뒤로가기, 취소 버튼, 닫기            |
| `DROPPED`           | 다음 단계로 일정 시간 넘어가지 않아 이탈  | 화면 이탈, 세션 멈춤, UI 혼란        |

---
## 4. 서비스별 단계 매핑 예시
**4.1 대출 (Loan)**

| 기존 단계        | 공통 Step       | meta.subType 예시 |
|------------------|-----------------|-------------------|
| 본인인증 완료      | IDENTITY_VERIFIED | null              |
| OCR 촬영/인식 완료 | DATA_COLLECTED    | OCR               |
| CB 조회 완료     | DATA_COLLECTED    | CB                |
| 심사 승인        | REVIEWED          | null              |
| 대출 실행        | COMPLETED         | null              |

---
## 5. Status & Reason Code Standardization
**Status 의미 구분**

| Status    | 의미                | 예시                        |
| --------- | ----------------- | ------------------------- |
| `SUCCESS` | 다음 단계로 정상 진입      | OCR → CB 조회로 넘어감          |
| `FAIL`    | 시스템/외부기관 실패       | Timeout, Validation Error |
| `CANCEL`  | 사용자가 의도로 중단       | 닫기, 뒤로가기                  |
| `DROPPED` | 무의도적 이탈 (시간초과/혼란) | OCR 후 2분간 입력 없음           |


**Reason 코드 예시**

| Reason Code                | 설명        |
| -------------------------- | --------- |
| `QUALITY_LOW`              | OCR 화질 불량 |
| `CAMERA_PERMISSION_DENIED` | 권한 거부     |
| `NETWORK_TIMEOUT`          | 네트워크 지연   |
| `USER_ABORT`               | 사용자 취소    |
| `POLICY_REJECT`            | 심사 정책 거절  |

---
## 6. 데이터 파이프라인 흐름

```
Service → ProgressEventSDK → Kafka (progress_event)
→ Kafka Streams (state machine + 이탈/병목 계산)
→ Redis (실시간 상태조회)
→ Elasticsearch (이력 + 퍼널 + 지표 분석)
→ Kibana Dashboard (전환율 / 이탈율 / 병목 Heatmap)
```

---

## 7. 샘플 이벤트

```json
{
  "traceId": "TX2025-001239",
  "service": "LOAN",
  "step": "DATA_COLLECTED",
  "status": "SUCCESS",
  "reason": null,
  "meta": {
    "channel": "MOBILE_WEB",
    "screenId": "ocr_upload",
    "device": "iPhone12",
    "clientVersion": "3.0.1"
  },
  "timestamp": 1730561111123
}

```

