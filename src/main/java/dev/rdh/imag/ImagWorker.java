package dev.rdh.imag;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

public class ImagWorker {
	private static Queue<File> files;
	private static LinkedHashMap<String, UnaryOperator<byte[]>> processors;
	private static final AtomicLong totalBytesSaved = new AtomicLong();
	private static final AtomicInteger nextNumber = new AtomicInteger();

	private File file;
	private final int number;
	private String currentProcessor = null;
	private boolean shouldRun = false;

	private Thread thread;

	public static void init(Queue<File> files, LinkedHashMap<String, UnaryOperator<byte[]>> processors) {
		ImagWorker.files = files;
		ImagWorker.processors = processors;
	}

	public static long getTotalBytesSaved() {
		return totalBytesSaved.get();
	}

	public ImagWorker() {
		number = nextNumber.incrementAndGet();
	}

	public int getNumber() {
		return number;
	}

	public String getStatus() {
		if(isIdle()) {
			return "IDLE";
		}

		return "Processing " + file.getName() + " with " + currentProcessor;
	}

	public synchronized boolean isIdle() {
		return file == null && currentProcessor == null;
	}

	@SuppressWarnings("SynchronizeOnNonFinalField")
	public void start() {
		shouldRun = true;
		thread = new Thread(() -> {
			while(shouldRun) {
				File file;
				synchronized(files) {
					file = files.poll();
				}
				synchronized(this) {
					this.file = file;
				}
				if(file == null) {
					currentProcessor = null;
					return;
				}
				process();
			}
		});
		thread.start();
	}

	public void stop() {
		shouldRun = false;
		thread.interrupt();
	}

	private void process() {
		byte[] data;
		final byte[] originalData;
		try {
			data = Files.readAllBytes(file.toPath());
			originalData = data;
		} catch(Exception e) {
			throw sneakyThrow(e);
		}
		for(Map.Entry<String, UnaryOperator<byte[]>> entry : processors.entrySet()) {
			synchronized(this) {
				currentProcessor = entry.getKey();
			}
			try {
				data = entry.getValue().apply(data);
			} catch(Exception e) {
				throw sneakyThrow(e);
			}
		}

		if(data.length >= originalData.length) {
			return;
		}

		try {
			Files.write(file.toPath(), data);
			totalBytesSaved.addAndGet(originalData.length - data.length);
		} catch(Exception e) {
			throw sneakyThrow(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> RuntimeException sneakyThrow(Throwable t) throws T {
		if(t == null) {
			throw new NullPointerException("t");
		}
		throw (T) t;
	}
}
