/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import com.arib.categoricalhashtable.CategoricalHashTable;
import com.arib.toast.Toast;
import eu.hansolo.steelseries.gauges.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
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
import static pitsoftware.MainWindow.pyFilepath;

/**
 *
 * @author aribdhuka
 */
public class GaugesWindowSerial extends javax.swing.JFrame {

    TreeMap<String, Radial> gauges;
    CategoricalHashTable<LogObject> logData;
    //Thread that handles parsing hexstrings in the background
    Thread parseThread;
    Runnable serialParser = new Runnable() {
        @Override
        public void run() {
            
            try {
                createSerial(serialPath);
            } catch (TooManyListenersException ex) {
                new MessageBox("too many listeners.").setVisible(true);
                Thread.currentThread().interrupt();
            } catch (IOException ex) {
                new MessageBox("io exception").setVisible(true);
                Thread.currentThread().interrupt();
            } catch (SerialPortException ex) {
                new MessageBox("serial port probably not found. most likely\n idk man. you prob gave some bs port.\n try again.").setVisible(true);
                Thread.currentThread().interrupt();
            }
            
            while(true) {
                if(!data.isEmpty()) {
                    suspend = true;
                    updateUI(data);
                    suspend = false;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GaugesWindowSerial.class.getName()).log(Level.SEVERE, null, ex);
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
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
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
    
    
    //holds the start time of the program
    static long logStartTime = 0;
    //list of all the windows open
    ArrayList<LiveChart> graphList;
    //holds if running
    boolean isRunning;
    
    
    //static variables for serial port
    static ArrayList<String> data;
    static SerialPort serial;
    static BufferedReader input;
    static StringBuilder incompleteData;
    String serialPath;
    static boolean suspend;
    
    
    /**
     * Creates new form GaugesWindow
     */
    public GaugesWindowSerial() {
        initComponents();
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//
//        this.setSize(screenSize);
        gauges = new TreeMap<>();
        logData = new CategoricalHashTable<>();
        drawGauges();
        
        //start off not running
        isRunning = false;
        //initiate the list of windows
        graphList = new ArrayList<>();
        //launch the thread that adds sample data.
        graphThread = new Thread(graphRunner);
        graphThread.start();
        
        //TODO: undo this.
        serialPath = "/dev/tty.usbmodem144401";
        
        incompleteData = new StringBuilder();
        data = new ArrayList<>();
        suspend = false;
    }
    
    private void createSerial(String portName) throws TooManyListenersException, IOException, SerialPortException {
        data = new ArrayList<>();
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
        
        serial.openPort();
        serial.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, 0, SerialPort.PARITY_NONE);
        serial.addEventListener(new PortReader());
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
        fuelOpenTimePanel = new javax.swing.JPanel();
        coolantPanel = new javax.swing.JPanel();
        mapPanel = new javax.swing.JPanel();
        AFRPanel = new javax.swing.JPanel();
        lamda1RawPanel = new javax.swing.JPanel();
        lamda2RawPanel = new javax.swing.JPanel();
        ignAnglePanel = new javax.swing.JPanel();
        rpmPanel = new javax.swing.JPanel();
        tpsPanel = new javax.swing.JPanel();
        bprPanel = new javax.swing.JPanel();
        bpfPanel = new javax.swing.JPanel();
        airPanel = new javax.swing.JPanel();
        barPanel = new javax.swing.JPanel();
        voltagePanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        speedPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        findPythonFileMenuItem = new javax.swing.JMenuItem();
        exportDataMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new java.awt.Dimension(1375, 800));

        fuelOpenTimePanel.setPreferredSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout fuelOpenTimePanelLayout = new javax.swing.GroupLayout(fuelOpenTimePanel);
        fuelOpenTimePanel.setLayout(fuelOpenTimePanelLayout);
        fuelOpenTimePanelLayout.setHorizontalGroup(
            fuelOpenTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        fuelOpenTimePanelLayout.setVerticalGroup(
            fuelOpenTimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        coolantPanel.setPreferredSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout coolantPanelLayout = new javax.swing.GroupLayout(coolantPanel);
        coolantPanel.setLayout(coolantPanelLayout);
        coolantPanelLayout.setHorizontalGroup(
            coolantPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        coolantPanelLayout.setVerticalGroup(
            coolantPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        mapPanel.setPreferredSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        AFRPanel.setPreferredSize(new java.awt.Dimension(200, 200));

        javax.swing.GroupLayout AFRPanelLayout = new javax.swing.GroupLayout(AFRPanel);
        AFRPanel.setLayout(AFRPanelLayout);
        AFRPanelLayout.setHorizontalGroup(
            AFRPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        AFRPanelLayout.setVerticalGroup(
            AFRPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        lamda1RawPanel.setPreferredSize(new java.awt.Dimension(200, 200));

        javax.swing.GroupLayout lamda1RawPanelLayout = new javax.swing.GroupLayout(lamda1RawPanel);
        lamda1RawPanel.setLayout(lamda1RawPanelLayout);
        lamda1RawPanelLayout.setHorizontalGroup(
            lamda1RawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        lamda1RawPanelLayout.setVerticalGroup(
            lamda1RawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        lamda2RawPanel.setPreferredSize(new java.awt.Dimension(200, 200));

        javax.swing.GroupLayout lamda2RawPanelLayout = new javax.swing.GroupLayout(lamda2RawPanel);
        lamda2RawPanel.setLayout(lamda2RawPanelLayout);
        lamda2RawPanelLayout.setHorizontalGroup(
            lamda2RawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        lamda2RawPanelLayout.setVerticalGroup(
            lamda2RawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        ignAnglePanel.setPreferredSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout ignAnglePanelLayout = new javax.swing.GroupLayout(ignAnglePanel);
        ignAnglePanel.setLayout(ignAnglePanelLayout);
        ignAnglePanelLayout.setHorizontalGroup(
            ignAnglePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        ignAnglePanelLayout.setVerticalGroup(
            ignAnglePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        rpmPanel.setPreferredSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout rpmPanelLayout = new javax.swing.GroupLayout(rpmPanel);
        rpmPanel.setLayout(rpmPanelLayout);
        rpmPanelLayout.setHorizontalGroup(
            rpmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        rpmPanelLayout.setVerticalGroup(
            rpmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        tpsPanel.setPreferredSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout tpsPanelLayout = new javax.swing.GroupLayout(tpsPanel);
        tpsPanel.setLayout(tpsPanelLayout);
        tpsPanelLayout.setHorizontalGroup(
            tpsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        tpsPanelLayout.setVerticalGroup(
            tpsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        bprPanel.setPreferredSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout bprPanelLayout = new javax.swing.GroupLayout(bprPanel);
        bprPanel.setLayout(bprPanelLayout);
        bprPanelLayout.setHorizontalGroup(
            bprPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        bprPanelLayout.setVerticalGroup(
            bprPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        bpfPanel.setPreferredSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout bpfPanelLayout = new javax.swing.GroupLayout(bpfPanel);
        bpfPanel.setLayout(bpfPanelLayout);
        bpfPanelLayout.setHorizontalGroup(
            bpfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        bpfPanelLayout.setVerticalGroup(
            bpfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        airPanel.setPreferredSize(new java.awt.Dimension(200, 200));
        airPanel.setSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout airPanelLayout = new javax.swing.GroupLayout(airPanel);
        airPanel.setLayout(airPanelLayout);
        airPanelLayout.setHorizontalGroup(
            airPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        airPanelLayout.setVerticalGroup(
            airPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        barPanel.setPreferredSize(new java.awt.Dimension(200, 200));
        barPanel.setSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout barPanelLayout = new javax.swing.GroupLayout(barPanel);
        barPanel.setLayout(barPanelLayout);
        barPanelLayout.setHorizontalGroup(
            barPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        barPanelLayout.setVerticalGroup(
            barPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        voltagePanel.setPreferredSize(new java.awt.Dimension(250, 188));
        voltagePanel.setSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout voltagePanelLayout = new javax.swing.GroupLayout(voltagePanel);
        voltagePanel.setLayout(voltagePanelLayout);
        voltagePanelLayout.setHorizontalGroup(
            voltagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        voltagePanelLayout.setVerticalGroup(
            voltagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        startButton.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        startButton.setText("Start This Bitch!");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        speedPanel.setPreferredSize(new java.awt.Dimension(200, 200));
        speedPanel.setSize(new java.awt.Dimension(250, 188));

        javax.swing.GroupLayout speedPanelLayout = new javax.swing.GroupLayout(speedPanel);
        speedPanel.setLayout(speedPanelLayout);
        speedPanelLayout.setHorizontalGroup(
            speedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        speedPanelLayout.setVerticalGroup(
            speedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(fuelOpenTimePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(coolantPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ignAnglePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rpmPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(bpfPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(bprPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tpsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(voltagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(AFRPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lamda1RawPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lamda2RawPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                        .addComponent(speedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(barPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(airPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(rpmPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(bprPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(bpfPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(47, 47, 47)
                                    .addComponent(tpsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(voltagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(ignAnglePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(fuelOpenTimePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(mapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(50, 50, 50)
                                    .addComponent(coolantPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(AFRPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lamda1RawPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lamda2RawPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(airPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(barPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(speedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(100, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        isRunning = !isRunning;
        if(isRunning) {
            //check if serial path is defined
            if(!serialPath.isEmpty()) {
                //start thread to read
                parseThread = new Thread(serialParser);
                parseThread.start();
                //set button text
                startButton.setText("STOP THIS BITCH!");
                //set start time
                logStartTime = System.currentTimeMillis();
            } else {
                //if path was not defined
                //set isRunning to false
                isRunning = false;
                //let the user know they need to select a serial path
                new MessageBox("DEFINE SERIAL PATH!! DUMBASS.").setVisible(true);
            }
        }
        else {
            startButton.setText("START THIS BITCH!");
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date date = new Date();
            String now = formatter.format(date);
            try {
                //copy file before its deleted
                hashTableToCSV();
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(parseThread.isAlive())
                parseThread.interrupt();
            
            try {
                serial.closePort();
            } catch (SerialPortException ex) {
                new MessageBox("uhh. This is incredibly unlikely.").setVisible(true);
            }
        }
        
    }//GEN-LAST:event_startButtonActionPerformed

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

    private void exportDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportDataMenuItemActionPerformed
        //Call the method to export
        hashTableToCSV();
    }//GEN-LAST:event_exportDataMenuItemActionPerformed

    /**
     * Draws all the gauges for each panel on initialization of frame
     */
    private void drawGauges() {
        createCircularGauge("FuelOpenTime", "MS", "FuelOpenTime", new Dimension(200,200), 0, 12, 5.5, false, 4.5, 11, fuelOpenTimePanel);
        createCircularGauge("A:F", "AFR", "AFR", new Dimension(200,200), 10, 22, 12, true, 10, 13, AFRPanel);
        createCircularGauge("Ignition Angle", "deg-ret", "IgnitionAngle", new Dimension(200,200), 0, 45, 50, false, 0, 0, ignAnglePanel);
        createCircularGauge("Raw Lambda Voltage", "Volts", "Analog3", new Dimension(200,200), 0, 5, 1, true, 0, 1, lamda1RawPanel);
        createCircularGauge("Raw Lambda Voltage", "Volts", "Analog4", new Dimension(200,200), 0, 5, 1, true, 0, 1, lamda2RawPanel);
        createCircularGauge("Manifold Air Pressure", "psi", "MAP", new Dimension(200,200), 0, 18, 5, true, 0, 5, mapPanel);
        createCircularGauge("Coolant", "Farenheit", "Coolant", new Dimension(200,200), 32, 225, 200, false, 200, 225, coolantPanel);
        createCircularGauge("Engine RPM", "RPMx1k", "RPM", new Dimension(400,400), 0, 14, 12, false, 10.5, 14, rpmPanel);
        createCircularGauge("Throttle Position", "%", "TPS", new Dimension(200,200), 0, 100, 75, false, 90, 100, tpsPanel);
        createCircularGauge("Brake Pressure Front", "psix1000", "Analog1", new Dimension(200,200), 0, 2, 2, false, 1.5, 2, bpfPanel);
        createCircularGauge("Brake Pressure Rear", "psix1000", "Analog2", new Dimension(200,200), 0, 2, 2, false, 1.5, 2, bprPanel);
        createCircularGauge("Intake Air Temp", "Farenheit", "AirTemp", new Dimension(200,200), 32, 110, 100, false, 100, 110, airPanel);
        createCircularGauge("Barometer", "psi", "Barometer", new Dimension(200,200), 0, 15, 13, true, 0, 0, barPanel);
        createCircularGauge("Battery Voltage", "Volts", "Voltage", new Dimension(200,200), 0, 15.5, 12, true, 0, 11.5, voltagePanel);
        createCircularGauge("Speed", "MPH", "Speed", new Dimension(200,200), 0, 99, 60, false, 60, 99, speedPanel);
        
    }
    
    //update the UI from a list of data
    public void updateUI(ArrayList<String> data) {
        //for each string, update the UI.
        for(String str : data) {
            updateUI(str);
        }
        data.clear();
    }
    
    public void updateUI(String data) {
        if(data.isEmpty())
            return;
        if(data.length() < 4)
            return;
        //if the length of the line is not the right size skip the value
        if(!data.substring(0, 4).equals("#005") && data.length() != 23) {
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
                parseGroupFive(data.substring(4));
                break;
            //group six contains battery voltage, ambient temperature, and coolant temperature
            case "006":
                parseGroupSix(data.substring(4));
                break;
            case "007":
                parseGroupSeven(data.substring(4));
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
                Toast.makeToast(this, "Parse fail", Toast.DURATION_SHORT);
                break;
        }
    }
    
    private void createCircularGauge(String title, String unit, String TAG, Dimension size, double min, double max, double threshold, boolean invertThreshold, double trackStart, double trackStop, JPanel parent) {
        //create object
        Radial gauge = new Radial();
        //set the title
        gauge.setTitle(title);
        //set the units
        gauge.setUnitString(unit);
        //set the size
        gauge.setSize(size);
        //set the max limit
        gauge.setMaxValue(max);
        //set the min value
        gauge.setMinValue(min);
        //set the threshold value for the blinking led
        gauge.setThreshold(threshold);
        //how to invert the threshhold, if true: if the current value is less than the threshold set blink,
        //if false, if current value is greater than the threshold set blink
        gauge.setThresholdBehaviourInverted(invertThreshold);
        //set the "redline" or track for the gauge
        if(trackStart != trackStop) {
            gauge.setTrackStop(trackStop);
            gauge.setTrackStart(trackStart);
            gauge.setTrackStartColor(Color.RED);
            gauge.setTrackStopColor(Color.RED);
            gauge.setTrackVisible(true);
        }
        //set the panel the gauge will go in size
        parent.setPreferredSize(size);
        //add the gauge to the panel
        parent.add(gauge);
        gauges.put(TAG, gauge);
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
        
        gauges.get("RPM").setValue(rpm/1000.0);
        gauges.get("TPS").setValue(tps);
        gauges.get("FuelOpenTime").setValue(fuelOpenTime);
        gauges.get("IgnitionAngle").setValue(ignAngle);
        
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
        
        gauges.get("Barometer").setValue(barometer);
        gauges.get("MAP").setValue(map);
        //lambda input no longer exists
        //ecu is mapped to analog 3 and 4 for 2 lambda sensors
        
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
        
        double avg = input3 + input4;
        avg /= 2;
        avg = (2*avg)+10;
        
        
        gauges.get("Analog1").setValue(input1);
        gauges.get("Analog2").setValue(input2);
        gauges.get("Analog3").setValue(input3);
        gauges.get("Analog4").setValue(input4);
        gauges.get("AFR").setValue(avg);
    }
    
    public void parseGroupFive(String line)
    {
        try {
            int transTeeth = Integer.parseInt(line.substring(0, line.length()-3));
            double speed = ((transTeeth/23.0)*.2323090909*60)*(3.141592654*.0003219697);
            speed *= 60;
            gauges.get("Speed").setValue(speed);
        } catch(NumberFormatException e) {
            System.out.println("speed format exception--" + line);
        }
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
        
        gauges.get("Voltage").setValue(batteryVoltage);
        gauges.get("AirTemp").setValue(airTemp);
        gauges.get("Coolant").setValue(coolantTemp);
    }

    private void parseGroupSeven(String line) {
        double inlet = Double.parseDouble(line.substring(0, line.charAt('F')));
        double outlet = Double.parseDouble(line.substring(line.charAt('F')+1, line.length()));
        
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
    
    
    //create the JFree Chart and show it
    private void showJFreeChart(String TAG) {
        //get the chart object from another method call
        JFreeChart chart = createJFreeChart(TAG);
        if(chart == null)
            return;
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
            
            Toast.makeToast(this, "Saved as: " + now, Toast.DURATION_LONG);
            
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
            java.util.logging.Logger.getLogger(GaugesWindowSerial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GaugesWindowSerial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GaugesWindowSerial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GaugesWindowSerial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GaugesWindowSerial().setVisible(true);
            }
        });
    }
    
    private static class PortReader implements SerialPortEventListener {
        
        @Override
        public void serialEvent(SerialPortEvent event) {
            try {
                if(!suspend) {
                    String tempString = serial.readString();
                    for(int i = 0; i < tempString.length(); i++) {
                        if(!incompleteData.toString().isEmpty() && incompleteData.charAt(incompleteData.length() - 1) == '\n') {
                            data.add(incompleteData.toString());
                            incompleteData = new StringBuilder();
                            i--;
                        } else {
                            incompleteData.append(tempString.charAt(i));
                        }
                    }
                    suspend = true;
                }
            } catch (SerialPortException ex) {
                System.out.println("error");
            }
            
            
        }

    } 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AFRPanel;
    private javax.swing.JPanel airPanel;
    private javax.swing.JPanel barPanel;
    private javax.swing.JPanel bpfPanel;
    private javax.swing.JPanel bprPanel;
    private javax.swing.JPanel coolantPanel;
    private javax.swing.JMenuItem exportDataMenuItem;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenuItem findPythonFileMenuItem;
    private javax.swing.JPanel fuelOpenTimePanel;
    private javax.swing.JPanel ignAnglePanel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel lamda1RawPanel;
    private javax.swing.JPanel lamda2RawPanel;
    private javax.swing.JPanel mapPanel;
    private javax.swing.JPanel rpmPanel;
    private javax.swing.JPanel speedPanel;
    private javax.swing.JButton startButton;
    private javax.swing.JPanel tpsPanel;
    private javax.swing.JPanel voltagePanel;
    // End of variables declaration//GEN-END:variables
}
