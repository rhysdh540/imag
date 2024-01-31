package dev.rdh.imag.config;

import org.gradle.api.provider.Property;

import dev.rdh.imag.config.optimizations.DisableableConfig;

public abstract class LoggingConfig extends DisableableConfig {
	public abstract Property<Boolean> getPrintHash();

	public abstract Property<Boolean> getPrintFiles();

	{
		getPrintHash().convention(true);
		getPrintFiles().convention(true);
	}
}
