package com.lunex.inputprocessor.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.inputprocessor.CallbackHTTPVisitor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;

public class InputProcessorHttpSnoopClientHandler extends SimpleChannelInboundHandler<HttpObject> {

	static final Logger logger = LoggerFactory.getLogger(InputProcessorHttpSnoopClientHandler.class);
	
	private CallbackHTTPVisitor callback;

	public InputProcessorHttpSnoopClientHandler(CallbackHTTPVisitor callback) {
		this.callback = callback;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
			throws Exception {
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