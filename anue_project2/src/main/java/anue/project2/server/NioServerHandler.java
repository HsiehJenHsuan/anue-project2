package anue.project2.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServerHandler implements Runnable {
	private Selector selector;
	private boolean running = true;

	public NioServerHandler(int port) throws IOException {
		// start selector
		selector = Selector.open();

		// start channel
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.configureBlocking(false);
		channel.socket().bind(new InetSocketAddress(port), 1024);
		channel.register(selector, SelectionKey.OP_ACCEPT);
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
				while(it.hasNext()){
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
		// get channel and accept it
		ServerSocketChannel serverSocketChanner = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = serverSocketChanner.accept();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
		
		// create buffer
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		// TODO handle Delayed Ack problem
		// read message and handle it
		buffer.flip(); // reset buffer
		byte[] receiveBytes = new byte[buffer.remaining()];
		buffer.get(receiveBytes);
		messageHandler(new String(receiveBytes,"UTF-8"));
		
		// response
		String response = responseHandler();
		byte[] responseBytes = response.getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(responseBytes.length);
		writeBuffer.put(responseBytes);
		writeBuffer.flip(); // reset buffer
		socketChannel.write(writeBuffer);
		
		//release
		key.cancel();
		socketChannel.close();
	}
	
	private void messageHandler(String message) {
		// extract handler here, build function base on require, like saving data, analyze data...etc
	}
	
	private String responseHandler() {
		// extract handler here, build function base on require, like return stock's price...etc
		return "response";
	}
}
