package dev.rdh.imag.config;

import org.gradle.api.Action;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import dev.rdh.imag.config.optimizations.DisableableConfig;
import dev.rdh.imag.config.optimizations.JsonConfig;
import dev.rdh.imag.config.optimizations.png.OxipngConfig.StripMode;
import dev.rdh.imag.config.optimizations.png.PngConfig;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class ImagExtension extends DisableableConfig {

	@Input
	public abstract SetProperty<AbstractArchiveTask> getTasks();

	@InputFiles
	public abstract ListProperty<RegularFile> getFiles();

	@Input
	public abstract Property<String> getFinalizeAfter();

	@Input
	public abstract Property<Boolean> getEnabled();

	@Input
	public abstract Property<Boolean> getCache();

	@Nested
	public abstract JsonConfig getJson();

	public void json(Action<JsonConfig> action) {
		action.execute(getJson());
	}

	@Nested
	public abstract PngConfig getPng();

	public void png(Action<PngConfig> action) {
		action.execute(getPng());
	}

	@Nested
	public abstract LoggingConfig getLogging();

	public void logging(Action<LoggingConfig> action) {
		action.execute(getLogging());
	}

	{
		getCache().convention(true);
		getFinalizeAfter().convention("assemble");
	}

	@Override
	public int hashCode() {
		return Objects.hash(getEnabled().get(), getCache().get(), getJson(), getPng());
	}

	public StripMode getNone() { return StripMode.NONE; }
	public StripMode getSafe() { return StripMode.SAFE; }
	public StripMode getAll() { return StripMode.ALL; }
}
