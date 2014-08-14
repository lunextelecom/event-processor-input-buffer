package com.lunex.inputprocessor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

public abstract class CallbackUDPVisitor {
	public String doJob(ChannelHandlerContext ctx, DatagramPacket msg) {
		return "";
	}
}
