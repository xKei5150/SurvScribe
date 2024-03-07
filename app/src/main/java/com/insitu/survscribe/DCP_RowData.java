package com.insitu.survscribe;
import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("RowData")
public class DCP_RowData {
    @XStreamAlias("isHeader")
    boolean isHeader;
    @XStreamAlias("blowsValue")
    String blowsValue;
    @XStreamAlias("penetrationValue")
    String penetrationValue;
    @XStreamAlias("differenceValue")
    String differenceValue;

    public DCP_RowData(boolean isHeader, String blowsValue, String penetrationValue, String differenceValue) {
        this.isHeader = isHeader;
        this.blowsValue = blowsValue;
        this.penetrationValue = penetrationValue;
        this.differenceValue = differenceValue;
    }

    // Getters
    public boolean isHeader() {
        return isHeader;
    }

    public String getBlowsValue() {
        return blowsValue;
    }

    public String getPenetrationValue() {
        return penetrationValue;
    }

    public String getDifferenceValue() {
        return differenceValue;
    }

    // Setters
    public void setHeader(boolean header) {
        isHeader = header;
    }

    public void setBlowsValue(String blowsValue) {
        this.blowsValue = blowsValue;
    }

    public void setPenetrationValue(String penetrationValue) {
        this.penetrationValue = penetrationValue;
    }

    public void setDifferenceValue(String differenceValue) {
        this.differenceValue = differenceValue;
    }
}