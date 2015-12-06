package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

public class InputOutputStream {
	
	private LinkedBlockingQueue<Integer> stream;
	
	private class MyInputStream extends InputStream {

		@Override
		public int read() throws IOException {
			try {
				return stream.take();
			} catch (InterruptedException e) {
				throw new IOException("Interrupted while waiting on the stream.take().");
			}
		}
		
	}
	
	private class MyOutputStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
			try {
				stream.put(b);
			} catch (InterruptedException e) {
				throw new IOException("Interrupted while waiting on the stream.put(" + b + ").");
			}
		}
		
	}
	
	private MyInputStream input;
	private MyOutputStream output;
	
	public InputOutputStream() {
		stream = new LinkedBlockingQueue<Integer>();
		
		input = new MyInputStream();
		output = new MyOutputStream();
	}

	public InputStream getInputStream() {
		return input;
	}

	public OutputStream getOutputStream() {
		return output;
	}
	
}
