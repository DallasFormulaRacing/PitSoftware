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
public class GPSLogObject extends LogObject {
    
    double lat;
    double longi;
    
    public GPSLogObject(String TAG, double lat, double longi) {
        this.TAG = TAG;
        this.lat = lat;
        this.longi = longi;
        this.time = System.currentTimeMillis() - MainWindow.logStartTime;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
    
    public double distanceTo(double lat, double longi) {
        throw new UnsupportedOperationException("Not Supported Yet."); //TODO Implement distance calculation off of two coordinates in decimal form.
    }
    
    public String toString() {
        return "(" + time + "," + lat + "," + longi + ")";
    }
}
