package com.example.progresstrackingservice.infra.stream;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
public class StepStatus {
    private long totalCount;
    private long successCount;
    private long failCount;
    private double successRate;

    public StepStatus(long total, long success, long fail) {
        this.totalCount = total;
        this.successCount = success;
        this.failCount = fail;
        recalculate();
    }

    public void recalculate() {
        if (totalCount == 0) this.successRate = 0.0;
        else this.successRate = (successCount * 100.0) / totalCount;
    }

    @Override
    public String toString() {
        return String.format(
                "StepStats{total=%d, success=%d, fail=%d, rate=%.2f%%}",
                totalCount, successCount, failCount, successRate
        );
    }
}
