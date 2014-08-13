package com.lunex.inputprocessor;

import java.io.IOException;

import com.lunex.inputprocessor.udp.InputProcessorUDPClient;
import com.lunex.inputprocessor.udp.InputProcessorUDPServer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        try {
        	ParameterHandler.getPropertiesValues();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
