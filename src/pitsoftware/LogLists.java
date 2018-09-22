/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import java.util.ArrayList;

/**
 *
 * @author aribdhuka
 */
public class LogLists {
    
    ArrayList<LogObject> rpmLog;
    ArrayList<LogObject> tpsLog;
    ArrayList<LogObject> fuelLog;
    ArrayList<LogObject> ignitionAngLog;
    ArrayList<LogObject> barometerLog;
    ArrayList<LogObject> mapLog;
    ArrayList<LogObject> lambdaLog;
    ArrayList<LogObject> batteryVoltageLog;
    ArrayList<LogObject> airTempLog;
    ArrayList<LogObject> coolantTempLog;
    ArrayList<LogObject> analogInput1Log;
    ArrayList<LogObject> analogInput2Log;
    ArrayList<LogObject> analogInput3Log;
    ArrayList<LogObject> analogInput4Log;
    
    public LogLists() {
        rpmLog = new ArrayList<>();
        tpsLog = new ArrayList<>();
        fuelLog = new ArrayList<>();
        ignitionAngLog = new ArrayList<>();
        barometerLog = new ArrayList<>();
        mapLog = new ArrayList<>();
        lambdaLog = new ArrayList<>();
        batteryVoltageLog = new ArrayList<>();
        airTempLog = new ArrayList<>();
        coolantTempLog = new ArrayList<>();
        analogInput1Log = new ArrayList<>();
        analogInput2Log = new ArrayList<>();
        analogInput3Log = new ArrayList<>();
        analogInput4Log = new ArrayList<>();
    }

    public ArrayList<LogObject> getRpmLog() {
        return rpmLog;
    }

    public void setRpmLog(ArrayList<LogObject> rpmLog) {
        this.rpmLog = rpmLog;
    }

    public ArrayList<LogObject> getTpsLog() {
        return tpsLog;
    }

    public void setTpsLog(ArrayList<LogObject> tpsLog) {
        this.tpsLog = tpsLog;
    }

    public ArrayList<LogObject> getFuelLog() {
        return fuelLog;
    }

    public void setFuelLog(ArrayList<LogObject> fuelLog) {
        this.fuelLog = fuelLog;
    }

    public ArrayList<LogObject> getIgnitionAngLog() {
        return ignitionAngLog;
    }

    public void setIgnitionAngLog(ArrayList<LogObject> ignitionAngLog) {
        this.ignitionAngLog = ignitionAngLog;
    }

    public ArrayList<LogObject> getBarometerLog() {
        return barometerLog;
    }

    public void setBarometerLog(ArrayList<LogObject> barometerLog) {
        this.barometerLog = barometerLog;
    }

    public ArrayList<LogObject> getMapLog() {
        return mapLog;
    }

    public void setMapLog(ArrayList<LogObject> mapLog) {
        this.mapLog = mapLog;
    }

    public ArrayList<LogObject> getLambdaLog() {
        return lambdaLog;
    }

    public void setLambdaLog(ArrayList<LogObject> lambdaLog) {
        this.lambdaLog = lambdaLog;
    }

    public ArrayList<LogObject> getAnalogInput1Log() {
        return analogInput1Log;
    }

    public void setAnalogInput1Log(ArrayList<LogObject> analogInput1Log) {
        this.analogInput1Log = analogInput1Log;
    }

    public ArrayList<LogObject> getAnalogInput2Log() {
        return analogInput2Log;
    }

    public void setAnalogInput2Log(ArrayList<LogObject> analogInput2Log) {
        this.analogInput2Log = analogInput2Log;
    }

    public ArrayList<LogObject> getAnalogInput3Log() {
        return analogInput3Log;
    }

    public void setAnalogInput3Log(ArrayList<LogObject> analogInput3Log) {
        this.analogInput3Log = analogInput3Log;
    }

    public ArrayList<LogObject> getAnalogInput4Log() {
        return analogInput4Log;
    }

    public void setAnalogInput4Log(ArrayList<LogObject> analogInput4Log) {
        this.analogInput4Log = analogInput4Log;
    }

    
}
