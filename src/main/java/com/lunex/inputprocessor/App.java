package com.lunex.inputprocessor;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
//        InputProcessorHttpSnoopClient client = new InputProcessorHttpSnoopClient("http://localhost:8080", new CallbackHTTPVisitor() {
//        	@Override
//        	public void doJob(ChannelHandlerContext ctx, Object msg) {
//        		// TODO Auto-generated method stub
//        		System.out.println(msg);
//        	}
//        });
//        try {
//			client.postRequestJsonContent(new JSONObject("{'a':'a', 'b':'b'}"), false);
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        
//        InputProcessorHttpSnoopServer server = new InputProcessorHttpSnoopServer(8080, false);
//        try {
//			server.startServer();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        try {
        	//ParameterHandler.getPropertiesValues();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
