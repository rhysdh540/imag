package dev.rdh.imag.config;

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

public abstract class ImagExtension {
	@Input
	public SetProperty<AbstractArchiveTask> getTasks() {
		return ImagPlugin.getProject().getObjects().setProperty(AbstractArchiveTask.class);
	}

	@InputFiles
	public ListProperty<RegularFile> getFiles() {
		return ImagPlugin.getProject().getObjects().listProperty(RegularFile.class);
	}

	@Input
	public Property<String> getFinalizeAfter() {
		return ImagPlugin.getProject().getObjects().property(String.class);
	}

	@Input
	public Property<Boolean> getEnabled() {
		return ImagPlugin.getProject().getObjects().property(Boolean.class);
	}

	@Nested
	public abstract JsonConfig getJson();

	public ImagExtension() {
		getEnabled().convention(true);
		getFinalizeAfter().convention("assemble");
	}
}
