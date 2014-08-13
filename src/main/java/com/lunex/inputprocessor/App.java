package com.lunex.inputprocessor;

import java.io.IOException;

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
        	
        	System.setProperty(Constants.HTTP_HOST_KEY, ParameterHandler.HTTP_HOST);
        	System.setProperty(Constants.HTTP_PORT_KEY, ParameterHandler.HTTP_HOST);
        	System.setProperty(Constants.HTTP_SSL_PORT_KEY, ParameterHandler.HTTP_SSL_PORT);
        	
        	System.setProperty(Constants.UDP_PORT_KEY, ParameterHandler.UDP_PORT);
        	
        	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
