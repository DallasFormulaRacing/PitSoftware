/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 *
 * @author aribdhuka
 */
public class FileReader {
    
    public static void read(BufferedInputStream input) throws IOException, InterruptedException {
        int avail = input.available();
        if(avail > 0) {
            String line = "";
            int len = 0;
            while(len < avail) {
                line += (char) input.read();
                len++;
            }
            System.out.println(line);
        } else {
            Thread.sleep(500);
        }
    }
    
}
