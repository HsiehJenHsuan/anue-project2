package anue.project2;

import java.io.IOException;
import java.util.Scanner;

import anue.project2.client.NioClient;
import anue.project2.server.NioServer;

public class NioTester {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, InterruptedException {
		NioServer server = new NioServer();
		server.start();
		
		Thread.sleep(100);
		
		NioClient client = new NioClient();
		client.start();
		
		while(client.sendMessage(new Scanner(System.in).nextLine()));
	}
}
