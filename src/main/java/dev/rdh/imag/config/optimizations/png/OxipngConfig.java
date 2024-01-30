package dev.rdh.imag.config.optimizations.png;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import dev.rdh.imag.config.optimizations.DisableableConfig;

import java.util.Objects;

import static dev.rdh.imag.ImagPlugin.getProject;


public abstract class OxipngConfig extends DisableableConfig {
	public final int maxLevel = 6;
	public final int defaultLevel = 2;

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

	{
		getLevel().convention(defaultLevel);
		getInterlace().convention(false);
		getStrip().convention(StripMode.NONE);
		getChangeBitDepth().convention(true);
		getChangeColorType().convention(true);
		getChangePalette().convention(true);
		getChangeGrayscale().convention(true);
		getPerformTransformations().convention(true);
		getRecompress().convention(true);
		getFix().convention(false);
		getUseZopfli().convention(false);
		getTimeout().convention(-1);
		getNumThreads().convention(-1);
		getAlphaOptimizations().convention(false);
		getScale16().convention(false);
		getFastFilterEvaluation().convention(false);
		getFilterStrategies().convention(getProject().getObjects().listProperty(FilterStrategy.class));
		getKeepChunks().convention(getProject().getObjects().listProperty(String.class));
	}

	@Override
	public int hashCode() {
		return Objects.hash(getEnabled().getOrNull(), getLevel().getOrNull(), getInterlace().getOrNull(),
				getStrip().getOrNull(), getChangeBitDepth().getOrNull(), getChangeColorType().getOrNull(),
				getChangePalette().getOrNull(), getChangeGrayscale().getOrNull(),
				getPerformTransformations().getOrNull(), getRecompress().getOrNull(), getFix().getOrNull(),
				getUseZopfli().getOrNull(), getTimeout().getOrNull(), getNumThreads().getOrNull(),
				getAlphaOptimizations().getOrNull(), getScale16().getOrNull(), getFastFilterEvaluation().getOrNull(),
				getCompressionLevel().getOrNull(), getFilterStrategies().getOrNull(), getKeepChunks().getOrNull());
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
