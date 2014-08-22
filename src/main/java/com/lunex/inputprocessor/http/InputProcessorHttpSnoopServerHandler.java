package com.lunex.inputprocessor.http;

import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.eventhandler.navigation.EventHandlerNavigation;

public class InputProcessorHttpSnoopServerHandler extends
		SimpleChannelInboundHandler<Object> {

	static final Logger logger = LoggerFactory.getLogger(InputProcessorHttpSnoopServerHandler.class);
	
	private HttpRequest request;
	/** Buffer that stores the response content */
	private final StringBuilder responseContentBuilder = new StringBuilder();
	private JSONObject jsonObject = new JSONObject();

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			HttpRequest request = this.request = (HttpRequest) msg;

			if (is100ContinueExpected(request)) {
				send100Continue(ctx);
			}

			responseContentBuilder.setLength(0);
			jsonObject = new JSONObject();

			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
			Map<String, List<String>> params = queryStringDecoder.parameters();
			if (!params.isEmpty()) {
				for (Entry<String, List<String>> p : params.entrySet()) {
					String key = p.getKey();
					List<String> vals = p.getValue();
					for (String val : vals) {
						jsonObject.append(key, val);
					}
				}
			}
		}

		if (msg instanceof HttpContent) {
			HttpContent httpContent = (HttpContent) msg;

			ByteBuf content = httpContent.content();
			if (content.isReadable()) {
				String dataContent = content.toString(CharsetUtil.UTF_8);
				String[] params = dataContent.split("&");
				for (int i = 0; i < params.length; i++) {
					String[] param = params[i].split("=");
					jsonObject.put(param[0], param[1]);
				}
			}

			if (msg instanceof LastHttpContent) {
				/*
				 * process last http content
				 */
				LastHttpContent trailer = (LastHttpContent) msg;
				if (!trailer.trailingHeaders().isEmpty()) {
					// TODO some thing
				}

				// TODO something with jsonObject and return response
				if (Boolean.valueOf(jsonObject.getString("async"))) {// process async
					Thread thread = new Thread(new PackageProcessorThread(jsonObject));
					thread.start();
				} else {
					// process and wait result
					EventHandlerNavigation eventHandler = new EventHandlerNavigation();
					System.out.println(this.jsonObject.toString());
					JSONObject contentResponse = eventHandler.processEvent(this.jsonObject);
					if (contentResponse != null) {
						responseContentBuilder.append(contentResponse);
					}
				}
				// write response
				if (!writeResponse(trailer, ctx)) {
					// If keep-alive is off, close the connection once the content is fully written.
					ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
				}
			}
		}
	}

	private boolean writeResponse(HttpObject currentObj,
			ChannelHandlerContext ctx) {
		// Decide whether to close the connection or not.
		boolean keepAlive = isKeepAlive(request);

		// Build the response object.
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
				currentObj.getDecoderResult().isSuccess() ? OK : BAD_REQUEST,
				Unpooled.copiedBuffer(responseContentBuilder.toString(),
						CharsetUtil.UTF_8));

		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
			// Add keep alive header as per:
			// http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
			response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}

		// Encode the cookie.
		String cookieString = request.headers().get(COOKIE);
		if (cookieString != null) {
			Set<Cookie> cookies = CookieDecoder.decode(cookieString);
			if (!cookies.isEmpty()) {
				// Reset the cookies if necessary.
				for (Cookie cookie : cookies) {
					response.headers().add(SET_COOKIE,
							ServerCookieEncoder.encode(cookie));
				}
			}
		}

		// Write the response.
		ctx.write(response);

		return keepAlive;
	}

	private static void send100Continue(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
				CONTINUE);
		ctx.write(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	public class PackageProcessorThread implements Runnable {

		JSONObject jsonObject;

		public PackageProcessorThread(JSONObject jsonObject) {
			this.jsonObject = jsonObject;
		}

		public void run() {
			// TODO with Event handler
			EventHandlerNavigation eventHandler = new EventHandlerNavigation();
			eventHandler.processEvent(this.jsonObject);
		}
	}
}