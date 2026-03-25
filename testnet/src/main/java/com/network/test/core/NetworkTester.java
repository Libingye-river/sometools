package com.network.test.core;

import com.network.test.model.TestItem;
import com.network.test.model.TestResult;
import com.network.test.reader.ExcelReader;
import com.network.test.ssh.PortCheckResult;
import com.network.test.ssh.SSHConnector;
import com.network.test.ssh.SSHResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网络测试核心类
 * 负责执行网络连通性检测
 */
public class NetworkTester {
    private static final Logger logger = LoggerFactory.getLogger(NetworkTester.class);

    private ExcelReader excelReader;
    private Map<String, SSHConnector> sshConnectors = new HashMap<>();
    private int portCheckTimeout = 5;  // 端口检测超时时间（秒）

    public NetworkTester() {
        this.excelReader = new ExcelReader();
    }

    /**
     * 添加 SSH 连接器（源服务器信息）
     */
    public void addSSHConnector(String sourceAddress, String username, String password) {
        SSHConnector connector = new SSHConnector(sourceAddress, username, password);
        sshConnectors.put(sourceAddress, connector);
        logger.info("添加 SSH 连接器: {}", sourceAddress);
    }

    /**
     * 执行所有测试
     */
    public List<TestSummary> runTests(List<TestItem> testItems) {
        List<TestSummary> summaries = new ArrayList<>();

        for (TestItem item : testItems) {
            TestSummary summary = runTest(item);
            summaries.add(summary);
        }

        return summaries;
    }

    /**
     * 执行单个测试项
     */
    public TestSummary runTest(TestItem item) {
        TestSummary summary = new TestSummary();
        summary.setSourceDesc(item.getSourceDesc());
        summary.setTargetDesc(item.getTargetDesc());
        summary.setResults(new ArrayList<>());

        logger.info("开始测试: {} -> {}", item.getSourceDesc(), item.getTargetDesc());

        for (String source : item.getSources()) {
            SSHConnector connector = sshConnectors.get(source);

            if (connector == null) {
                logger.error("未找到源地址 {} 的 SSH 连接配置", source);
                continue;
            }

            for (String target : item.getTargets()) {
                for (String portStr : item.getPorts()) {
                    try {
                        int port = Integer.parseInt(portStr);
                        TestResult result = checkPort(connector, source, target, port);
                        summary.getResults().add(result);
                    } catch (NumberFormatException e) {
                        logger.warn("无效的端口号: {}", portStr);
                    }
                }
            }
        }

        // 统计结果
        summary.setTotalResults(summary.getResults().size());
        long successCount = summary.getResults().stream().filter(TestResult::isConnected).count();
        summary.setSuccessCount((int) successCount);

        // 判断是否全部连通
        summary.setAllConnected(summary.getTotalResults() > 0 &&
                summary.getSuccessCount() == summary.getTotalResults());

        if (summary.isAllConnected()) {
            summary.setMessage(String.format("%s 到 %s 完全联通",
                    item.getSourceDesc(), item.getTargetDesc()));
        } else {
            summary.setMessage(String.format("%s 到 %s 部分不通（成功 %d/%d）",
                    item.getSourceDesc(), item.getTargetDesc(),
                    summary.getSuccessCount(), summary.getTotalResults()));
        }

        logger.info("测试完成: {}", summary.getMessage());

        return summary;
    }

    /**
     * 检测单个端口
     */
    private TestResult checkPort(SSHConnector connector, String source, String target, int port) {
        long startTime = System.currentTimeMillis();

        PortCheckResult checkResult = connector.checkPort(target, port, portCheckTimeout);

        long elapsed = System.currentTimeMillis() - startTime;

        return new TestResult(
                source,
                target,
                port,
                checkResult.isConnected(),
                String.format("耗时: %d ms", elapsed)
        );
    }

    /**
     * 测试所有 SSH 连接是否可用
     */
    public boolean testAllSSHConnections() {
        boolean allSuccess = true;

        for (Map.Entry<String, SSHConnector> entry : sshConnectors.entrySet()) {
            String source = entry.getKey();
            SSHConnector connector = entry.getValue();

            if (!connector.testConnection()) {
                logger.error("SSH 连接失败: {}", source);
                allSuccess = false;
            } else {
                logger.info("SSH 连接成功: {}", source);
            }
        }

        return allSuccess;
    }

    /**
     * 读取 Excel 文件
     */
    public List<TestItem> readExcel(String filePath) {
        return excelReader.readExcelFile(filePath);
    }

    /**
     * 设置端口检测超时时间
     */
    public void setPortCheckTimeout(int timeout) {
        this.portCheckTimeout = timeout;
    }
}
