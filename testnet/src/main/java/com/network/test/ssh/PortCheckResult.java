package com.network.test.ssh;

/**
 * 端口检测结果
 */
public class PortCheckResult {
    private String targetHost;
    private int port;
    private boolean connected;
    private String message;

    public PortCheckResult() {}

    public PortCheckResult(String targetHost, int port, boolean connected, String message) {
        this.targetHost = targetHost;
        this.port = port;
        this.connected = connected;
        this.message = message;
    }

    public String getTargetHost() { return targetHost; }
    public void setTargetHost(String targetHost) { this.targetHost = targetHost; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return String.format("%s:%d [%s] %s", targetHost, port, connected ? "连通" : "不通", message);
    }
}
