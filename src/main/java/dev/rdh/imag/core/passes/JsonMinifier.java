package dev.rdh.imag.core.passes;

import org.gradle.api.provider.Property;

import dev.rdh.imag.ImagPlugin;
import dev.rdh.imag.config.ImagExtension;
import dev.rdh.imag.config.optimizations.JsonConfig;
import dev.rdh.imag.core.FileProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonMinifier implements FileProcessor {
	private final String[] supportedExtensions;
	private final JsonConfig config;
	private final Property<Boolean> imagEnabled;

	public JsonMinifier() {
		ImagExtension config = ImagPlugin.getProject().getExtensions().getByType(ImagExtension.class);
		this.imagEnabled = config.getEnabled();
		JsonConfig json = config.getJson();
		List<String> extensions = new ArrayList<>(json.getExtraFileExtensions().get());
		extensions.add("json");
		this.supportedExtensions = extensions.toArray(new String[0]);
		this.config = json;
	}

	@Override
	public byte[] process(byte[] fileContents) {
		String json = new String(fileContents);
		StringBuilder result = new StringBuilder();
		boolean inString = false;
		boolean escaped = false;
		for(char c : json.toCharArray()) {
			if (escaped) {
				result.append(c);
				escaped = false;
				continue;
			}

			if (c == '"') {
				inString = !inString;
				result.append(c);
			} else if (c == '\\') {
				escaped = true;
				result.append(c);
			} else if(inString || !shouldIgnore(c)) {
				result.append(c);
			}
		}

		return result.toString().getBytes();
	}

	private static boolean shouldIgnore(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}

	@Override
	public String[] getSupportedExtensions() {
		return supportedExtensions;
	}

	@Override
	public boolean shouldProcess(File file) {
		return FileProcessor.super.shouldProcess(file) && config.getEnabled().get() && imagEnabled.get();
	}
}
