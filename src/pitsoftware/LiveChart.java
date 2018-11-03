/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author aribdhuka
 */
public class LiveChart extends javax.swing.JFrame implements ChartMouseListener {

    // Chartpanel object exists so that it can be accessed in the chartmouselistener methods.
    ChartPanel chartPanel;

    // X and Y crosshairs
    Crosshair xCrosshair;
    Crosshair yCrosshair;

    // X and Y vals
    public double xCor = 0;
    public double yCor = 0;
    
    //List of value markers
    ArrayList<ValueMarker> staticValueMarkers;
    /**
     * Creates new form LiveChart
     */
    public LiveChart(final JFreeChart chart) {
        //create the GUI
        initComponents();
        
        //init the valuemarker list
        staticValueMarkers = new ArrayList<>();
        
        
        //close only this window when closed
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //if the chart provided isnt null
        if(chart != null) {
            initChart(chart);
        }
        
        // Create the global object crosshairs
        this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.xCrosshair.setLabelVisible(true);
        this.yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.yCrosshair.setLabelVisible(true);
    }
    
    //update the chart shown when given a new chart
    public void updateChart(JFreeChart chart) {
        initChart(chart);
    }
    
    //create the first instance of a chart
    private void initChart(JFreeChart chart) {
        //save the chartPanel reference to be used locally
        chartPanel = new ChartPanel(chart);
        //make sure the size is correct
        chartPanel.setPreferredSize(new java.awt.Dimension(400, 300));
        
        //add the mouse listener
        chartPanel.addChartMouseListener(this);
        
        //add the panels contents to the frame
        chartFrame.setContentPane(chartPanel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chartFrame = new javax.swing.JInternalFrame();
        xCordLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        yCordLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        staticMarkersLabel = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(400, 500));
        setMinimumSize(new java.awt.Dimension(400, 375));
        setPreferredSize(new java.awt.Dimension(400, 375));
        setSize(new java.awt.Dimension(400, 375));

        chartFrame.setMaximumSize(new java.awt.Dimension(400, 300));
        chartFrame.setMinimumSize(new java.awt.Dimension(400, 300));
        chartFrame.setPreferredSize(new java.awt.Dimension(400, 300));
        chartFrame.setSize(new java.awt.Dimension(400, 300));
        chartFrame.setVisible(true);

        javax.swing.GroupLayout chartFrameLayout = new javax.swing.GroupLayout(chartFrame.getContentPane());
        chartFrame.getContentPane().setLayout(chartFrameLayout);
        chartFrameLayout.setHorizontalGroup(
            chartFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        chartFrameLayout.setVerticalGroup(
            chartFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 277, Short.MAX_VALUE)
        );

        xCordLabel.setText("jLabel2");

        jLabel2.setText("Y Cord:");

        yCordLabel.setText("jLabel2");

        jLabel1.setText("X Cord:");

        jLabel3.setText("Static Markers:");

        staticMarkersLabel.setEditable(false);
        staticMarkersLabel.setColumns(20);
        staticMarkersLabel.setRows(5);
        jScrollPane1.setViewportView(staticMarkersLabel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chartFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(xCordLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(yCordLabel)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(chartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(xCordLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(yCordLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(LiveChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LiveChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LiveChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LiveChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LiveChart(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JInternalFrame chartFrame;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea staticMarkersLabel;
    private javax.swing.JLabel xCordLabel;
    private javax.swing.JLabel yCordLabel;
    // End of variables declaration//GEN-END:variables

    //on mouse click
    @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
        //create a value marker at the current position
        ValueMarker v = new ValueMarker(xCor);
        //mark the statics with blue paint
        v.setPaint(Color.BLUE);
        //add the value marker to the list of static value markers
        staticValueMarkers.add(v);
        //set the window to the maximum size
        this.setSize(this.getMaximumSize());
    }

    //on mouse hover over the chart
    @Override
    public void chartMouseMoved(ChartMouseEvent cme) {
        // The data area of where the chart is.
        Rectangle2D dataArea = this.chartPanel.getScreenDataArea();
        // Get the chart from the chart mouse event
        JFreeChart chart = cme.getChart();
        // Get the xy plot object from the chart
        XYPlot plot = (XYPlot) chart.getPlot();
        // Clear all markers
        // This will be a problem for static markers we want to create
        plot.clearDomainMarkers();
        // Get the xAxis
        ValueAxis xAxis = plot.getDomainAxis();
        // Get the xCordinate from the xPositon of the mouse
        xCor = xAxis.java2DToValue(cme.getTrigger().getX(), dataArea,
                RectangleEdge.BOTTOM);
        // Find the y cordinate from the plots data set given a x cordinate
        yCor = DatasetUtilities.findYValue(plot.getDataset(), 0, xCor);
        // Create a marker at the x Coordinate with black paint
        ValueMarker marker = new ValueMarker(xCor);
        marker.setPaint(Color.BLACK);
        // Add a marker on the x axis given a marker. This essentially makes the marker verticle
        plot.addDomainMarker(marker);
        //Holds text value for all static markers
        String staticMarkerText = "";
        // All the statics that need to be shows should be added to plot
        for(ValueMarker v : staticValueMarkers) {
            plot.addDomainMarker(v);
        }
        
        // String object that holds values for all the series on the plot.
        String yCordss = "";
        // Repeat the loop for each series in the plot
        for (int i = 0; i < plot.getDataset().getSeriesCount(); i++) {
            // Get the collection from the plots data set
            XYSeriesCollection col = (XYSeriesCollection) plot.getDataset();
            // Get the plots name from the series's object
            String plotName = plot.getDataset().getSeriesKey(i).toString();
            // Create a new collection 
            XYSeriesCollection col2 = new XYSeriesCollection();
            // Add the series with the name we found to the other collection
            // We do this because the findYValue() method takes a collection
            col2.addSeries(col.getSeries(plotName));
            // Get the y value for the current series.
            double val = DatasetUtilities.findYValue(col2, 0, xCor);
            //for each static value marker
            for(ValueMarker v : staticValueMarkers) {
                //get the x value of the marker
                double staticX = v.getValue();
                //find its correspoinding y value for the current line
                double staticY = DatasetUtilities.findYValue(col2, 0, staticX);
                //format the x and y
                String staticXStr = String.format("%.2f", staticX);
                String staticYStr = String.format("%.2f", staticY);
                //update the string that holds the data that will update the label
                staticMarkerText += " " + staticXStr + " " + plotName + ": " + staticYStr + "\n";
            }
            // Add the value to the string
            yCordss += val + "\n";
        }
        
        //update the label for static markers
        staticMarkersLabel.setText(staticMarkerText);

        // Set the textviews at the bottom of the file.
        xCordLabel.setText(xCor + "");
        yCordLabel.setText(yCordss);

        // Set this objects crosshair data to the value we have
        this.xCrosshair.setValue(xCor);
        this.yCrosshair.setValue(yCor);
    }
}