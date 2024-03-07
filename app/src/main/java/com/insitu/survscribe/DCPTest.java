package com.insitu.survscribe;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

public class DCPTest {
    @XStreamAlias("siteInfo")
    private SiteInformation siteInfo;
    private List<DCP_RowData> DCPRowDataList;

    // Constructor, getters, and setters

    public SiteInformation getSiteInfo() {
        return siteInfo;
    }

    public void setSiteInfo(SiteInformation siteInfo) {
        this.siteInfo = siteInfo;
    }

    public List<DCP_RowData> getRowDataList() {
        return DCPRowDataList;
    }

    public void setRowDataList(List<DCP_RowData> DCPRowDataList) {
        this.DCPRowDataList = DCPRowDataList;
    }
}
