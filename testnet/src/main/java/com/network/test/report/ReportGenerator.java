package com.network.test.report;

import com.network.test.core.TestSummary;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 报告生成器
 * 生成网络检测报告
 */
public class ReportGenerator {

    /**
     * 生成报告并输出到控制台
     */
    public void generateReport(List<TestSummary> summaries) {
        System.out.println("\n========================================");
        System.out.println("      网络连通性检测报告");
        System.out.println("========================================");
        System.out.println("生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("检测数量: " + summaries.size());
        System.out.println("========================================\n");

        int allSuccess = 0;
        int partialSuccess = 0;
        int allFailed = 0;

        for (TestSummary summary : summaries) {
            System.out.println(summary.toString());

            if (summary.isAllConnected()) {
                allSuccess++;
            } else if (summary.getSuccessCount() > 0) {
                partialSuccess++;
            } else {
                allFailed++;
            }
        }

        System.out.println("========================================");
        System.out.println("      检测统计汇总");
        System.out.println("========================================");
        System.out.println(String.format("完全联通: %d", allSuccess));
        System.out.println(String.format("部分联通: %d", partialSuccess));
        System.out.println(String.format("完全不通: %d", allFailed));
        System.out.println("========================================\n");
    }

    /**
     * 生成报告并保存到文件
     */
    public void generateReportToFile(List<TestSummary> summaries, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("========================================");
            writer.println("      网络连通性检测报告");
            writer.println("========================================");
            writer.println("生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("检测数量: " + summaries.size());
            writer.println("========================================\n");

            int allSuccess = 0;
            int partialSuccess = 0;
            int allFailed = 0;

            for (TestSummary summary : summaries) {
                writer.println(summary.toString());

                if (summary.isAllConnected()) {
                    allSuccess++;
                } else if (summary.getSuccessCount() > 0) {
                    partialSuccess++;
                } else {
                    allFailed++;
                }
            }

            writer.println("========================================");
            writer.println("      检测统计汇总");
            writer.println("========================================");
            writer.println(String.format("完全联通: %d", allSuccess));
            writer.println(String.format("部分联通: %d", partialSuccess));
            writer.println(String.format("完全不通: %d", allFailed));
            writer.println("========================================\n");

            System.out.println("报告已保存到: " + filePath);

        } catch (IOException e) {
            System.err.println("保存报告失败: " + e.getMessage());
        }
    }
}
