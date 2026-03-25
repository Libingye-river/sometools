package com.network.test.reader;

import com.network.test.model.TestItem;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 文件读取器
 * 读取 NASP-ACL 表格中的网络访问配置
 */
public class ExcelReader {
    private static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);

    // 列索引（0-based）
    private static final int COL_SOURCE_DESC = 4;    // E列
    private static final int COL_SOURCE_ADDR = 5;    // F列
    private static final int COL_TARGET_DESC = 7;    // H列
    private static final int COL_TARGET_ADDR = 8;    // I列
    private static final int COL_PORTS = 11;         // L列

    /**
     * 读取 Excel 文件中的所有测试项
     */
    public List<TestItem> readExcelFile(String filePath) {
        List<TestItem> items = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            logger.error("文件不存在: {}", filePath);
            return items;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);  // 读取第一个工作表

            // 跳过表头，从第2行开始读取
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (row == null) continue;

                TestItem item = readTestItem(row);
                if (item != null) {
                    items.add(item);
                    logger.info("读取测试项: {}", item);
                }
            }

            logger.info("共读取 {} 个测试项", items.size());

        } catch (Exception e) {
            logger.error("读取 Excel 文件失败: {}", filePath, e);
        }

        return items;
    }

    /**
     * 从单行读取测试项
     */
    private TestItem readTestItem(Row row) {
        try {
            String sourceDesc = getCellValue(row, COL_SOURCE_DESC);
            String sourceAddrStr = getCellValue(row, COL_SOURCE_ADDR);
            String targetDesc = getCellValue(row, COL_TARGET_DESC);
            String targetAddrStr = getCellValue(row, COL_TARGET_ADDR);
            String portsStr = getCellValue(row, COL_PORTS);

            // 如果关键字段为空，跳过该行
            if (isBlank(sourceAddrStr) || isBlank(targetAddrStr)) {
                return null;
            }

            List<String> sources = parseAddresses(sourceAddrStr);
            List<String> targets = parseAddresses(targetAddrStr);
            List<String> ports = parsePorts(portsStr);

            return new TestItem(sourceDesc, sources, targetDesc, targets, ports);

        } catch (Exception e) {
            logger.warn("读取行 {} 数据失败", row.getRowNum(), e);
            return null;
        }
    }

    /**
     * 获取单元格值
     */
    private String getCellValue(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }

    /**
     * 判断字符串是否为空
     */
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 解析地址列表（支持多个地址，用换行、逗号、分号分隔）
     */
    private List<String> parseAddresses(String addrStr) {
        List<String> addresses = new ArrayList<>();
        if (isBlank(addrStr)) {
            return addresses;
        }

        // 移除换行符
        addrStr = addrStr.replace("\n", ",").replace("\r", ",");

        String[] parts = addrStr.split("[,;]");
        for (String part : parts) {
            String addr = part.trim();
            if (!addr.isEmpty()) {
                addresses.add(addr);
            }
        }

        return addresses;
    }

    /**
     * 解析端口列表（支持多个端口，用换行、逗号、分号分隔）
     */
    private List<String> parsePorts(String portsStr) {
        List<String> ports = new ArrayList<>();
        if (isBlank(portsStr)) {
            return ports;
        }

        // 移除换行符
        portsStr = portsStr.replace("\n", ",").replace("\r", ",");

        String[] parts = portsStr.split("[,;]");
        for (String part : parts) {
            String port = part.trim();
            if (!port.isEmpty()) {
                ports.add(port);
            }
        }

        return ports;
    }
}
