/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

/**
 *
 * @author aribdhuka
 */

import java.io.*;
import java.util.*;
import javax.comm.*;

public class SerialComm implements Runnable, SerialPortEventListener {
    static CommPortIdentifier portId;
    static Enumeration portList;
    static CommPortIdentifier workingIdentifier;

    InputStream inputStream;
    SerialPort serialPort;
    Thread readThread;
    
    boolean initpass;
    
    //array list to hold data that has not been parsed yet
    ArrayList<String> data;

    public SerialComm() {
        initpass = false;
        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                 if (portId.getName().equals("COM1")) {
			// if (portId.getName().equals("/dev/term/a")) {
                    workingIdentifier = portId;
                }
            }
        }
        data = new ArrayList<>();
        if(workingIdentifier != null) {
            try {
                //maybe this just crashes because I don't have a serial port?
                serialPort = (SerialPort) workingIdentifier.open("PitSoftware", 2000);
            } catch (PortInUseException e) {System.out.println(e);}
            try {
                inputStream = serialPort.getInputStream();
            } catch (IOException e) {System.out.println(e);}
            try {
                serialPort.addEventListener(this);
            } catch (TooManyListenersException e) {System.out.println(e);}
            serialPort.notifyOnDataAvailable(true);
            try {
                serialPort.setSerialPortParams(9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            } catch (UnsupportedCommOperationException e) {System.out.println(e);}
            readThread = new Thread(this);
            readThread.start();
            initpass = true;
        } else {
            System.out.println("Correct port not found.");
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {System.out.println(e);}
    }
    
    public boolean isRunning() {
        return readThread.isAlive();
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        switch(event.getEventType()) {
        case SerialPortEvent.BI:
        case SerialPortEvent.OE:
        case SerialPortEvent.FE:
        case SerialPortEvent.PE:
        case SerialPortEvent.CD:
        case SerialPortEvent.CTS:
        case SerialPortEvent.DSR:
        case SerialPortEvent.RI:
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            break;
        case SerialPortEvent.DATA_AVAILABLE:
            byte[] readBuffer = new byte[20];

            try {
                while (inputStream.available() > 0) {
                    int numBytes = inputStream.read(readBuffer);
                }
                String readVal = new String(readBuffer);
                data.add(readVal);
                System.out.print(readVal);
            } catch (IOException e) {System.out.println(e);}
            break;
        }
    }
}