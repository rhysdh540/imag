package dev.rdh.imag.config.optimizations.png;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;

import dev.rdh.imag.ImagPlugin;
import dev.rdh.imag.config.optimizations.OptimizationConfig;

public abstract class PngConfig extends OptimizationConfig {

	@Nested
	public abstract OxipngConfig getOxipng();

	public void oxipng(Action<OxipngConfig> action) {
		action.execute(getOxipng());
	}
}
