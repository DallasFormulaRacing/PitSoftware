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
        
        data = new ArrayList<>();
        String[] portList = SerialPortList.getPortNames();
        
        for(String s : portList) {
            if(s.equals("COM5")) {
                serial = new SerialPort(s);
                System.out.println("started");

                isRunning = true;
                break;
            }
        }
        
        
        if(serial.isOpened())
            serial.closePort();
        
        serial.openPort();
        
        serial.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, 1, SerialPort.PARITY_NONE);
        
        serial.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
        
        serial.addEventListener(new PortReader());
        
        
    }
    
    private static class PortReader implements SerialPortEventListener {
        
        @Override
        public void serialEvent(SerialPortEvent event) {
            String receivedData;
            String corrected = "";
            System.out.println("proc");
            try {
                receivedData = serial.readHexString(10);
                
                for(int i = 0; i < receivedData.length(); i++) {
                    if(receivedData.charAt(i) != ' ') {
                        corrected += receivedData.charAt(i); 
                    }
                }
//                receivedData = serial.readString(event.getEventValue());
                data.add(corrected);
                System.out.println("raw Data: " + corrected);
            } catch(SerialPortException ex) {
                System.out.println(ex);

            }

//            if(event.isRXCHAR() && event.getEventValue() > 0) {
//                try {
//                    receivedData = serial.readString(event.getEventValue());
//                    data.add(receivedData);
//                    System.out.println("Raw Data: " + receivedData);
//
//                } catch (SerialPortException ex) {
//                    System.out.println(ex);
//                }
//            }
        }

    } 

    
}
