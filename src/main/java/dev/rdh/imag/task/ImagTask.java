package dev.rdh.imag.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.tasks.TaskAction;

import dev.rdh.imag.ImagPlugin;
import dev.rdh.imag.config.ImagExtension;
import dev.rdh.imag.config.optimizations.JsonConfig;
import dev.rdh.imag.core.FileProcessor;
import dev.rdh.imag.core.passes.JsonMinifier;

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

public class ImagTask extends DefaultTask {
	private JsonConfig jsonConfig;
	private Supplier<File> file;

	public void setConfig(ImagExtension config) {
		jsonConfig = config.getJson();
	}

	public void setFile(Supplier<File> file) {
		this.file = file;
	}

	@TaskAction
	public void run() {
		Project project = ImagPlugin.getProject();
		File jar = file.get();
		if(!jar.exists()) {
			project.getLogger().error(jar.getName() + " does not exist");
			return;
		}
		project.getLogger().lifecycle("Minifying " + jar.getName());
		project.getLogger().lifecycle("Original size: " + formatBytes(jar.length()));

		Directory tempDir = project.getLayout().getBuildDirectory().dir("imag/" + jar.getName()).get();
		project.copy(spec -> {
			spec.from(project.zipTree(jar));
			spec.into(tempDir);
		});

		List<FileProcessor> processors = new ArrayList<>();
		processors.add(new JsonMinifier(jsonConfig));

		for(File file : tempDir.getAsFileTree()) {
			if(!file.isFile()) continue;

			try {
				byte[] contents = Files.readAllBytes(file.toPath());
				for(FileProcessor processor : processors) {
					contents = processor.process(contents);
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

	private static String formatBytes(long bytes) {
		if(bytes == 1) {
			return "1 byte";
		}

		return String.format("%,d bytes", bytes);
	}
}
