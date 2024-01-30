package dev.rdh.imag.config.optimizations.png;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import dev.rdh.imag.config.optimizations.DisableableConfig;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class EctConfig extends DisableableConfig {
	public final int maxLevel = 9;
	public final int defaultLevel = 3;

	@Input
	public abstract Property<Integer> getLevel();

	@Input
	public abstract Property<Boolean> getStrip();

	public void strip() {
		getStrip().set(true);
	}

	@Input
	public abstract Property<Boolean> getStrictLosslessness();

	public void strictLosslessness() {
		getStrictLosslessness().set(true);
	}

	@Input
	public abstract Property<Boolean> getReuse();

	public void reuseFilter() {
		getReuse().set(true);
	}

	@Input
	public abstract Property<FilterMode> getFilterMode();

	@Input
	public abstract Property<Integer> getPaletteSortStrategies();

	@Input
	public abstract Property<Boolean> getDeflateMultithreading();

	public void deflateMultithreading() {
		getDeflateMultithreading().set(true);
	}

	@Input
	public abstract Property<Integer> getDeflateMultithreadingThreads();

	public void deflateMultithreading(int threads) {
		getDeflateMultithreadingThreads().set(threads);
	}

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
	public FilterMode getAllFilters() { return FilterMode.ALL; }
	public FilterMode getAllFiltersB() { return FilterMode.ALL_B; }

	@Override
	public int hashCode() {
		return Objects.hash(getEnabled().get(), getLevel().get(), getStrip().get(), getStrictLosslessness().get(),
				getReuse().get(), getFilterMode().get(), getPaletteSortStrategies().get(),
				getDeflateMultithreading().get(), getDeflateMultithreadingThreads().get());
	}

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
