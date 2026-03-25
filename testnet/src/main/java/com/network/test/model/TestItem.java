package com.network.test.model;

import java.util.List;

/**
 * 网络检测项数据模型
 */
public class TestItem {
    private String sourceDesc;      // E列：访问源描述
    private List<String> sources;    // F列：访问源地址
    private String targetDesc;      // H列：访问目标描述
    private List<String> targets;    // I列：访问目标地址
    private List<String> ports;      // L列：端口

    public TestItem() {}

    public TestItem(String sourceDesc, List<String> sources,
                   String targetDesc, List<String> targets, List<String> ports) {
        this.sourceDesc = sourceDesc;
        this.sources = sources;
        this.targetDesc = targetDesc;
        this.targets = targets;
        this.ports = ports;
    }

    // Getters and Setters
    public String getSourceDesc() { return sourceDesc; }
    public void setSourceDesc(String sourceDesc) { this.sourceDesc = sourceDesc; }

    public List<String> getSources() { return sources; }
    public void setSources(List<String> sources) { this.sources = sources; }

    public String getTargetDesc() { return targetDesc; }
    public void setTargetDesc(String targetDesc) { this.targetDesc = targetDesc; }

    public List<String> getTargets() { return targets; }
    public void setTargets(List<String> targets) { this.targets = targets; }

    public List<String> getPorts() { return ports; }
    public void setPorts(List<String> ports) { this.ports = ports; }

    @Override
    public String toString() {
        return "TestItem{" +
                "sourceDesc='" + sourceDesc + '\'' +
                ", sources=" + sources +
                ", targetDesc='" + targetDesc + '\'' +
                ", targets=" + targets +
                ", ports=" + ports +
                '}';
    }
}
