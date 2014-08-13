package com.lunex.inputprocessor.udp;

import com.lunex.inputprocessor.Visitor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class InputProcessorUDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	
	private Visitor visitor;
	
	public InputProcessorUDPClientHandler(Visitor visitor) {
		this.visitor = visitor;
	}
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		//String response = msg.content().toString(CharsetUtil.UTF_8);
		// TODO with response here
		if (this.visitor != null) {
			this.visitor.doJob(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
