package dev.rdh.imag.process;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

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
		try {
			if(iterations == -1) { // dynamically choose iterations based on file size
				iterations = ITERATION_MAP.entrySet().stream()
						.filter(e -> e.getKey() > input.length)
						.findFirst()
						.orElseThrow()
						.getValue();
			}

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
		} catch(Exception e) {
			return input;
		}
	}
}
