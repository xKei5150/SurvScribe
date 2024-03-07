package com.insitu.survscribe;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

public class FDTest {
    @XStreamAlias("siteInfo")
    private SiteInformation siteInfo;
    @XStreamAlias("FDDataList")
    private List<FD_Data> FDDataList;

    // Constructor
    public FDTest() {

    }

    // Getters and Setters
    public SiteInformation getSiteInfo() {
        return siteInfo;
    }

    public void setSiteInfo(SiteInformation siteInfo) {
        this.siteInfo = siteInfo;
    }

    public List<FD_Data> getFdDataList() {
        return FDDataList;
    }

    public void setFDDataList(List<FD_Data> FDDataList) {
        this.FDDataList = FDDataList;
    }
}
