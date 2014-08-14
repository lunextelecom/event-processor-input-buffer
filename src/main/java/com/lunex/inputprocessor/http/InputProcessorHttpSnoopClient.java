package com.lunex.inputprocessor.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.ClientCookieEncoder;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.lang.reflect.Method;
import java.net.URI;

import com.lunex.inputprocessor.CallbackHTTPVisitor;
import com.lunex.inputprocessor.Constants;
import com.lunex.inputprocessor.Constants.HTTP_METHOD;
import com.lunex.inputprocessor.testdemo.HttpSnoopClientInitializer;

public class InputProcessorHttpSnoopClient {
	
	private String url;
	private CallbackHTTPVisitor callback;
	public InputProcessorHttpSnoopClient(String url, CallbackHTTPVisitor callback) {
		this.url = url;
		this.callback = callback;
	}
	
	public void sendRequest(Constants.HTTP_METHOD method) throws Exception {
		URI uri = new URI(url);
		String scheme = uri.getScheme();
		String host = uri.getHost();
		int port = uri.getPort();
		if (port == -1) {
			if ("http".equalsIgnoreCase(scheme)) {
				port = 80;
			} else if ("https".equalsIgnoreCase(scheme)) {
				port = 443;
			}
		}

		if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
			System.err.println("Only HTTP(S) is supported.");
			return;
		}

		// Configure SSL context if necessary.
		final boolean ssl = "https".equalsIgnoreCase(scheme);
		final SslContext sslCtx;
		if (ssl) {
			sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
		} else {
			sslCtx = null;
		}

		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new InputProcessorHttpSnoopClientInitializer(sslCtx, callback));

			// Make the connection attempt.
			Channel ch = b.connect(host, port).sync().channel();

			// Prepare the HTTP request.
			HttpMethod m = HttpMethod.POST;
			if (method.equals(HTTP_METHOD.POST)) {
				m = HttpMethod.POST;
			} else if (method.equals(HTTP_METHOD.GET)) {
				m = HttpMethod.GET;
			}
			
			HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, m, uri.getRawPath());
			request.headers().set(HttpHeaders.Names.HOST, host);
			request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
			request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

			// Set some example cookies.
			//request.headers().set(HttpHeaders.Names.COOKIE, ClientCookieEncoder.encode(new DefaultCookie("my-cookie", "foo"), new DefaultCookie("another-cookie", "bar")));
			//String json = "{\"foo\":\"bar\"}";
			//request.headers().set("json", json);
			// Send the HTTP request.
			ChannelFuture chanel = ch.writeAndFlush(request);

			// Wait for the server to close the connection.
			ch.closeFuture().sync();
		} finally {
			// Shut down executor threads to exit.
			group.shutdownGracefully();
		}
	}
}
