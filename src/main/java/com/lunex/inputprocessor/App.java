package com.lunex.inputprocessor;

import com.lunex.inputprocessor.http.InputProcessorHttpSnoopServer;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		Thread threadHttpServer = new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				InputProcessorHttpSnoopServer server = new InputProcessorHttpSnoopServer(
						8087, false);
				try {
					server.startServer();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		threadHttpServer.start();
		
		Thread threadUDPServer = new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				InputProcessorHttpSnoopServer server = new InputProcessorHttpSnoopServer(
						8086, false);
				try {
					server.startServer();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		threadUDPServer.start();

		try {
			// ParameterHandler.getPropertiesValues();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
