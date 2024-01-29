package dev.rdh.imag.core;

import dev.rdh.imag.ImagPlugin;
import dev.rdh.imag.config.ImagExtension;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class CacheManager {
	private CacheManager() {}

	private static final Path CACHE_DIR = ImagPlugin.getProject().getRootDir().toPath().resolve(".gradle").resolve("imag-cache");

	private static void makeCacheDir() {
		if(Files.exists(CACHE_DIR)) return;
		try {
			Files.createDirectories(CACHE_DIR);
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	static {
		makeCacheDir();
	}

	public static byte[] getCached(Object... keys) {
		String hash = hash(keys);
		try {
			return Files.readAllBytes(CACHE_DIR.resolve(hash));
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static boolean isCached(Object... keys) {
		if(!ImagPlugin.getProject().getExtensions().getByType(ImagExtension.class).getCache().get()) return false;
		String hash = hash(keys);
		return Files.exists(CACHE_DIR.resolve(hash));
	}

	public static void cache(byte[] postprocessed, Object... keys) {
		if(!ImagPlugin.getProject().getExtensions().getByType(ImagExtension.class).getCache().get()) return;
		String hash = hash(keys);
		try {
			Files.write(CACHE_DIR.resolve(hash), postprocessed);
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static String hash(Object... keys) {
		return Integer.toHexString(Arrays.deepHashCode(keys));
	}
}
