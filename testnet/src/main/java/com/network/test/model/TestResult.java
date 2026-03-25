package com.network.test.model;

/**
 * 单个端口检测结果模型
 */
public class TestResult {
    private String source;        // 源地址
    private String target;        // 目标地址
    private int port;            // 端口
    private boolean connected;    // 是否连通
    private String message;      // 检测消息（如耗时、错误信息等）

    public TestResult() {}

    public TestResult(String source, String target, int port, boolean connected, String message) {
        this.source = source;
        this.target = target;
        this.port = port;
        this.connected = connected;
        this.message = message;
    }

    // Getters and Setters
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return String.format("%s -> %s:%d [%s] %s",
                source, target, port,
                connected ? "连通" : "不通",
                message != null ? message : "");
    }
}
