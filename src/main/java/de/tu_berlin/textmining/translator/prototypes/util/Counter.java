package de.tu_berlin.textmining.translator.prototypes.util;

import java.util.HashMap;
import java.util.Map;

public class Counter {
	
	private final static int DEFAULT_INCREMENT = 1;
	
	private final Map<String, Long> counts;
	private long total;

	public Counter() {
		this.counts = new HashMap<String, Long>();
		this.total = 0;
	}

	public void increment(String key) {
		this.increment(key, Counter.DEFAULT_INCREMENT);
	}
	
	public void increment(String key, int increment) {
		Long count = this.counts.get(key);
		if (count != null) {
			this.counts.put(key, count + increment);
		} else {
			this.counts.put(key, (long) increment);
		}
		
		this.total++;
	}
	
	public long getTotal() {
		return total;
	}
	
	public long getAbsoluteCount(String key) {
		Long count = this.counts.get(key);
		return (count != null) ? count : 0;
	}
	
	public double getRelativeCount(String key) {
		long count = this.getAbsoluteCount(key);
		if (this.total > 0) {
			return (double) count / this.total;
		} else {
			return 0.0;
		}
	}
}
