package com.github.xl1te.creativeversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PingVersion {
	private static final Pattern VERSION = Pattern.compile("(\\d+\\.\\d+(?:\\.\\d+)?)");

	static String detect(String label) {
		Matcher matcher = VERSION.matcher(label);
		String only = null;
		while (matcher.find()) {
			String found = matcher.group(1);
			if (only == null) {
				only = found;
			} else if (ItemVersions.compare(found, only) != 0) {
				return null;
			}
		}
		return only;
	}
}
