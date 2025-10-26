package com.example.progresstrackingservice.domain.model;

public enum ProgressStatus {
    SUCCESS,   // 단계 수행 성공
    FAIL,      // 실패 발생
    CANCEL,    // 사용자가 취소
    DROPPED;   // 이탈(다음 단계 미진행)

    public static boolean isFinal(ProgressStatus status) {
        return status == SUCCESS || status == FAIL || status == CANCEL;
    }
}
