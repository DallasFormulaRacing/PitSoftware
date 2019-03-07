/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import eu.hansolo.steelseries.gauges.Radial;

/**
 * Automatically scales values given when provided a scale
 * This makes sure that the LCD reads out the proper value, while keeping the gauge correct as well
 * @author aribdhuka
 */
public class ScaledRadial extends Radial {
    
    //the scale for this gauge
    //so if the gauge represents 1000 per tick, the scale is 1000
    private double scale;
    
    //Scaled Radial provided a scale
    public ScaledRadial(double scale) {
        this.scale = scale;
    }
    
    //if not provided, scale is 1.
    public ScaledRadial() {
        scale = 1;
    }
    
    //set the scaled value, so apply the scale to the gauge and raw to the lcd
    public void setScaledValue(double value) {
        this.setValue(value/scale);
        this.setLcdValue(value);
    }

    //getters and setters for scale
    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
    
    
    
}
