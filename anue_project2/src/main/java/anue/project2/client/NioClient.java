package anue.project2.client;

import java.io.IOException;

public class NioClient {
	public String ip = "127.0.0.1";
	public int port = 8888;
	private static NioClientHandler clientHandler;

	public void start() throws IOException {
		NioClientHandler clientHandler = new NioClientHandler(ip, port);
		new Thread(clientHandler).start();
	}

	public boolean sendMessage(String message) throws IOException {
		if (message.equals("quit")) {
			return false;
		}
		clientHandler.sendMessage(message);
		return true;
	}
	
	public static void main(String[] args) throws IOException {
		NioClient client = new NioClient();
		client.start();
	}
}
