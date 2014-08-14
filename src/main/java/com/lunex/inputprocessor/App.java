package com.lunex.inputprocessor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

import com.lunex.inputprocessor.Constants.HTTP_METHOD;
import com.lunex.inputprocessor.http.InputProcessorHttpSnoopClient;
import com.lunex.inputprocessor.http.InputProcessorHttpSnoopServer;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        try {
        	//ParameterHandler.getPropertiesValues();
//        	InputProcessorHttpSnoopServer server = new InputProcessorHttpSnoopServer(8080, false);
//        	server.startServer();
        	InputProcessorHttpSnoopClient client = new InputProcessorHttpSnoopClient("http://localhost:8080", new CallbackHTTPVisitor(){
        		@Override
        		public void doJob(ChannelHandlerContext ctx, Object msg) {
        			// TODO Auto-generated method stub
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
        	});
        	client.sendRequest(HTTP_METHOD.GET);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
