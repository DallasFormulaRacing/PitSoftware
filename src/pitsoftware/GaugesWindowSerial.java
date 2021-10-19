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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.logging.Logger;
import javax.swing.JMenuItem;
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
/**
 *
 * @author aribdhuka
 */
public class GaugesWindowSerial extends javax.swing.JFrame {

    TreeMap<String, ScaledRadial> gauges;
    CategoricalHashTable<LogObject> logData;
    //Thread that handles parsing hexstrings in the background
    Thread parseThread;
    long startTime;
    int timeoutCount = 0;
    int timeoutThreshold = 300; // desired number of seconds (15) divided by 50 milliseconds
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
                        Logger.getLogger(GaugesWindowSerial.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    timeoutCount += 1;
                    if (timeoutCount >= timeoutThreshold) {
                        new MessageBox("No data received, check \nconfiguration!").setVisible(true);
                        timeoutCount = 0;
                        throw new RuntimeException("No data received for 15 seconds");
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
    long currTime;
    long currAccelTime;
    
    //static variables for serial port
    static final LinkedList<String> data = new LinkedList<>();
    static SerialPort serial;
    static BufferedReader input;
    static StringBuilder incompleteData;
    String serialPath;
    static boolean suspend;
    
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
    
    
    private static double x = 0;
    private static double y = 0;
    private static double z = 0;
    
    private static double speed = 0;
    private static double transTeeth = 0;
    
    
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
        serialPath = "";
        
        incompleteData = new StringBuilder();
        suspend = false;
        currTime = 0;
        currAccelTime = 0;
        
        createSerialMenu();
    }
    
    private void createSerialMenu() {
        String[] portList = SerialPortList.getPortNames();
        if(portList.length == 1) {
            serialPath = portList[0];
            Toast.makeToast(this, "Selected: " + serialPath, Toast.DURATION_MEDIUM);
        }
        for(String s : portList) {
            JMenuItem item = new JMenuItem(s);
            applyAction(item);
            serialMenu.add(item);
        }
    }
    
    private void applyAction(JMenuItem item) {
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serialPath = item.getText();
                Toast.makeToast(GaugesWindowSerial.this, "Selected: " + serialPath, Toast.DURATION_MEDIUM);
            }
        });

    }
    
    private void createSerial(String portName) throws TooManyListenersException, IOException, SerialPortException {
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
        findSerialPortsMenuItem = new javax.swing.JMenuItem();
        exportDataMenuItem = new javax.swing.JMenuItem();
        serialMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new java.awt.Dimension(1375, 800));

        fuelOpenTimePanel.setPreferredSize(new java.awt.Dimension(250, 188));
        fuelOpenTimePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fuelOpenTimePanelMouseReleased(evt);
            }
        });

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
        coolantPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                coolantPanelMouseReleased(evt);
            }
        });

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
        mapPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mapPanelMouseReleased(evt);
            }
        });

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
        AFRPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AFRPanelMouseClicked(evt);
            }
        });

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
        lamda1RawPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lamda1RawPanelMouseReleased(evt);
            }
        });

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
        lamda2RawPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lamda2RawPanelMouseReleased(evt);
            }
        });

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
        ignAnglePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ignAnglePanelMouseReleased(evt);
            }
        });

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
        rpmPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                rpmPanelMouseReleased(evt);
            }
        });

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
        tpsPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tpsPanelMouseReleased(evt);
            }
        });

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
        bprPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                bprPanelMouseReleased(evt);
            }
        });

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
        bpfPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                bpfPanelMouseReleased(evt);
            }
        });

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
        airPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                airPanelMouseReleased(evt);
            }
        });

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
        barPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                barPanelMouseReleased(evt);
            }
        });

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
        voltagePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                voltagePanelMouseReleased(evt);
            }
        });

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
        speedPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                speedPanelMouseReleased(evt);
            }
        });

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

        findSerialPortsMenuItem.setText("Find Serial Ports");
        findSerialPortsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findSerialPortsMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(findSerialPortsMenuItem);

        exportDataMenuItem.setText("Export Data");
        exportDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportDataMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(exportDataMenuItem);

        jMenuBar1.add(jMenu1);

        serialMenu.setText("Ports");
        jMenuBar1.add(serialMenu);

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
                                .addComponent(coolantPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(AFRPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lamda1RawPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lamda2RawPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(airPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(barPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(speedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                try {
                    createSerial(serialPath);
                    parseThread = new Thread(serialParser);
                    parseThread.start();
                    //set button text
                    startButton.setText("STOP THIS BITCH!");
                    //set start time
                    logStartTime = System.currentTimeMillis();
                    currTime = 0;
                    currAccelTime = 0;
                } catch (TooManyListenersException ex) {
                    new MessageBox("too many listeners.").setVisible(true);
                } catch (IOException ex) {
                    new MessageBox("io exception").setVisible(true);
                } catch (SerialPortException ex) {
                    new MessageBox("serial port probably not found. most likely\n idk man. you prob gave some bs port.\n try again.").setVisible(true);
                }
            } else {
                //if path was not defined
                //set isRunning to false
                isRunning = false;
                //let the user know they need to select a serial path
                new MessageBox("DEFINE SERIAL PATH!! DUMBASS.").setVisible(true);
            }
        }
        else {
            try {
                serial.closePort();
                serial = null;
                data.clear();
            } catch (SerialPortException ex) {
                new MessageBox("uhh. This is incredibly unlikely.").setVisible(true);
            }
            startButton.setText("START THIS BITCH!");
            try {
                //copy file before its deleted
                hashTableToCSV();
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(parseThread.isAlive())
                parseThread.interrupt();
        }
        
    }//GEN-LAST:event_startButtonActionPerformed

    private void exportDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportDataMenuItemActionPerformed
        //Call the method to export
        hashTableToCSV();
    }//GEN-LAST:event_exportDataMenuItemActionPerformed

    private void findSerialPortsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findSerialPortsMenuItemActionPerformed
        serialMenu.removeAll();
        createSerialMenu();
    }//GEN-LAST:event_findSerialPortsMenuItemActionPerformed

    private void rpmPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rpmPanelMouseReleased
        showJFreeChart("Time,RPM");        
    }//GEN-LAST:event_rpmPanelMouseReleased

    private void coolantPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_coolantPanelMouseReleased
        showJFreeChart("Time,Coolant");
    }//GEN-LAST:event_coolantPanelMouseReleased

    private void ignAnglePanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ignAnglePanelMouseReleased
        showJFreeChart("Time,IgnitionAngle");
    }//GEN-LAST:event_ignAnglePanelMouseReleased

    private void fuelOpenTimePanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fuelOpenTimePanelMouseReleased
        showJFreeChart("Time,FuelOpenTime");
    }//GEN-LAST:event_fuelOpenTimePanelMouseReleased

    private void mapPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mapPanelMouseReleased
        showJFreeChart("Time,MAP");
    }//GEN-LAST:event_mapPanelMouseReleased

    private void AFRPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AFRPanelMouseClicked
        showJFreeChart("Time,AFR");
    }//GEN-LAST:event_AFRPanelMouseClicked

    private void lamda1RawPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lamda1RawPanelMouseReleased
        showJFreeChart("Time,RadiatorInlet");
    }//GEN-LAST:event_lamda1RawPanelMouseReleased

    private void lamda2RawPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lamda2RawPanelMouseReleased
        showJFreeChart("Time,RadiatorOutlet");
    }//GEN-LAST:event_lamda2RawPanelMouseReleased

    private void speedPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_speedPanelMouseReleased
        showJFreeChart("Time,WheelspeedRear");
    }//GEN-LAST:event_speedPanelMouseReleased

    private void barPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barPanelMouseReleased
        showJFreeChart("Time,Barometer");
    }//GEN-LAST:event_barPanelMouseReleased

    private void airPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_airPanelMouseReleased
        showJFreeChart("Time,AirTemp");
    }//GEN-LAST:event_airPanelMouseReleased

    private void voltagePanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_voltagePanelMouseReleased
        showJFreeChart("Time,Voltage");
    }//GEN-LAST:event_voltagePanelMouseReleased

    private void tpsPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tpsPanelMouseReleased
        showJFreeChart("Time,TPS");
    }//GEN-LAST:event_tpsPanelMouseReleased

    private void bpfPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bpfPanelMouseReleased
        showJFreeChart("Time,Analog1");
    }//GEN-LAST:event_bpfPanelMouseReleased

    private void bprPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bprPanelMouseReleased
        showJFreeChart("Time,Analog2");
    }//GEN-LAST:event_bprPanelMouseReleased

    /**
     * Draws all the gauges for each panel on initialization of frame
     */
    private void drawGauges() {
        createCircularGauge("FuelOpenTime", "MS", "FuelOpenTime", new Dimension(200,200), 0, 12, 5.5, false, 4.5, 11, fuelOpenTimePanel);
        createCircularGauge("A:F", "AFR", "AFR", new Dimension(200,200), 10, 22, 12, true, 10, 13, AFRPanel);
        createCircularGauge("Ignition Angle", "deg-ret", "IgnitionAngle", new Dimension(200,200), 0, 45, 50, false, 0, 0, ignAnglePanel);
//        createCircularGauge("Raw Lambda Voltage", "Volts", "Analog3", new Dimension(200,200), 0, 5, 1, true, 0, 1, lamda1RawPanel);
//        createCircularGauge("Raw Lambda Voltage", "Volts", "Analog4", new Dimension(200,200), 0, 5, 1, true, 0, 1, lamda2RawPanel);
        createCircularGauge("Inlet Temp", "Farhenheit", "RadiatorInlet", new Dimension(200,200), 32, 225, 200, false, 200, 225, lamda1RawPanel);
        createCircularGauge("Outlet Temp", "Farhenheit", "RadiatorOutlet", new Dimension(200,200), 32, 225, 200, false, 200, 225, lamda2RawPanel);
        createCircularGauge("Manifold Air Pressure", "psi", "MAP", new Dimension(200,200), 0, 18, 5, true, 0, 5, mapPanel);
        createCircularGauge("Coolant", "Farenheit", "Coolant", new Dimension(200,200), 32, 225, 200, false, 200, 225, coolantPanel);
        createCircularGauge("Engine RPM", "RPMx1K", "RPM", new Dimension(400,400), 1000, 0, 14, 12, false, 10.5, 14, rpmPanel);
        createCircularGauge("Throttle Position", "%", "TPS", new Dimension(200,200), 0, 100, 75, false, 90, 100, tpsPanel);
        createCircularGauge("Brake Pressure Front", "psix1000", "Analog1", new Dimension(200,200), 1000, 0, 2, 2, false, 1.5, 2, bpfPanel);
        createCircularGauge("Brake Pressure Rear", "psix1000", "Analog2", new Dimension(200,200), 1000, 0, 2, 2, false, 1.5, 2, bprPanel);
        createCircularGauge("Intake Air Temp", "Farenheit", "AirTemp", new Dimension(200,200), 32, 110, 100, false, 100, 110, airPanel);
        createCircularGauge("Barometer", "psi", "Barometer", new Dimension(200,200), 0, 15, 13, true, 0, 0, barPanel);
        createCircularGauge("Battery Voltage", "Volts", "Voltage", new Dimension(200,200), 0, 15.5, 12, true, 0, 11.5, voltagePanel);
        createCircularGauge("Speed", "MPH", "Speed", new Dimension(200,200), 0, 99, 60, false, 60, 99, speedPanel);
        
        
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
                Toast.makeToast(this, "Parse fail", Toast.DURATION_SHORT);
                break;
        }
    }
    
    
    //create with default scale
    private Radial createCircularGauge(String title, String unit, String TAG, Dimension size, double min, double max, double threshold, boolean invertThreshold, double trackStart, double trackStop, JPanel parent) {
        return createCircularGauge(title, unit, TAG, size, 1.0, min, max, threshold, invertThreshold, trackStart, trackStop, parent);
    }
    
    private Radial createCircularGauge(String title, String unit, String TAG, Dimension size, double scale, double min, double max, double threshold, boolean invertThreshold, double trackStart, double trackStop, JPanel parent) {
        //create object
        ScaledRadial gauge = new ScaledRadial();
        //set the title
        gauge.setTitle(title);
        //set the units
        gauge.setUnitString(unit);
        //set the size
        gauge.setSize(size);
        //set the scale
        gauge.setScale(scale);
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
        gauge.setMaxMeasuredValueVisible(true);
        gauge.setMinMeasuredValueVisible(true);
        //set the panel the gauge will go in size
        parent.setPreferredSize(size);
        //add the gauge to the panel
        parent.add(gauge);
        gauges.put(TAG, gauge);
        
        return gauge;
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
//        double batteryVoltage, airTemp, coolantTemp;
        
        double batVol1, batVol2;
        batVol1 = Integer.parseInt(line.substring(0,2), 16);
        batVol2 = Integer.parseInt(line.substring(2,4), 16) * 256;
        volts = batVol1 + batVol2;
        volts *= 0.01;
        //logData.put(new SimpleLogObject("Time,Voltage", batteryVoltage, currTime));
        
        double airTemp1, airTemp2;
        airTemp1 = Integer.parseInt(line.substring(4,6), 16);
        airTemp2 = Integer.parseInt(line.substring(6,8), 16) * 256;
        airTemp = airTemp1 + airTemp2;
        airTemp *= 0.1;
        //logData.put(new SimpleLogObject("Time,Air", airTemp, currTime));

        double coolantTemp1, coolantTemp2;
        coolantTemp1 = Integer.parseInt(line.substring(8,10), 16);
        coolantTemp2 = Integer.parseInt(line.substring(10,12), 16) * 256;
        coolant = coolantTemp1 + coolantTemp2;
        coolant *= 0.1;
        //logData.put(new SimpleLogObject("Time,Coolant", coolantTemp, currTime));
        
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
                Toast.makeToast(this, "Try again.", Toast.DURATION_SHORT);
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
            Toast.makeToast(this, "Try again.", Toast.DURATION_SHORT);
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
            FileNameDialog fnd = new FileNameDialog(this, true, filename);
            fnd.setVisible(true);
            while(filename[0].isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GaugesWindowSerial.class.getName()).log(Level.SEVERE, null, ex);
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
            
            Toast.makeToast(this, "Saved as: " + now, Toast.DURATION_LONG);
            
        } catch (IOException x) {
            Toast.makeToast(this, "Save Error!", Toast.DURATION_MEDIUM);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AFRPanel;
    private javax.swing.JPanel airPanel;
    private javax.swing.JPanel barPanel;
    private javax.swing.JPanel bpfPanel;
    private javax.swing.JPanel bprPanel;
    private javax.swing.JPanel coolantPanel;
    private javax.swing.JMenuItem exportDataMenuItem;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenuItem findSerialPortsMenuItem;
    private javax.swing.JPanel fuelOpenTimePanel;
    private javax.swing.JPanel ignAnglePanel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel lamda1RawPanel;
    private javax.swing.JPanel lamda2RawPanel;
    private javax.swing.JPanel mapPanel;
    private javax.swing.JPanel rpmPanel;
    private javax.swing.JMenu serialMenu;
    private javax.swing.JPanel speedPanel;
    private javax.swing.JButton startButton;
    private javax.swing.JPanel tpsPanel;
    private javax.swing.JPanel voltagePanel;
    // End of variables declaration//GEN-END:variables
}
