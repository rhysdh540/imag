package dev.rdh.imag.core;

import dev.rdh.imag.Util;

import java.io.File;
import java.util.Arrays;

public interface FileProcessor {
	byte[] process(byte[] fileContents);

	String[] getSupportedExtensions();

	default boolean shouldProcess(File file) {
		String extension = Util.getFileExtension(file);
		return Arrays.stream(getSupportedExtensions()).anyMatch(s -> s.equalsIgnoreCase(extension));
	}
}
