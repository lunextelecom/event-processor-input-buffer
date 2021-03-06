package com.lunex.inputprocessor.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

import org.json.JSONObject;

import com.lunex.inputprocessor.CallbackUDPVisitor;

public class InputProcessorUDPClient {

	public int port;
	private final String INET_SOCKET_ADDRESS = "255.255.255.255";

	public InputProcessorUDPClient(int port) {
		this.port = port;
	}

	/**
	 * Submit content string
	 * @param content
	 * @param timeout
	 * @param callback
	 * @throws Exception
	 */
	public void submitContent(String content, long timeout, CallbackUDPVisitor callback) throws Exception {

		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new InputProcessorUDPClientHandler(callback));

			Channel ch = b.bind(0).sync().channel();

			ch.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer(content,
							CharsetUtil.UTF_8), new InetSocketAddress(
							INET_SOCKET_ADDRESS, port))).sync();

			if (!ch.closeFuture().await(timeout)) {
				System.err.println("Request timed out.");
			}
		} finally {
			group.shutdownGracefully();
		}
	}

	/**
	 * Submit bytesArray
	 * @param bytesArray
	 * @param timeout
	 * @param callback
	 * @throws Exception
	 */
	public void submitBytesArray(byte[] bytesArray, long timeout, CallbackUDPVisitor callback) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new InputProcessorUDPClientHandler(callback));

			Channel ch = b.bind(0).sync().channel();
			ch.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer(bytesArray),
							new InetSocketAddress(INET_SOCKET_ADDRESS, port)))
					.sync();

			if (!ch.closeFuture().await(timeout)) {
				System.err.println("Request timed out.");
			}
		} finally {
			group.shutdownGracefully();
		}
	}

	/**
	 * Submit jsonObject
	 * @param jsonObject
	 * @param timeout
	 * @param callback
	 * @throws Exception
	 */
	public void submitJsonObject(JSONObject jsonObject, long timeout, CallbackUDPVisitor callback) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new InputProcessorUDPClientHandler(callback));

			Channel ch = b.bind(0).sync().channel();
			ch.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer(
							jsonObject.toString(), CharsetUtil.UTF_8),
							new InetSocketAddress(INET_SOCKET_ADDRESS, port)))
					.sync();

			if (!ch.closeFuture().await(timeout)) {
				System.err.println("Request timed out.");
			}
		} finally {
			group.shutdownGracefully();
		}
	}
}
