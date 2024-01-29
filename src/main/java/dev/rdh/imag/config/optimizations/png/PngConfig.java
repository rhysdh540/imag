package dev.rdh.imag.config.optimizations.png;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

import dev.rdh.imag.config.optimizations.DisableableConfig;

public abstract class PngConfig extends DisableableConfig {

	@Nested
	public abstract OxipngConfig getOxipng();

	public void oxipng(Action<OxipngConfig> action) {
		action.execute(getOxipng());
	}
}
