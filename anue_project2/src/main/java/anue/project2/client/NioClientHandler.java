package anue.project2.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClientHandler implements Runnable {
	private Selector selector;
	private SocketChannel channel;
	private boolean running = true;

	public NioClientHandler(String ip, int port) throws IOException {
		// start channel
		channel = SocketChannel.open(new InetSocketAddress(ip, port));
		channel.configureBlocking(false);

		// start selector
		selector = Selector.open();
		channel.register(selector, SelectionKey.OP_CONNECT);
	}

	@Override
	public void run() {
		while (running) {
			try {
				// check channel
				selector.select(1000);
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				SelectionKey key = null;
				while (it.hasNext()) {
					// handle input
					key = it.next();
					it.remove();
					inputHandler(key);

					// release
					key.cancel();
					key.channel().close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// release
		try {
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		this.running = false;
	}

	private void inputHandler(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		// create buffer
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		// TODO handle Delayed Ack problem
		// read message and handle it
		buffer.flip(); // reset buffer
		byte[] receiveBytes = new byte[buffer.remaining()];
		buffer.get(receiveBytes);
		messageHandler(new String(receiveBytes, "UTF-8"));

		// release
		key.cancel();
		socketChannel.close();
	}
	
	private void messageHandler(String message) {
		// extract handler here, build function base on require, like saving data, analyze data...etc
		System.out.println("fuck");
	}
	
	public void sendMessage(String message) throws IOException {
		channel.register(selector, SelectionKey.OP_READ);
		
		byte[] messageBytes = message.getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(messageBytes.length);
		writeBuffer.put(messageBytes);
		writeBuffer.flip(); // reset buffer
		channel.write(writeBuffer);
	}
}
