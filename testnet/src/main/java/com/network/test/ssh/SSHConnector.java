package com.network.test.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * SSH 连接器
 * 用于连接到源地址服务器并执行命令
 */
public class SSHConnector {
    private static final Logger logger = LoggerFactory.getLogger(SSHConnector.class);

    private String host;
    private int port;
    private String username;
    private String password;
    private int connectTimeout = 10000;  // 连接超时时间（毫秒）

    public SSHConnector(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public SSHConnector(String host, String username, String password) {
        this(host, 22, username, password);
    }

    /**
     * 通过 SSH 在远程服务器上执行命令
     */
    public SSHResult executeCommand(String command) {
        SSHResult result = new SSHResult();
        Session session = null;
        Channel channel = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(connectTimeout);

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.connect(connectTimeout);

            // 读取命令输出
            InputStream in = channel.getInputStream();
            InputStream err = channel.getExtInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(err));

            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = errReader.readLine()) != null) {
                error.append(line).append("\n");
            }

            result.setOutput(output.toString());
            result.setError(error.toString());
            result.setExitCode(channel.getExitStatus());
            result.setSuccess(true);

            logger.debug("命令执行成功: {} -> {}", command, output.toString());

        } catch (Exception e) {
            logger.error("SSH 命令执行失败: {}", command, e);
            result.setSuccess(false);
            result.setError(e.getMessage());
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }

        return result;
    }

    /**
     * 检测端口连通性（从源服务器检测到目标端口的连接）
     * 使用 nc (netcat) 或 timeout 命令
     */
    public PortCheckResult checkPort(String targetHost, int port, int timeoutSeconds) {
        PortCheckResult result = new PortCheckResult();
        result.setTargetHost(targetHost);
        result.setPort(port);

        // 优先使用 nc 命令（更准确）
        String command = String.format("nc -z -w %d %s %d", timeoutSeconds, targetHost, port);
        logger.info("执行端口检测: {}", command);

        SSHResult sshResult = executeCommand(command);

        if (sshResult.isSuccess() && sshResult.getExitCode() == 0) {
            result.setConnected(true);
            result.setMessage("端口开放");
            logger.info("端口 {}:{} 连通", targetHost, port);
        } else {
            result.setConnected(false);
            result.setMessage("端口不可达");
            logger.warn("端口 {}:{} 不通", targetHost, port);
        }

        return result;
    }

    /**
     * 尝试使用 telnet 检测端口（nc 不可用时的备选方案）
     */
    public PortCheckResult checkPortWithTelnet(String targetHost, int port, int timeoutSeconds) {
        PortCheckResult result = new PortCheckResult();
        result.setTargetHost(targetHost);
        result.setPort(port);

        // 使用 timeout + telnet 组合
        String command = String.format("timeout %d telnet %s %d 2>&1 | grep -E 'Connected|Connection refused|timeout'",
                timeoutSeconds, targetHost, port);
        logger.info("使用 telnet 检测端口: {}", command);

        SSHResult sshResult = executeCommand(command);

        String output = sshResult.getOutput();
        if (output.contains("Connected") || output.contains("Escape character")) {
            result.setConnected(true);
            result.setMessage("端口开放");
        } else {
            result.setConnected(false);
            result.setMessage("端口不可达");
        }

        return result;
    }

    /**
     * 检测到源的 SSH 连接是否可用
     */
    public boolean testConnection() {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(connectTimeout);
            session.disconnect();
            return true;
        } catch (Exception e) {
            logger.error("SSH 连接测试失败: {}:{}", host, port, e);
            return false;
        }
    }

    /**
     * 设置连接超时时间
     */
    public void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }
}
