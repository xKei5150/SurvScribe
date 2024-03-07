package com.insitu.survscribe;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

public class SPTest {
    @XStreamAlias("siteInfo")
    private SiteInformation siteInfo;
    @XStreamAlias("SPDataList")
    private List<SP_Data> SPDataList;

    public SiteInformation getSiteInfo() {
        return siteInfo;
    }

    public void setSiteInfo(SiteInformation siteInfo) {
        this.siteInfo = siteInfo;
    }

    public List<SP_Data> getSPDataList() {
        return SPDataList;
    }

    public void setSPDataList(List<SP_Data> SPDataList) {
        this.SPDataList = SPDataList;
    }
}
