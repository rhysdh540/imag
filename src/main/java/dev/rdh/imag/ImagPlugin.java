package dev.rdh.imag;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.rdh.imag.config.ImagExtension;
import dev.rdh.imag.task.ImagTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImagPlugin implements Plugin<Project> {

	@Nullable
	private static Project project;

	@NotNull
	public static Project getProject() {
		if(project == null) {
			throw new IllegalStateException("Project not set");
		}
		return project;
	}

	@Override
	public void apply(@NotNull Project project) {
		ImagPlugin.project = project;
		ImagExtension extension = project.getExtensions().create("imag", ImagExtension.class);

		project.afterEvaluate(p -> {
			if(!extension.getEnabled().orElse(true).get()) {
				project.getLogger().lifecycle("Imag is disabled");
				return;
			}

			Set<AbstractArchiveTask> tasks = new HashSet<>(extension.getTasks().get());
			tasks.add(p.getTasks().named("jar", Jar.class).get());

			for(AbstractArchiveTask task : tasks) {
				p.getTasks().create("imag" + capitalize(task.getName()), ImagTask.class, imagTask -> {
					imagTask.setGroup("imag");
					imagTask.setDescription("Optimize the " + task.getName() + " task's output");
					imagTask.dependsOn(task);
					imagTask.getInputs().files(task.getArchiveFile());
					imagTask.setFile(task.getArchiveFile().get()::getAsFile);
					imagTask.setConfig(extension);
				});
			}

			List<FileSystemLocation> files = new ArrayList<>(extension.getFiles().get());
			for(FileSystemLocation file : files) {
				p.getTasks().create("imag" + capitalize(file.getAsFile().getName()), ImagTask.class, imagTask -> {
					imagTask.setGroup("imag");
					imagTask.setDescription("Optimize " + file.getAsFile().getName());
					imagTask.dependsOn(file);
					imagTask.setFile(file::getAsFile);
					imagTask.setConfig(extension);
				});
			}
		});
	}

	private static String capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
}
