/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import jssc.SerialPortException;

/**
 *
 * @author aribdhuka
 */
public class MainWindow extends javax.swing.JFrame {

    
    SerialCommunicator serialcomm;
    Thread parseThread;
    Runnable pyParser = () -> {
        while(true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            try {
                BufferedReader data = PythonComm.run();
                String line;
                ArrayList<String> strData = new ArrayList<>();
                while((line = data.readLine()) != null) {
                    strData.add(line);
                    System.out.println(line);
                }
                updateUI(strData);
            } catch(IOException e){ 
                System.out.println(e);
            }
        }
    };
    Runnable parser = () -> {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        while(serialcomm.isRunning) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                
            }
            System.out.println(serialcomm.data.isEmpty());
            if(!serialcomm.data.isEmpty()) {
                String fullLine = serialcomm.data.get(0);
                serialcomm.data.remove(0);
                String identifier = fullLine.substring(1,4);
                System.out.println("Identifier: " + identifier);
                switch (identifier) {
                    case "001":
                        parseGroupOne(fullLine.substring(4));
                        break;
                    case "002":
                        parseGroupTwo(fullLine.substring(4));
                        break;
                    case "003":
                        parseGroupThree(fullLine.substring(4));
                        break;
                    case "004":
                        break;
                    case "005":
                        break;
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
    };
    
    static long logStartTime = 0;
    ArrayList<LogObject> logData;
    
    public MainWindow() {
        initComponents();
        logData = new ArrayList<>();
    }
    
    public void updateUI(ArrayList<String> data) {
        while(!data.isEmpty()) {
            String fullLine = data.get(0);
            data.remove(0);
            if(fullLine.length() < 19 || fullLine.length() > 21)
                continue;
            String identifier = fullLine.substring(1,4);
            System.out.println("Identifier: " + identifier);
            switch (identifier) {
                case "001":
                    parseGroupOne(fullLine.substring(4));
                    break;
                case "002":
                    parseGroupTwo(fullLine.substring(4));
                    break;
                case "003":
                    parseGroupThree(fullLine.substring(4));
                    break;
                case "004":
                    break;
                case "005":
                    break;
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
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
        jLabel1.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel1.setMinimumSize(new java.awt.Dimension(26, 20));

        rpmTextField.setEditable(false);
        rpmTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rpmTextFieldActionPerformed(evt);
            }
        });

        tpsTextField.setEditable(false);

        jLabel2.setText("TPS %");
        jLabel2.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel2.setMinimumSize(new java.awt.Dimension(26, 20));

        fuelTimingTextField.setEditable(false);

        ignitionAngleTextField.setEditable(false);

        batteryVoltageTextField.setEditable(false);

        airTempTextField.setEditable(false);

        coolantTempTextField.setEditable(false);

        jLabel5.setText("Fuel Open Time (ms)");
        jLabel5.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel5.setMinimumSize(new java.awt.Dimension(26, 20));

        jLabel6.setText("Ignition Angle (deg)");
        jLabel6.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel6.setMinimumSize(new java.awt.Dimension(26, 20));

        jLabel7.setText("Battery Voltage (V)");
        jLabel7.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel7.setMinimumSize(new java.awt.Dimension(26, 20));

        jLabel8.setText("Air Temp");
        jLabel8.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel8.setMinimumSize(new java.awt.Dimension(26, 20));

        jLabel9.setText("Coolant Temp");
        jLabel9.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel9.setMinimumSize(new java.awt.Dimension(26, 20));

        jLabel11.setText("Analog Input #3");
        jLabel11.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel11.setMinimumSize(new java.awt.Dimension(26, 20));

        jLabel12.setText("Analog Input #4");
        jLabel12.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel12.setMinimumSize(new java.awt.Dimension(26, 20));

        jLabel13.setText("Barometer");
        jLabel13.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel13.setMinimumSize(new java.awt.Dimension(26, 20));

        jLabel14.setText("MAP");
        jLabel14.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel14.setMinimumSize(new java.awt.Dimension(26, 20));

        jLabel15.setText("Lambda");
        jLabel15.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel15.setMinimumSize(new java.awt.Dimension(26, 20));

        jLabel3.setText("Analog Input #1");
        jLabel3.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel3.setMinimumSize(new java.awt.Dimension(26, 20));

        analogInput1TextField.setEditable(false);

        analogInput2TextField.setEditable(false);

        jLabel4.setText("Analog Input #2");
        jLabel4.setMaximumSize(new java.awt.Dimension(26, 20));
        jLabel4.setMinimumSize(new java.awt.Dimension(26, 20));

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
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(batteryVoltageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(coolantTempTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(airTempTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tpsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rpmTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fuelTimingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(analogInput4TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(BarometerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(analogInput2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(analogInput1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(analogInput3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lambdaTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mapTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ignitionAngleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(batteryVoltageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(airTempTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(coolantTempTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(analogInput4TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BarometerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(mapTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lambdaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(36, 36, 36)
                .addComponent(Start, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void StartActionPerformed(java.awt.event.ActionEvent evt) {                                      
        // TODO: add your handling code here:
        logStartTime = System.currentTimeMillis();
        parseThread = new Thread(pyParser);
        parseThread.start();
        
    }                                     

    private void rpmTextFieldActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO show RPM vs Time graph
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
        java.awt.EventQueue.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
    
    private void parseGroupOne(String line) {
        int rpm;
        double tps, fuelOpenTime, ignAngle;

        int rpm1, rpm2;
        rpm1 = Integer.parseUnsignedInt(line.substring(0, 2), 16);
        rpm2 = Integer.parseUnsignedInt(line.substring(2,4), 16) * 256;
        rpm = rpm1 + rpm2;
        logData.add(new SimpleLogObject("RPM", rpm));
        
        int tps1, tps2;
        tps1 = Integer.parseInt(line.substring(4, 6), 16);
        tps2 = Integer.parseInt(line.substring(6, 8), 16) * 256;
        tps = tps1 + tps2;
        tps *= .1;
        logData.add(new SimpleLogObject("TPS", tps)); // right way of doing it
        
        int fot1, fot2;
        fot1 = Integer.parseInt((line.substring(8,10)), 16);
        fot2 = Integer.parseInt((line.substring(10,12)), 16) * 256;
        fuelOpenTime = fot1 + fot2;
        fuelOpenTime *= .01;
        logData.add(new SimpleLogObject("FUELOPENTIME", fuelOpenTime));
        
        int ignAngle1, ignAngle2;
        ignAngle1 = Integer.parseInt((line.substring(12, 14)), 16);
        ignAngle2 = Integer.parseInt((line.substring(14, 16)), 16) * 256;
        ignAngle = ignAngle1 + ignAngle2;
        ignAngle *= .1;
        logData.add(new SimpleLogObject("IGNITIONANGLE", ignAngle));
        
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
        logData.add(new SimpleLogObject("BAR", barometer));
        
        double map1, map2;
        map1 = Integer.parseInt(line.substring(4,6), 16);
        map2 = Integer.parseInt(line.substring(6,8), 16) * 256;
        map = map1 + map2;
        map *= 0.01;
        logData.add(new SimpleLogObject("MAP", map));
        
        double lambda1, lambda2;
        lambda1 = Integer.parseInt(line.substring(8,10), 16);
        lambda2 = Integer.parseInt(line.substring(10,12), 16) * 256;
        lambda = lambda1 + lambda2;
        lambda *= 0.001;
        logData.add(new SimpleLogObject("LAMBDA", lambda));
        
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
        logData.add(new SimpleLogObject("INPUT1", input1));
        
        in1 = Integer.parseInt(line.substring(4,6), 16);
        in2 = Integer.parseInt(line.substring(6,8), 16) * 256;
        input2 = in1 + in2;
        input2 *= 0.001;
        logData.add(new SimpleLogObject("INPUT2", input2));
        
        in1 = Integer.parseInt(line.substring(8,10), 16);
        in2 = Integer.parseInt(line.substring(10,12), 16) * 256;
        input3 = in1 + in2;
        input3 *= 0.001;
        logData.add(new SimpleLogObject("INPUT3", input3));
        
        in1 = Integer.parseInt(line.substring(12,14), 16);
        in2 = Integer.parseInt(line.substring(14,16), 16) * 256;
        input4 = in1 + in2;
        input4 *= 0.001;
        logData.add(new SimpleLogObject("INPUT4", input4));
        
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
        logData.add(new SimpleLogObject("BATTERY", batteryVoltage));
        
        double airTemp1, airTemp2;
        airTemp1 = Integer.parseInt(line.substring(4,6), 16);
        airTemp2 = Integer.parseInt(line.substring(6,8), 16) * 256;
        airTemp = airTemp1 + airTemp2;
        airTemp *= 0.1;
        logData.add(new SimpleLogObject("AIR", airTemp));

        double coolantTemp1, coolantTemp2;
        coolantTemp1 = Integer.parseInt(line.substring(8,10), 16);
        coolantTemp2 = Integer.parseInt(line.substring(10,12), 16) * 256;
        coolantTemp = coolantTemp1 + coolantTemp2;
        coolantTemp *= 0.1;
        logData.add(new SimpleLogObject("COOLANT", coolantTemp));
        
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

    // Variables declaration - do not modify                     
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
    // End of variables declaration                   
}
