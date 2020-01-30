package de.sekmi.li2b2.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.regex.Pattern;


public class InjectingFilterReader extends Reader {

	private BufferedReader source;
	private CharBuffer buffer;
	private Pattern pattern;
	private String injectString;
	private String readAhead;
	private boolean atInjectionPoint;
	private boolean injectionFinished;
	private boolean injectBefore;

	public InjectingFilterReader(int bufferSize, BufferedReader source, Pattern injectionPoint, String injectString, boolean injectBefore) {
		this.source = source;
		this.pattern = injectionPoint;
		this.buffer = CharBuffer.allocate(bufferSize);
		buffer.flip();
		this.injectString = injectString;
		this.readAhead = null;
		this.atInjectionPoint = false;
		this.injectionFinished = false;
		this.injectBefore = injectBefore;
	}

	public InjectingFilterReader(BufferedReader source, Pattern injectionPoint, String injectString, boolean injectBefore) {
		this(4096, source, injectionPoint, injectString, injectBefore);
	}

	public void setInject(String injectString, boolean injectBefore) {
		this.injectString = injectString;
		this.injectBefore = injectBefore;
	}

	@Override
	public void close() throws IOException {
		source.close();
	}

	private void fillBuffer() throws IOException {
		try{
			buffer.compact();
			if( readAhead == null ) {
				readAhead = source.readLine();
			}
			if( atInjectionPoint && !injectionFinished ) {
				if( injectString.length() < buffer.remaining() ) {
					// enough space in buffer, write the injection code
					buffer.put(injectString);
					this.injectionFinished = true;
				}else {
					// not enough space in buffer, return to let 
					// the read operation free up more space in the buffer
					return; // flip will be called by finally
				}
			}
			while( readAhead != null && buffer.remaining() > readAhead.length()+1 ) {
				if( !injectionFinished && pattern.matcher(readAhead).matches() ) {
					this.atInjectionPoint = true;
					if( injectBefore == false ) {
						buffer.put(readAhead);
						buffer.put('\n');
						readAhead = source.readLine();
					}
					break;
				}
				buffer.put(readAhead);
				buffer.put('\n');
				readAhead = source.readLine();
			}
			// flip will be called by finally
		}finally {
			buffer.flip();			
		}
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		fillBuffer();
		if( buffer.hasRemaining() == false ) {
			return -1;
		}
		// copy from buffer
		int nbytes = Math.min(len,buffer.remaining());
		buffer.get(cbuf, off, nbytes);
		return nbytes;
	}

}
