package com.lunex.inputprocessor;

import io.netty.channel.socket.DatagramPacket;

public abstract class CallbackVisitor {
	public String doJob(DatagramPacket msg) {
		return "";
	}
}
