package com.lunex.inputprocessor.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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
    private ServerBootstrap bootStrap;
    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public int numThread = 1;

	public InputProcessorHttpSnoopServer(int port, boolean isSSL) {
		this.isSSL = isSSL;
		this.port = port;
	}

	/**
	 * Start HTTP server to listen request from client
	 * @throws Exception
	 */
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
		bossGroup = new NioEventLoopGroup(numThread);
		workerGroup = new NioEventLoopGroup();
		try {
			bootStrap = new ServerBootstrap();
			bootStrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new InputProcessorHttpSnoopServerInitializer(sslCtx));

			channel = bootStrap.bind(port).sync().channel();

			// System.out.println("Open your web browser and navigate to " +
			// (isSSL ? "https" : "http") + "://" + host +":" + port + '/');

			ChannelFuture channelFuture = channel.closeFuture();
			channelFuture.sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	/**
	 * Shutdown server
	 */
	public void stopServer() {
		channel.close();
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
}
