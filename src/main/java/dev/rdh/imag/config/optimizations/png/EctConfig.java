package dev.rdh.imag.config.optimizations.png;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import dev.rdh.imag.config.optimizations.DisableableConfig;

@SuppressWarnings("unused")
public abstract class EctConfig extends DisableableConfig {
	public final int maxLevel = 9;
	public final int defaultLevel = 3;

	@Input
	public abstract Property<Integer> getLevel();

	@Input
	public abstract Property<Boolean> getStrip();

	@Input
	public abstract Property<Boolean> getStrictLosslessness();

	@Input
	public abstract Property<Boolean> getReuse();

	@Input
	public abstract Property<FilterMode> getFilterMode();

	@Input
	public abstract Property<Integer> getPaletteSortStrategies();

	@Input
	public abstract Property<Boolean> getDeflateMultithreading();

	@Input
	public abstract Property<Integer> getDeflateMultithreadingThreads();

	{
		getLevel().convention(defaultLevel);
		getStrip().convention(false);
		getStrictLosslessness().convention(false);
		getReuse().convention(false);
		getFilterMode().convention(FilterMode.DEFAULT);
		getPaletteSortStrategies().convention(-1);
		getDeflateMultithreading().convention(false);
		getDeflateMultithreadingThreads().convention(-1);
	}

	public FilterMode getDefault() { return FilterMode.DEFAULT; }
	public FilterMode getAll() { return FilterMode.ALL; }
	public FilterMode getAllB() { return FilterMode.ALL_B; }

	public enum FilterMode {
		DEFAULT, ALL, ALL_B;

		@Override
		public String toString() {
			if(this == DEFAULT) {
				throw new UnsupportedOperationException();
			}
			return name().toLowerCase().replace("all", "allfilters").replace("_", "-");
		}
	}
}