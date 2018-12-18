/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pitsoftware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Arib
 */
public class PythonComm {
    
    public static BufferedReader run() throws IOException {
        
        System.out.println("start");
        
        String filepath = MainWindow.pyFilepath;
        
        if(!filepath.isEmpty()) {
        
            Process process = Runtime.getRuntime().exec("python " + filepath);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            while((line = errors.readLine()) != null) {
                System.out.println("Error: " + line);
            }

            return reader;
        }
        
        return null;
    }
}
