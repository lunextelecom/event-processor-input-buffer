package com.lunex.inputprocessor.http;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;
import org.junit.Test;

import com.lunex.inputprocessor.CallbackHTTPVisitor;

import junit.framework.TestCase;

public class InputProcessorHttpSnoopServerTest extends TestCase {

	@Test
	public void testStartServer() {
		final CountDownLatch responseWaiter = new CountDownLatch(2);
		final InputProcessorHttpSnoopServer server = new InputProcessorHttpSnoopServer(8087, false);
		Thread serverT =  new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					responseWaiter.countDown();
					server.startServer();
				} catch (Exception e) {
					assertEquals(1, 2);
				}	
			}
		});
		serverT.start();
		
		InputProcessorHttpSnoopClient client = new InputProcessorHttpSnoopClient("http://localhost:8087", new CallbackHTTPVisitor(){
			@Override
			public void doJob(ChannelHandlerContext ctx, Object msg) {
				super.doJob(ctx, msg);
				responseWaiter.countDown();
			}
		});
		
		try {
			client.postRequestJsonContent(new JSONObject("{\"key\":\"value\"}"), false);
		} catch (Exception e1) {
			assertEquals(1, 2);
		}
		
		try {
			responseWaiter.await();
		} catch (InterruptedException e) {
			assertEquals(1, 2);
		}
		assertEquals(1, 1);
		server.stopServer();
	}

}
