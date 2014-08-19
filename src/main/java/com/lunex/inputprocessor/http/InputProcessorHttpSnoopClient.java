package com.lunex.inputprocessor.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.net.ssl.SSLException;

import org.json.JSONObject;

import com.lunex.inputprocessor.CallbackHTTPVisitor;

public class InputProcessorHttpSnoopClient {

	private String url;
	private URI uri;
	private int port;
	private String host;
	private String scheme;
	public CallbackHTTPVisitor callback;
	private SslContext sslCtx;
	public Object msg;

	public InputProcessorHttpSnoopClient(String url, CallbackHTTPVisitor callback) {
		this.url = url;
		this.callback = callback;
	}

	private boolean preProcessURL() throws URISyntaxException, SSLException {
		this.uri = new URI(url);
		this.scheme = uri.getScheme();
		this.host = uri.getHost();
		this.port = uri.getPort();
		if (port == -1) {
			if ("http".equalsIgnoreCase(scheme)) {
				port = 80;
			} else if ("https".equalsIgnoreCase(scheme)) {
				port = 443;
			}
		}

		if (!"http".equalsIgnoreCase(scheme)
				&& !"https".equalsIgnoreCase(scheme)) {
			System.err.println("Only HTTP(S) is supported.");
			return false;
		}

		// Configure SSL context if necessary.
		final boolean ssl = "https".equalsIgnoreCase(scheme);
		if (ssl) {
			sslCtx = SslContext
					.newClientContext(InsecureTrustManagerFactory.INSTANCE);
		} else {
			sslCtx = null;
		}
		return true;
	}

	public void postRequestJsonContent(JSONObject jsonObject, boolean async) throws Exception {
		try {
			if (!this.preProcessURL()) {
				return;
			}
		} catch (URISyntaxException ex) {
			throw ex;
		} catch (SSLException ex) {
			throw ex;
		}

		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new InputProcessorHttpSnoopClientInitializer(sslCtx, callback));

			// Make the connection attempt.
			Channel ch = b.connect(host, port).sync().channel();

			// Prepare the HTTP request.
			HttpRequest request = new DefaultFullHttpRequest(
					HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
			request.headers().set(HttpHeaders.Names.HOST, host);
			request.headers().set(HttpHeaders.Names.CONNECTION,
					HttpHeaders.Values.CLOSE);
			request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING,
					HttpHeaders.Values.GZIP);
			request.headers().set(HttpHeaders.Names.ACCEPT_CHARSET,
					"ISO-8859-1,utf-8;q=0.7,*;q=0.7");

			HttpDataFactory factory = new DefaultHttpDataFactory(
					DefaultHttpDataFactory.MINSIZE);
			HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(
					factory, request, false); // false => not multipart

			// add Form attribute for body
			Iterator<?> keys = jsonObject.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				bodyRequestEncoder.addBodyAttribute(key, jsonObject.get(key).toString());
			}
			bodyRequestEncoder.addBodyAttribute("async", String.valueOf(async));
			request = bodyRequestEncoder.finalizeRequest();

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
