package com.neusoft.qiangzi.remotecontrol;

public class HomeState {
    private boolean ketingLightOn;
    private boolean woshiLightOn;
    private boolean airconLightOn;
    private int airconTemp;

    public boolean isKetingLightOn() {
        return ketingLightOn;
    }

    public void setKetingLightOn(boolean ketingLightOn) {
        this.ketingLightOn = ketingLightOn;
    }

    public boolean isWoshiLightOn() {
        return woshiLightOn;
    }

    public void setWoshiLightOn(boolean woshiLightOn) {
        this.woshiLightOn = woshiLightOn;
    }

    public boolean isAirconLightOn() {
        return airconLightOn;
    }

    public void setAirconOn(boolean airconLightOn) {
        this.airconLightOn = airconLightOn;
    }

    public int getAirconTemp() {
        return airconTemp;
    }

    public void setAirconTemp(int airconTemp) {
        this.airconTemp = airconTemp;
    }
}
