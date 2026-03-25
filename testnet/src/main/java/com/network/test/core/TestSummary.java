package com.network.test.core;

import com.network.test.model.TestResult;

import java.util.List;

/**
 * 测试结果汇总
 */
public class TestSummary {
    private String sourceDesc;     // 访问源描述
    private String targetDesc;     // 访问目标描述
    private List<TestResult> results;  // 详细测试结果
    private int totalResults;      // 总检测数量
    private int successCount;      // 成功数量
    private boolean allConnected;  // 是否全部连通
    private String message;        // 汇总消息

    public TestSummary() {}

    // Getters and Setters
    public String getSourceDesc() { return sourceDesc; }
    public void setSourceDesc(String sourceDesc) { this.sourceDesc = sourceDesc; }

    public String getTargetDesc() { return targetDesc; }
    public void setTargetDesc(String targetDesc) { this.targetDesc = targetDesc; }

    public List<TestResult> getResults() { return results; }
    public void setResults(List<TestResult> results) { this.results = results; }

    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }

    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }

    public boolean isAllConnected() { return allConnected; }
    public void setAllConnected(boolean allConnected) { this.allConnected = allConnected; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append(String.format("访问源: %s\n", sourceDesc));
        sb.append(String.format("访问目标: %s\n", targetDesc));
        sb.append(String.format("检测结果: %s\n", message));
        sb.append("详细结果:\n");

        for (TestResult result : results) {
            sb.append(String.format("  %s\n", result.toString()));
        }

        return sb.toString();
    }
}
