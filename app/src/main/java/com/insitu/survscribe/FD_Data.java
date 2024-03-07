package com.insitu.survscribe;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("FD_Data") // Alias for the whole class
public class FD_Data {

    @XStreamAlias("timeStarted")
    private String timeStarted;

    @XStreamAlias("timeFinished")
    private String timeFinished;

    @XStreamAlias("soilTypes")
    private String soilTypes;

    @XStreamAlias("massOfDrySoilFromHole")
    private String massOfDrySoilFromHole;

    @XStreamAlias("initialMassOfSandAndJar")
    private String initialMassOfSandAndJar;

    @XStreamAlias("finalMassOfSandAndJar")
    private String finalMassOfSandAndJar;

    @XStreamAlias("massOfSandFillTheHole")
    private String massOfSandFillTheHole;
    @XStreamAlias("remarks")
    private String remarks;

    public FD_Data(String timeStarted, String timeFinished, String soilTypes, String massOfDrySoilFromHole, String initialMassOfSandAndJar, String finalMassOfSandAndJar, String massOfSandFillTheHole) {
        this.timeStarted = timeStarted;
        this.timeFinished = timeFinished;
        this.soilTypes = soilTypes;
        this.massOfDrySoilFromHole = massOfDrySoilFromHole;
        this.initialMassOfSandAndJar = initialMassOfSandAndJar;
        this.finalMassOfSandAndJar = finalMassOfSandAndJar;
        this.massOfSandFillTheHole = massOfSandFillTheHole;
    }

    public String getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(String timeStarted) {
        this.timeStarted = timeStarted;
    }

    public String getTimeFinished() {
        return timeFinished;
    }

    public void setTimeFinished(String timeFinished) {
        this.timeFinished = timeFinished;
    }

    public String getSoilTypes() {
        return soilTypes;
    }

    public void setSoilTypes(String soilTypes) {
        this.soilTypes = soilTypes;
    }

    public String getMassOfDrySoilFromHole() {
        return massOfDrySoilFromHole;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setMassOfDrySoilFromHole(String massOfDrySoilFromHole) {
        this.massOfDrySoilFromHole = massOfDrySoilFromHole;
    }

    public String getInitialMassOfSandAndJar() {
        return initialMassOfSandAndJar;
    }

    public void setInitialMassOfSandAndJar(String initialMassOfSandAndJar) {
        this.initialMassOfSandAndJar = initialMassOfSandAndJar;
    }

    public String getFinalMassOfSandAndJar() {
        return finalMassOfSandAndJar;
    }

    public void setFinalMassOfSandAndJar(String finalMassOfSandAndJar) {
        this.finalMassOfSandAndJar = finalMassOfSandAndJar;
    }

    public String getMassOfSandFillTheHole() {
        return massOfSandFillTheHole;
    }

    public void setMassOfSandFillTheHole(String massOfSandFillTheHole) {
        this.massOfSandFillTheHole = massOfSandFillTheHole;
    }
}
