package com.lunex.inputprocessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.inputprocessor.http.InputProcessorHttpSnoopServer;

/**
 * Hello world!
 *
 */
public class App {
	
	static final Logger logger = LoggerFactory.getLogger(App.class);
	
	public static void main(String[] args) {
		
		// load log properties
		Properties props = new Properties();
		try {
			props.load(new FileInputStream("log4j.properties"));
			PropertyConfigurator.configure(props);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		// load param from properties file
		try {
			ParameterHandler.getPropertiesValues();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.error(e.getMessage());
			return;
		}
		
		Thread threadHttpServer = new Thread(new Runnable() {
			public void run() {
				InputProcessorHttpSnoopServer server = new InputProcessorHttpSnoopServer(
						Integer.parseInt(ParameterHandler.HTTP_PORT), false);
				try {
					server.startServer();
					logger.info("Started HTTP server");
				} catch (Exception e1) {
					logger.error(e1.getMessage());
				}
			}
		});
		threadHttpServer.start();
		
		Thread threadUDPServer = new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				InputProcessorHttpSnoopServer server = new InputProcessorHttpSnoopServer(
						Integer.parseInt(ParameterHandler.UDP_PORT), false);
				try {
					server.startServer();
					logger.info("Started UDP server");
				} catch (Exception e1) {
					logger.error(e1.getMessage());
				}
			}
		});
		threadUDPServer.start();

	}
}
