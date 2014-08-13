package com.lunex.inputprocessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ParameterHandler {

	public static String HTTP_HOST = "";
	public static String HTTP_PORT = "";
	public static String HTTP_SSL_PORT = "";
	public static String UDP_PORT = "";
	public static String UDP_TIME_OUT = "";
	
	public static void getPropertiesValues() throws IOException {
		 
        Properties prop = new Properties();
        String propFileName = "config.properties";
 
        InputStream inputStream = ParameterHandler.class.getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);
        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
 
        HTTP_HOST = prop.getProperty(Constants.HTTP_HOST_KEY);
        HTTP_PORT = prop.getProperty(Constants.HTTP_PORT_KEY);
        HTTP_SSL_PORT = prop.getProperty(Constants.HTTP_SSL_PORT_KEY);
        
        UDP_PORT = prop.getProperty(Constants.UDP_PORT_KEY);
        UDP_TIME_OUT = prop.getProperty("UDP.TIME.OUT");       
    }
	
}
