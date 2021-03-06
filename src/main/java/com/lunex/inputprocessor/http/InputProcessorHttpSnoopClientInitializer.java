package com.lunex.inputprocessor.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.ssl.SslContext;

import com.lunex.inputprocessor.CallbackHTTPVisitor;

public class InputProcessorHttpSnoopClientInitializer extends
	ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private CallbackHTTPVisitor callback;

    public InputProcessorHttpSnoopClientInitializer(SslContext sslCtx,
	    CallbackHTTPVisitor callback) {
	this.sslCtx = sslCtx;
	this.callback = callback;
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

	p.addLast(new InputProcessorHttpSnoopClientHandler(callback));
    }
}