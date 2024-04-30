package com.insitu.survscribe;

import java.util.List;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class SiteInformation {
    @XStreamAlias("soilImage")
    private String soilImage;
    @XStreamAlias("testType")
    private String testType;
    @XStreamAlias("companyName")
    private String companyName;
    @XStreamAlias("dateConducted")
    private String dateConducted;
    @XStreamAlias("projectName")
    private String projectName;
    @XStreamAlias("time")
    private String time;
    @XStreamAlias("ownerLessor")
    private String ownerLessor;
    @XStreamAlias("teamLeader")
    private String teamLeader;
    @XStreamAlias("location")
    private String location;
    @XStreamAlias("teamMembers")
    private List<String> teamMembers;
    @XStreamAlias("weather")
    private String weather;
    @XStreamAlias("materialClassification")
    private String materialClassification;
    // Constructor
    public SiteInformation(String soilImage, String testType, String companyName, String dateConducted, String projectName,
                           String time, String ownerLessor, String teamLeader, String location,
                           List<String> teamMembers, String weather, String materialClassification) {
        this.soilImage = soilImage;
        this.testType = testType;
        this.companyName = companyName;
        this.dateConducted = dateConducted;
        this.projectName = projectName;
        this.time = time;
        this.ownerLessor = ownerLessor;
        this.teamLeader = teamLeader;
        this.location = location;
        this.teamMembers = teamMembers;
        this.weather = weather;
        this.materialClassification = materialClassification;
    }

    public String getSoilImage() {
        return soilImage;
    }

    public void setSoilImage(String soilImage) {
        this.soilImage = soilImage;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDateConducted() {
        return dateConducted;
    }

    public void setDateConducted(String dateConducted) {
        this.dateConducted = dateConducted;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOwnerLessor() {
        return ownerLessor;
    }

    public void setOwnerLessor(String ownerLessor) {
        this.ownerLessor = ownerLessor;
    }

    public String getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(String teamLeader) {
        this.teamLeader = teamLeader;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<String> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getMaterialClassification() {
        return materialClassification;
    }

    public void setMaterialClassification(String materialClassification) {
        this.materialClassification = materialClassification;
    }
}