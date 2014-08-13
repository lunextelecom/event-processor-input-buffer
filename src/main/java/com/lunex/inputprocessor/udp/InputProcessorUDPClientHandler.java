package com.lunex.inputprocessor.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class InputProcessorUDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	@Override
	public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
			throws Exception {
		String response = msg.content().toString(CharsetUtil.UTF_8);
		/*if (response.startsWith("QOTM: ")) {
			System.out.println("Quote of the Moment: " + response.substring(6));
			ctx.close();
		}*/
		// TODO with response here
		System.out.println("response: " + response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
