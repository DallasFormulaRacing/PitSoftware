/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import com.arib.categoricalhashtable.CategoricalHashTable;
import eu.hansolo.steelseries.gauges.AbstractGauge;
import java.util.TreeMap;

/**
 *
 * @author aribdhuka
 */
public class Logger {
    
    //holds the data
    CategoricalHashTable<LogObject> data;
    //holds the gauges
    TreeMap<String, AbstractGauge> gauges;
    
    //default constructor
    public Logger() {
        data = new CategoricalHashTable<>();
        gauges = new TreeMap<>();
    }
    
    //given gauges
    public Logger(TreeMap<String, AbstractGauge> gauges) {
        data = new CategoricalHashTable<>();
        this.gauges = gauges;
    }
    
    //puts the data
    public void put(String TAG, double value) {
        data.put(new SimpleLogObject(TAG, value));
        signal(TAG, value);
    }
    
    //signals the corresponding gauge to update
    public void signal(String tag, double value) {
        AbstractGauge gauge = gauges.get(tag);
        if(gauge != null) {
            if(gauge instanceof ScaledRadial){
                ((ScaledRadial) gauge).setScaledValue(value);
            }
            else if (gauge instanceof ScaledLinear) {
                ((ScaledLinear) gauge).setScaledValue(value);
            }
        }
    }
    
    //adds a gauge to the tree map
    public void addGauge(String tag, AbstractGauge o) {
        gauges.put(tag, o);
    }
    
    
}
