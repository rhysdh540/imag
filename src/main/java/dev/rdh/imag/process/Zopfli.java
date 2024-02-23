package dev.rdh.imag.process;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Zopfli {
	private static final String[] BASE = {"zopflipng", "-y", "--filters=p"};
	private static final Map<Long, Integer> ITERATION_MAP = new LinkedHashMap<>();
	static {
		ITERATION_MAP.put(1000L         , 10000); // <1kb -> 10k iterations
		ITERATION_MAP.put(10000L        , 7500 ); // <10kb -> 7.5k iterations
		ITERATION_MAP.put(100000L       , 1000 ); // <100kb -> 1k iterations
		ITERATION_MAP.put(1000000L      , 100  ); // <1mb -> 100 iterations
		ITERATION_MAP.put(Long.MAX_VALUE, 15   ); // >1mb -> 15 iterations
	}

	public static byte[] process(byte[] input, int iterations) {
		if(iterations < 0) { // dynamically choose iterations based on file size
			for(Entry<Long, Integer> e : ITERATION_MAP.entrySet()) {
				if(e.getKey() > input.length) {
					iterations = e.getValue();
					break;
				}
			}

			if(iterations == -1) {
				throw new AssertionError();
			}
		}

		try {
			return process0(input, iterations);
		} catch(Throwable t) {
			try {
				return processCli(input, iterations);
			} catch(Throwable t2) {
				return input;
			}
		}
	}

	private static byte[] processCli(byte[] input, int iterations) throws Throwable {
		File temp = File.createTempFile(String.valueOf(Arrays.hashCode(input)), ".png");
		temp.deleteOnExit();
		Files.write(temp.toPath(), input);
		ProcessBuilder pb = new ProcessBuilder(BASE)
				.redirectOutput(ProcessBuilder.Redirect.PIPE);
		pb.command().add("--iterations=" + iterations);
		pb.command().add(temp.getAbsolutePath());
		pb.command().add(temp.getAbsolutePath());

		Process p = pb.start();
		p.waitFor();

		return Files.readAllBytes(temp.toPath());
	}

	private static native byte[] process0(byte[] input, int iterations) throws Throwable;
}
