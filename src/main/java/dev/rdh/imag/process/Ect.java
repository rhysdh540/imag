package dev.rdh.imag.process;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.util.Arrays;

public class Ect {
	private static final String[] BASE = {"ect", "-9", "--mt-deflate", "--pal_sort=120"};
	public static byte[] process(byte[] input, boolean brute) {
		try {
			return process0(input, brute);
		} catch(Throwable t) {
			try {
				return processCli(input, brute);
			} catch(Throwable t2) {
				return input;
			}
		}
	}

	private static byte[] processCli(byte[] input, boolean brute) throws Throwable {
		File temp = File.createTempFile(String.valueOf(Arrays.hashCode(input)), ".png");
		temp.deleteOnExit();
		Files.write(temp.toPath(), input);
		ProcessBuilder pb = new ProcessBuilder(BASE)
				.redirectOutput(Redirect.DISCARD);
		pb.command().add(brute ? "--allfilters-b" : "--allfilters");
		pb.command().add(temp.getAbsolutePath());
		pb.start().waitFor();

		return Files.readAllBytes(temp.toPath());
	}

	private static native byte[] process0(byte[] input, boolean brute) throws Throwable;
}
