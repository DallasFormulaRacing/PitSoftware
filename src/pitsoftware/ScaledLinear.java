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
public class ScaledLinear extends Linear {
    
    double scale;
    
    public ScaledLinear() {
        scale = 1;
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
