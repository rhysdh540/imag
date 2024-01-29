package dev.rdh.imag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

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
}
