package dev.rdh.imag.config.optimizations;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import dev.rdh.imag.ImagPlugin;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class JsonConfig extends OptimizationConfig {

	@Input
	public abstract ListProperty<String> getExtraFileExtensions();

	{
		getExtraFileExtensions().convention(Arrays.asList("mcmeta"));
	}
}
