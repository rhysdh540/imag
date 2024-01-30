package dev.rdh.imag.config.optimizations.png;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

import dev.rdh.imag.config.optimizations.DisableableConfig;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class PngConfig extends DisableableConfig {

	@Nested
	public abstract OxipngConfig getOxipng();

	@Nested
	public abstract EctConfig getEct();

	public void oxipng(Action<OxipngConfig> action) {
		action.execute(getOxipng());
	}

	public void ect(Action<EctConfig> action) {
		action.execute(getEct());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getEnabled().get(), getOxipng(), getEct());
	}
}
