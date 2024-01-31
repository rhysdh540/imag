package dev.rdh.imag.core.passes;

import org.gradle.api.provider.Property;

import dev.rdh.imag.ImagPlugin;
import dev.rdh.imag.Util;
import dev.rdh.imag.config.ImagExtension;
import dev.rdh.imag.config.optimizations.png.EctConfig;
import dev.rdh.imag.core.FileProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Ect implements FileProcessor {
	private final Property<Boolean> pngEnabled;
	private final Property<Boolean> imagEnabled;
	private final EctConfig config;

	public Ect() {
		ImagExtension config = ImagPlugin.getProject().getExtensions().getByType(ImagExtension.class);
		this.pngEnabled = config.getPng().getEnabled();
		this.imagEnabled = config.getEnabled();
		this.config = config.getPng().getEct();
	}

	@Override
	public byte[] process(byte[] fileContents) {
		List<String> args = new ArrayList<>();
		args.add("ect");
		args.add("-" + config.getLevel().get());
		if(config.getStrip().get()) {
			args.add("-strip");
		}
		if(config.getStrictLosslessness().get()) {
			args.add("--strict");
		}
		if(config.getReuse().get()) {
			args.add("--reuse");
		}
		if(config.getFilterMode().get() != EctConfig.FilterMode.DEFAULT) {
			args.add("--" + config.getFilterMode().get());
		}
		if(config.getPaletteSortStrategies().get() != -1) {
			args.add("--pal_sort=" + config.getPaletteSortStrategies().get());
		}
		if(config.getDeflateMultithreading().get()) {
			args.add("--mt-deflate");
		}
		if(config.getDeflateMultithreadingThreads().get() != -1) {
			args.add("--mt-deflate=" + config.getDeflateMultithreadingThreads().get());
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
