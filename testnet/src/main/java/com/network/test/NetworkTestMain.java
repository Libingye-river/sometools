package com.network.test;

import com.network.test.core.NetworkTester;
import com.network.test.core.TestSummary;
import com.network.test.model.TestItem;
import com.network.test.report.ReportGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络连通性检测工具 - 主程序
 */
public class NetworkTestMain {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("      网络连通性检测工具");
        System.out.println("========================================\n");

        // 配置参数
        String[] excelFiles = {
                "NASP-ACL-20260203-024.xls",
                "NASP-ACL-20260225-017.xls"
        };

        String reportFile = "network_test_report.txt";
        int portCheckTimeout = 5;  // 端口检测超时时间（秒）

        // 创建测试器
        NetworkTester tester = new NetworkTester();
        tester.setPortCheckTimeout(portCheckTimeout);

        // ========== 配置源服务器的 SSH 连接信息 ==========
        // 请根据实际情况修改以下配置
        // 格式：tester.addSSHConnector("源地址", "用户名", "密码");

        // 示例配置（请修改为实际的服务器信息）：
        // tester.addSSHConnector("192.168.1.100", "root", "password");
        // tester.addSSHConnector("192.168.1.101", "admin", "password");
        // tester.addSSHConnector("10.0.0.1", "user", "password");

        // ========== 注意 ==========
        // 1. 需要为 Excel 表格中 F 列的每个源地址配置对应的 SSH 连接
        // 2. 确保 SSH 用户有权限在源服务器上执行 nc/telnet 命令
        // 3. 确保源服务器已安装 nc (netcat) 或 telnet 工具

        // 读取 Excel 文件
        List<TestItem> allItems = new ArrayList<>();
        for (String excelFile : excelFiles) {
            File file = new File(excelFile);
            if (file.exists()) {
                System.out.println("读取文件: " + excelFile);
                List<TestItem> items = tester.readExcel(excelFile);
                allItems.addAll(items);
            } else {
                System.out.println("警告: 文件不存在 - " + excelFile);
            }
        }

        if (allItems.isEmpty()) {
            System.out.println("错误: 未读取到任何测试项，请检查 Excel 文件和配置");
            return;
        }

        System.out.println("\n共读取 " + allItems.size() + " 个测试项\n");

        // 测试 SSH 连接
        System.out.println("测试 SSH 连接...");
        if (!tester.testAllSSHConnections()) {
            System.out.println("警告: 部分 SSH 连接失败，请检查配置\n");
        } else {
            System.out.println("所有 SSH 连接正常\n");
        }

        // 执行测试
        System.out.println("开始网络连通性检测...");
        List<TestSummary> summaries = tester.runTests(allItems);

        // 生成报告
        ReportGenerator reportGenerator = new ReportGenerator();
        reportGenerator.generateReport(summaries);
        reportGenerator.generateReportToFile(summaries, reportFile);

        System.out.println("检测完成！");
    }
}
