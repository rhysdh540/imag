package dev.rdh.imag.config.optimizations;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;

import java.util.Arrays;

public abstract class JsonConfig extends DisableableConfig {

	@Input
	public abstract ListProperty<String> getExtraFileExtensions();

	{
		getExtraFileExtensions().convention(Arrays.asList("mcmeta"));
	}
}
