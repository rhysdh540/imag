package dev.rdh.imag.core;

import dev.rdh.imag.ImagPlugin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class CacheManager {
	private CacheManager() {}

	private static final Path CACHE_DIR = ImagPlugin.getProject().getRootDir().toPath().resolve(".gradle").resolve("imag-cache");

	static {
		try {
			Files.createDirectories(CACHE_DIR);
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static byte[] getCached(byte[] preprocessed) {
		String hash = hash(preprocessed);
		try {
			return Files.readAllBytes(CACHE_DIR.resolve(hash));
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static boolean isCached(byte[] preprocessed) {
		String hash = hash(preprocessed);
		return Files.exists(CACHE_DIR.resolve(hash));
	}

	public static void cache(byte[] preprocessed, byte[] postprocessed) {
		String hash = hash(preprocessed);
		try {
			Files.write(CACHE_DIR.resolve(hash), postprocessed);
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static String hash(byte[] data) {
		return Integer.toHexString(Arrays.hashCode(data));
	}
}
