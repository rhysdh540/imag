package dev.rdh.imag.core;

import java.io.File;

public interface FileProcessor {
	byte[] process(byte[] fileContents);

	String[] getSupportedExtensions();

	default boolean shouldProcess(File file) {
		String filename = file.getName();
		for(String ext : getSupportedExtensions()) {
			if(filename.endsWith(ext)) {
				return true;
			}
		}
		return false;
	}
}
