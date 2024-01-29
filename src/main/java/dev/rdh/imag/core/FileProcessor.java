package dev.rdh.imag.core;

public interface FileProcessor {
	byte[] process(byte[] fileContents);

	String[] getSupportedExtensions();
}
