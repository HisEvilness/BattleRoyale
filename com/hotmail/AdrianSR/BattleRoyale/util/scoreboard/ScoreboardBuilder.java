package com.hotmail.AdrianSR.BattleRoyale.util.scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

import com.hotmail.AdrianSR.BattleRoyale.util.Matcher;

public class ScoreboardBuilder {

	private final List<String> data = new ArrayList<String>();
	private       String next_space = "";
	
	public ScoreboardBuilder() {
		/* nothing */
	}

	public ScoreboardBuilder(List<String> data) {
		this.data.addAll(data); /* avoid unmodifiable lists */
	}

	public ScoreboardBuilder(String[] data) {
		this(Arrays.asList(data));
	}

	public ScoreboardBuilder append(Object object) {
		data.add(String.valueOf(object));
		return this;
	}

	public ScoreboardBuilder appendSpace() {
		return append(nextSpace());
	}
	
	public ScoreboardBuilder appendBeforeNextSpace(Object object) {
		String item = String.valueOf(object);
		if (item == null || item.isEmpty()) {
			return this;
		}

		for (int i = 0; i < data.size(); i++) {
			if (StringUtils.isBlank(data.get(i))) { /* this checks that it is a space */
				insert(i, item);
				break;
			}
		}
		return this;
	}

	public ScoreboardBuilder appendAfterNextSpace(Object object) {
		String item = String.valueOf(object);
		if (item == null || item.isEmpty()) {
			return this;
		}

		for (int i = 0; i < data.size(); i++) {
			String it = data.get(i);
			int pos = (i + 1);
			if (StringUtils.isBlank(it)) { /* this checks that it is a space */
				if (pos > data.size()) {
					data.add(item);
				} else {
					insert(pos, item);
				}
				break;
			}
		}
		return this;
	}

	public ScoreboardBuilder delete(int x) {
		data.remove(x);
		return this;
	}

	public ScoreboardBuilder deleteIf(Predicate<? super String> predicate) {
		data.removeIf(predicate);
		return this;
	}

	public ScoreboardBuilder replace(int x, Object object) {
		data.set(x, String.valueOf(object));
		return this;
	}

	public ScoreboardBuilder replace(int x, Matcher matcher, String replacement) {
		String value = data.get(x);
		if (value != null) {
			replace(x, matcher.replace(value, replacement));
		}
		return this;
	}

	public ScoreboardBuilder insert(int x, Object object) {
		data.add(x, String.valueOf(object));
		return this;
	}

	public int indexOf(String key) {
		return data.indexOf(key);
	}

	public int lastIndexOf(String key) {
		return data.lastIndexOf(key);
	}

	public ScoreboardBuilder reverse() {
		Collections.reverse(data);
		return this;
	}

	public ScoreboardBuilder clear() {
		data.clear();
		return this;
	}

	public ScoreboardBuilder clearNulls() {
		data.removeIf(item -> item == null);
		return this;
	}

	public ScoreboardBuilder clearSpaces() {
		data.removeIf(item -> item == null || item.isEmpty());
		return this;
	}

	public ScoreboardBuilder clearBlanks() {
		data.removeIf(item -> StringUtils.isBlank(item));
		return this;
	}

	public String get(int x) {
		return data.get(x);
	}
	
	public String getNextSpace() {
		return ( next_space + " " );
	}

	public Optional<String> getOptional(int x) {
		return Optional.ofNullable(get(x));
	}

	public boolean isValid() {
		return data != null && !data.isEmpty();
	}

	public List<String> toList() {
		List<String> copy = new ArrayList<String>();
		copy.addAll(data);
		return copy;
	}

	public String[] toArray() {
		return toList().toArray(new String[data.size()]);
	}

	public Optional<List<String>> toOptional() {
		return Optional.of(toList());
	}

	public Optional<String[]> toOptionalArray() {
		return Optional.of(toArray());
	}

	public Stream<String> stream() {
		return data.stream();
	}
	
	private String nextSpace() {
		return ( next_space += " " );
	}
}