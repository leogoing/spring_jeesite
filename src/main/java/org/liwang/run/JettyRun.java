package org.liwang.run;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;

/**
 * 启动jetty服务器
 * @author Administrator
 *
 */
public class JettyRun {

	public static void main(String[] args) throws Exception {
		
		Server server = new Server(8181);
		
		ResourceHandler handler = new ResourceHandler();
		handler.setResourceBase(".");
		handler.setDirectoriesListed(true);
		
		server.setHandler(handler);
		
		server.start();
		server.join();
		
	}
	
}
