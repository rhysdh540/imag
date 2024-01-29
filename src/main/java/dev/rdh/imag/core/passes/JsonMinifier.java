package dev.rdh.imag.core.passes;

import dev.rdh.imag.config.optimizations.JsonConfig;
import dev.rdh.imag.core.FileProcessor;

import java.util.ArrayList;
import java.util.List;

public class JsonMinifier implements FileProcessor {
	private final String[] supportedExtensions;
	private final JsonConfig config;

	public JsonMinifier(JsonConfig config) {
		List<String> extensions = new ArrayList<>(config.getExtraFileExtensions().get());
		extensions.add("json");
		supportedExtensions = extensions.toArray(new String[0]);
		this.config = config;
	}

	@Override
	public byte[] process(byte[] fileContents) {
		if(!config.getEnabled().get()) {
			return fileContents;
		}

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
}
