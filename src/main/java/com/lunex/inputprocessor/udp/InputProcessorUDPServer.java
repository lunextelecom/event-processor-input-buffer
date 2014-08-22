package com.lunex.inputprocessor.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class InputProcessorUDPServer {

	Logger logger = LoggerFactory.getLogger(InputProcessorUDPServer.class);

	private Bootstrap bootStrap;
	private int port;
	private Channel channel;
	private EventLoopGroup group;

	public InputProcessorUDPServer(int port) {
		this.port = port;
	}

	/**
	 * Start server UDP
	 * 
	 * @throws Exception
	 */
	public void startServer() throws Exception {
		group = new NioEventLoopGroup();
		try {
			bootStrap = new Bootstrap();
			bootStrap.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new InputProcessorUDPServerHandler());

			channel = bootStrap.bind(port).sync().channel();
			channel.closeFuture().await();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			group.shutdownGracefully();
		}
	}

	/**
	 * Stop server UDP
	 */
	public void stopServer() {
		if (channel != null) {
			channel.close();
		}
		if (group != null) {
			group.shutdownGracefully();
		}
	}
}
