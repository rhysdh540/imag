package dev.rdh.imag;

import dev.rdh.imag.process.Ect;
import dev.rdh.imag.process.Oxipng;
import dev.rdh.imag.process.Zopfli;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;
import java.util.function.UnaryOperator;

public class ImagMain {
	public static void main(String[] args) {
		int iterations = -1;
		int threads = -1;
		boolean bruteForce = false;

		List<File> files = new ArrayList<>();

		if(args.length == 0) {
			args = new String[] {"-h"};
		}
		for(String arg : args) {
			if(!arg.startsWith("-") && arg.endsWith(".png")) {
				File file = new File(arg);
				if(!file.exists()) {
					System.out.println("File not found: " + arg);
				}
				files.add(file);
			}

			if(arg.equals("--help") || arg.equals("-h")) {
				System.out.println("Usage: imag [options] <file1.png> <file2.png> ...");
				System.out.println("Options:");
				System.out.println("  -i, --iterations=<n>   Number of Zopfli iterations to run (default: based on file size)");
				System.out.println("  -t, --threads=<n>      Number of threads to use (default: number of cores)");
				System.out.println("  -b, --brute(-force)    Try all PNG filter modes, including brute force strategies (default: false)");
				return;
			}

			if(arg.equals("--brute-force") || arg.equals("-b") || arg.equals("--brute")) {
				bruteForce = true;
			}

			if(arg.startsWith("--iterations=") || arg.startsWith("-i=")) {
				try {
					iterations = Integer.parseInt(arg.split("=")[1]);
				} catch(Exception e) {
					System.out.println("Invalid number of iterations: " + arg.split("=")[1]);
					return;
				}
			}

			if(arg.startsWith("--threads=") || arg.startsWith("-t=")) {
				try {
					threads = Integer.parseInt(arg.split("=")[1]);
					if(threads < 1) {
						throw new Exception();
					}
				} catch(Exception e) {
					System.out.println("Invalid number of threads: " + arg.split("=")[1]);
					return;
				}
			}
		}

		if(files.isEmpty()) {
			System.out.println("No files specified");
			return;
		}

		if(threads == -1) {
			threads = Math.min(Runtime.getRuntime().availableProcessors(), files.size());
		}

		files.sort(Comparator.comparingLong(File::length).reversed());
		Queue<File> queue = files.stream().collect(ArrayDeque::new, ArrayDeque::add, ArrayDeque::addAll);
		files.clear();

		final int originalFiles = queue.size();
		final long originalSize = queue.stream().mapToLong(File::length).sum();

		LinkedHashMap<String, UnaryOperator<byte[]>> processors = new LinkedHashMap<>(1);
		processors.put("oxipng", Oxipng::process);
		final boolean finalBruteForce = bruteForce;
		final int finalIterations = iterations;
		processors.put("ect", bytes -> Ect.process(bytes, finalBruteForce));
		processors.put("zopfli", bytes -> Zopfli.process(bytes, finalIterations));

		ImagWorker.init(queue, processors);
		ImagWorker[] workers = new ImagWorker[threads];
		for(int i = 0; i < threads; i++) {
			workers[i] = new ImagWorker();
			workers[i].start();
		}

		long start = System.currentTimeMillis();
		System.out.print("\033[?25l\033[H\033[2J");

		Thread shutdownHook = new Thread(() -> System.out.println("\033[?25hInterrupted, stopping..."));
		Runtime.getRuntime().addShutdownHook(shutdownHook);

		do {
			StringBuilder sb = new StringBuilder();
			sb.append("\033[H");
			sb.append("Files remaining: ").append(queue.size()).append("/").append(originalFiles).append("\n");
			for(ImagWorker worker : workers) {
				sb.append("Worker ").append(worker.getNumber()).append(": ").append(worker.getStatus()).append(" ".repeat(20)).append("\n");
			}
			System.out.print(sb);
		} while(!Arrays.stream(workers).allMatch(ImagWorker::isIdle));

		for(ImagWorker worker : workers) {
			worker.stop();
		}
		Runtime.getRuntime().removeShutdownHook(shutdownHook);

		System.out.println("\033[H\033[2JDone!");
		long saved = ImagWorker.getTotalBytesSaved();
		System.out.println("Saved " + saved + " bytes (" + (100 * saved / originalSize) + "% of " + originalSize + ")");
		System.out.println("\033[?25hTime: " + formatTime(System.currentTimeMillis() - start));
	}

	public static String formatTime(long timeInMillis) {
		long hours = timeInMillis / 3600000;
		long minutes = (timeInMillis % 3600000) / 60000;
		long seconds = (timeInMillis % 60000) / 1000;
		long milliseconds = timeInMillis % 1000;

		StringBuilder sb = new StringBuilder();
		if (hours > 0) {
			sb.append(hours).append("h ");
		}
		if (hours > 0 || minutes > 0) {
			sb.append(minutes).append("m ");
		}
		if (hours > 0 || minutes > 0 || seconds > 0) {
			sb.append(seconds).append("s ");
		}
		sb.append(milliseconds).append("ms");

		return sb.toString().trim();
	}
}