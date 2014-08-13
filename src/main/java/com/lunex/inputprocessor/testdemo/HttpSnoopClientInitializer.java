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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.ssl.SslContext;

public class HttpSnoopClientInitializer extends
		ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;

	public HttpSnoopClientInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();

		// Enable HTTPS if necessary.
		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}

		p.addLast(new HttpClientCodec());

		// Remove the following line if you don't want automatic content
		// decompression.
		p.addLast(new HttpContentDecompressor());

		// Uncomment the following line if you don't want to handle
		// HttpContents.
		// p.addLast(new HttpObjectAggregator(1048576));

		p.addLast(new HttpSnoopClientHandler());
	}
}
