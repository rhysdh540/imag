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

import dev.rdh.imag.ImagPlugin;
import dev.rdh.imag.config.optimizations.JsonConfig;
import dev.rdh.imag.config.optimizations.png.OxipngConfig.StripMode;
import dev.rdh.imag.config.optimizations.png.PngConfig;

public abstract class ImagExtension {

	private SetProperty<AbstractArchiveTask> tasks;
	@Input
	public SetProperty<AbstractArchiveTask> getTasks() {
		if(tasks == null) {
			tasks = ImagPlugin.getProject().getObjects().setProperty(AbstractArchiveTask.class);
		}
		return tasks;
	}

	private ListProperty<RegularFile> files;
	@InputFiles
	public ListProperty<RegularFile> getFiles() {
		if(files == null) {
			files = ImagPlugin.getProject().getObjects().listProperty(RegularFile.class);
		}
		return files;
	}

	private Property<String> finalizeAfter;

	@Input
	public Property<String> getFinalizeAfter() {
		if(finalizeAfter == null) {
			finalizeAfter = ImagPlugin.getProject().getObjects().property(String.class).convention("assemble");
		}
		return finalizeAfter;
	}

	private Property<Boolean> enabled;
	@Input
	public Property<Boolean> getEnabled() {
		if(enabled == null) {
			enabled = ImagPlugin.getProject().getObjects().property(Boolean.class).convention(true);
		}
		return enabled;
	}

	private Property<Boolean> cache;
	@Input
	public Property<Boolean> getCache() {
		if(cache == null) {
			cache = ImagPlugin.getProject().getObjects().property(Boolean.class).convention(true);
		}
		return cache;
	}

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

	public StripMode getNone() { return StripMode.NONE; }
	public StripMode getSafe() { return StripMode.SAFE; }
	public StripMode getAll() { return StripMode.ALL; }
}
