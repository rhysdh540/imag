package dev.rdh.imag.process;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

public class Oxipng {
	private static final String[] BASE = {"oxipng", "-i", "0", "-a", "--zc", "1", "-f", "0-9", "--strip", "all", "--stdout", "-"};
	public static byte[] process(byte[] input) {
		try {
			File temp = File.createTempFile(String.valueOf(Arrays.hashCode(input)), ".png");
			temp.deleteOnExit();
			Files.write(temp.toPath(), input);
			ProcessBuilder pb = new ProcessBuilder(BASE)
					.redirectOutput(ProcessBuilder.Redirect.PIPE);
			pb.command().add(temp.getAbsolutePath());
			Process p = pb.start();
			p.waitFor();

			return p.getInputStream().readAllBytes();
		} catch(Exception e) {
			return input;
		}
	}
}
