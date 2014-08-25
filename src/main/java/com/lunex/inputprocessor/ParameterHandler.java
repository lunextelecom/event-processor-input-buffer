package com.lunex.inputprocessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ParameterHandler {

    public static String HTTP_PORT = "";
    public static String HTTP_SSL_PORT = "";
    public static String UDP_PORT = "";

    public static void getPropertiesValues() throws IOException {

	Properties prop = new Properties();
	String propFileName = "config.properties";

	InputStream inputStream = new FileInputStream(propFileName);
	prop.load(inputStream);
	HTTP_PORT = prop.getProperty(Constants.HTTP_PORT_KEY);
	HTTP_SSL_PORT = prop.getProperty(Constants.HTTP_SSL_PORT_KEY);

	UDP_PORT = prop.getProperty(Constants.UDP_PORT_KEY);
    }

}
