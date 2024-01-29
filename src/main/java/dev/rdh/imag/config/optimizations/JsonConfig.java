package dev.rdh.imag.config.optimizations;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import dev.rdh.imag.ImagPlugin;

public abstract class JsonConfig {
	@Input
	public abstract ListProperty<String> getExtraFileExtensions();

	@Input
	public abstract Property<Boolean> getEnabled();

	{
		getEnabled().convention(true);
		getExtraFileExtensions().convention(ImagPlugin.getProject().getObjects().listProperty(String.class));
		getExtraFileExtensions().add("mcmeta");
	}
}
