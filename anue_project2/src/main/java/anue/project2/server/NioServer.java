package anue.project2.server;

import java.io.IOException;

public class NioServer {
	public int port = 8888;

	public void start() throws IOException {
		NioServerHandler serverHandle = new NioServerHandler(port);
		new Thread(serverHandle).start();
	}

	public static void main(String[] args) throws IOException {
		NioServer server = new NioServer();
		server.start();
	}
}
