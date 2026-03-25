package com.network.test;

import com.network.test.core.NetworkTester;
import com.network.test.model.TestItem;

import java.io.File;
import java.util.List;

/**
 * Excel 数据读取测试
 * 直接读取真实的 Excel 文件并展示解析结果
 */
public class ExcelDataTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("      Excel 数据读取测试");
        System.out.println("========================================\n");

        NetworkTester tester = new NetworkTester();

        // 读取 Excel 文件
        String[] excelFiles = {
                "D:/claude-code/testnet/NASP-ACL-20260203-024.xls",
                "D:/claude-code/testnet/NASP-ACL-20260225-017.xls"
        };

        List<TestItem> allItems = new java.util.ArrayList<>();

        for (String excelFile : excelFiles) {
            File file = new File(excelFile);
            if (file.exists()) {
                System.out.println("========================================");
                System.out.println("读取文件: " + new File(excelFile).getName());
                System.out.println("========================================\n");

                List<TestItem> items = tester.readExcel(excelFile);
                allItems.addAll(items);

                System.out.println("共读取 " + items.size() + " 个测试项\n");

                // 显示每个测试项的详细信息
                for (int i = 0; i < items.size(); i++) {
                    TestItem item = items.get(i);
                    System.out.println("--- 测试项 " + (i + 1) + " ---");
                    System.out.println("访问源描述: " + item.getSourceDesc());
                    System.out.println("访问源地址: " + item.getSources());
                    System.out.println("访问目标描述: " + item.getTargetDesc());
                    System.out.println("访问目标地址: " + item.getTargets());
                    System.out.println("端口: " + item.getPorts());

                    // 计算检测组合数
                    int totalCombinations = item.getSources().size() *
                                           item.getTargets().size() *
                                           item.getPorts().size();
                    System.out.println("需要检测的组合数: " + totalCombinations);
                    System.out.println();
                }

            } else {
                System.out.println("文件不存在: " + excelFile);
            }
        }

        // 统计汇总
        System.out.println("========================================");
        System.out.println("      数据汇总");
        System.out.println("========================================");
        System.out.println("总测试项数: " + allItems.size());

        int totalSources = 0;
        int totalTargets = 0;
        int totalPorts = 0;
        int totalCombinations = 0;

        for (TestItem item : allItems) {
            totalSources += item.getSources().size();
            totalTargets += item.getTargets().size();
            totalPorts += item.getPorts().size();
            totalCombinations += item.getSources().size() *
                               item.getTargets().size() *
                               item.getPorts().size();
        }

        System.out.println("总源地址数: " + totalSources);
        System.out.println("总目标地址数: " + totalTargets);
        System.out.println("总端口数: " + totalPorts);
        System.out.println("总检测组合数: " + totalCombinations);
        System.out.println("========================================\n");
    }
}
