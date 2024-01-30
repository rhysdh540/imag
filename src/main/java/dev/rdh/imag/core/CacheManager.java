package dev.rdh.imag.core;


import dev.rdh.imag.ImagPlugin;
import dev.rdh.imag.config.ImagExtension;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.rdh.imag.Util.hash;

public final class CacheManager {
	private CacheManager() {}

	private static final Path MAIN_CACHE_DIR = ImagPlugin.getProject().getRootDir().toPath().resolve(".gradle").resolve("imag-cache");
	private static Path CACHE_DIR;

	private static void makeCacheDir() {
		if(Files.exists(CACHE_DIR)) return;
		try {
			Files.createDirectories(CACHE_DIR);
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	static {
		String newHash = hash(ImagPlugin.getProject().getExtensions().getByType(ImagExtension.class));
		CACHE_DIR = MAIN_CACHE_DIR.resolve(newHash);
		makeCacheDir();
	}

	public static byte[] getCached(byte[] preprocessed) {
		String hash = hash(preprocessed);
		makeCacheDir();
		try {
			return Files.readAllBytes(CACHE_DIR.resolve(hash));
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static boolean isCached(byte[] original) {
		if(!ImagPlugin.getProject().getExtensions().getByType(ImagExtension.class).getCache().get()) return false;
		String hash = hash(original);
		return Files.exists(CACHE_DIR.resolve(hash));
	}

	public static void cache(byte[] processed, byte[] original) {
		if(!ImagPlugin.getProject().getExtensions().getByType(ImagExtension.class).getCache().get()) return;
		makeCacheDir();
		String hash = hash(original);
		try {
			Files.write(CACHE_DIR.resolve(hash), processed);
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
