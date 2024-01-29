package dev.rdh.imag.config.optimizations;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;

import java.util.Collections;
import java.util.Objects;

public abstract class JsonConfig extends DisableableConfig {

	@Input
	public abstract ListProperty<String> getExtraFileExtensions();

	{
		getExtraFileExtensions().convention(Collections.singletonList("mcmeta"));
	}

	@Override
	public int hashCode() {
		return Objects.hash(getEnabled().get(), getExtraFileExtensions().get());
	}
}
