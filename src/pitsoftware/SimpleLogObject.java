/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

/**
 *
 * @author aribdhuka
 */
public class SimpleLogObject extends LogObject {
    
    double value;
    
    public SimpleLogObject() {
        super();
        value = 0;
    }
    
    public SimpleLogObject(String TAG, double value) {
        this.TAG = TAG;
        this.value = value;
        this.time = System.currentTimeMillis() - MainWindow.logStartTime;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
}
