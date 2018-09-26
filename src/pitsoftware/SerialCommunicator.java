/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 *
 * @author aribdhuka
 */
public class SerialCommunicator {
    
    static SerialPort serial;
    
    //array list to hold data that has not been parsed yet
    static ArrayList<String> data;
    
    boolean isRunning;
    
    public SerialCommunicator() throws SerialPortException {
        String[] portList = SerialPortList.getPortNames();
        
        for(String s : portList) {
            if(s.equals("COM2")) {
                serial = new SerialPort(s);
                isRunning = true;
            }
        }
        
        serial.closePort();
        
        serial.openPort();
        
        serial.setParams(SerialPort.BAUDRATE_256000, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        
        serial.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
        
        serial.addEventListener(new PortReader());
        
    }
    
    private static class PortReader implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0) {
                String receivedData;
                try {
                    receivedData = serial.readString(event.getEventValue());
                    data.add(receivedData);

                } catch (SerialPortException ex) {
                    Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    } 

    
}
