package dev.rdh.imag.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;

import dev.rdh.imag.ImagPlugin;
import dev.rdh.imag.Util;
import dev.rdh.imag.config.ImagExtension;
import dev.rdh.imag.core.CacheManager;
import dev.rdh.imag.core.FileProcessor;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ServiceLoader;
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

	@Input
	public ImagExtension getConfig() {
		return config;
	}

	public void setFile(Supplier<File> file) {
		this.file = file;
	}

	public void setFile(File file) {
		setFile(() -> file);
	}

	@InputFile
	public File getFile() {
		return file.get();
	}

	@TaskAction
	public void run() {
		Project project = ImagPlugin.getProject();
		Logger logger = project.getLogger();
		if(!config.getEnabled().get()) {
			logger.lifecycle("Imag is disabled");
			return;
		}

		File jar = file.get();
		if(!jar.exists()) {
			logger.error(jar.getName() + " does not exist");
			return;
		}
		logger.lifecycle("Minifying " + jar.getName());
		logger.lifecycle("Original size: " + formatBytes(jar.length()));
		if(config.getLogging().getPrintHash().get()) {
			logger.lifecycle("Hash: " + Util.hash(project.getExtensions().getByType(ImagExtension.class)));
		}

		Directory tempDir = project.getLayout().getBuildDirectory().get().dir("imag").dir(jar.getName());
		if(tempDir.getAsFile().exists()) {
			deleteDirectory(tempDir.getAsFile());
		}
		project.copy(spec -> {
			spec.from(project.zipTree(jar));
			spec.into(tempDir);
		});

		ServiceLoader<FileProcessor> processors = ServiceLoader.load(FileProcessor.class);

		for(File file : tempDir.getAsFileTree()) {
			if(!file.isFile()) continue;

			try {
				byte[] contents = Files.readAllBytes(file.toPath());
				if(CacheManager.isCached(contents)) {
					byte[] newContents = CacheManager.getCached(contents);
					logger.lifecycle("Using cached " + file.getName() + " (" + formatBytes(contents.length - newContents.length) + " smaller)");
					contents = newContents;
				} else {
					byte[] processed = contents;
					boolean processedOnce = false;

					for(FileProcessor processor : processors) {
						if(processor.shouldProcess(file)) {
							processedOnce = true;
							if(config.getLogging().getPrintFiles().get()) {
								logger.info("Processing " + file.getName() + " with " + processor.getClass().getSimpleName());
							}
							processed = processor.process(processed);
						}
					}

					if(processedOnce) {
						if(config.getLogging().getPrintFiles().get()) {
							logger.lifecycle("Processed " + file.getName() + " (" + formatBytes(contents.length - processed.length) + " smaller)");
						}
						CacheManager.cache(processed, contents);
					}
					contents = processed;
				}
				Files.write(file.toPath(), contents);
			} catch(IOException e) {
				logger.error("Failed to minify " + file.getName(), e);
			}
		}

		if(!jar.delete()) {
			logger.error("Failed to delete " + jar.getName());
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

		logger.lifecycle("New size: " + formatBytes(jar.length()));
	}
}
