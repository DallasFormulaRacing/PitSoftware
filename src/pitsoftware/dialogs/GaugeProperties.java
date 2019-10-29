/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware.dialogs;

import eu.hansolo.steelseries.gauges.AbstractGauge;
import eu.hansolo.steelseries.gauges.Linear;
import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.Model;
import java.awt.Color;
import pitsoftware.CustomizableWindow;
import java.awt.Dimension;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JPanel;
import pitsoftware.MessageBox;
import pitsoftware.ScaledLinear;
import pitsoftware.ScaledRadial;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JFrame;

/**
 *
 * @author aribdhuka
 */
public class GaugeProperties extends javax.swing.JDialog {

    /**
     * Creates new form GaugeProperties
     */
    AbstractGauge gauge;
    String[] TAG;
    public GaugeProperties(java.awt.Frame parent, boolean modal, AbstractGauge gauge, String[] tag) {
        super(parent, modal);
        TAG = tag;
        initComponents();
        this.gauge = gauge;
        String[] tags = new String[] {"Time,BrakePressureFront",
                                        "Time,BrakePressureRear",
                                        "Time,RPM",
                                        "Time,FuelOpenTime",
                                        "Time,IgnitionAngle",
                                        "Time,Barometer",
                                        "Time,Lambda",
                                        "Time,Analog1",
                                        "Time,Analog2",
                                        "Time,Analog3",
                                        "Time,Analog4",
                                        "Time,Voltage",
                                        "Time,SteeringAngle",
                                        "Time,RadiatorInlet",
                                        "Time,OilPressure",
                                        "Time,TransmissionSpeed",
                                        "Time,AirTemp",
                                        "Time,Coolant",
                                        "Time,MAP",
                                        "Time,TPS",
                                        "Time,CrankPosition",
                                        "Time,CamPosition",
                                        "Time,GPSLat",
                                        "Time,GPSLong",
                                        "Time,AccelX",
                                        "Time,AccelY",
                                        "Time,AccelZ",
                                        "Time,WheelspeedFL",
                                        "Time,WheelspeedFR",
                                        "Time,WheelspeedRL",
                                        "Time,WheelspeedRR",
                                        "Time,DamperFL",
                                        "Time,DamperFR",
                                        "Time,DamperRL",
                                        "Time,DamperRR"};
        tagList.setListData(tags);
        tagList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    TAG[0] = tagList.getSelectedValue();
                }
            }
        });
        
        //Set fields in the window to the values that are stored in the gauge
        titleField.setText(gauge.getTitle());
        unitField.setText(gauge.getUnitString());
        heightField.setText("" + (int)gauge.getSize().getHeight());
        widthField.setText("" + (int)gauge.getSize().getWidth());
        minField.setText("" + gauge.getMinValue());
        maxField.setText("" + gauge.getMaxValue());
        warningField.setText(""+ gauge.getThreshold());
        invertThresholdCheckBox.setSelected(gauge.isThresholdBehaviourInverted());
        if(gauge.isTrackVisible()) {
            redlineMinField.setText("" + gauge.getTrackStart());
            redlineMaxField.setText("" + gauge.getTrackStop());
        }

        tagList.setSelectedValue(tag, modal);
        
        if(gauge instanceof ScaledRadial)
        {
            widthField.setVisible(false);
            jLabel11.setVisible(false);
            scaleField.setText(""+((ScaledRadial) gauge).getScale());
            if( !((ScaledRadial) gauge).getTag().equals(""))
                tagList.setSelectedValue(((ScaledRadial) gauge).getTag(), true);
        }
        else if (gauge instanceof ScaledLinear)
        {
            scaleField.setText(""+((ScaledLinear) gauge).getScale());
            if( !((ScaledLinear) gauge).getTag().equals("") )
                tagList.setSelectedValue(((ScaledLinear) gauge).getTag(), true);
            if(gauge.getWidth() < gauge.getHeight())
                widthField.setText("" + (int)gauge.getSize().getWidth());
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
        titleField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        unitField = new javax.swing.JTextField();
        heightField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        scaleField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        minField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        maxField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        warningField = new javax.swing.JTextField();
        invertThresholdCheckBox = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        redlineMinField = new javax.swing.JTextField();
        redlineMaxField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        createButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tagList = new javax.swing.JList<>();
        jLabel6 = new javax.swing.JLabel();
        deleteButton = new javax.swing.JButton();
        widthField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Title: ");

        titleField.setToolTipText("");

        jLabel2.setText("Unit:");

        unitField.setToolTipText("");

        heightField.setText("200");
        heightField.setToolTipText("");
        heightField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                heightFieldFocusLost(evt);
            }
        });

        jLabel3.setText("Height:  ");

        scaleField.setText("1");
        scaleField.setToolTipText("");
        scaleField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                scaleFieldFocusLost(evt);
            }
        });

        jLabel4.setText("Scale:  ");

        minField.setText("0");
        minField.setToolTipText("");
        minField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                minFieldFocusLost(evt);
            }
        });

        jLabel5.setText("Min:");

        maxField.setText("100");
        maxField.setToolTipText("");
        maxField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                maxFieldFocusLost(evt);
            }
        });

        jLabel7.setText("Warning:");

        warningField.setText("90");
        warningField.setToolTipText("");
        warningField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                warningFieldFocusLost(evt);
            }
        });

        invertThresholdCheckBox.setText("Invert?");

        jLabel8.setText("Redline:");

        redlineMinField.setToolTipText("");
        redlineMinField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                redlineMinFieldFocusLost(evt);
            }
        });

        redlineMaxField.setToolTipText("");
        redlineMaxField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                redlineMaxFieldFocusLost(evt);
            }
        });

        jLabel9.setText("<");

        jLabel10.setText("<");

        createButton.setBackground(new java.awt.Color(0, 122, 255));
        createButton.setText("Create");
        createButton.setToolTipText("");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        tagList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(tagList);

        jLabel6.setText("TAG");

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        widthField.setText("200");
        widthField.setToolTipText("");
        widthField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                widthFieldFocusLost(evt);
            }
        });

        jLabel11.setText("Width:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(182, 182, 182)
                                .addComponent(redlineMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(177, 177, 177)
                                        .addComponent(invertThresholdCheckBox))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5))
                                        .addGap(23, 23, 23)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(unitField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(heightField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(1, 1, 1))
                                            .addComponent(scaleField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addGap(1, 1, 1)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(warningField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(minField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(redlineMinField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(11, 11, 11)
                                                .addComponent(jLabel11)
                                                .addGap(1, 1, 1)
                                                .addComponent(widthField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(maxField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(createButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(155, 155, 155))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(unitField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(heightField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(widthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(scaleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(minField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(maxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(warningField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(invertThresholdCheckBox))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(redlineMinField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel10)
                            .addComponent(redlineMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(createButton)
                            .addComponent(cancelButton)
                            .addComponent(deleteButton))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void heightFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_heightFieldFocusLost
        if(heightField.getText().isEmpty())
            new MessageBox("Enter a size.\nDefault size is 200.\nRadialGauge at Bottom is 100 for reference.").setVisible(true);

        else {
            try {
                int attemptParse;
                attemptParse = Integer.parseInt(heightField.getText());
                if(attemptParse == 0)
                    //new MessageBox("Nice try.\nPlease give it a size.").setVisible(true);
                    new MessageBox(this, "Nice try.\\nPlease give it a size.", true);
            } catch(NumberFormatException e) {
                new MessageBox("Does that look like a fucking number to you?\nEnter a real number.").setVisible(true);
                heightField.requestFocus();

            }
        }
    }//GEN-LAST:event_heightFieldFocusLost

    private void scaleFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scaleFieldFocusLost
        if(scaleField.getText().isEmpty())
            //new MessageBox("Enter a scale.\nA scale is like an rpm gauge showing 4 while the real value is 4000.\nThe scale is 1000x in this case.").setVisible(true);
            new MessageBox(this, "Enter a scale.\\nA scale is like an rpm gauge showing 4 while the real value is 4000.\\nThe scale is 1000x in this case.", true);

        else {
            try {
                double attemptParse;
                attemptParse = Double.parseDouble(scaleField.getText());
                if(attemptParse == 0)
                    //new MessageBox("Nice try.\nPlease give it a size.").setVisible(true);
                    new MessageBox(this, "Nice try.\\nPlease give it a size.", true);
            } catch(NumberFormatException e) {
                //new MessageBox("Does that look like a fucking number to you?\nEnter a real number.").setVisible(true);
                new MessageBox(this, "Does that look like a fucking number to you?\\nEnter a real number.", true);
                scaleField.requestFocus();
            }
        }
    }//GEN-LAST:event_scaleFieldFocusLost

    private void minFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minFieldFocusLost
        if(minField.getText().isEmpty())
            //new MessageBox("Enter a min number. Leave 0 for default.").setVisible(true);
            new MessageBox(new javax.swing.JFrame(), "Does that look like a fucking number to you?\nEnter a real number.", true);

        else {
            try {
                double attemptParse;
                attemptParse = Double.parseDouble(minField.getText());
            } catch(NumberFormatException e) {
                //new MessageBox("Does that look like a fucking number to you?\nEnter a real number.").setVisible(true);
                new MessageBox(new javax.swing.JFrame(), "Enter a min number. Leave 0 for default.", true);
                minField.requestFocus();
            }
        }
    }//GEN-LAST:event_minFieldFocusLost

    private void maxFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxFieldFocusLost
        if(maxField.getText().isEmpty())
            //new MessageBox("Enter a min number. Leave 100 for default autoscale.").setVisible(true);
            new MessageBox(new javax.swing.JFrame(), "Enter a min number. Leave 0 for default.", true);
        else {
            try {
                double attemptParse;
                attemptParse = Double.parseDouble(maxField.getText());
            } catch(NumberFormatException e) {
                //new MessageBox("Does that look like a fucking number to you?\nEnter a real number.").setVisible(true);
            new MessageBox(new javax.swing.JFrame(), "Does that look like a fucking number to you?\nEnter a real number.", true);                
                maxField.requestFocus();
            }
        }
    }//GEN-LAST:event_maxFieldFocusLost

    private void warningFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_warningFieldFocusLost
        if(!warningField.getText().isEmpty()) {
            try {
                double attemptParse;
                attemptParse = Double.parseDouble(maxField.getText());
            } catch(NumberFormatException e) {
                //new MessageBox("Does that look like a fucking number to you?\nEnter a real number.").setVisible(true);
                new MessageBox(new javax.swing.JFrame(), "Does that look like a fucking number to you?\nEnter a real number.", true);                

                maxField.requestFocus();
            }
        }
    }//GEN-LAST:event_warningFieldFocusLost

    private void redlineMinFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_redlineMinFieldFocusLost
        if(!redlineMinField.getText().isEmpty()) {
            try {
                double attemptParse;
                attemptParse = Double.parseDouble(redlineMinField.getText());
            } catch(NumberFormatException e) {
                //new MessageBox("Does that look like a fucking number to you?\nEnter a real number.").setVisible(true);
                new MessageBox(new javax.swing.JFrame(), "Does that look like a fucking number to you?\nEnter a real number.", true);                
                redlineMinField.requestFocus();
            }
        }
    }//GEN-LAST:event_redlineMinFieldFocusLost

    private void redlineMaxFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_redlineMaxFieldFocusLost
        if(!redlineMaxField.getText().isEmpty()) {
            try {
                double attemptParse;
                attemptParse = Double.parseDouble(redlineMaxField.getText());
            } catch(NumberFormatException e) {
                //new MessageBox("Does that look like a fucking number to you?\nEnter a real number.").setVisible(true);
                new MessageBox(new javax.swing.JFrame(), "Does that look like a fucking number to you?\nEnter a real number.", true);                
                redlineMaxField.requestFocus();
            }
        }
    }//GEN-LAST:event_redlineMaxFieldFocusLost

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        //all number checking should be done. check for min < max.
        //get attributes
        String title = titleField.getText();
        String units = unitField.getText();
        Dimension size = new Dimension(Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()));
        double scale = Double.parseDouble(scaleField.getText());
        double min;
        if(minField.getText().isEmpty())
            min = 0;
        else
            min = Double.parseDouble(minField.getText());
        double max;
        if(maxField.getText().isEmpty())
            max = 0;
        else
            max = Double.parseDouble(maxField.getText());
        double warning = 0;
        if(!warningField.getText().isEmpty())
            warning = Double.parseDouble(warningField.getText());
        boolean invert = invertThresholdCheckBox.isSelected();
        double redmin;
        if(redlineMinField.getText().isEmpty())
            redmin = 0;
        else
            redmin = Double.parseDouble(redlineMinField.getText());
        double redmax;
        if(redlineMaxField.getText().isEmpty())
            redmax = 0;
        else
            redmax = Double.parseDouble(redlineMaxField.getText());
        
        //check for errors
        if(min > max) {
            if(max != 100) {
                //new MessageBox("The stop can't happen after the start.\nAre you even thinking dumbass?\nRe-enter proper values.").setVisible(true);
                new MessageBox(new javax.swing.JFrame(), "The stop can't happen after the start.\nAre you even thinking dumbass?\nRe-enter proper values.", true);                

                minField.setText("");
                maxField.setText("");
                return;
            }
        }
        if(redmin != 0 || redmax != 0) {
            if(redmin > redmax) {
                //new MessageBox("The stop can't happen after the start.\nAre you even thinking dumbass?\nRe-enter proper values.").setVisible(true);
                new MessageBox(new javax.swing.JFrame(), "The stop can't happen after the start.\nAre you even thinking dumbass?\nRe-enter proper values.", true);
                redlineMinField.setText("");
                redlineMaxField.setText("");
                return;
            }
            if(redmin < min) {
                //new MessageBox("The redline cannot start before the minimum value of the gauge.").setVisible(true);
                new MessageBox(new javax.swing.JFrame(), "The redline cannot start before the minimum value of the gauge.", true);
                return;
            }
            if(redmax > max) {
                //new MessageBox("The redline cannot end after the maximum value of the gauge.").setVisible(true);
                new MessageBox(new javax.swing.JFrame(), "The redline cannot start before the minimum value of the gauge.", true);
                return;
            }
        }
        if(TAG[0].isEmpty()) {
            //new MessageBox("Please select a tag.").setVisible(true);
            new MessageBox(new javax.swing.JFrame(), "Please select a tag.", true);
            return;
        }
        
        //set attributes
        gauge.setTitle(title);
        gauge.setUnitString(units);
        if(gauge instanceof ScaledRadial) {
            gauge.setSize(size.height,size.height);
            gauge.getParent().setSize(gauge.getSize());
            ((ScaledRadial) gauge).setScale(scale);
            ((ScaledRadial) gauge).setTag(TAG[0]);
        } else if (gauge instanceof ScaledLinear) {
            ((ScaledLinear) gauge).setScale(scale);
            ((ScaledLinear) gauge).setTag(TAG[0]);
            
            gauge.setSize(size.width, size.height);
            gauge.getParent().setPreferredSize(gauge.getSize());
            gauge.getParent().setSize(gauge.getSize());
        } else {
                gauge.setSize(100, size.height);
                gauge.getParent().setPreferredSize(gauge.getSize());
                gauge.getParent().setSize(gauge.getSize());

            
        }
        gauge.setMinValue(min);
        if(max != 100)
            gauge.setMaxValue(max);
        if(warning != 0)
            gauge.setThreshold(warning);
        gauge.setThresholdBehaviourInverted(invert);
        if(!(redmin == 0 && redmax == 0)) {
            gauge.setTrackStart(redmin);
            gauge.setTrackStop(redmax);
            gauge.setTrackStartColor(Color.RED);
            gauge.setTrackStopColor(Color.RED);
            gauge.setTrackVisible(true);
        }
        
        this.dispose();
    }//GEN-LAST:event_createButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
        CustomizableWindow frame = ((CustomizableWindow)this.getParent());
        Container contentPane = ((JFrame)this.getParent()).getContentPane();
        Component[] components = contentPane.getComponents();
        for(int i = 1; i < components.length; i++)
        {
            if(((JPanel)components[i]).getComponents()[0].equals(gauge))
            {
                contentPane.remove(components[i]);
                contentPane.repaint();
            }
        }
        frame.setCancel(true);
        gauge.dispose();
        this.dispose();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        CustomizableWindow frame = ((CustomizableWindow)this.getParent());
        frame.setCancel(true);
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void widthFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_widthFieldFocusLost
         if(widthField.getText().isEmpty())
            new MessageBox("Enter a size.\nDefault size is 200.\nRadialGauge at Bottom is 100 for reference.").setVisible(true);
        else {
            try {
                int attemptParse;
                attemptParse = Integer.parseInt(widthField.getText());
                if(attemptParse == 0)
                    new MessageBox("Nice try.\nPlease give it a size.").setVisible(true);
            } catch(NumberFormatException e) {
                new MessageBox("Does that look like a fucking number to you?\nEnter a real number.").setVisible(true);
                widthField.requestFocus();
            }
        }
    }//GEN-LAST:event_widthFieldFocusLost

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
            java.util.logging.Logger.getLogger(GaugeProperties.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GaugeProperties.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GaugeProperties.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GaugeProperties.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GaugeProperties dialog = new GaugeProperties(new javax.swing.JFrame(), true, new ScaledRadial(), new String[] {""});
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField heightField;
    private javax.swing.JCheckBox invertThresholdCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField maxField;
    private javax.swing.JTextField minField;
    private javax.swing.JTextField redlineMaxField;
    private javax.swing.JTextField redlineMinField;
    private javax.swing.JTextField scaleField;
    private javax.swing.JList<String> tagList;
    private javax.swing.JTextField titleField;
    private javax.swing.JTextField unitField;
    private javax.swing.JTextField warningField;
    private javax.swing.JTextField widthField;
    // End of variables declaration//GEN-END:variables
}
