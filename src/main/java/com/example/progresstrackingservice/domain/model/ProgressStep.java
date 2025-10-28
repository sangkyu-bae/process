package com.example.progresstrackingservice.domain.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사용자 여정의 공통 단계 정의
 * (각 서비스의 세부 단계는 meta.subType 으로 확장)
 */


@Getter @AllArgsConstructor
public enum ProgressStep {

    // ===== 공통 인증/수집 단계 =====
    IDENTITY_VERIFIED("본인인증 완료","IDENTITY_VERIFIED"),
    DATA_COLLECTED("데이터 수집 완료","DATA_COLLECTED"),
    AGREEMENT_SIGNED("약관 동의 완료","AGREEMENT_SIGNED"),

    // ===== 심사/승인 단계 =====
    REVIEWED("심사 승인 완료","REVIEWED"),
    APPROVED("최종 승인 완료","APPROVED"),

    // ===== 실행/완료 단계 =====
    COMPLETED("대출 실행 / 결제 완료","COMPLETED"),
    CANCELED("사용자 취소","CANCELED"),
    FAILED("단계 실패","FAILED");

    private final String description;

    private final String name;

}
