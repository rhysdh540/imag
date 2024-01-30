package dev.rdh.imag.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.tasks.TaskAction;

import dev.rdh.imag.ImagPlugin;
import dev.rdh.imag.config.ImagExtension;
import dev.rdh.imag.core.CacheManager;
import dev.rdh.imag.core.FileProcessor;
import dev.rdh.imag.core.passes.Ect;
import dev.rdh.imag.core.passes.JsonMinifier;
import dev.rdh.imag.core.passes.Oxipng;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static dev.rdh.imag.Util.formatBytes;
import static org.gradle.util.internal.GFileUtils.deleteDirectory;

public class ImagTask extends DefaultTask {
	private ImagExtension config;
	private Supplier<File> file;

	public void setConfig(ImagExtension config) {
		this.config = config;
	}

	public void setFile(Supplier<File> file) {
		this.file = file;
	}

	@TaskAction
	public void run() {
		Project project = ImagPlugin.getProject();
		if(!config.getEnabled().get()) {
			project.getLogger().lifecycle("Imag is disabled");
			return;
		}

		File jar = file.get();
		if(!jar.exists()) {
			project.getLogger().error(jar.getName() + " does not exist");
			return;
		}
		project.getLogger().lifecycle("Minifying " + jar.getName());
		project.getLogger().lifecycle("Original size: " + formatBytes(jar.length()));

		Directory tempDir = project.getLayout().getBuildDirectory().get().dir("imag").dir(jar.getName());
		if(tempDir.getAsFile().exists()) {
			deleteDirectory(tempDir.getAsFile());
		}
		project.copy(spec -> {
			spec.from(project.zipTree(jar));
			spec.into(tempDir);
		});

		List<FileProcessor> processors = new ArrayList<>();
		processors.add(new JsonMinifier(config));
		processors.add(new Ect(config));
		processors.add(new Oxipng(config));

		for(File file : tempDir.getAsFileTree()) {
			if(!file.isFile()) continue;

			try {
				byte[] contents = Files.readAllBytes(file.toPath());
				if(CacheManager.isCached(contents)) {
					byte[] newContents = CacheManager.getCached(contents);
					project.getLogger().lifecycle("Using cached " + file.getName() + " (" + formatBytes(contents.length - newContents.length) + " smaller)");
					contents = newContents;
				} else {
					byte[] processed = contents;
					boolean processedOnce = false;

					for(FileProcessor processor : processors) {
						if(processor.shouldProcess(file)) {
							processedOnce = true;
							project.getLogger().lifecycle("Processing " + file.getName() + " with " + processor.getClass().getSimpleName());
							processed = processor.process(processed);
						}
					}

					if(processedOnce) {
						project.getLogger().lifecycle("Processed " + file.getName() + " (" + formatBytes(contents.length - processed.length) + " saved)");
						CacheManager.cache(processed, contents);
					}
					contents = processed;
				}
				Files.write(file.toPath(), contents);
			} catch(IOException e) {
				project.getLogger().error("Failed to minify " + file.getName(), e);
			}
		}

		if(!jar.delete()) {
			project.getLogger().error("Failed to delete " + jar.getName());
		}

		try(ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(jar.toPath()))) {
			out.setLevel(Deflater.BEST_COMPRESSION);
			out.setMethod(ZipOutputStream.DEFLATED);
			for(File file : tempDir.getAsFileTree()) {
				if(!file.isFile()) continue;

				out.putNextEntry(new ZipEntry(file.getName()));
				out.write(Files.readAllBytes(file.toPath()));
				out.closeEntry();
			}
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}

		project.getLogger().lifecycle("New size: " + formatBytes(jar.length()));
	}
}
