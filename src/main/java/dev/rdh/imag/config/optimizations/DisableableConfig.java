package dev.rdh.imag.config.optimizations;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public abstract class DisableableConfig {
	@Input
	public abstract Property<Boolean> getEnabled();

	{
		getEnabled().convention(true);
	}

	public void disable() {
		getEnabled().set(false);
	}

	public abstract int hashCode();
}
