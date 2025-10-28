package com.example.progresstrackingservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class ProgressEvent {
    private String traceId;
    private String service;
    private ProgressStep step;
    private ProgressStatus status;
    private String reason;
    private Long duration;
    private Instant timestamp;
    private Map<String, Object> meta;

    public void validate() {
        if (traceId == null || service == null || step == null || status == null) {
            throw new InvalidProgressEventException("Missing required fields");
        }
    }

}
