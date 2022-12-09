package com.infolitz.commwithc.shared.wrapper;

public class PortflagWrapper {
    private static int portvalue; //static keyword is given to access both this variable from C and java
    private static Float speedvalue;
    private static Float rpmvalue;

    /*public PortflagWrapper(*//*int value*//*) {
//        setPort(value);
    }*/

    public int getPort() {
        return portvalue;
    }

    public void setPort(int pvalue) {
        PortflagWrapper.portvalue = pvalue;
    }
    public Float getSpeed() {
        return speedvalue;
    }

    public void setSpeed(Float svalue) {
        PortflagWrapper.speedvalue = svalue;
    }
    public Float getRpm() {
        return rpmvalue;
    }

    public void setRpm(Float rvalue) {
        PortflagWrapper.rpmvalue = rvalue;
    }
}
