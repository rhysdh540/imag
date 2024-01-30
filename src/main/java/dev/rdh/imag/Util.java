package dev.rdh.imag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public final class Util {
	public static String hash(Object... keys) {
		return Integer.toHexString(Arrays.deepHashCode(keys));
	}

	public static String formatBytes(long bytes) {
		if(bytes == 1) {
			return "1 byte";
		}

		return String.format("%,d bytes", bytes);
	}

	public static void transferInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		for(int read; (read = in.read(buffer)) != -1;) {
			out.write(buffer, 0, read);
		}
	}

	public static String getFileExtension(File file) {
		String name = file.getName();
		int lastDot = name.lastIndexOf('.');
		if(lastDot == -1) {
			return "";
		}
		return name.substring(lastDot + 1);
	}

	public static byte[] processFileWithCommand(List<String> command, byte[] fileContents, String extension) {
		try {
			@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
			Path tempFile = File.createTempFile(Util.hash(fileContents), "." + extension).toPath();
			tempFile.toFile().deleteOnExit();
			Files.write(tempFile, fileContents);
			command.add(tempFile.toAbsolutePath().toString());

			runProcess(command);
			return Files.readAllBytes(tempFile);
		} catch (Exception e) {
			throw new UncheckedIOException(e instanceof IOException ? (IOException) e : new IOException(e));
		}
	}

	public static void runProcess(List<String> args) {
		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			Process p = pb.start();
			int exit = p.waitFor();
			if(exit != 0) {
				transferInputStream(p.getErrorStream(), System.err);
				throw new IOException(args.get(0) + " exited with code " + exit);
			}
		} catch (Exception e) {
			throw new UncheckedIOException(e instanceof IOException ? (IOException) e : new IOException(e));
		}
	}
}
