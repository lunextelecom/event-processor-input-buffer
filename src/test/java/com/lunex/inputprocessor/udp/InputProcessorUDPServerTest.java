package com.lunex.inputprocessor.udp;

import static org.junit.Assert.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.util.concurrent.CountDownLatch;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.lunex.inputprocessor.CallbackUDPVisitor;

public class InputProcessorUDPServerTest {

	@Test
	public void testStartServer() {
		final CountDownLatch responseWaiter = new CountDownLatch(2);
		final InputProcessorUDPServer server = new InputProcessorUDPServer(8086);
		Thread serverT = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					responseWaiter.countDown();
					server.startServer();
				} catch (Exception e) {
					assertEquals(1, 2);
				}
			}
		});
		serverT.start();

		InputProcessorUDPClient client = new InputProcessorUDPClient(8086);
		try {
			client.submitJsonObject(new JSONObject("{\"async\":true}"), 60000, new CallbackUDPVisitor() {
						@Override
						public void doJob(ChannelHandlerContext ctx,
								DatagramPacket msg) {
							responseWaiter.countDown();
						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			responseWaiter.await();
			server.stopServer();
		} catch (InterruptedException e) {
			assertEquals(1, 2);
		}
		assertEquals(1, 1);
	}

}
