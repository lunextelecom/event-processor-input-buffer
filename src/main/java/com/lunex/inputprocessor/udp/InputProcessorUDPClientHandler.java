package com.lunex.inputprocessor.udp;

import com.lunex.inputprocessor.CallbackUDPVisitor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class InputProcessorUDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	
	private CallbackUDPVisitor callback;
	
	public InputProcessorUDPClientHandler(CallbackUDPVisitor visitor) {
		this.callback = visitor;
	}
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		//String response = msg.content().toString(CharsetUtil.UTF_8);
		// TODO with response here
		if (this.callback != null) {
			this.callback.doJob(ctx, msg);
		}
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
