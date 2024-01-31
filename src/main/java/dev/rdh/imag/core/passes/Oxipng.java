package dev.rdh.imag.core.passes;

import org.gradle.api.provider.Property;

import dev.rdh.imag.ImagPlugin;
import dev.rdh.imag.Util;
import dev.rdh.imag.config.ImagExtension;
import dev.rdh.imag.config.optimizations.png.OxipngConfig;
import dev.rdh.imag.config.optimizations.png.OxipngConfig.StripMode;
import dev.rdh.imag.core.FileProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Oxipng implements FileProcessor {
	private final OxipngConfig config;
	private final Property<Boolean> pngEnabled;
	private final Property<Boolean> imagEnabled;

	public Oxipng() {
		ImagExtension config = ImagPlugin.getProject().getExtensions().getByType(ImagExtension.class);
		this.config = config.getPng().getOxipng();
		this.imagEnabled = config.getEnabled();
		this.pngEnabled = config.getEnabled();
	}

	@Override
	public byte[] process(byte[] fileContents) {
		List<String> args = new ArrayList<>();
		args.add("oxipng");
		args.add("-o");
		args.add(config.getLevel().get().toString());
		if(config.getInterlace().get()) {
			args.add("-i");
			args.add("1");
		}
		if(config.getStrip().get() != StripMode.NONE) {
			args.add("-s");
			args.add(String.valueOf(config.getStrip().get()));
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
		if(config.getNumThreads().get() != -1) {
			args.add("-t");
			args.add(String.valueOf(config.getNumThreads().get()));
		}
		if(config.getTimeout().get() != -1) {
			args.add("--timeout");
			args.add(String.valueOf(config.getTimeout().get()));
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
			String[] strategies = config.getFilterStrategies().get().stream().map(String::valueOf).toArray(String[]::new);
			args.add("--filters");
			args.add(String.join(",", strategies));
		}
		if(config.getCompressionLevel().isPresent()) {
			args.add("--zc");
			args.add(String.valueOf(config.getCompressionLevel().get()));
		}
		if(config.getKeepChunks().isPresent() && !config.getKeepChunks().get().isEmpty()) {
			String[] chunks = config.getKeepChunks().get().stream().map(String::valueOf).toArray(String[]::new);
			args.add("--keep");
			args.add(String.join(",", chunks));
		}

		return Util.processFileWithCommand(args, fileContents, "png");
	}

	@Override
	public String[] getSupportedExtensions() {
		return new String[] { "png" };
	}

	@Override
	public boolean shouldProcess(File file) {
		return FileProcessor.super.shouldProcess(file) && config.getEnabled().get() && pngEnabled.get() && imagEnabled.get();
	}
}
