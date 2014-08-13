package com.lunex.inputprocessor.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class InputProcessorUDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet)
			throws Exception {
		System.out.println(packet);
		String packageContent = packet.content().toString(CharsetUtil.UTF_8);
		// TODO with package
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
}
