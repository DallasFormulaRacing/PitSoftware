/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import eu.hansolo.steelseries.gauges.Linear;

/**
 *
 * @author aribdhuka
 */
public class ScaledLinear extends Linear implements Scaled {
    
    private double scale;
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public ScaledLinear() {
        scale = 1;
        tag = "";
    }
    
    public ScaledLinear(double scale) {
        this.scale = scale;
    }

    public void setScaledValue(double value) {
        this.setValue(value/scale);
        this.setLcdValue(value);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
    
    
    
}
