# 网络连通性检测工具

## 功能说明

本工具用于检测服务器 A（源地址）到服务器 B（目标地址）之间指定端口的网络连通性。

**应用场景：**
- 部署在一台单独的服务器上（检测服务器）
- 检测服务器与源地址网络连通
- 检测服务器与目标地址网络不通
- 通过 SSH 连接到源地址，从源端执行网络检测

## 项目结构

```
testnet/
├── pom.xml                                    # Maven 配置文件
├── config.properties                           # 配置文件模板
├── README.md                                  # 使用说明
├── NASP-ACL-20260203-024.xls                   # 测试数据文件 1
├── NASP-ACL-20260225-017.xls                   # 测试数据文件 2
└── src/main/java/com/network/test/
    ├── NetworkTestMain.java                   # 主程序入口
    ├── model/
    │   ├── TestItem.java                      # 测试项模型
    │   └── TestResult.java                    # 测试结果模型
    ├── reader/
    │   └── ExcelReader.java                   # Excel 文件读取器
    ├── ssh/
    │   ├── SSHConnector.java                  # SSH 连接器
    │   ├── SSHResult.java                     # SSH 执行结果
    │   └── PortCheckResult.java               # 端口检测结果
    ├── core/
    │   ├── NetworkTester.java                 # 网络测试核心类
    │   └── TestSummary.java                   # 测试结果汇总
    └── report/
        └── ReportGenerator.java                # 报告生成器
```

## Excel 文件格式

工具读取 NASP-ACL 格式的 Excel 文件，列对应关系：

| 列 | 说明          | 示例       |
|----|---------------|------------|
| E  | 访问源描述    | 业务服务器A |
| F  | 访问源地址    | 192.168.1.100<br>192.168.1.101 |
| H  | 访问目标描述  | 数据库服务器B |
| I  | 访问目标地址  | 10.0.0.1<br>10.0.0.2 |
| L  | 端口          | 3306<br>8080 |

## 使用步骤

### 1. 编译项目

```bash
cd D:/claude-code/testnet
mvn clean package
```

### 2. 配置 SSH 连接

编辑 `NetworkTestMain.java`，配置源服务器的 SSH 连接信息：

```java
// 在 main 方法中添加以下配置
tester.addSSHConnector("192.168.1.100", "root", "password");
tester.addSSHConnector("192.168.1.101", "admin", "password");
```

**注意：** host 地址必须与 Excel 文件中 F 列的地址完全一致。

### 3. 运行程序

```bash
mvn exec:java -Dexec.mainClass="com.network.test.NetworkTestMain"
```

或使用编译后的 jar：

```bash
java -cp target/network-test-1.0-SNAPSHOT.jar com.network.test.NetworkTestMain
```

## 输出结果

程序会生成两种输出：

### 1. 控制台输出
实时显示检测进度和结果摘要。

### 2. 报告文件 (network_test_report.txt)
包含完整的检测报告，格式如下：

```
========================================
      网络连通性检测报告
========================================
生成时间: 2026-03-22 15:30:00
检测数量: 5
========================================

========================================
访问源: 业务服务器A
访问目标: 数据库服务器B
检测结果: 业务服务器A 到 数据库服务器B 完全联通
详细结果:
  192.168.1.100 -> 10.0.0.1:3306 [连通] 耗时: 45 ms
  192.168.1.101 -> 10.0.0.2:3306 [连通] 耗时: 52 ms
========================================

========================================
      检测统计汇总
========================================
完全联通: 3
部分联通: 1
完全不通: 1
========================================
```

## 依赖要求

### 检测服务器
- Java 11 或更高版本
- Maven 3.6+

### 源服务器
- SSH 服务开启
- 已安装 `nc` (netcat) 工具（推荐）
  - Ubuntu/Debian: `sudo apt-get install netcat`
  - CentOS/RHEL: `sudo yum install nc`
- 或 `telnet` 工具作为备选

## 注意事项

1. 确保 Excel 文件与程序在同一目录下
2. 确保所有源地址的 SSH 连接信息已正确配置
3. 确保 SSH 用户有执行 `nc` 或 `telnet` 命令的权限
4. 端口检测超时时间默认为 5 秒，可根据需要调整
5. 本工具仅做生产前的简单网络验证，不用于长期监控
