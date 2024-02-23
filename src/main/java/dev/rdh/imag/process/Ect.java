package dev.rdh.imag.process;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.util.Arrays;

public class Ect {
	private static final String[] BASE = {"ect", "-9", "--mt-deflate", "--pal_sort=120"};
	public static byte[] process(byte[] input, boolean brute) {
		try {
			File temp = File.createTempFile(String.valueOf(Arrays.hashCode(input)), ".png");
			temp.deleteOnExit();
			Files.write(temp.toPath(), input);
			ProcessBuilder pb = new ProcessBuilder(BASE)
					.redirectOutput(Redirect.DISCARD);
			pb.command().add(brute ? "--allfilters-b" : "--allfilters");
			pb.command().add(temp.getAbsolutePath());
			pb.start().waitFor();

			return Files.readAllBytes(temp.toPath());
		} catch(Exception e) {
			return input;
		}
	}
}
