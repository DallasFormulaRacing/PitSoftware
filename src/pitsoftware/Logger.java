/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import com.arib.categoricalhashtable.CategoricalHashTable;
import com.arib.toast.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.LinkedList;
import java.util.TooManyListenersException;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.swing.JFrame;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author aribdhuka
 */
public class Logger {
    
    //holds the data
    CategoricalHashTable<LogObject> logData;
    //holds the gauges
    TreeMap<String, Scaled> gauges;
    //parent to send error toasts to.
    JFrame parent;
    
    //default constructor
    public Logger() {
        logData = new CategoricalHashTable<>();
        gauges = new TreeMap<>();
        this.parent = new JFrame();
    }
    
    //given gauges
    public Logger(TreeMap<String, Scaled> gauges, JFrame parent) {
        logData = new CategoricalHashTable<>();
        this.gauges = gauges;
        this.parent = parent;
    }
    
    //puts the data
    public void put(String TAG, double value) {
        logData.put(new SimpleLogObject(TAG, value));
        signal(TAG, value);
    }
    
    //signals the corresponding gauge to update
    public void signal(String tag, double value) {
        Scaled gauge = gauges.get(tag);
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
    public void addGauge(String tag, Scaled o) {
        gauges.put(tag, o);
    }
    
    
    /**
     * 
     * 
     *      BELOW THIS COMMENT HANDLES SERIAL COMMUNICATION
     * 
     */
    
    //Thread that handles parsing hexstrings in the background
    Thread parseThread;
    long startTime;
    Runnable serialParser = new Runnable() {
        @Override
        public void run() {

            startTime = System.currentTimeMillis();

            while(true) {
                if(!data.isEmpty()) {
                    updateUI();
                    if(!graphList.isEmpty() && System.currentTimeMillis() - startTime > 500) {
                        //for each LiveChart window
                        for(LiveChart c : graphList) {
                            //if c is still open
                            if(c.isActive()) {
                                //update its chart
                                c.updateChart(createJFreeChart(c.getTag()));
                            }
                        }
                        startTime = System.currentTimeMillis();
                    }
                } else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }
    };

    //This thread is for adding objects to the CategoricalHashMap continuously as well as updating the new window charts
    Thread graphThread;
    //Runnable code for the Testing code
    Runnable graphRunner = new Runnable() {
        @Override
        public void run() {
            while(true) {
                //if the graphList isnt empty (A window is open)
                if(!graphList.isEmpty()) {
                    //for each LiveChart window
                    for(LiveChart c : graphList) {
                        //if c is still open
                        if(c.isActive()) {
                            //update its chart
                            c.updateChart(createJFreeChart(c.getTag()));
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    };

    //holds the start time of the program
    static long logStartTime = 0;
    //list of all the windows open
    ArrayList<LiveChart> graphList;
    
    //holds if running
    boolean isRunning;
    //holds current time to log to
    long currTime;
    long currAccelTime;
    
    //static variables for serial port
    static final LinkedList<String> data = new LinkedList<>();
    static SerialPort serial;
    static BufferedReader input;
    static StringBuilder incompleteData;
    String serialPath;
    static boolean suspend;
    
    public void createSerial(String portName) throws TooManyListenersException, IOException, SerialPortException {
        data.clear();
        String[] portList = SerialPortList.getPortNames();
        for(String s : portList) {
            System.out.println("jssc: " + s);
        }
        
        for(String s : portList) {
            if(s.equals(portName)) {
                serial = new SerialPort(portName);
                System.out.println("started");

                break;
            }
        }
        
        if(serial.isOpened())
            serial.closePort();
        serial.openPort();
        serial.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, 0, SerialPort.PARITY_NONE);
        serial.addEventListener(new PortReader());
    }

        //update the UI from a list of data
    public void updateUI() {
        //for each string, update the UI.
        while(!data.isEmpty()) {
            String currData = "";
            synchronized(data) {
                currData = data.getFirst();
                data.removeFirst();
            }
            updateUI(currData);
        }
    }
    
    public void updateUI(String data) {
        if(data.isEmpty())
            return;
        if(data.length() < 4)
            return;
        //if the length of the line is not the right size skip the value
        if(!(data.substring(0, 4).equals("#005") || data.substring(0, 4).equals("#007")) && data.length() != 23) {
            System.out.print("Invalid CAN String!--");
            System.out.println(data);
            return;
        }
        //get the identifier of the data value
        String identifier = data.substring(1,4);
        //switch on the identifier
        switch (identifier) {
            //group one is RPM, TPS, fuelopentime, ignition angle
            case "001":
                parseGroupOne(data.substring(4));
                writeToMap();
                break;
            //group two is barometer, MAP, and lambda
            case "002":
                parseGroupTwo(data.substring(4));
                break;
            //group three contains the analog inputs
            case "003":
                parseGroupThree(data.substring(4));
                break;
            case "004":
                break;
            //group five contains speed
            case "005":
                parseGroupFive(data.substring(4));
                break;
            //group six contains battery voltage, ambient temperature, and coolant temperature
            case "006":
                parseGroupSix(data.substring(4));
                break;
            //group seven contains inlet outlet
            case "007":
                parseGroupSeven(data.substring(4));
                break;
            case "008":
                break;
            case "009":
                break;
            case "010":
                break;
            case "011":
                break;
            case "012":
                break;
            case "013":
                break;
            case "014":
                break;
            case "015":
                break;
            case "016":
                break;
            case "017":
                parseGroupSeventeen(data.substring(4));
                break;
            default:
                Toast.makeToast(parent, "Parse fail", Toast.DURATION_SHORT);
                break;
        }
    }
    
    /**
     * All parsing from below is done using the PE3 protocol available on BOX
     * There are supplemental documents that explain why the string are being split the way they are
     * In basic different parts of the hexstring contain different data elements we are not to look at the element as a whole
     * Multiplications are done because of offsets.
     * There are two variables for each element because one part of the byte contains the high bits and the other contains the low order bits because of protocol
     * @param line 
     */
    private void parseGroupOne(String line) {

        int rpm1, rpm2;
        rpm1 = Integer.parseUnsignedInt(line.substring(0, 2), 16);
        rpm2 = Integer.parseUnsignedInt(line.substring(2,4), 16) * 256;
        RPM = rpm1 + rpm2;
        //logData.put(new SimpleLogObject("Time,RPM", rpm, currTime));
        
        int tps1, tps2;
        tps1 = Integer.parseInt(line.substring(4, 6), 16);
        tps2 = Integer.parseInt(line.substring(6, 8), 16) * 256;
        TPS = tps1 + tps2;
        TPS *= .1;
        //logData.put(new SimpleLogObject("Time,TPS", tps, currTime)); // right way of doing it
        
        int fot1, fot2;
        fot1 = Integer.parseInt((line.substring(8,10)), 16);
        fot2 = Integer.parseInt((line.substring(10,12)), 16) * 256;
        FOT = fot1 + fot2;
        FOT *= .01;
        //logData.put(new SimpleLogObject("Time,FuelOpenTime", fuelOpenTime, currTime));
        
        int ignAngle1, ignAngle2;
        ignAngle1 = Integer.parseInt((line.substring(12, 14)), 16);
        ignAngle2 = Integer.parseInt((line.substring(14, 16)), 16) * 256;
        ignAngle = ignAngle1 + ignAngle2;
        ignAngle *= .1;
        //logData.put(new SimpleLogObject("Time,IgnitionAngle", ignAngle, currTime));
        
        gauges.get("RPM").setScaledValue(RPM);
        gauges.get("TPS").setScaledValue(TPS);
        gauges.get("FuelOpenTime").setScaledValue(FOT);
        gauges.get("IgnitionAngle").setScaledValue(ignAngle);
        currTime += 50;
        
    }

    private void parseGroupTwo(String line)
    {

        double barometer1, barometer2;
        barometer1 = Integer.parseInt(line.substring(0,2), 16);
        barometer2 = Integer.parseInt(line.substring(2,4), 16) * 256;
        bar = barometer1 + barometer2;
        bar *= 0.01;
        //logData.put(new SimpleLogObject("Time,Barometer", barometer, currTime));
        
        double map1, map2;
        map1 = Integer.parseInt(line.substring(4,6), 16);
        map2 = Integer.parseInt(line.substring(6,8), 16) * 256;
        MAP = map1 + map2;
        MAP *= 0.01;
        //logData.put(new SimpleLogObject("Time,MAP", map, currTime));
        
        double lambda1, lambda2;
        lambda1 = Integer.parseInt(line.substring(8,10), 16);
        lambda2 = Integer.parseInt(line.substring(10,12), 16) * 256;
        lambda = lambda1 + lambda2;
        lambda *= 0.001;
        //logData.put(new SimpleLogObject("Time,Lambda", lambda, currTime));
        
        gauges.get("Barometer").setScaledValue(bar);
        gauges.get("MAP").setScaledValue(MAP);
        //lambda input no longer exists
        //ecu is mapped to analog 3 and 4 for 2 lambda sensors
        
    }

    private void parseGroupThree(String line) throws NumberFormatException
    {
//        double input1, input2, input3, input4;
        double in1, in2;
        
        in1 = Integer.parseInt(line.substring(0,2), 16);
        in2 = Integer.parseInt(line.substring(2,4), 16) * 256;
        analog1 = in1 + in2;
//        logData.put(new SimpleLogObject("Time,Analog1", input1, currTime));
        
        in1 = Integer.parseInt(line.substring(4,6), 16);
        in2 = Integer.parseInt(line.substring(6,8), 16) * 256;
        analog2 = in1 + in2;
        //logData.put(new SimpleLogObject("Time,Analog2", input2, currTime));
        
        in1 = Integer.parseInt(line.substring(8,10), 16);
        in2 = Integer.parseInt(line.substring(10,12), 16) * 256;
        analog3 = in1 + in2;
        analog3 *= 0.001;
        //logData.put(new SimpleLogObject("Time,Analog3", input3, currTime));
        
        in1 = Integer.parseInt(line.substring(12,14), 16);
        in2 = Integer.parseInt(line.substring(14,16), 16) * 256;
        analog4 = in1 + in2;
        analog4 *= 0.001;
        //logData.put(new SimpleLogObject("Time,Analog4", input4, currTime));
        
        double avg = analog3+ analog4;
        avg /= 2;
        avg = (2*avg)+10;
        //logData.put(new SimpleLogObject("Time,AFR", avg, currTime));
        
        
        gauges.get("Analog1").setScaledValue(analog1);
        gauges.get("Analog2").setScaledValue(analog2);
//        gauges.get("Analog3").setScaledValue(analog3);
//        gauges.get("Analog4").setScaledValue(analog4);
        gauges.get("AFR").setScaledValue(avg);
    }
    
    public void parseGroupFive(String line)
    {
        try {
            transTeeth = Integer.parseInt(line.substring(0, line.length()-3));
            speed = ((transTeeth/23.0)*.2323090909*60)*(3.141592654*.0003219697);
            speed *= 60;
            gauges.get("Speed").setScaledValue(speed);
            //logData.put(new SimpleLogObject("Time,WheelspeedRear", speed, currTime));
        } catch(NumberFormatException e) {
            System.out.println("speed format exception--" + line);
        }
    }

    public void parseGroupSix(String line)
    {
        int tempType;
        
        double batVol1, batVol2;
        batVol1 = Integer.parseInt(line.substring(0,2), 16);
        batVol2 = Integer.parseInt(line.substring(2,4), 16) * 256;
        volts = batVol1 + batVol2;
        volts *= 0.01;
        
        double airTemp1, airTemp2;
        airTemp1 = Integer.parseInt(line.substring(4,6), 16);
        airTemp2 = Integer.parseInt(line.substring(6,8), 16) * 256;
        airTemp = airTemp1 + airTemp2;
        airTemp *= 0.1;

        double coolantTemp1, coolantTemp2;
        coolantTemp1 = Integer.parseInt(line.substring(8,10), 16);
        coolantTemp2 = Integer.parseInt(line.substring(10,12), 16) * 256;
        coolant = coolantTemp1 + coolantTemp2;
        coolant *= 0.1;
        
        gauges.get("Voltage").setScaledValue(volts);
        gauges.get("AirTemp").setScaledValue(airTemp);
        gauges.get("Coolant").setScaledValue(coolant);
    }

    private void parseGroupSeven(String line) {
        double inlet = Double.parseDouble(line.substring(0, line.indexOf('F')));
        double outlet = Double.parseDouble(line.substring(line.indexOf('F')+1, line.length()));
        
        radiatorInlet = inlet;
        radiatorOutlet = outlet;
        
        gauges.get("RadiatorInlet").setScaledValue(inlet);
        gauges.get("RadiatorOutlet").setScaledValue(outlet);
    }

    private void parseGroupSeventeen(String line)
    {
        String[] split = line.split(",");
        if(split.length == 3) {
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);
            double z = Double.parseDouble(split[2]);
            logData.put(new SimpleLogObject("Time,xAccel", x, currAccelTime));
            logData.put(new SimpleLogObject("Time,yAccel", y, currAccelTime));
            logData.put(new SimpleLogObject("Time,zAccel", z, currAccelTime));
        }
        
        currAccelTime += 5;
    }
    
        //create the JFree Chart and show it
    private void showJFreeChart(String TAG) {
        if(suspend) {
            //get the chart object from another method call
            JFreeChart chart;
            try {
                chart = createJFreeChart(TAG);
            } catch (ConcurrentModificationException e){
                Toast.makeToast(parent, "Try again.", Toast.DURATION_SHORT);
                return;
            }
            if(chart == null)
                return;
            //create a new window that has the chart.
            LiveChart liveChart = new LiveChart(chart, TAG);
            //add the chart to the list of windows
            graphList.add(liveChart);
            //show the window
            liveChart.setVisible(true);
        } else {
            Toast.makeToast(parent, "Try again.", Toast.DURATION_SHORT);
        }
    }
    
    //creates a JFreeChart object given a TAG
    private JFreeChart createJFreeChart(String TAG) throws ConcurrentModificationException {
        //create the collection of series
        XYSeriesCollection collection = new XYSeriesCollection();
        
        //create the series of data
        XYSeries series = new XYSeries(TAG);
        //get the list of values that have our tag from the HashMap
        LinkedList<LogObject> dataList = logData.getList(TAG);
        //if we have data
        if(dataList != null) {
            //for each LogObject in the list
            for(LogObject lo : dataList) {
                //if the LogObject is an instance of SimpleLogObject
                //setting up for different data types such as GPS log object
                if(lo instanceof SimpleLogObject) {
                    //add the log object to the series
                    series.add(lo.getTime(), ((SimpleLogObject) lo).getValue());
                }
            }
        }
        
        //add the series to the collection
        collection.addSeries(series);
        
        String[] tagSplit = TAG.split(",");
        // Create a JFreeChart from the Factory, given parameters (Chart Title, Domain name, Range name, series collection, PlotOrientation, show legend, show tooltips, show url)
        JFreeChart chart = ChartFactory.createXYLineChart(
                TAG,
                tagSplit[0],
                tagSplit[1],
                collection,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        return chart;
        
    }
    
    private void writeToMap() {
        logData.put(new SimpleLogObject("Time,RPM", RPM, currTime));
        logData.put(new SimpleLogObject("Time,TPS", TPS, currTime));
        logData.put(new SimpleLogObject("Time,FuelOpenTime", FOT, currTime));
        logData.put(new SimpleLogObject("Time,IgnitionAngle", ignAngle, currTime));
        logData.put(new SimpleLogObject("Time,Barometer", bar, currTime));
        logData.put(new SimpleLogObject("Time,MAP", MAP, currTime));
        logData.put(new SimpleLogObject("Time,Lambda", lambda, currTime));
        logData.put(new SimpleLogObject("Time,Analog1", analog1, currTime));
        logData.put(new SimpleLogObject("Time,Analog2", analog2, currTime));
        logData.put(new SimpleLogObject("Time,Analog3", analog3, currTime));
        logData.put(new SimpleLogObject("Time,Analog4", analog4, currTime));
        logData.put(new SimpleLogObject("Time,Voltage", volts, currTime));
        logData.put(new SimpleLogObject("Time,AirTemp", airTemp, currTime));
        logData.put(new SimpleLogObject("Time,Coolant", coolant, currTime));
        logData.put(new SimpleLogObject("Time,WheelspeedRear", speed, currTime));
        logData.put(new SimpleLogObject("Time,TransmissionTeeth", transTeeth, currTime));
        logData.put(new SimpleLogObject("Time,RadiatorInlet", radiatorInlet, currTime));
        logData.put(new SimpleLogObject("Time,RadiatorOutlet", radiatorOutlet, currTime));
        
    }
    
    public void hashTableToCSV() {
        try {
            //ask for filename
            String[] filename = new String[1];
            FileNameDialog fnd = new FileNameDialog(parent, true, filename);
            fnd.setVisible(true);
            while(filename[0].isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(GaugesWindowSerial.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //get todays date
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy--HH-mm-ss");  
            Date date = new Date();
            String now = formatter.format(date);
            String filenamestr = "";
            if(filename[0].equals("!#@$NONAME")) {
                filenamestr = now;
            } else {
                filenamestr = filename[0];
            }
            // Creates a new csv file to put data into. File is located within 'PitSoftware' git folder
            FileOutputStream csv = new FileOutputStream(new File(filenamestr + ".csv"), true);
            // Allows program to print/write data into file
            PrintWriter pw = new PrintWriter(csv);
            
            // Loop continues based on total number of tags in array 'tags' from importCSV
            for (String tag : logData.getTags()) {
                // Creates array of SimpleLogObject that only includes data from 'dataMap' under 'tag'
                LinkedList<LogObject> data = logData.getList(tag);
                if(!data.isEmpty()) {
                    // Prints 'tag' before data is printed if data is not empty
                    pw.println(tag);
                
                    long lastTime = -1;
                    // Loop that prints data under 'tag' on separate lines
                    for (LogObject lo : data) {
                        // if the current time is not the same as the last time
                        //Prints each piece of data to a unique cell on one line
                        if(lo.getTime() != lastTime) {
                            pw.println(lo.toString());
                            // Sends single data line to print in file
                            pw.flush();
                        }
                        
                        //update last time
                        lastTime = lo.getTime();
                        
                    }
                    //print end tag
                    pw.println("END");
                
                }
            }
            
            Toast.makeToast(parent, "Saved as: " + now, Toast.DURATION_LONG);
            
        } catch (IOException x) {
            Toast.makeToast(parent, "Save Error!", Toast.DURATION_MEDIUM);
        }
    } 
    
    /**
     * Handles what to do when a serial event occurs
     */
    private static class PortReader implements SerialPortEventListener {
        
        //on new serial data
        @Override
        public void serialEvent(SerialPortEvent event) {
            //try to read from the port
            try {
                //get the string from the port
                if(serial != null) {
                    String tempString = serial.readString();
                        if(tempString != null) {
                        //for each char we read
                        for(int i = 0; i < tempString.length(); i++) {
                            //if the data string is not empty and the end of the data string is a new line (ie the data string is complete)
                            if(!incompleteData.toString().isEmpty() && incompleteData.charAt(incompleteData.length() - 1) == '\n') {
                                //add the data string to the main array
                                synchronized(data) {
                                    data.add(incompleteData.toString());
                                }
                                //reset the data string
                                incompleteData = new StringBuilder();
                                //move back one position since we didnt read this char
                                i--;
                            //if the data string is empty or the data string is not complete (marked by newline at the end)
                            } else {
                                //add the current char to the data string
                                incompleteData.append(tempString.charAt(i));
                            }
                        }
                    }
                }
            //catch read error
            } catch (SerialPortException ex) {
                //print error
                System.out.println("port read error");
            }
            
            
        }

    } 
    
    
    /**
     * 
     * 
     *      BELOW THIS COMMENT ARE VARIABLES FOR SERIAL COMMUNICATIONS
     *      THESE ARE ESSENTIALLY BUFFERS SO THAT ALL VARIABLES ARE STORED UNDER
     *          THE SAME TIME STAMP.
     * 
     */
    private static int RPM = 0;
    private static double TPS = 0;
    private static double FOT = 0;
    private static double ignAngle = 0;

    private static double bar = 0;
    private static double MAP = 0;
    private static double lambda = 0;

    private static double analog1 = 0;
    private static double analog2 = 0;
    private static double analog3 = 0;
    private static double analog4 = 0;

    private static double volts = 0;
    private static double airTemp = 0;
    private static double coolant = 0;
    
    private static double radiatorInlet = 0;
    private static double radiatorOutlet = 0;
    
    private static double speed = 0;
    private static double transTeeth = 0;
}
