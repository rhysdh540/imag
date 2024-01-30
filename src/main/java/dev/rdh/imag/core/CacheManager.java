package dev.rdh.imag.core;

import dev.rdh.imag.ImagPlugin;
import dev.rdh.imag.Util;
import dev.rdh.imag.config.ImagExtension;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.rdh.imag.Util.hash;

public final class CacheManager {
	private CacheManager() {}

	private static final Path CACHE_DIR = ImagPlugin.getProject().getRootDir().toPath().resolve(".gradle").resolve("imag-cache");
	private static final Path SETTINGS_HASH = CACHE_DIR.resolve("settings.hash");

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
		try {
			String oldHash = Files.exists(SETTINGS_HASH) ? Files.readAllLines(SETTINGS_HASH).get(0) : "";
			String newHash = hash(ImagPlugin.getProject().getExtensions().getByType(ImagExtension.class));
			if(!oldHash.equals(newHash)) {
				Util.deleteDirectory(CACHE_DIR);
				Files.createDirectories(CACHE_DIR);
				Files.write(SETTINGS_HASH, newHash.getBytes());
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
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
