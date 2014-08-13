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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.Random;

public class QuoteOfTheMomentServerHandler extends
		SimpleChannelInboundHandler<DatagramPacket> {

	private static final Random random = new Random();

	// Quotes from Mohandas K. Gandhi:
	private static final String[] quotes = {
			"Where there is love there is life.",
			"First they ignore you, then they laugh at you, then they fight you, then you win.",
			"Be the change you want to see in the world.",
			"The weak can never forgive. Forgiveness is the attribute of the strong.", };

	private static String nextQuote() {
		int quoteId;
		synchronized (random) {
			quoteId = random.nextInt(quotes.length);
		}
		return quotes[quoteId];
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet)
			throws Exception {
		System.err.println(packet);
		String packageContent = packet.content().toString(CharsetUtil.UTF_8);
		if ("QOTM?".equals(packageContent)) {
			ctx.write(new DatagramPacket(Unpooled.copiedBuffer("QOTM: " + nextQuote(), CharsetUtil.UTF_8), packet.sender()));
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		// We don't close the channel because we can keep serving requests.
	}
}