package com.lunex.inputprocessor.testdemo;

/*
 2    * Copyright 2012 The Netty Project
 3    *
 4    * The Netty Project licenses this file to you under the Apache License,
 5    * version 2.0 (the "License"); you may not use this file except in compliance
 6    * with the License. You may obtain a copy of the License at:
 7    *
 8    *   http://www.apache.org/licenses/LICENSE-2.0
 9    *
 10   * Unless required by applicable law or agreed to in writing, software
 11   * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 12   * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 13   * License for the specific language governing permissions and limitations
 14   * under the License.
 15   */

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

/**
 * A UDP broadcast client that asks for a quote of the moment (QOTM) to
 * {@link QuoteOfTheMomentServer}.
 *
 * Inspired by <a href=
 * "http://docs.oracle.com/javase/tutorial/networking/datagrams/clientServer.html"
 * >the official Java tutorial</a>.
 */
public final class QuoteOfTheMomentClient {

	static final int PORT = Integer
			.parseInt(System.getProperty("port", "7686"));

	public static void main(String[] args) throws Exception {

		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST, true)
					.handler(new QuoteOfTheMomentClientHandler());

			Channel ch = b.bind(0).sync().channel();

			// Broadcast the QOTM request to port 8080.
			ch.writeAndFlush(
					new DatagramPacket(Unpooled.copiedBuffer("QOTM?",
							CharsetUtil.UTF_8), new InetSocketAddress(
							"255.255.255.255", PORT))).sync();

			// QuoteOfTheMomentClientHandler will close the DatagramChannel when
			// a
			// response is received. If the channel is not closed within 5
			// seconds,
			// print an error message and quit.
			if (!ch.closeFuture().await(5000)) {
				System.err.println("QOTM request timed out.");
			}
		} finally {
			group.shutdownGracefully();
		}
	}
}