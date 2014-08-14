package com.lunex.inputprocessor.http;

import com.lunex.inputprocessor.CallbackHTTPVisitor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class InputProcessorHttpSnoopClientHandler extends SimpleChannelInboundHandler<HttpObject> {

	private CallbackHTTPVisitor callback;
	
	public InputProcessorHttpSnoopClientHandler(CallbackHTTPVisitor callback) {
		this.callback = callback;
	}
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {		
		if (callback != null) {
			callback.doJob(ctx, msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}