package com.lunex.inputprocessor.udp;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lunex.eventhandler.navigation.EventHandlerNavigation;
import com.lunex.eventhandler.utils.JSONObjectUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class InputProcessorUDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	Logger logger = LoggerFactory.getLogger(InputProcessorUDPServerHandler.class);
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
		String responseContent = "Get package and Process async";
		if (packet != null) {
			ByteBuf buf = packet.content();
			if (buf.isReadable()) {
				String packageContent = packet.content().toString(CharsetUtil.UTF_8);
				JSONObject jsonObject = new JSONObject(packageContent);
				Boolean asycn = JSONObjectUtils.getBoolean("async", jsonObject);
				if (asycn != null && asycn) {
					Thread thread = new Thread(new PackageProcessorThread(jsonObject));
					thread.start();
		
					// TODO with packageContent to get responseContent
					responseContent = "{\"result:\": \"\", \"description\":\"Get package and Processing async\"}";
		
				} else {
					// TODO with packageContent to get responseContent
					
					EventHandlerNavigation eventHandler = new EventHandlerNavigation();
					logger.info("request:" + jsonObject.toString());
					System.out.println("request: " + jsonObject.toString());
					JSONObject responseObject = eventHandler.processEvent(jsonObject);
					if (responseObject != null) {
						responseContent = responseObject.toString();
						logger.info("response: " + responseContent);
					} else {
						logger.error("response is null");
						System.out.println("response is null");
					}
					
				}
				// Sent response to client sender
				ctx.write(new DatagramPacket(Unpooled.copiedBuffer(responseContent, CharsetUtil.UTF_8), packet.sender()));
			} else {
				logger.error("package content can not be read");
			}
		} else {
			logger.error("package is null");
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		logger.error(cause.getMessage());
		// We don't close the channel because we can keep serving requests.
	}

	public class PackageProcessorThread implements Runnable {

		JSONObject jsonObject;

		public PackageProcessorThread(JSONObject jsonObject) {
			this.jsonObject = jsonObject;
		}

		public void run() {
			// TODO something
			EventHandlerNavigation eventHandler = new EventHandlerNavigation();
			logger.info(jsonObject.toString());
			System.out.println(jsonObject.toString());
			JSONObject responseObject = eventHandler.processEvent(jsonObject);
			if (responseObject != null) {
				logger.info(responseObject.toString());
				System.out.println(jsonObject.toString());
			} else {
				logger.error("response is null");
				System.out.println("response is null");
			}
		}
	}
}
