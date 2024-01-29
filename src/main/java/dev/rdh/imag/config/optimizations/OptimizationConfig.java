package dev.rdh.imag.config.optimizations;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;

import dev.rdh.imag.ImagPlugin;

import java.lang.reflect.Field;

public abstract class OptimizationConfig {
	@Input
	public abstract Property<Boolean> getEnabled();

	{
		getEnabled().convention(true);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for(Field field : getClass().getDeclaredFields()) {
			try {
				Object value = field.get(this);
				if(!(value instanceof Provider<?> provider)) {
					continue;
				}
				hash = hash * 31 + provider.get().hashCode();
			} catch (IllegalAccessException e) {
				ImagPlugin.getProject().getLogger().error("Failed to get field value for hashcode", e);
			}
		}

		return hash;
	}
}
