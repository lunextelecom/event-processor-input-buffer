package com.lunex.inputprocessor.udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class InputProcessorUDPServerHandler extends
		SimpleChannelInboundHandler<DatagramPacket> {

	@Override
	public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
		System.out.println(packet);
		
		String packageContent = packet.content().toString(CharsetUtil.UTF_8);
		String responseContent = "Get package and Process async";
		
		if (packageContent.startsWith("async")) {		
			Thread thread = new Thread(new PackageProcessorThread(packet));
			thread.start();
			
			// TODO with packageContent to get responseContent	
			responseContent = "Get package and Processing async";
			
		} else {
			// TODO
			responseContent = "result";
			
		}
		// Sent response to client sender
		ctx.write(new DatagramPacket(Unpooled.copiedBuffer(responseContent, CharsetUtil.UTF_8), packet.sender()));
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		// We don't close the channel because we can keep serving requests.
	}

	public class PackageProcessorThread implements Runnable {

		DatagramPacket packet;

		public PackageProcessorThread(DatagramPacket packet) {
			this.packet = packet;
		}

		public void run() {
			// TODO with Event handler
		}		
	}
}
