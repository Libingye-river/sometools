package com.network.test.ssh;

/**
 * SSH 命令执行结果
 */
public class SSHResult {
    private boolean success;
    private String output;
    private String error;
    private int exitCode;

    public SSHResult() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public int getExitCode() { return exitCode; }
    public void setExitCode(int exitCode) { this.exitCode = exitCode; }
}
