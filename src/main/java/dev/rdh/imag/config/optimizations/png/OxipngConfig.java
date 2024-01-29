package dev.rdh.imag.config.optimizations.png;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import dev.rdh.imag.config.optimizations.OptimizationConfig;

import static dev.rdh.imag.ImagPlugin.getProject;


public abstract class OxipngConfig extends OptimizationConfig {
	int MAX_LEVEL = 6;
	int DEFAULT_LEVEL = 2;

	@Input
	public abstract Property<Integer> getLevel();

	@Input
	public abstract Property<Boolean> getInterlace();

	@Input
	public abstract Property<StripMode> getStrip();

	@Input
	public abstract Property<Boolean> getChangeBitDepth();

	@Input
	public abstract Property<Boolean> getChangeColorType();

	@Input
	public abstract Property<Boolean> getChangePalette();

	@Input
	public abstract Property<Boolean> getChangeGrayscale();

	@Input
	public abstract Property<Boolean> getPerformTransformations();

	@Input
	public abstract Property<Boolean> getRecompress();

	@Input
	public abstract Property<Boolean> getFix();

	@Input
	public abstract Property<Boolean> getUseZopfli();

	@Input
	public abstract Property<Integer> getTimeout();

	@Input
	public abstract Property<Integer> getNumThreads();

	@Input
	public abstract Property<Boolean> getAlphaOptimizations();

	@Input
	public abstract Property<Boolean> getScale16();

	@Input
	public abstract Property<Boolean> getFastFilterEvaluation();

	@Input
	public abstract Property<Integer> getCompressionLevel();

	@Input
	public abstract ListProperty<FilterStrategy> getFilterStrategies();

	@Input
	public abstract ListProperty<String> getKeepChunks();

	public int getMax() {
		return MAX_LEVEL;
	}

	{
		getLevel().convention(DEFAULT_LEVEL);
		getInterlace().convention(false);
		getStrip().convention(StripMode.NONE);
		getChangeBitDepth().convention(false);
		getChangeColorType().convention(false);
		getChangePalette().convention(false);
		getChangeGrayscale().convention(false);
		getPerformTransformations().convention(false);
		getRecompress().convention(false);
		getFix().convention(false);
		getUseZopfli().convention(false);
		getTimeout().convention(0);
		getNumThreads().convention(0);
		getAlphaOptimizations().convention(false);
		getScale16().convention(false);
		getFastFilterEvaluation().convention(false);
		getFilterStrategies().convention(getProject().getObjects().listProperty(FilterStrategy.class));
		getKeepChunks().convention(getProject().getObjects().listProperty(String.class));
	}

	public enum FilterStrategy {
		NONE(0),
		SUB(1),
		UP(2),
		AVERAGE(3),
		PAETH(4),
		MIN_SUM(5),
		ENTROPY(6),
		BIGRAMS(7),
		BIG_ENT(8),
		BRUTE(9);

		private final int value;

		FilterStrategy(int value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}

	public enum StripMode {
		NONE,
		SAFE,
		ALL;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}
}
