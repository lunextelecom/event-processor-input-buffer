package com.lunex.inputprocessor.http;

import com.lunex.inputprocessor.Constants;

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
	public boolean isSSL = (System.getProperty(Constants.HTTP_SSL_PORT_KEY) != null && System.getProperty(Constants.HTTP_SSL_PORT_KEY) != "");
	public int port = Integer.parseInt(isSSL ? System.getProperty(Constants.HTTP_PORT_KEY) : System.getProperty(Constants.HTTP_SSL_PORT_KEY));
	public String host = System.getProperty(Constants.HTTP_HOST_KEY);
	public int numThread = 1;
	private ServerBootstrap bootStrap;
	private Channel channel;

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
