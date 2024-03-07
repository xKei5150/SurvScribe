package com.insitu.survscribe;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("SP_Data")
public class SP_Data {
    public boolean isHeader;
    @XStreamAlias("depthOfRefusal")
    private String depthOfRefusal;
    @XStreamAlias("nValues")
    private String nValues;
    @XStreamAlias("imagePath")
    private String imagePath;

    // Constructor
    public SP_Data(boolean isHeader, String depthOfRefusal, String nValues, String imagePath) {
        this.isHeader = isHeader;
        this.depthOfRefusal = depthOfRefusal;
        this.nValues = nValues;
        this.imagePath = imagePath;
    }

    public String getDepthOfRefusal() {
        return depthOfRefusal;
    }

    public void setDepthOfRefusal(String depthOfRefusal) {
        this.depthOfRefusal = depthOfRefusal;
    }

    public String getnValues() {
        return nValues;
    }

    public void setnValues(String nValues) {
        this.nValues = nValues;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
