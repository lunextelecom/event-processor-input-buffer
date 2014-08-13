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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class HttpSnoopClientHandler extends
		SimpleChannelInboundHandler<HttpObject> {

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
			throws Exception {
		if (msg instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) msg;

			System.err.println("STATUS: " + response.getStatus());
			System.err.println("VERSION: " + response.getProtocolVersion());
			System.err.println();

			if (!response.headers().isEmpty()) {
				for (String name : response.headers().names()) {
					for (String value : response.headers().getAll(name)) {
						System.err.println("HEADER: " + name + " = " + value);
					}
				}
				System.err.println();
			}

			if (HttpHeaders.isTransferEncodingChunked(response)) {
				System.err.println("CHUNKED CONTENT {");
			} else {
				System.err.println("CONTENT {");
			}
		}
		if (msg instanceof HttpContent) {
			HttpContent content = (HttpContent) msg;

			System.err.print(content.content().toString(CharsetUtil.UTF_8));
			System.err.flush();

			if (content instanceof LastHttpContent) {
				System.err.println("} END OF CONTENT");
				ctx.close();
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
