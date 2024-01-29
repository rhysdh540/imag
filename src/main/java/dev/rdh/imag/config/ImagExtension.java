package dev.rdh.imag.config;

import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import dev.rdh.imag.config.optimizations.JsonConfig;

public abstract class ImagExtension {
	@Input
	public abstract SetProperty<AbstractArchiveTask> getTasks();

	@InputFiles
	public abstract ListProperty<FileSystemLocation> getFiles();

	@Input
	public abstract Property<String> getFinalizeAfter();

	@Input
	public abstract Property<Boolean> getEnabled();

	@Nested
	public abstract JsonConfig getJson();

	public void setTasks(AbstractArchiveTask... tasks) {
		getTasks().empty();
		getTasks().addAll(tasks);
	}

	public void setFiles(FileSystemLocation... files) {
		getFiles().empty();
		getFiles().addAll(files);
	}

	public void setFinalizeAfter(String finalizeAfter) {
		getFinalizeAfter().set(finalizeAfter);
	}

	public void setEnabled(boolean enabled) {
		getEnabled().set(enabled);
	}

	{
		getEnabled().convention(true);
		getFinalizeAfter().convention("assemble");
	}
}
