/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.*;
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
    static CommPort comm;
    //array list to hold data that has not been parsed yet
    static ArrayList<String> data;
    String hexString;
    
    boolean isRunning;
    
    
    public SerialCommunicator(String portName) throws SerialPortException, PortInUseException {
        
        data = new ArrayList<>();
        String[] portList = SerialPortList.getPortNames();
        
//        Enumeration portIdents = CommPortIdentifier.getPortIdentifiers();
//        while(portIdents.hasMoreElements()) {
//            CommPortIdentifier portID = (CommPortIdentifier) portIdents.nextElement();
//            if(portID.getName().equals(portName)) {
//                comm = portID.open("XbeePort", 20);
//            }
//
//        }
        
        for(String s : portList) {
            if(s.equals(portName)) {
                serial = new SerialPort(s);
                System.out.println("started");

                isRunning = true;
                break;
            }
        }
        
        
        if(serial.isOpened())
            serial.closePort();
        
        serial.openPort();
        
        serial.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, 0, SerialPort.PARITY_NONE);
        
        serial.addEventListener(new PortReader());        
        
    }
    
    private static class PortReader implements SerialPortEventListener {
        
        @Override
        public void serialEvent(SerialPortEvent event) {
            String receivedData;
            String corrected = "";
            System.out.println("proc");
            try {
//                receivedData = serial.readHexString(20);
                receivedData = "";
                corrected = serial.readString();
                
                String[] arr;
                if(!corrected.isEmpty()){
                    arr = corrected.split("\n");
                    for(String s : arr) {
                        if(s.length() == 20) {
                            data.add(s);
                            System.out.println(s);
                        }
                    }
                }
                corrected = "";
//                receivedData = serial.readString(event.getEventValue());
//                data.add(corrected);
            } catch(SerialPortException ex) {
                System.out.println(ex);

            }
        }
        
//        @Override
//        public void serialEvent(SerialPortEvent event) {
//            if(event.getEventType() == SerialPortEvent.)
//        }

    } 

    
}
