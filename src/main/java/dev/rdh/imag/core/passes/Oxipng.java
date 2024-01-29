package dev.rdh.imag.core.passes;

import org.gradle.api.provider.Property;

import dev.rdh.imag.config.optimizations.png.OxipngConfig;
import dev.rdh.imag.config.optimizations.png.OxipngConfig.StripMode;
import dev.rdh.imag.config.optimizations.png.PngConfig;
import dev.rdh.imag.core.CacheManager;
import dev.rdh.imag.core.FileProcessor;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Oxipng implements FileProcessor {
	private final OxipngConfig config;
	private final Property<Boolean> pngEnabled;

	public Oxipng(PngConfig config) {
		this.config = config.getOxipng();
		this.pngEnabled = config.getEnabled();
	}

	@Override
	public byte[] process(byte[] fileContents) {
		if(!config.getEnabled().get() || !pngEnabled.get()) {
			return fileContents;
		}

		if(CacheManager.isCached(config, fileContents)) {
			return CacheManager.getCached(config, fileContents);
		}

		List<String> args = new ArrayList<>();
		args.add("oxipng");
		args.add("-o" + config.getLevel().get());
		if(config.getInterlace().get()) {
			args.add("-i 1");
		}
		if(config.getStrip().get() != StripMode.NONE) {
			args.add("-s" + config.getStrip().get());
		}
		if(!config.getChangeBitDepth().get()) {
			args.add("--nb");
		}
		if(!config.getChangeColorType().get()) {
			args.add("--nc");
		}
		if(!config.getChangePalette().get()) {
			args.add("--np");
		}
		if(!config.getChangeGrayscale().get()) {
			args.add("--ng");
		}
		if(!config.getPerformTransformations().get()) {
			args.add("--nx");
		}
		if(!config.getRecompress().get()) {
			args.add("--nz");
		}
		if(config.getFix().get()) {
			args.add("--fix");
		}
		if(config.getNumThreads().isPresent()) {
			args.add("-t " + config.getNumThreads().get());
		}
		if(config.getTimeout().isPresent()) {
			args.add("--timeout " + config.getTimeout().get());
		}
		if(config.getUseZopfli().get()) {
			args.add("-Z");
		}
		if(config.getScale16().get()) {
			args.add("--scale16");
		}
		if(config.getAlphaOptimizations().get()) {
			args.add("-a");
		}
		if(config.getFastFilterEvaluation().get()) {
			args.add("--fast");
		}
		if(config.getFilterStrategies().isPresent() && !config.getFilterStrategies().get().isEmpty()) {
			args.add("--filters " + String.join(",", config.getFilterStrategies().get().stream().map(String::valueOf).toArray(String[]::new)));
		}
		if(config.getCompressionLevel().isPresent()) {
			args.add("--zc " + config.getCompressionLevel().get());
		}
		if(config.getKeepChunks().isPresent() && !config.getKeepChunks().get().isEmpty()) {
			args.add("--keep " + String.join(",", config.getKeepChunks().get().stream().map(String::valueOf).toArray(String[]::new)));
		}

		try {
			File tempFile = File.createTempFile(CacheManager.hash(fileContents), ".png");
			Files.write(tempFile.toPath(), fileContents);
			args.add(tempFile.getAbsolutePath());
			ProcessBuilder pb = new ProcessBuilder(args);
			Process p = pb.start();
			p.waitFor();
			byte[] result = Files.readAllBytes(tempFile.toPath());
			CacheManager.cache(fileContents, config, result);
			return result;
		} catch (Exception e) {
			throw new UncheckedIOException(e instanceof IOException io ? io : new IOException(e));
		}
	}

	@Override
	public String[] getSupportedExtensions() {
		return new String[] { "png" };
	}
}
