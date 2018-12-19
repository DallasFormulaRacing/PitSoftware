/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import com.arib.categoricalhashtable.CategoricalHashTable;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
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
            //sleep for 100 ms
            //this is so that we are not forcing python to close and reopen a serial connection.
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            long beforeTime = System.currentTimeMillis();
            //try to run the Python Communicator
            try {
                //get the BufferedReader from the run command from the class that runs the python code
                BufferedReader data = PythonComm.run();
                //print time it took to get data.
                System.out.println("Time elapsed: + " + (System.currentTimeMillis() - beforeTime));
                //ensure BufferedReader is not null
                if(data == null)
                    continue;
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
    
    Runnable fileParser = new Runnable() {
        @Override
        public void run() {
            //try to createa a buffered input with the given file
            BufferedInputStream reader = null;
            try {
                String filepath = pyFilepath.substring(0, pyFilepath.lastIndexOf("/") + 1) + "data.txt";
                reader = new BufferedInputStream(new FileInputStream(new File(filepath)));
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
            
            //if reader failed
            if(reader == null) {
                new MessageBox("Couldn't find file!").setVisible(true);
                return;
            }
            
            //current item
            String curr = "";
            
            while(true) {
                try {
                    int avail = reader.available();
                    if(avail > 0) {
                        char c = (char) reader.read();
                        if(c == '\n') {
                            updateUI(curr);
                            curr = "";
                        } else {
                            curr += c;
                        }
                    } else {
                        try {
                            Thread.sleep(50);
                        } catch(InterruptedException e) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                } catch (IOException e) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, e);
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
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    };
    
    //holds the python filepath
    static String pyFilepath;
    //Process that is running the python script
    Process pyScript;
    //holds the start time of the program
    static long logStartTime = 0;
    //HashMap of all the values we receive
    CategoricalHashTable<LogObject> logData;
    //list of all the windows open
    ArrayList<LiveChart> graphList;
    //holds if running
    boolean isRunning;
    
    //Constructor for this class
    public MainWindow() {
        //start off not running
        isRunning = false;
        //create its components
        initComponents();
        //initiate the hashmap
        logData = new CategoricalHashTable<>();
        //initiate the list of windows
        graphList = new ArrayList<>();
        //launch the thread that adds sample data.
        graphThread = new Thread(graphRunner);
        graphThread.start();
        pyFilepath = "";
    }
    
    //update the UI from a list of data
    public void updateUI(ArrayList<String> data) {
        //for each string, update the UI.
        for(String str : data) {
            updateUI(str);
        }
    }
    
    public void updateUI(String data) {
        //if the length of the line is not the right size skip the value
        if(data.length() != 20) {
            System.out.println("Invalid CAN String!");
            return;
        }
        //get the identifier of the data value
        String identifier = data.substring(1,4);
        //switch on the identifier
        switch (identifier) {
            //group one is RPM, TPS, fuelopentime, ignition angle
            case "001":
                parseGroupOne(data.substring(4));
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
            case "005":
                break;
            //group six contains battery voltage, ambient temperature, and coolant temperature
            case "006":
                parseGroupSix(data.substring(4));
                break;
            case "007":
                break;
            case "008":
                parseGroupEight(data.substring(4));
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
                parseGroupFourteen(data.substring(4));
                break;
            case "015":
                parseGroupFifteen(data.substring(4));
                break;
            case "016":
                parseGroupSixteen(data.substring(4));
                break;
            case "017":
                break;
            default:
                System.out.println("Parse fail");
                break;
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

        fileChooser = new javax.swing.JFileChooser();
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
        startButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        findPythonFileMenuItem = new javax.swing.JMenuItem();
        exportDataMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("RPM");

        rpmTextField.setEditable(false);
        rpmTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rpmTextFieldMouseClicked(evt);
            }
        });

        tpsTextField.setEditable(false);
        tpsTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tpsTextFieldActionPerformed(evt);
            }
        });

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

        startButton.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        startButton.setText("START THIS BITCH!");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        findPythonFileMenuItem.setText("Find Python File");
        findPythonFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findPythonFileMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(findPythonFileMenuItem);

        exportDataMenuItem.setText("Export Data");
        exportDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportDataMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(exportDataMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

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
                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //on start button pressed
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        isRunning = !isRunning;
        if(isRunning) {
            try {
                //start python script
                pyScript = Runtime.getRuntime().exec("python " + pyFilepath);
                Thread.sleep(250);
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            startButton.setText("STOP THIS BITCH!"); 
            logStartTime = System.currentTimeMillis();
            parseThread = new Thread(fileParser);
            parseThread.start();
        }
        else {
            startButton.setText("START THIS BITCH!");
            pyScript.destroy();
            if(parseThread.isAlive())
                parseThread.interrupt();
        }
        
    }//GEN-LAST:event_startButtonActionPerformed

    //when the field is clicked
    private void rpmTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rpmTextFieldMouseClicked
        // TODO add your handling code here:
        showJFreeChart("Time,RPM");
    }//GEN-LAST:event_rpmTextFieldMouseClicked

    private void findPythonFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findPythonFileMenuItemActionPerformed
        // TODO add your handling code here:
        //open file for vehicleData
        // Open a separate dialog to select a .csv file
        fileChooser = new JFileChooser() {

            // Override approveSelection method because we only want to approve
            //  the selection if its is a .csv file.
            @Override
            public void approveSelection() {
                File chosenFile = getSelectedFile();

                // Make sure that the chosen file exists
                if (chosenFile.exists()) {
                    // Get the file extension to make sure it is .csv
                    String filePath = chosenFile.getAbsolutePath();
                    int lastIndex = filePath.lastIndexOf(".");
                    String fileExtension = filePath.substring(lastIndex,
                        filePath.length());

                    // approve selection if it is a .csv file
                    if (fileExtension.equals(".py")) {
                        super.approveSelection();
                    } else {
                        // do nothing - that selection should not be approved
                    }

                }
            }
        };

        // showOpenDialog returns the chosen option and if it as an approve
        //  option then the file should be imported and opened
        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            pyFilepath = fileChooser.getSelectedFile().getAbsolutePath();
        }
    }//GEN-LAST:event_findPythonFileMenuItemActionPerformed

    //Exports data that we have logged into a CSV file
    private void exportDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportDataMenuItemActionPerformed
        //Call the method to export
        hashTableToCSV();
    }//GEN-LAST:event_exportDataMenuItemActionPerformed

    private void tpsTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tpsTextFieldActionPerformed
        showJFreeChart("Time,TPS");
    }//GEN-LAST:event_tpsTextFieldActionPerformed

    //create the JFree Chart and show it
    private void showJFreeChart(String TAG) {
        //get the chart object from another method call
        JFreeChart chart = createJFreeChart(TAG);
        //create a new window that has the chart.
        LiveChart liveChart = new LiveChart(chart, TAG);
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
    
    public void hashTableToCSV()
    {
        try {
            //get todays date
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");  
            Date date = new Date();
            String now = formatter.format(date);
            // Creates a new csv file to put data into. File is located within 'PitSoftware' git folder
            FileOutputStream csv = new FileOutputStream(new File(now + ".csv"), true);
            // Allows program to print/write data into file
            PrintWriter pw = new PrintWriter(csv);
            
            // Loop continues based on total number of tags in array 'tags' from importCSV
            for (String tag : logData.getTags()) {
                // Creates array of SimpleLogObject that only includes data from 'dataMap' under 'tag'
                LinkedList<LogObject> data = logData.getList(tag);
                if(!data.isEmpty()) {
                    // Prints 'tag' before data is printed if data is not empty
                    pw.println(tag);
                
                    // Loop that prints data under 'tag' on separate lines
                    for (LogObject lo : data) {
                        // Prints each piece of data to a unique cell on one line
                        pw.println(lo.toString());
                        // Sends single data line to print in file
                        pw.flush();
                    }
                    //print end tag
                    pw.println("END\n");
                
                }
            }
            
            System.out.println ("File sample.csv has been created" );
            
        } catch (IOException x) {
            System.out.println(x);
        }
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
    private javax.swing.JTextField airTempTextField;
    private javax.swing.JTextField analogInput1TextField;
    private javax.swing.JTextField analogInput2TextField;
    private javax.swing.JTextField analogInput3TextField;
    private javax.swing.JTextField analogInput4TextField;
    private javax.swing.JTextField batteryVoltageTextField;
    private javax.swing.JTextField coolantTempTextField;
    private javax.swing.JMenuItem exportDataMenuItem;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenuItem findPythonFileMenuItem;
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
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JTextField lambdaTextField;
    private javax.swing.JTextField mapTextField;
    private javax.swing.JTextField rpmTextField;
    private javax.swing.JButton startButton;
    private javax.swing.JTextField tpsTextField;
    // End of variables declaration//GEN-END:variables
}
