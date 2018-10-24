/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import jssc.SerialPortException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import static pitsoftware.MainWindow.logStartTime;

/**
 *
 * @author aribdhuka
 */
public class MainWindow extends javax.swing.JFrame {

    //SerialCommunicator class that reads data over serial port
    SerialCommunicator serialcomm;
    //Thread that handles parsing hexstrings in the background
    Thread parseThread;
    //Runnable code for the parseThread
    Runnable pyParser = () -> {
        while(true) {
            //sleep for 500 ms
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            //try to run the Python Communicator
            try {
                //get the BufferedReader from the run command from the class that runs the python code
                BufferedReader data = PythonComm.run();
                //holds the current line of data
                String line;
                //list of hexstrings
                ArrayList<String> strData = new ArrayList<>();
                //while there is another line
                while((line = data.readLine()) != null) {
                    //add the line to the list
                    strData.add(line);
                    //print the line for status
                    System.out.println(line);
                }
                //update the UI
                updateUI(strData);
            } catch(IOException e){ 
                System.out.println(e);
            }
        }
    };    
    
    //This thread is for adding objects to the CategoricalHashMap continuously as well as updating the new window charts
    Thread testingThread;
    //Runnable code for the Testing code
    Runnable testingRunner = new Runnable() {
        @Override
        public void run() {
            //current "time"
            int i = 0;
            while(true) {
                //create LogObject
                SimpleLogObject slo = new SimpleLogObject("Time,RPM", (i*1000)%14000);
                //set its time to the fake time value we created
                slo.setTime(i);
                //put the data in the CategoricalHashMap
                logData.put(slo);
                //try to sleep for a second
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                //if the graphList isnt empty (A window is open)
                if(!graphList.isEmpty()) {
                    //for each LiveChart window
                    for(LiveChart c : graphList) {
                        //if c is still open
                        if(c.isActive()) {
                            //update its chart
                            c.updateChart(createJFreeChart("Time,RPM"));
                        }
                    }
                }
                //increment the time
                i++;
            }
        }
    };
    
    //holds the start time of the program
    static long logStartTime = 0;
    //HashMap of all the values we receive
    CategoricalHashMap logData;
    //list of all the windows open
    ArrayList<LiveChart> graphList;
    
    //Constructor for this class
    public MainWindow() {
        //create its components
        initComponents();
        //initiate the hashmap
        logData = new CategoricalHashMap();
        //initiate the list of windows
        graphList = new ArrayList<>();
        //launch the thread that adds sample data.
        testingThread = new Thread(testingRunner);
        testingThread.start();
    }
    
    //update the UI from a list of data
    public void updateUI(ArrayList<String> data) {
        //while the list has data
        while(!data.isEmpty()) {
            //get the first element
            String fullLine = data.get(0);
            //remove the element from the list
            data.remove(0);
            //if the length of the line is not the right size skip the value
            if(fullLine.length() != 20)
                continue;
            //get the identifier of the data value
            String identifier = fullLine.substring(1,4);
            //switch on the identifier
            switch (identifier) {
                //group one is RPM, TPS, fuelopentime, ignition angle
                case "001":
                    parseGroupOne(fullLine.substring(4));
                    break;
                //group two is barometer, MAP, and lambda
                case "002":
                    parseGroupTwo(fullLine.substring(4));
                    break;
                //group three contains the analog inputs
                case "003":
                    parseGroupThree(fullLine.substring(4));
                    break;
                case "004":
                    break;
                case "005":
                    break;
                //group six contains battery voltage, ambient temperature, and coolant temperature
                case "006":
                    parseGroupSix(fullLine.substring(4));
                    break;
                case "007":
                    break;
                case "008":
                    parseGroupEight(fullLine.substring(4));
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
                    parseGroupFourteen(fullLine.substring(4));
                    break;
                case "015":
                    parseGroupFifteen(fullLine.substring(4));
                    break;
                case "016":
                    parseGroupSixteen(fullLine.substring(4));
                    break;
                case "017":
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        rpmTextField = new javax.swing.JTextField();
        tpsTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        fuelTimingTextField = new javax.swing.JTextField();
        ignitionAngleTextField = new javax.swing.JTextField();
        batteryVoltageTextField = new javax.swing.JTextField();
        airTempTextField = new javax.swing.JTextField();
        coolantTempTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        analogInput1TextField = new javax.swing.JTextField();
        analogInput2TextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        analogInput3TextField = new javax.swing.JTextField();
        analogInput4TextField = new javax.swing.JTextField();
        BarometerTextField = new javax.swing.JTextField();
        mapTextField = new javax.swing.JTextField();
        lambdaTextField = new javax.swing.JTextField();
        Start = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("RPM");

        rpmTextField.setEditable(false);
        rpmTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rpmTextFieldMouseClicked(evt);
            }
        });

        tpsTextField.setEditable(false);

        jLabel2.setText("TPS %");

        fuelTimingTextField.setEditable(false);

        ignitionAngleTextField.setEditable(false);

        batteryVoltageTextField.setEditable(false);

        airTempTextField.setEditable(false);

        coolantTempTextField.setEditable(false);

        jLabel5.setText("Fuel Open Time (ms)");

        jLabel6.setText("Ignition Angle (deg)");

        jLabel7.setText("Battery Voltage (V)");

        jLabel8.setText("Air Temp");

        jLabel9.setText("Coolant Temp");

        jLabel11.setText("Analog Input #3");

        jLabel12.setText("Analog Input #4");

        jLabel13.setText("Barometer");

        jLabel14.setText("MAP");

        jLabel15.setText("Lambda");

        jLabel3.setText("Analog Input #1");

        analogInput1TextField.setEditable(false);

        analogInput2TextField.setEditable(false);

        jLabel4.setText("Analog Input #2");

        analogInput3TextField.setEditable(false);

        analogInput4TextField.setEditable(false);

        BarometerTextField.setEditable(false);

        mapTextField.setEditable(false);

        lambdaTextField.setEditable(false);

        Start.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        Start.setText("START THIS BITCH!");
        Start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ignitionAngleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(batteryVoltageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(coolantTempTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(airTempTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tpsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rpmTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fuelTimingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel5))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(analogInput4TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(BarometerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(analogInput2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(analogInput1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(analogInput3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel11)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lambdaTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mapTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel15)))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Start, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(107, 107, 107)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rpmTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tpsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fuelTimingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ignitionAngleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(batteryVoltageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(airTempTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(coolantTempTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(analogInput1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(analogInput2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(analogInput3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(analogInput4TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BarometerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(mapTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lambdaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))))
                .addGap(36, 36, 36)
                .addComponent(Start, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //on start button pressed
    private void StartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartActionPerformed
        // TODO add your handling code here:
        logStartTime = System.currentTimeMillis();
        parseThread = new Thread(pyParser);
        parseThread.start();
        
    }//GEN-LAST:event_StartActionPerformed

    //when the field is clicked
    private void rpmTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rpmTextFieldMouseClicked
        // TODO add your handling code here:
        showJFreeChart("Time,RPM");
    }//GEN-LAST:event_rpmTextFieldMouseClicked

    //create the JFree Chart and show it
    private void showJFreeChart(String TAG) {
        //get the chart object from another method call
        JFreeChart chart = createJFreeChart(TAG);
        //create a new window that has the chart.
        LiveChart liveChart = new LiveChart(chart);
        //add the chart to the list of windows
        graphList.add(liveChart);
        //show the window
        liveChart.setVisible(true);
    }
    
    //creates a JFreeChart object given a TAG
    private JFreeChart createJFreeChart(String TAG) {
        //create the collection of series
        XYSeriesCollection data = new XYSeriesCollection();
        
        //create the series of data
        XYSeries series = new XYSeries(TAG);
        //get the list of values that have our tag from the HashMap
        LinkedList<LogObject> dataList = logData.getList(TAG);
        //for each LogObject in the list
        for(LogObject lo : dataList) {
            //if the LogObject is an instance of SimpleLogObject
            //setting up for different data types such as GPS log object
            if(lo instanceof SimpleLogObject) {
                //add the log object to the series
                series.add(lo.getTime(), ((SimpleLogObject) lo).getValue());
            }
        }
        
        //add the series to the collection
        data.addSeries(series);
        
        String[] tagSplit = TAG.split(",");
        // Create a JFreeChart from the Factory, given parameters (Chart Title, Domain name, Range name, series collection, PlotOrientation, show legend, show tooltips, show url)
        JFreeChart chart = ChartFactory.createXYLineChart(
                TAG,
                tagSplit[0],
                tagSplit[1],
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        return chart;
        
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
        int rpm;
        double tps, fuelOpenTime, ignAngle;

        int rpm1, rpm2;
        rpm1 = Integer.parseUnsignedInt(line.substring(0, 2), 16);
        rpm2 = Integer.parseUnsignedInt(line.substring(2,4), 16) * 256;
        rpm = rpm1 + rpm2;
        logData.put(new SimpleLogObject("Time,RPM", rpm));
        
        int tps1, tps2;
        tps1 = Integer.parseInt(line.substring(4, 6), 16);
        tps2 = Integer.parseInt(line.substring(6, 8), 16) * 256;
        tps = tps1 + tps2;
        tps *= .1;
        logData.put(new SimpleLogObject("Time,TPS", tps)); // right way of doing it
        
        int fot1, fot2;
        fot1 = Integer.parseInt((line.substring(8,10)), 16);
        fot2 = Integer.parseInt((line.substring(10,12)), 16) * 256;
        fuelOpenTime = fot1 + fot2;
        fuelOpenTime *= .01;
        logData.put(new SimpleLogObject("Time,FuelOpenTime", fuelOpenTime));
        
        int ignAngle1, ignAngle2;
        ignAngle1 = Integer.parseInt((line.substring(12, 14)), 16);
        ignAngle2 = Integer.parseInt((line.substring(14, 16)), 16) * 256;
        ignAngle = ignAngle1 + ignAngle2;
        ignAngle *= .1;
        logData.put(new SimpleLogObject("Time,IgnitionAngle", ignAngle));
        
        rpmTextField.setText(rpm + "");
        tpsTextField.setText(tps + "");
        fuelTimingTextField.setText(fuelOpenTime + "");
        ignitionAngleTextField.setText(ignAngle + "");
        
    }

    private void parseGroupTwo(String line)
    {
        double barometer, map, lambda;

        double barometer1, barometer2;
        barometer1 = Integer.parseInt(line.substring(0,2), 16);
        barometer2 = Integer.parseInt(line.substring(2,4), 16) * 256;
        barometer = barometer1 + barometer2;
        barometer *= 0.01;
        logData.put(new SimpleLogObject("Time,Barometer", barometer));
        
        double map1, map2;
        map1 = Integer.parseInt(line.substring(4,6), 16);
        map2 = Integer.parseInt(line.substring(6,8), 16) * 256;
        map = map1 + map2;
        map *= 0.01;
        logData.put(new SimpleLogObject("Time,MAP", map));
        
        double lambda1, lambda2;
        lambda1 = Integer.parseInt(line.substring(8,10), 16);
        lambda2 = Integer.parseInt(line.substring(10,12), 16) * 256;
        lambda = lambda1 + lambda2;
        lambda *= 0.001;
        logData.put(new SimpleLogObject("Time,Lambda", lambda));
        
        BarometerTextField.setText(barometer + "");
        mapTextField.setText(map + "");
        lambdaTextField.setText(lambda + "");
        
    }

    private void parseGroupThree(String line) throws NumberFormatException
    {
        double input1, input2, input3, input4;
        double in1, in2;
        
        in1 = Integer.parseInt(line.substring(0,2), 16);
        in2 = Integer.parseInt(line.substring(2,4), 16) * 256;
        input1 = in1 + in2;
        input1 *= 0.001;
        logData.put(new SimpleLogObject("Time,Input1", input1));
        
        in1 = Integer.parseInt(line.substring(4,6), 16);
        in2 = Integer.parseInt(line.substring(6,8), 16) * 256;
        input2 = in1 + in2;
        input2 *= 0.001;
        logData.put(new SimpleLogObject("Time,Input2", input2));
        
        in1 = Integer.parseInt(line.substring(8,10), 16);
        in2 = Integer.parseInt(line.substring(10,12), 16) * 256;
        input3 = in1 + in2;
        input3 *= 0.001;
        logData.put(new SimpleLogObject("Time,Input3", input3));
        
        in1 = Integer.parseInt(line.substring(12,14), 16);
        in2 = Integer.parseInt(line.substring(14,16), 16) * 256;
        input4 = in1 + in2;
        input4 *= 0.001;
        logData.put(new SimpleLogObject("Time,Input4", input4));
        
        analogInput1TextField.setText(input1 + "");
        analogInput2TextField.setText(input2 + "");
        analogInput3TextField.setText(input3 + "");
        analogInput4TextField.setText(input4 + "");
        
    }

    public void parseGroupSix(String line)
    {
        int tempType;
        double batteryVoltage, airTemp, coolantTemp;
        
        double batVol1, batVol2;
        batVol1 = Integer.parseInt(line.substring(0,2), 16);
        batVol2 = Integer.parseInt(line.substring(2,4), 16) * 256;
        batteryVoltage = batVol1 + batVol2;
        batteryVoltage *= 0.01;
        logData.put(new SimpleLogObject("Time,Voltage", batteryVoltage));
        
        double airTemp1, airTemp2;
        airTemp1 = Integer.parseInt(line.substring(4,6), 16);
        airTemp2 = Integer.parseInt(line.substring(6,8), 16) * 256;
        airTemp = airTemp1 + airTemp2;
        airTemp *= 0.1;
        logData.put(new SimpleLogObject("Time,Air", airTemp));

        double coolantTemp1, coolantTemp2;
        coolantTemp1 = Integer.parseInt(line.substring(8,10), 16);
        coolantTemp2 = Integer.parseInt(line.substring(10,12), 16) * 256;
        coolantTemp = coolantTemp1 + coolantTemp2;
        coolantTemp *= 0.1;
        logData.put(new SimpleLogObject("Time,Coolant", coolantTemp));
        
        batteryVoltageTextField.setText(batteryVoltage + "");
        airTempTextField.setText(airTemp + "");
        coolantTempTextField.setText(coolantTemp + "");
    }

    private void parseGroupEight(String line)
    {

    }

    private void parseGroupFourteen(String line)
    {

    }

    private void parseGroupFifteen(String line)
    {

    }

    private void parseGroupSixteen(String line)
    {

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField BarometerTextField;
    private javax.swing.JButton Start;
    private javax.swing.JTextField airTempTextField;
    private javax.swing.JTextField analogInput1TextField;
    private javax.swing.JTextField analogInput2TextField;
    private javax.swing.JTextField analogInput3TextField;
    private javax.swing.JTextField analogInput4TextField;
    private javax.swing.JTextField batteryVoltageTextField;
    private javax.swing.JTextField coolantTempTextField;
    private javax.swing.JTextField fuelTimingTextField;
    private javax.swing.JTextField ignitionAngleTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField lambdaTextField;
    private javax.swing.JTextField mapTextField;
    private javax.swing.JTextField rpmTextField;
    private javax.swing.JTextField tpsTextField;
    // End of variables declaration//GEN-END:variables
}
