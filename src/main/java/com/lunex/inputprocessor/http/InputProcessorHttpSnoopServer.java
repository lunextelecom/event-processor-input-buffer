package com.lunex.inputprocessor.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class InputProcessorHttpSnoopServer {
	private boolean isSSL;
	private int port;
	private String host;	
	private ServerBootstrap bootStrap;
	private Channel channel;
	
	public int numThread = 1;

	public InputProcessorHttpSnoopServer(String host, int port, boolean isSSL) {
		this.isSSL = isSSL;
		this.host = host;
		this.port = port;
	}
	
	public void startServer() throws Exception {
		// Configure SSL.
		final SslContext sslCtx;
		if (isSSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
		} else {
			sslCtx = null;
		}

		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(numThread);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			bootStrap = new ServerBootstrap();
			bootStrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.DEBUG))
					.childHandler(new InputProcessorHttpSnoopServerInitializer(sslCtx));

			channel = bootStrap.bind(port).sync().channel();

			System.out.println("Open your web browser and navigate to " + (isSSL ? "https" : "http") + "://"  + host +":" + port + '/');

			channel.closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}	
}
