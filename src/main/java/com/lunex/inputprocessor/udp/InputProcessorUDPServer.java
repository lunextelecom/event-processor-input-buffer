package com.lunex.inputprocessor.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import com.lunex.inputprocessor.Constants;

public class InputProcessorUDPServer {
	private Bootstrap bootStrap;
	private int port;

	public InputProcessorUDPServer(int port) {
		this.port = port;
	}
	
	public void startServer() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			bootStrap = new Bootstrap();
			bootStrap.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new InputProcessorUDPServerHandler());

			bootStrap.bind(port).sync().channel().closeFuture().await();
		} finally {
			group.shutdownGracefully();
		}
	}
}
