package dev.rdh.imag.config.optimizations;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;

import dev.rdh.imag.ImagPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public abstract class DisableableConfig {
	@Input
	public abstract Property<Boolean> getEnabled();

	{
		getEnabled().convention(true);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for(Method method : getClass().getDeclaredMethods()) {
			try {
				if(!method.getName().startsWith("get")) {
					continue;
				}
				if(method.getParameterCount() != 0) {
					continue;
				}
				if(method.getReturnType() != Property.class) {
					continue;
				}
				method.setAccessible(true);
				Provider<?> value = (Provider<?>) method.invoke(this);
				hash = hash * 31 + Objects.hashCode(value.getOrNull());
			} catch (InvocationTargetException | IllegalAccessException e) {
				ImagPlugin.getProject().getLogger().error("Failed to get field value for hashcode", e);
			}
		}

		return hash;
	}
}
